import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Manager {
    HashMap<Integer, Task> taskManager = new HashMap<>();
    HashMap<Integer, Subtask> subtaskManager = new HashMap<>();
    HashMap<Integer, Epic> epicManager = new HashMap<>();
   /* Hash<Task> taskList;*/
    ArrayList<Subtask> subtaskList;
   HashMap<Integer, Subtask> subtaskWithEpicId;


    private static final String STATUS_NEW = "NEW";
    private static final String STATUS_IN_PROGRESS = "IN_PROGRESS";
    private static final String STATUS_DONE = "DONE";
    private int taskCounter = 0;
    private int subtaskCounter = 0;

    public int getTaskCounter() {
        return taskCounter;
    }

    public void setTaskCounter(int counter) {
        this.taskCounter = counter;
    }

    public int getSubtaskCounter() {
        return subtaskCounter;
    }

    public void setSubtaskCounter(int subtaskCounter) {
        this.subtaskCounter = subtaskCounter;
    }

    // использую public, так как вызываю этот метод для проверки работоспособности в классе main.
    Object createTask(String name, String description) {
        Object newTask;
        int number = getTaskCounter();
        setTaskCounter(number + 1);
        int newNumber = getTaskCounter();
        Task task = new Task(name, description, newNumber, STATUS_NEW);
        taskManager.put(newNumber, task);
        newTask = task;
        return newTask;
    }

    Object createEpic(String name, String description) {
        Object newEpic;
        int number = getTaskCounter();
        setTaskCounter(number + 1);
        int newNumber = getTaskCounter();
        Epic epic = new Epic(name, description, newNumber, STATUS_NEW);
        epicManager.put(newNumber, epic);
        newEpic = epic;
        return newEpic;
    }

    Object createSubtask(String subTaskName, String subTaskDescription) {
        Object newSubtask;
        int number = getTaskCounter();
        int secondNumber = getSubtaskCounter();
        setSubtaskCounter(secondNumber + 1);
        secondNumber = getSubtaskCounter();
        Subtask subtask = new Subtask(subTaskName, subTaskDescription, number, STATUS_NEW, secondNumber);
        subtaskManager.put(secondNumber, subtask);
        newSubtask = subtask;
        return newSubtask;
    }


    public HashMap<Integer, Task> showAllTasks() {
        return taskManager;
    }

    public HashMap<Integer, Epic> showAllEpic() {
        return epicManager;
    }

    public HashMap<Integer, Subtask> showAllSubtasks() {
        return subtaskManager;
    }

    public HashMap<Integer, Task> deleteAllTasks() {
        taskManager.clear();
        return taskManager;
    }

    public HashMap<Integer, Epic> deleteAllEpics(){
        epicManager.clear();
        return epicManager;

    }

    public HashMap<Integer, Subtask> deleteAllSubtasks() {
        subtaskManager.clear();
        return subtaskManager;
    }

    public HashMap<Integer, Task> deleteTaskById(String id) {
        try {
            int taskId = Integer.parseInt(id);
            taskManager.remove(taskId);

        } catch (NullPointerException e) {
            System.out.println("Задачи с таким ID нет, попробуйте еще раз.");
        }
        return taskManager;
    }

    public HashMap<Integer, Epic> deleteEpicById(String id) {
        try {
            int epicId = Integer.parseInt(id);
            epicManager.remove(epicId);
            for (int i = 1; i <= getSubtaskCounter(); i++) {
                if (epicId == subtaskManager.get(i).mainTaskId) {
                    subtaskManager.remove(i);
                }
            }
        } catch(NullPointerException e) {
                System.out.println("Задачи с таким ID нет, попробуйте еще раз.");
            }
        return epicManager;
        }

    public HashMap<Integer, Subtask> deleteSubtaskById(String id)  {
        int subtaskId = Integer.parseInt(id);
        for (int i = 1; i <= getSubtaskCounter(); i++) {
            if (subtaskId == subtaskManager.get(i).mainTaskId) {
                subtaskManager.remove(i);
            }
        }
        return subtaskManager;
    }

    Object showTaskById(String userInput) {
        Object task;
        int taskId = Integer.parseInt(userInput);
        task = taskManager.get(taskId);
        return task;
    }

    Object showEpicById(String userInput) {
        Object epic;
        int epicId = Integer.parseInt(userInput);
        epic = epicManager.get(epicId);
        return epic;
    }

    Object showSubtaskById(String userInput) {
        Object subtask;
        int subtaskId = Integer.parseInt(userInput);
        subtask = subtaskManager.get(subtaskId);
        return subtask;
    }



    public HashMap<Integer, Subtask> showSubtasksByEpicId(String userInput) {
        subtaskWithEpicId = new HashMap<>();
        int taskId = Integer.parseInt(userInput);
        for (int i = 1; i <= getSubtaskCounter() + getTaskCounter(); i++) {
                    if (subtaskManager.containsKey(i) && taskId == subtaskManager.get(i).mainTaskId) {
                        subtaskWithEpicId.put(i, subtaskManager.get(i));

                }
            }
        return subtaskWithEpicId;
        }

    Object updateTask(String userInput, String status) {
        Object updatedTask;
        int taskId = Integer.parseInt(userInput);
        Task task;
        if (status.equals("1")) {
            task = new Task(taskManager.get(taskId).name, taskManager.get(taskId).description,
                    taskManager.get(taskId).mainTaskId, STATUS_DONE);
        } else {
            task = new Task(taskManager.get(taskId).name, taskManager.get(taskId).description,
                    taskManager.get(taskId).mainTaskId, STATUS_IN_PROGRESS);
        }
        taskManager.put(taskManager.get(taskId).mainTaskId, task);
        updatedTask = taskManager.get(taskId);
        return updatedTask;
    }

    Object updateSubtask(String userInput, String status) {
        Object updatedSubtask;
        int subtaskId = Integer.parseInt(userInput);
        int mainTaskId = subtaskManager.get(subtaskId).mainTaskId;
        Subtask subtask;
        if (status.equals("1")) {
            subtask = new Subtask(subtaskManager.get(subtaskId).name, subtaskManager.get(subtaskId).description,
                    subtaskManager.get(subtaskId).mainTaskId, STATUS_DONE, subtaskManager.get(subtaskId).subtaskID);
        } else {
            subtask = new Subtask(subtaskManager.get(subtaskId).name, subtaskManager.get(subtaskId).description,
                    subtaskManager.get(subtaskId).mainTaskId, STATUS_IN_PROGRESS, subtaskManager.get(subtaskId).subtaskID);
        }
        subtaskManager.put(subtaskManager.get(subtaskId).subtaskID, subtask);
        updatedSubtask = subtaskManager.get(subtaskId);
        updateEpic(mainTaskId);
        return updatedSubtask;
    }

    private Object updateEpic(int mainTaskId){
        Object updatedEpic;
        subtaskList = new ArrayList<>();
        int newStatus = 0;
        int inProgress = 0;
        int done = 0;

        for (int i = 1; i <= getSubtaskCounter() + getTaskCounter(); i++) {
            if (subtaskManager.containsKey(i) && mainTaskId == subtaskManager.get(i).mainTaskId) {
                subtaskList.add(subtaskManager.get(i));
            }
            for (Subtask subtask : subtaskList) {
                String status = subtask.status;
                if (status.equals(STATUS_NEW)) {
                    newStatus += 1;
                } else if (status.equals(STATUS_IN_PROGRESS)) {
                    inProgress += 1;
                } else {
                    done += 1;
                }
            }
        }
        if(inProgress == 0 && newStatus == 0 && done > 0) {
            Epic epic = new Epic(epicManager.get(mainTaskId).name, epicManager.get(mainTaskId).description,
                    epicManager.get(mainTaskId).mainTaskId, STATUS_DONE);
            epicManager.put(epicManager.get(mainTaskId).mainTaskId, epic);
        } else if(inProgress > 0) {
            Epic epic = new Epic(epicManager.get(mainTaskId).name, epicManager.get(mainTaskId).description,
                    epicManager.get(mainTaskId).mainTaskId, STATUS_IN_PROGRESS);
            epicManager.put(epicManager.get(mainTaskId).mainTaskId, epic);
        } else {
            Epic epic = new Epic(epicManager.get(mainTaskId).name, epicManager.get(mainTaskId).description,
                    epicManager.get(mainTaskId).mainTaskId, STATUS_NEW);
            epicManager.put(epicManager.get(mainTaskId).mainTaskId, epic);
        }
        updatedEpic = taskManager.get(mainTaskId);
        return updatedEpic;
    }

        @Override
        public String toString () {
            return "Manager{" +
                    "taskManager=" + taskManager +
                    ", SubtaskManager=" + subtaskManager +
                    ", EpicManager=" + epicManager +
                    '}';
        }

        @Override
        public boolean equals (Object o){
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Manager manager = (Manager) o;
            return taskCounter == manager.taskCounter && Objects.equals(taskManager, manager.taskManager) &&
                    Objects.equals(subtaskManager, manager.subtaskManager) &&
                    Objects.equals(epicManager, manager.epicManager);
        }

        @Override
        public int hashCode () {
            return Objects.hash(taskManager, subtaskManager, epicManager, taskCounter);
        }

}

