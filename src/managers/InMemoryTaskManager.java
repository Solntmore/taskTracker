package managers;

import task.*;
import interfaces.*;

import javax.naming.directory.InvalidAttributesException;
import java.io.IOException;
import java.util.*;

import static managers.Validator.validator;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> taskMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicMap = new HashMap<>();
    protected Set<Task> prioritizedSet = new TreeSet<>();

    private int taskCounter = 0;
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    @Override
    public String toString() {
        return "Manager{" +
                "taskMap=" + taskMap +
                ", SubtaskMap=" + subtaskMap +
                ", EpicMap=" + epicMap +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) o;
        return taskCounter == inMemoryTaskManager.taskCounter && Objects.equals(taskMap, inMemoryTaskManager.taskMap) &&
                Objects.equals(subtaskMap, inMemoryTaskManager.subtaskMap) &&
                Objects.equals(epicMap, inMemoryTaskManager.epicMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskMap, subtaskMap, epicMap, taskCounter);
    }

    /* в методах, где по какой-то причине создание задачи невозможно или подобных случаях была идея выкидывать исключение,
     * однако, такие исключения пришлось бы где-то обрабатывать, и делать это в классе проверки не логично, вроде бы как,
     * это происходит на фронте. После обсуждений с наставником и студентами пришли к варианту, что возвращать null
     * и писать текстовое сообщение будет для данной ситуации логично, поэтому во всех методах при каком-то варианте,
     * когда нарушается сценарий возвращается null
     */
    @Override
    public Object createTask(Task task) throws IOException {
        try {
            validator(task.getStartTime(), task.getDuration(), taskMap, subtaskMap);
            task.setMainTaskId(incrementTaskCounter());
            taskMap.put(task.getMainTaskId(), task);
            addTaskToPrioritizedSet(task);
            return task;
        } catch (InvalidAttributesException e) {
            System.out.println("Задачи не могут пересекаться во времени!");
            return null;
        }
    }


    @Override
    public Object createEpic(Epic epic) throws IOException {
        epic.setMainTaskId(incrementTaskCounter());
        epicMap.put(epic.getMainTaskId(), epic);
        return epic;
    }

    @Override
    public Object createSubtask(Subtask subtask) throws IOException {
        try {
            validator(subtask.getStartTime(), subtask.getDuration(), taskMap, subtaskMap);
            int id = subtask.getEpicId();

            Epic epic = epicMap.get(id);
            if (epic != null) {
                subtask.setMainTaskId(incrementTaskCounter());
                subtaskMap.put(subtask.getMainTaskId(), subtask);

                epic.addSubtaskMap(subtask.getMainTaskId(), subtask);
                epic.getStartTime();
                epic.getEndTime();
                epic.getDuration();
                epicMap.put(id, epic);
                updateEpic(id);
                addTaskToPrioritizedSet(subtask);
                return subtask;
            }
            return null;


        } catch (InvalidAttributesException e) {
            System.out.println("Задачи не могут пересекаться во времени!");
            return null;
        }
    }

    @Override
    public HashMap<Integer, Task> showAllTasks() {
        return taskMap;
    }

    @Override
    public HashMap<Integer, Epic> showAllEpics() {
        return epicMap;
    }

    @Override
    public HashMap<Integer, Subtask> showAllSubtasks() {
        return subtaskMap;
    }

    @Override
    public HashMap<Integer, Task> deleteAllTasks() throws IOException {
        removeAllTasksFromPrioritizedSet();
        taskMap.clear();
        return taskMap;
    }

    @Override
    public HashMap<Integer, Epic> deleteAllEpics() throws IOException {
        epicMap.clear();
        subtaskMap.clear();
        return epicMap;

    }

    @Override
    public HashMap<Integer, Subtask> deleteAllSubtasks() throws IOException {
        removeAllSubtasksFromPrioritizedSet();
        subtaskMap.clear();
        for (int i = 0; i < epicMap.size(); i++) {
            if (epicMap.containsKey(i)) {
                Epic epic = epicMap.get(i);
                epic.clearSubtaskMap();
            }
        }
        return subtaskMap;
    }

    @Override
    public HashMap<Integer, Task> deleteTaskById(int id) throws IOException {
        if (taskMap.containsKey(id)) {
            removeTaskFromPrioritizedSet(taskMap.get(id));
            inMemoryHistoryManager.remove(id);
            taskMap.remove(id);
            return taskMap;
        }
        return null;
    }

    @Override
    public HashMap<Integer, Epic> deleteEpicById(int id) throws IOException {
        if (epicMap.containsKey(id)) {
            for (int i = 0; i < getTaskCounter(); i++) {
                if (subtaskMap.containsKey(i) && id == subtaskMap.get(i).getEpicId()) {
                    removeTaskFromPrioritizedSet(subtaskMap.get(i));
                    subtaskMap.remove(i);
                    inMemoryHistoryManager.remove(i);
                }
            }

            inMemoryHistoryManager.remove(id);
            epicMap.remove(id);
            return epicMap;
        }
        return null;
    }

    @Override
    public HashMap<Integer, Subtask> deleteSubtaskById(int id) throws IOException {
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            if (epicMap.containsKey(subtask.getEpicId())) {
                Epic epic = epicMap.get(subtask.getEpicId());
                epic.removeFromSubtaskMap(id);
                epicMap.put(epic.getMainTaskId(), epic);
                inMemoryHistoryManager.remove(id);
                removeTaskFromPrioritizedSet(subtaskMap.get(id));
                subtaskMap.remove(id);
                return subtaskMap;
            }
            return subtaskMap;
        }
        return null;

    }

    @Override
    public Task showTaskById(int id) throws IOException {
        Task task = taskMap.get(id);
        if (task != null) {
            inMemoryHistoryManager.addTask(task);
            return task;
        }
        return null;
    }

    @Override
    public Epic showEpicById(int id) throws IOException {
        Epic epic = epicMap.get(id);
        if (epic != null) {
            inMemoryHistoryManager.addTask(epic);
            return epic;
        }
        return null;
    }

    @Override
    public Subtask showSubtaskById(int id) throws IOException {
        Subtask subtask = subtaskMap.get(id);
        if (subtask != null) {
            inMemoryHistoryManager.addTask(subtask);
            return subtask;
        }
        return null;
    }

    @Override
    public HashMap<Integer, Subtask> showSubtasksByEpicId(int id) throws IOException {
        if (epicMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            return epic.getSubtaskMap();
        } else {
            return null;
        }
    }

    @Override
    public Task updateTask(int id, Task.Status status) throws IOException {
        if (taskMap.containsKey(id)) {
            Task task = taskMap.get(id);
            removeTaskFromPrioritizedSet(task);
            task = new Task(task.getName(), task.getDescription(), id, status, task.getStartTime(), task.getDuration());
            addTaskToPrioritizedSet(task);
            taskMap.put(id, task);
            return task;
        } else {
            return null;
        }
    }

    @Override
    public Subtask updateSubtask(int id, Task.Status status) throws IOException {
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            removeTaskFromPrioritizedSet(subtask);
            int epicId = subtask.getEpicId();
            Epic epic = epicMap.get(epicId);
            subtask = new Subtask(subtask.getName(), subtask.getDescription(), epicId, status, id,
                    subtask.getStartTime(), subtask.getDuration());
            addTaskToPrioritizedSet(subtask);
            subtaskMap.put(id, subtask);
            epic.addSubtaskMap(id, subtask);
            updateEpic(epicId);
            return subtask;
        } else {
            return null;
        }
    }

    @Override
    public Epic updateEpic(int epicId) throws IOException {
        if (epicMap.containsKey(epicId)) {
            Epic epic = epicMap.get(epicId);
            int newStatus = 0;
            int inProgress = 0;
            int done = 0;

            for (int i = 0; i <= getTaskCounter(); i++) {
                if (epic.containsSubtaskMap(i)) {
                    Subtask subtask = epic.getFromSubtaskMap(i);
                    Task.Status status = subtask.getStatus();
                    if (status.equals(Task.Status.NEW)) {
                        newStatus += 1;
                    } else if (status.equals(Task.Status.IN_PROGRESS)) {
                        inProgress += 1;
                    } else {
                        done += 1;
                    }
                }
            }

            if (inProgress == 0 && newStatus == 0 && done > 0) {
                epic.setStatus(Task.Status.DONE);
                epicMap.put(epicId, epic);
            } else if (inProgress > 0 || done > 0 && newStatus > 0) {
                epic.setStatus(Task.Status.IN_PROGRESS);
                epicMap.put(epicId, epic);
            } else {
                epic.setStatus(Task.Status.NEW);
                epicMap.put(epicId, epic);
            }
            return epic;
        } else {
            return null;
        }
    }

    public int getTaskCounter() {
        return taskCounter;
    }

    public int incrementTaskCounter() {
        return ++taskCounter;
    }

    public void setTaskCounter(int counter) {
        this.taskCounter = counter;
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    private void addTaskToPrioritizedSet(Task task) {
        prioritizedSet.add(task);
    }

    private void removeTaskFromPrioritizedSet(Task task) {
        prioritizedSet.remove(task);
    }

    private void removeAllTasksFromPrioritizedSet() {
        for (Task task : taskMap.values()) {
            removeTaskFromPrioritizedSet(task);
        }
    }

    private void removeAllSubtasksFromPrioritizedSet() {
        for (Subtask subtask : subtaskMap.values()) {
            removeTaskFromPrioritizedSet(subtask);
        }
    }

    public Set<Task> getPrioritizedSet() {
        return prioritizedSet;
    }
}

