import interfaces.HistoryManager;
import interfaces.TaskManager;
import managers.Managers;
import task.*;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();
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

        System.out.println(taskManager.showAllTasks());
        System.out.println(taskManager.showAllEpic());
        System.out.println(taskManager.showAllSubtasks() + "\n");

        System.out.println(taskManager.showTaskById(1));
        System.out.println(historyManager.getHistory() + "\n");
        System.out.println(taskManager.showEpicById(3));
        System.out.println(historyManager.getHistory() + "\n");
        System.out.println(taskManager.showSubtaskById(4));
        System.out.println(historyManager.getHistory() + "\n");

        taskManager.updateTask(1, Task.Status.DONE);
        taskManager.updateSubtask(4, Task.Status.DONE);
        taskManager.updateSubtask(5, Task.Status.DONE);

        System.out.println(taskManager.showAllTasks());
        System.out.println(taskManager.showAllEpic());
        System.out.println(taskManager.showAllSubtasks() + "\n");

        taskManager.deleteTaskById(1);
        taskManager.deleteSubtaskById(4);
        taskManager.deleteEpicById(3);

        System.out.println(taskManager.showAllTasks() + "\n");
        System.out.println(taskManager.showAllEpic() + "\n");
        System.out.println(taskManager.showAllSubtasks() + "\n");

        System.out.println(taskManager.deleteAllTasks());
        System.out.println(taskManager.deleteAllEpics());
        System.out.println(taskManager.deleteAllSubtasks());

        System.out.println(taskManager.showAllTasks() + "\n");
        System.out.println(taskManager.showAllEpic() + "\n");
        System.out.println(taskManager.showAllSubtasks() + "\n");


    }
}
