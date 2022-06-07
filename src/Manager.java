import java.util.HashMap;
import java.util.Objects;

public class Manager {
    /*мы вообще все приваты опускаем вниз? логично хешмеп оставлять наверху. Наставник сказал, что эти требования
     * вообще зависит от команды у всех свои правила */
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

    /*При удалении всех эпиков будет логично очистить и мапу сабтасков */
    public HashMap<Integer, Epic> deleteAllEpics() {
        epicMap.clear();
        subtaskMap.clear();
        return epicMap;

    }

    public HashMap<Integer, Subtask> deleteAllSubtasks() {
        subtaskMap.clear();
        for (int i = 0; i < epicMap.size(); i++) {
            if (epicMapContainsId(i)) {
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
            if (epicMapContainsId(id) && subtaskMapContainsId(i) && id == subtaskMap.get(i).getEpicId()) {
                subtaskMap.remove(i);
            }
        }
        epicMap.remove(id);
        return epicMap;
    }

    /* Если вызвать на пустую мапу, будет NullExpointerException, поэтому сделал защиту */
    public HashMap<Integer, Subtask> deleteSubtaskById(int id) {
        if (subtaskMapContainsId(id)) {
            Subtask subtask = subtaskMap.get(id);
            if (epicMapContainsId(subtask.getEpicId())) {
                Epic epic = epicMap.get(subtask.getEpicId());
                epic.subtaskMap.remove(id);
                epicMap.put(epic.getMainTaskId(), epic);
                subtaskMap.remove(id);
            }
        }
        return subtaskMap;
    }

    Object showTaskById(int id) {
        Object task;
        task = taskMap.get(id);
        return task;
    }

    Object showEpicById(int id) {
        Object epic;
        epic = epicMap.get(id);
        return epic;
    }

    Object showSubtaskById(int id) {
        Object subtask;
        subtask = subtaskMap.get(id);
        return subtask;
    }

    public HashMap<Integer, Subtask> showSubtasksByEpicId(int id) {
        if (subtaskMapContainsId(id)) {
            Epic epic = epicMap.get(id);
            return epic.subtaskMap;
        } else {
            return null;
        }
    }

    Object updateTask(int id, String status) {
        Object updatedTask;
        Task task = taskMap.get(id);
        if (status.equals("1")) {
            task = new Task(task.getName(), task.getDescription(),
                    task.getMainTaskId(), Task.Status.DONE);
        } else {
            task = new Task(task.getName(), task.getDescription(),
                    task.getMainTaskId(), Task.Status.IN_PROGRESS);
        }
        taskMap.put(task.getMainTaskId(), task);
        updatedTask = task;
        return updatedTask;
    }

    Object updateSubtask(int id, String status) {
        Subtask subtask = subtaskMap.get(id);
        int epicId = subtask.getEpicId();
        Epic epic = epicMap.get(epicId);

        if (status.equals("1")) {
            subtask = new Subtask(subtask.getName(), subtask.getDescription(),
                    epicId, Task.Status.DONE, id);
        } else {
            subtask = new Subtask(subtask.getName(), subtask.getDescription(),
                    epicId, Task.Status.IN_PROGRESS, id);
        }
        subtaskMap.put(id, subtask);
        epic.subtaskMap.put(id, subtask);
        updateEpic(epicId);
        return subtask;
    }

    boolean taskMapContainsId(int id) {
        return taskMap.containsKey(id);
    }

    boolean epicMapContainsId(int id) {
        return epicMap.containsKey(id);
    }

    boolean subtaskMapContainsId(int id) {
        return subtaskMap.containsKey(id);
    }

    private Object updateEpic(int epicId) {
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
            epic = new Epic(epicMap.get(epicId).getName(), epicMap.get(epicId).getDescription(),
                    epicMap.get(epicId).getMainTaskId(), Task.Status.DONE, epic.subtaskMap);
            epicMap.put(epicId, epic);
        } else if (inProgress > 0 || done > 0 && newStatus > 0) {
            epic = new Epic(epicMap.get(epicId).getName(), epicMap.get(epicId).getDescription(),
                    epicMap.get(epicId).getMainTaskId(), Task.Status.IN_PROGRESS, epic.subtaskMap);
            epicMap.put(epicId, epic);
        } else {
            epic = new Epic(epicMap.get(epicId).getName(), epicMap.get(epicId).getDescription(),
                    epicMap.get(epicId).getMainTaskId(), Task.Status.NEW, epic.subtaskMap);
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

