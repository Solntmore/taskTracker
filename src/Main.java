import managers.InMemoryHistoryManager;
import managers.InMemoryTaskManager;
import managers.Managers;
import task.*;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager inMemoryTaskManager = Managers.getDefault();
        InMemoryHistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
        HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
        System.out.println("Поехали!");

        Task task = new Task("Задача-1", "описание", 0, Task.Status.NEW);
        inMemoryTaskManager.createTask(task);
        task = new Task("Задача-2", "описание", 0, Task.Status.NEW);
        inMemoryTaskManager.createTask(task);
        Epic epic = new Epic("Эпик-1", "описание", 0, Task.Status.NEW);
        inMemoryTaskManager.createEpic(epic);
        Subtask subtask = new Subtask("Подзадача эпика-1", "описание", 3, Task.Status.NEW, 0);
        inMemoryTaskManager.createSubtask(subtask);
        subtask = new Subtask("Подзадача эпика-2", "описание", 3, Task.Status.NEW, 0);
        inMemoryTaskManager.createSubtask(subtask);

        System.out.println(inMemoryTaskManager.showAllTasks());
        System.out.println(inMemoryTaskManager.showAllEpic());
        System.out.println(inMemoryTaskManager.showAllSubtasks() + "\n");

        System.out.println(inMemoryTaskManager.showTaskById(1));
        System.out.println(inMemoryHistoryManager.getHistory() + "\n");
        System.out.println(inMemoryTaskManager.showEpicById(3));
        System.out.println(inMemoryHistoryManager.getHistory() + "\n");
        System.out.println(inMemoryTaskManager.showSubtaskById(4));
        System.out.println(inMemoryHistoryManager.getHistory() + "\n");

        inMemoryTaskManager.updateTask(1, Task.Status.DONE);
        inMemoryTaskManager.updateSubtask(4, Task.Status.DONE);
        inMemoryTaskManager.updateSubtask(5, Task.Status.DONE);

        System.out.println(inMemoryTaskManager.showAllTasks());
        System.out.println(inMemoryTaskManager.showAllEpic());
        System.out.println(inMemoryTaskManager.showAllSubtasks() + "\n");

        inMemoryTaskManager.deleteTaskById(1);
        inMemoryTaskManager.deleteSubtaskById(4);
        inMemoryTaskManager.deleteEpicById(3);

        System.out.println(inMemoryTaskManager.showAllTasks() + "\n");
        System.out.println(inMemoryTaskManager.showAllEpic() + "\n");
        System.out.println(inMemoryTaskManager.showAllSubtasks() + "\n");

        System.out.println(inMemoryTaskManager.deleteAllTasks());
        System.out.println(inMemoryTaskManager.deleteAllEpics());
        System.out.println(inMemoryTaskManager.deleteAllSubtasks());

        System.out.println(inMemoryTaskManager.showAllTasks() + "\n");
        System.out.println(inMemoryTaskManager.showAllEpic() + "\n");
        System.out.println(inMemoryTaskManager.showAllSubtasks() + "\n");


    }
}
