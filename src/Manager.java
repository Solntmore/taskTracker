import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Manager {
    HashMap<Integer, Task> taskMap = new HashMap<>();
    HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
    HashMap<Integer, Epic> epicMap = new HashMap<>();
    ArrayList<Subtask> subtaskList;
    HashMap<Integer, Subtask> subtaskWithEpicId;

    /* Не сделал инкрементацию в методе getCounter, так как в некоторых методах я просто обращаюсь к значению
     taskCounter без цели дальнейшего его изменения(например, при удаления Эпика, чтобы удалились все его подзадачи
      и при выдаче всех подзадач эпика */
    private int taskCounter = 0;

    public enum Status {
        NEW, IN_PROGRESS, DONE
    }

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

    Object createTask(Task task) {
        Object newTask;
        setTaskCounter(getTaskCounter() + 1);
        task.setMainTaskId(getTaskCounter());
        taskMap.put(task.mainTaskId, task);
        newTask = task;
        return newTask;
    }

    Object createEpic(Epic epic) {
        Object newEpic;
        setTaskCounter(getTaskCounter() + 1);
        epic.setMainTaskId(getTaskCounter());
        epicMap.put(epic.mainTaskId, epic);
        newEpic = epic;
        return newEpic;
    }

    Object createSubtask(Subtask subtask) {
        Object newSubtask;
        setTaskCounter(getTaskCounter() + 1);
        subtask.setSubtaskId(getTaskCounter());
        subtaskMap.put(subtask.getSubtaskId(), subtask);
        newSubtask = subtask;
        return newSubtask;
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
        return epicMap;

    }

    public HashMap<Integer, Subtask> deleteAllSubtasks() {
        subtaskMap.clear();
        return subtaskMap;
    }

    public HashMap<Integer, Task> deleteTaskById(int id) {
        taskMap.remove(id);
        return taskMap;
    }

    public HashMap<Integer, Epic> deleteEpicById(int id) {
        epicMap.remove(id);
        for (int i = 1; i <= getTaskCounter(); i++) {
            if (id == subtaskMap.get(i).getMainTaskId()) {
                subtaskMap.remove(i);
            }
        }
        return epicMap;
    }

    public HashMap<Integer, Subtask> deleteSubtaskById(int id) {
        subtaskMap.remove(id);
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
        subtaskWithEpicId = new HashMap<>();
        for (int i = 1; i <= getTaskCounter() + getTaskCounter(); i++) {
            if (subtaskMap.containsKey(i) && id == subtaskMap.get(i).getMainTaskId()) {
                subtaskWithEpicId.put(i, subtaskMap.get(i));

            }
        }
        return subtaskWithEpicId;
    }

    Object updateTask(int id, String status) {
        Object updatedTask;
        Task task;
        if (status.equals("1")) {
            task = new Task(taskMap.get(id).getName(), taskMap.get(id).getDescription(),
                    taskMap.get(id).getMainTaskId(), Status.DONE);
        } else {
            task = new Task(taskMap.get(id).getName(), taskMap.get(id).getDescription(),
                    taskMap.get(id).getMainTaskId(), Status.IN_PROGRESS);
        }
        taskMap.put(taskMap.get(id).getMainTaskId(), task);
        updatedTask = taskMap.get(id);
        return updatedTask;
    }

    Object updateSubtask(int id, String status) {
        Object updatedSubtask;
        int mainTaskId = subtaskMap.get(id).getMainTaskId();
        Subtask subtask;
        if (status.equals("1")) {
            subtask = new Subtask(subtaskMap.get(id).getName(), subtaskMap.get(id).getDescription(),
                    subtaskMap.get(id).getMainTaskId(), Status.DONE, subtaskMap.get(id).getSubtaskId());
        } else {
            subtask = new Subtask(subtaskMap.get(id).getName(), subtaskMap.get(id).getDescription(),
                    subtaskMap.get(id).getMainTaskId(), Status.IN_PROGRESS,
                    subtaskMap.get(id).getSubtaskId());
        }
        subtaskMap.put(subtaskMap.get(id).getSubtaskId(), subtask);
        updatedSubtask = subtaskMap.get(id);
        updateEpic(mainTaskId);
        return updatedSubtask;
    }

    /*не закрываю к методам проверки наличия доступ, так как мне кажется логично вызывать их первыми при вводе id во
    фронт-части, и в случает значения true вызывать метод удаления, а в случае false не вызывать*/

    boolean taskMapContainsId(int id) {
        return taskMap.containsKey(id);
    }

    boolean epicMapContainsId(int id) {
        return epicMap.containsKey(id);
    }

    boolean subtaskMapContainsId(int id) {
        return subtaskMap.containsKey(id);
    }

    private Object updateEpic(int mainTaskId) {
        Object updatedEpic;
        subtaskList = new ArrayList<>();
        int newStatus = 0;
        int inProgress = 0;
        int done = 0;

        for (int i = 1; i <= getTaskCounter(); i++) {
            if (subtaskMap.containsKey(i) && mainTaskId == subtaskMap.get(i).getMainTaskId()) {
                subtaskList.add(subtaskMap.get(i));
            }
            for (Subtask subtask : subtaskList) {
                Status status = subtask.getStatus();
                if (status.equals(Status.NEW)) {
                    newStatus += 1;
                } else if (status.equals(Status.IN_PROGRESS)) {
                    inProgress += 1;
                } else {
                    done += 1;
                }
            }
        }
        if (inProgress == 0 && newStatus == 0 && done > 0) {
            Epic epic = new Epic(epicMap.get(mainTaskId).getName(), epicMap.get(mainTaskId).getDescription(),
                    epicMap.get(mainTaskId).getMainTaskId(), Status.DONE);
            epicMap.put(epicMap.get(mainTaskId).getMainTaskId(), epic);
        } else if (inProgress > 0) {
            Epic epic = new Epic(epicMap.get(mainTaskId).getName(), epicMap.get(mainTaskId).getDescription(),
                    epicMap.get(mainTaskId).getMainTaskId(), Status.IN_PROGRESS);
            epicMap.put(epicMap.get(mainTaskId).getMainTaskId(), epic);
        } else {
            Epic epic = new Epic(epicMap.get(mainTaskId).getName(), epicMap.get(mainTaskId).getDescription(),
                    epicMap.get(mainTaskId).getMainTaskId(), Status.NEW);
            epicMap.put(epicMap.get(mainTaskId).getMainTaskId(), epic);
        }
        updatedEpic = taskMap.get(mainTaskId);
        return updatedEpic;
    }

    private int getTaskCounter() {
        return taskCounter;
    }

    private void setTaskCounter(int counter) {
        this.taskCounter = counter;
    }


}

