package managers;

import interfaces.HistoryManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.time.Duration;
import java.time.LocalDateTime;

import static managers.Managers.getDefaultHistory;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {
    HistoryManager taskManager;
    static Task task;
    static Epic epic;
    static Subtask subtask1;
    static Subtask subtask2;
    static Subtask subtask3;
    final static LocalDateTime DAY_1 = LocalDateTime.of(2022, 6, 1, 0, 0);
    final static LocalDateTime DAY_2 = LocalDateTime.of(2022, 6, 2, 0, 0);
    final static LocalDateTime DAY_3 = LocalDateTime.of(2022, 6, 3, 0, 0);
    final static LocalDateTime DAY_4 = LocalDateTime.of(2022, 6, 4, 0, 0);

    @BeforeEach
    void beforeEach() {
        taskManager = getDefaultHistory();
        task = new Task("Задача-1", "описание", 1, Task.Status.NEW,
                DAY_1, Duration.ofMinutes(1439));
        epic = new Epic("Эпик-1", "описание", 2, Task.Status.NEW,
                null, null);
        subtask1 = new Subtask("Подзадача эпика-1", "описание", 2, Task.Status.NEW, 3,
                DAY_2, Duration.ofMinutes(1439));
        subtask2 = new Subtask("Подзадача эпика-2", "описание", 2, Task.Status.IN_PROGRESS, 4,
                DAY_3, Duration.ofMinutes(1439));
        subtask3 = new Subtask("Подзадача эпика-3", "описание", 2, Task.Status.DONE, 5,
                DAY_4, Duration.ofMinutes(1439));

    }

    @Test
    void addTaskWithEmptyHistory() {
        HistoryManager testManager = getDefaultHistory();
        taskManager.addTask(task);
        testManager.addTask(task);
        assertEquals(taskManager.getHistory(), testManager.getHistory(), "История не совпадает.");
    }

    @Test
    void addCopyTask() {
        HistoryManager testManager = getDefaultHistory();
        testManager.addTask(task);
        for (int i = 0; i < 2; i++) {
            taskManager.addTask(task);
        }
        assertEquals(taskManager.getHistory(), testManager.getHistory(), "История не совпадает.");
    }

    //добавлено в соответствии с "можно еще добавить простой тест, где будет проверятся,
    // что в историю добавилось несколько задач, и они все в стории сохранились "
    @Test
    void addThreeTasksInHistory() {
        HistoryManager testManager = getDefaultHistory();
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask1);
        testManager.addTask(task);
        testManager.addTask(epic);
        testManager.addTask(subtask1);
        assertEquals(taskManager.getHistory(), testManager.getHistory(), "История не совпадает.");
    }

    @Test
    void removeFromStartOfHistory() {
        HistoryManager testManager = getDefaultHistory();
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask1);
        taskManager.remove(1);
        testManager.addTask(epic);
        testManager.addTask(subtask1);
        assertEquals(taskManager.getHistory(), testManager.getHistory(), "История не совпадает.");
    }

    @Test
    void removeFromMiddleOfHistory() {
        HistoryManager testManager = getDefaultHistory();
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask1);
        taskManager.remove(2);
        testManager.addTask(task);
        testManager.addTask(subtask1);
        assertEquals(taskManager.getHistory(), testManager.getHistory(), "История не совпадает.");
    }

    @Test
    void removeFromEndOfHistory() {
        HistoryManager testManager = getDefaultHistory();
        taskManager.addTask(task);
        taskManager.addTask(epic);
        taskManager.addTask(subtask1);
        taskManager.remove(3);
        testManager.addTask(task);
        testManager.addTask(epic);
        assertEquals(taskManager.getHistory(), testManager.getHistory(), "История не совпадает.");
    }
}
