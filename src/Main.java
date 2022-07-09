import interfaces.TaskManager;
import managers.Managers;
import task.*;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();


        System.out.println("Поехали!");

        Task task = new Task("Задача-1", "описание", 0, Task.Status.NEW);
        taskManager.createTask(task);
        task = new Task("Задача-2", "описание", 0, Task.Status.NEW);
        taskManager.createTask(task);
        Epic epic = new Epic("Эпик-1", "описание", 0, Task.Status.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача эпика-1", "описание", 3, Task.Status.NEW, 0);
        taskManager.createSubtask(subtask);
        subtask = new Subtask("Подзадача эпика-2", "описание", 3, Task.Status.NEW, 0);
        taskManager.createSubtask(subtask);
        subtask = new Subtask("Подзадача эпика-3", "описание", 3, Task.Status.NEW, 0);
        taskManager.createSubtask(subtask);
        epic = new Epic("Эпик-2", "описание", 0, Task.Status.NEW);
        taskManager.createEpic(epic);

        System.out.println(taskManager.showAllTasks());
        System.out.println(taskManager.showAllEpic());
        System.out.println(taskManager.showAllSubtasks());

        System.out.println("\n" + taskManager.showTaskById(1));
        printHistory();
        System.out.println("\n" + taskManager.showEpicById(3));
        printHistory();
        System.out.println("\n" + taskManager.showSubtaskById(4));
        printHistory();
        System.out.println("\n" + taskManager.showTaskById(1));
        printHistory();
        System.out.println("\n" + taskManager.showEpicById(7));
        printHistory();
        System.out.println("\n" + taskManager.showSubtaskById(4));
        printHistory();
        taskManager.deleteTaskById(1);
        printHistory();
        taskManager.deleteSubtaskById(4);
        printHistory();
        taskManager.deleteEpicById(3);
        printHistory();
        /* Чтобы при проверке не мешали методы, работоспособность которых проверили в прошлых спринтах,
        закомментировал их.
        taskManager.updateTask(1, Task.Status.DONE);
        taskManager.updateSubtask(4, Task.Status.DONE);
        taskManager.updateSubtask(5, Task.Status.DONE);

        System.out.println(taskManager.showAllTasks());
        System.out.println(taskManager.showAllEpic());
        System.out.println(taskManager.showAllSubtasks() + "\n");

        System.out.println(taskManager.showAllTasks() + "\n");
        System.out.println(taskManager.showAllEpic() + "\n");
        System.out.println(taskManager.showAllSubtasks() + "\n");

        System.out.println(taskManager.deleteAllTasks());
        System.out.println(taskManager.deleteAllEpics());
        System.out.println(taskManager.deleteAllSubtasks());

        System.out.println(taskManager.showAllTasks() + "\n");
        System.out.println(taskManager.showAllEpic() + "\n");
        System.out.println(taskManager.showAllSubtasks() + "\n");*/
    }

    private static void printHistory() {
        TaskManager taskManager = Managers.getDefault();
        List<Task> historyList = taskManager.getHistory();
        System.out.println("\n" + "History:");
        for (Task task : historyList) {
            System.out.println(task);
        }
    }

}
