import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
        System.out.println("Поехали!");

        Task task = new Task("Задача-1", "описание", 0, Task.Status.NEW);
        manager.createTask(task);
        task = new Task("Задача-2", "описание", 0, Task.Status.NEW);
        manager.createTask(task);
        Epic epic = new Epic("Эпик-1", "описание", 0, Task.Status.NEW, subtaskMap);
        manager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача эпика-1", "описание", 3, Task.Status.NEW, 0);
        manager.createSubtask(subtask);
        subtask = new Subtask("Подзадача эпика-2", "описание", 3, Task.Status.NEW, 0);
        manager.createSubtask(subtask);

        System.out.println(manager.showAllTasks());
        System.out.println(manager.showAllEpic());
        System.out.println(manager.showAllSubtasks() + "\n");

        System.out.println(manager.showTaskById(1));
        System.out.println(manager.showEpicById(3));
        System.out.println(manager.showSubtaskById(4) + "\n");

        manager.updateTask(1, Task.Status.DONE);
        manager.updateSubtask(4, Task.Status.DONE);
        manager.updateSubtask(5, Task.Status.DONE);


        System.out.println(manager.showAllTasks());
        System.out.println(manager.showAllEpic());
        System.out.println(manager.showAllSubtasks() + "\n");

        manager.deleteTaskById(1);
        manager.deleteSubtaskById(4);
        manager.deleteEpicById(3);

        System.out.println(manager.showAllTasks());
        System.out.println(manager.showAllEpic());
        System.out.println(manager.showAllSubtasks() + "\n");

        System.out.println(manager.deleteAllTasks());
        System.out.println(manager.deleteAllEpics());
        System.out.println(manager.deleteAllSubtasks());

        System.out.println(manager.showAllTasks());
        System.out.println(manager.showAllEpic());
        System.out.println(manager.showAllSubtasks() + "\n");
    }
}
