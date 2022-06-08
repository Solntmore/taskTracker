import java.util.HashMap;
import java.util.Objects;

public class Manager {
    private HashMap<Integer, Task> taskMap = new HashMap<>();
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    private HashMap<Integer, Epic> epicMap = new HashMap<>();
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
        Manager manager = (Manager) o;
        return taskCounter == manager.taskCounter && Objects.equals(taskMap, manager.taskMap) &&
                Objects.equals(subtaskMap, manager.subtaskMap) &&
                Objects.equals(epicMap, manager.epicMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskMap, subtaskMap, epicMap, taskCounter);
    }

    public Object createTask(Task task) {
        task.setMainTaskId(incrementTaskCounter());
        taskMap.put(task.getMainTaskId(), task);
        return task;
    }

    public Object createEpic(Epic epic) {
        epic.setMainTaskId(incrementTaskCounter());
        epicMap.put(epic.getMainTaskId(), epic);
        return epic;
    }

    public Object createSubtask(Subtask subtask) {
        int id = subtask.getEpicId();

        subtask.setMainTaskId(incrementTaskCounter());
        subtaskMap.put(subtask.getMainTaskId(), subtask);
        Epic epic = epicMap.get(id);
        epic.subtaskMap.put(subtask.getMainTaskId(), subtask);
        epicMap.put(id, epic);
        return subtask;
    }


    public HashMap<Integer, Task> showAllTasks() {
        return taskMap;
    }

    public HashMap<Integer, Epic> showAllEpic() {
        return epicMap;
    }

    public HashMap<Integer, Subtask> showAllSubtasks() {
        return subtaskMap;
    }

    public HashMap<Integer, Task> deleteAllTasks() {
        taskMap.clear();
        return taskMap;
    }

    public HashMap<Integer, Epic> deleteAllEpics() {
        epicMap.clear();
        subtaskMap.clear();
        return epicMap;

    }

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

    public HashMap<Integer, Task> deleteTaskById(int id) {
        taskMap.remove(id);
        return taskMap;
    }

    public HashMap<Integer, Epic> deleteEpicById(int id) {
        for (int i = 0; i < getTaskCounter(); i++) {
            if (epicMap.containsKey(id) && subtaskMap.containsKey(i) && id == subtaskMap.get(i).getEpicId()) {
                subtaskMap.remove(i);
            }
        }
        epicMap.remove(id);
        return epicMap;
    }

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

    public Task showTaskById(int id) {
        Task task = taskMap.get(id);
        return task;
    }

    public Epic showEpicById(int id) {
        Epic epic = epicMap.get(id);
        return epic;
    }

    public Subtask showSubtaskById(int id) {
        Subtask subtask = subtaskMap.get(id);
        return subtask;
    }

    public HashMap<Integer, Subtask> showSubtasksByEpicId(int id) {
        if (subtaskMap.containsKey(id)) {
            Epic epic = epicMap.get(id);
            return epic.subtaskMap;
        } else {
            return null;
        }
    }

    public Task updateTask(int id, Task.Status status) {
        Task task = taskMap.get(id);
        task = new Task(task.getName(), task.getDescription(), id, status);
        taskMap.put(id, task);
        return task;
    }

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

    private Epic updateEpic(int epicId) {
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

    private int getTaskCounter() {
        return taskCounter;
    }

    private int incrementTaskCounter() {
        return ++taskCounter;
    }

    private void setTaskCounter(int counter) {
        this.taskCounter = counter;
    }


}

