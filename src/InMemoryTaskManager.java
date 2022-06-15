import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class InMemoryTaskManager implements TaskManager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
    private List<Tasks> browsingList = new ArrayList<>();
    private int taskCounter = 0;

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

    @Override
    public Object createTask(Task task) {
        task.setMainTaskId(incrementTaskCounter());
        taskMap.put(task.getMainTaskId(), task);
        return task;
    }

    @Override
    public Object createEpic(Epic epic) {
        epic.setMainTaskId(incrementTaskCounter());
        epicMap.put(epic.getMainTaskId(), epic);
        return epic;
    }

    @Override
    public Object createSubtask(Subtask subtask) {
        int id = subtask.getEpicId();

        subtask.setMainTaskId(incrementTaskCounter());
        subtaskMap.put(subtask.getMainTaskId(), subtask);
        Epic epic = epicMap.get(id);
        epic.subtaskMap.put(subtask.getMainTaskId(), subtask);
        epicMap.put(id, epic);
        return subtask;
    }

    @Override
    public HashMap<Integer, Task> showAllTasks() {
        return taskMap;
    }

    @Override
    public HashMap<Integer, Epic> showAllEpic() {
        return epicMap;
    }

    @Override
    public HashMap<Integer, Subtask> showAllSubtasks() {
        return subtaskMap;
    }

    @Override
    public HashMap<Integer, Task> deleteAllTasks() {
        taskMap.clear();
        return taskMap;
    }

    @Override
    public HashMap<Integer, Epic> deleteAllEpics() {
        epicMap.clear();
        subtaskMap.clear();
        return epicMap;

    }

    @Override
    public HashMap<Integer, Subtask> deleteAllSubtasks() {
        subtaskMap.clear();
        for (int i = 0; i < epicMap.size(); i++) {
            if (epicMap.containsKey(i)) {
                Epic epic = epicMap.get(i);
                epic.subtaskMap.clear();
            }
        }
        return subtaskMap;
    }

    @Override
    public HashMap<Integer, Task> deleteTaskById(int id) {
        taskMap.remove(id);
        return taskMap;
    }

    @Override
    public HashMap<Integer, Epic> deleteEpicById(int id) {
        for (int i = 0; i < getTaskCounter(); i++) {
            if (epicMap.containsKey(id) && subtaskMap.containsKey(i) && id == subtaskMap.get(i).getEpicId()) {
                subtaskMap.remove(i);
            }
        }
        epicMap.remove(id);
        return epicMap;
    }

    @Override
    public HashMap<Integer, Subtask> deleteSubtaskById(int id) {
        if (subtaskMap.containsKey(id)) {
            Subtask subtask = subtaskMap.get(id);
            if (epicMap.containsKey(subtask.getEpicId())) {
                Epic epic = epicMap.get(subtask.getEpicId());
                epic.subtaskMap.remove(id);
                epicMap.put(epic.getMainTaskId(), epic);
                subtaskMap.remove(id);
            }
        }
        return subtaskMap;
    }

    @Override
    public Task showTaskById(int id) {
        Task task = taskMap.get(id);
        checkSizeBrowsingList();
        browsingList.add(task);
        return task;
    }

    @Override
    public Epic showEpicById(int id) {
        Epic epic = epicMap.get(id);
        checkSizeBrowsingList();
        browsingList.add(epic);
        return epic;
    }

    @Override
    public Subtask showSubtaskById(int id) {
        Subtask subtask = subtaskMap.get(id);
        checkSizeBrowsingList();
        browsingList.add(subtask);
        return subtask;
    }

    /* добавлять ли сабтаски при запросе их по эпику в историю просмотров?
    Было бы логично, но в задании Так как в истории отображается, к каким задачам было обращение в методах getTask(),
    getSubtask() и getEpic(), эти данные в полях менеджера будут обновляться при вызове этих трех методов.*/

    @Override
    public HashMap<Integer, Subtask> showSubtasksByEpicId(int id) {
        if (subtaskMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            return epic.subtaskMap;
        } else {
            return null;
        }
    }

    @Override
    public Task updateTask(int id, Task.Status status) {
        Task task = taskMap.get(id);
        task = new Task(task.getName(), task.getDescription(), id, status);
        taskMap.put(id, task);
        return task;
    }

    @Override
    public Subtask updateSubtask(int id, Task.Status status) {
        Subtask subtask = subtaskMap.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epicMap.get(epicId);
        subtask = new Subtask(subtask.getName(), subtask.getDescription(), epicId, status, id);
        subtaskMap.put(id, subtask);
        epic.subtaskMap.put(id, subtask);
        updateEpic(epicId);
        return subtask;
    }

    @Override
    public Epic updateEpic(int epicId) {
        Epic epic = epicMap.get(epicId);
        int newStatus = 0;
        int inProgress = 0;
        int done = 0;

        for (int i = 0; i < getTaskCounter(); i++) {
            if (epic.subtaskMap.get(i) != null) {
                Subtask subtask = epic.subtaskMap.get(i);
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
            epic = new Epic(epic.getName(), epic.getDescription(),
                    epic.getMainTaskId(), Task.Status.DONE, epic.subtaskMap);
            epicMap.put(epicId, epic);
        } else if (inProgress > 0 || done > 0 && newStatus > 0) {
            epic = new Epic(epic.getName(), epic.getDescription(),
                    epic.getMainTaskId(), Task.Status.IN_PROGRESS, epic.subtaskMap);
            epicMap.put(epicId, epic);
        } else {
            epic = new Epic(epic.getName(), epic.getDescription(),
                    epic.getMainTaskId(), Task.Status.NEW, epic.subtaskMap);
            epicMap.put(epicId, epic);
        }
        return epic;
    }

    @Override
    public int getTaskCounter() {
        return taskCounter;
    }

    @Override
    public int incrementTaskCounter() {
        return ++taskCounter;
    }

    @Override
    public void setTaskCounter(int counter) {
        this.taskCounter = counter;
    }

    @Override
    public List<Tasks> getHistory() {
        return browsingList;
    }

    private void checkSizeBrowsingList() {
        if (browsingList.size() > 10) {
            browsingList.remove(0);
        }
    }
}

