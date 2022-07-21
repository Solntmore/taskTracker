package managers;

import task.*;
import interfaces.*;

import java.io.IOException;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected HashMap<Integer, Task> taskMap = new HashMap<>();
    protected HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    protected HashMap<Integer, Epic> epicMap = new HashMap<>();

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

    @Override
    public Object createTask(Task task) throws IOException {
        task.setMainTaskId(incrementTaskCounter());
        taskMap.put(task.getMainTaskId(), task);
        return task;
    }

    @Override
    public Object createEpic(Epic epic) throws IOException {
        epic.setMainTaskId(incrementTaskCounter());
        epicMap.put(epic.getMainTaskId(), epic);
        return epic;
    }

    @Override
    public Object createSubtask(Subtask subtask) throws IOException {
        int id = subtask.getEpicId();

        subtask.setMainTaskId(incrementTaskCounter());
        subtaskMap.put(subtask.getMainTaskId(), subtask);
        Epic epic = epicMap.get(id);
        epic.addSubtaskMap(subtask.getMainTaskId(), subtask);
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
    public HashMap<Integer, Task> deleteAllTasks() throws IOException {
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
        inMemoryHistoryManager.remove(id);
        taskMap.remove(id);
        return taskMap;
    }

    @Override
    public HashMap<Integer, Epic> deleteEpicById(int id) throws IOException {
        for (int i = 0; i < getTaskCounter(); i++) {
            if (epicMap.containsKey(id) && subtaskMap.containsKey(i) && id == subtaskMap.get(i).getEpicId()) {
                subtaskMap.remove(i);
                inMemoryHistoryManager.remove(i);
            }
        }
        inMemoryHistoryManager.remove(id);
        epicMap.remove(id);
        return epicMap;
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
                subtaskMap.remove(id);
            }
        }
        return subtaskMap;
    }

    @Override
    public Task showTaskById(int id) throws IOException {
        Task task = taskMap.get(id);
        inMemoryHistoryManager.addTask(task);
        return task;
    }

    @Override
    public Epic showEpicById(int id) throws IOException {
        Epic epic = epicMap.get(id);
        inMemoryHistoryManager.addTask(epic);
        return epic;
    }

    @Override
    public Subtask showSubtaskById(int id) throws IOException {
        Subtask subtask = subtaskMap.get(id);
        inMemoryHistoryManager.addTask(subtask);
        return subtask;
    }

    @Override
    public HashMap<Integer, Subtask> showSubtasksByEpicId(int id) throws IOException {
        if (subtaskMap.containsKey(id)) {
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
            task = new Task(task.getName(), task.getDescription(), id, status);
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
            int epicId = subtask.getEpicId();
            Epic epic = epicMap.get(epicId);
            subtask = new Subtask(subtask.getName(), subtask.getDescription(), epicId, status, id);
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


}

