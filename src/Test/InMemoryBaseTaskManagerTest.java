package test;

import managers.FileBackedTasksManager;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.Duration;


public class InMemoryBaseTaskManagerTest extends test.BaseTaskManagerTest<InMemoryTaskManager> {
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
}
