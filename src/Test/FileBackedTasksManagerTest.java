package Test;

import managers.FileBackedTasksManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = new FileBackedTasksManager("taskManager.csv");
        task = new Task("Задача-1", "описание", 0, Task.Status.NEW, DAY_1, Duration.ofMinutes(1439));
        epic = new Epic("Эпик-1", "описание", 0, Task.Status.NEW, null, null);
        subtask1 = new Subtask("Подзадача эпика-1", "описание", 1, Task.Status.NEW, 0,
                DAY_2, Duration.ofMinutes(1439));
        subtask2 = new Subtask("Подзадача эпика-2", "описание", 1, Task.Status.IN_PROGRESS, 0,
                DAY_3, Duration.ofMinutes(1439));
        subtask3 = new Subtask("Подзадача эпика-3", "описание", 1, Task.Status.DONE, 0,
                DAY_4, Duration.ofMinutes(1439));

    }

    @Test
    void testWithEmptyTaskList() throws IOException {
        //очищаю историю всех задач от прошлых записей, так как при создании нового экземпляра файл не перезаписывается
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();
        FileBackedTasksManager loadTaskManager = taskManager.loadFromFile("taskManager.csv");
        assertEquals(taskManager.showAllTasks(), loadTaskManager.showAllTasks());
        assertEquals(taskManager.showAllSubtasks(), loadTaskManager.showAllSubtasks());
        assertEquals(taskManager.showAllEpics(), loadTaskManager.showAllEpics());
    }

    @Test
    void testWithEpicWithoutSubtasks() throws IOException {
        taskManager.createEpic(epic);
        FileBackedTasksManager loadTaskManager = taskManager.loadFromFile("taskManager.csv");
        assertEquals(taskManager.showAllEpics(), loadTaskManager.showAllEpics());
    }

    @Test
    void testWithEmptyHistoryList() throws IOException {
        List<Task> newHistoryList = new ArrayList<>();
        assertEquals(newHistoryList, taskManager.getHistory(), "Списки не равны");
    }

}

