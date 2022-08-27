package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import server.KVServer;
import task.Epic;
import task.Subtask;
import task.Task;
import helpTools.Utils;

import java.io.IOException;
import java.time.Duration;
import java.util.*;

import static helpTools.Сonstants.KV_SERVER_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HTTPTaskManagerTest extends BaseTaskManagerTest<HTTPTaskManager> {

    private KVServer server;

    @BeforeEach
    void beforeEach() throws IOException {
        server = Utils.getKVServer();
        taskManager = Managers.getDefaultHttpTaskManager("http://localhost:8078");
        task = new Task("Задача-1", "описание", 0, Task.Status.NEW, DAY_1, Duration.ofMinutes(1439));
        epic = new Epic("Эпик-1", "описание", 0, Task.Status.NEW, null, null);
        subtask1 = new Subtask("Подзадача эпика-1", "описание", 1, Task.Status.NEW, 0,
                DAY_2, Duration.ofMinutes(1439));
        subtask2 = new Subtask("Подзадача эпика-2", "описание", 1, Task.Status.IN_PROGRESS, 0,
                DAY_3, Duration.ofMinutes(1439));
        subtask3 = new Subtask("Подзадача эпика-3", "описание", 1, Task.Status.DONE, 0,
                DAY_4, Duration.ofMinutes(1439));

    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    /*нельзя протестировать метод save без метода recoveryFromFileTest или наоборот,
    поэтому тестируется в рамках одного теста*/
    @Disabled
    void saveTest() {

    }

    @Test
    void recoveryFromFileTest() throws IOException {
        HashMap<Integer, Task> testTaskMap = new HashMap<>();
        HashMap<Integer, Epic> testEpicMap = new HashMap<>();
        HashMap<Integer, Subtask> testSubtaskMap = new HashMap<>();
        List<Task> historyList = new ArrayList<>();
        testEpicMap.put(1, (Epic) taskManager.createEpic(epic));
        testTaskMap.put(2, (Task) taskManager.createTask(task));
        testSubtaskMap.put(3, (Subtask) taskManager.createSubtask(subtask1));
        testSubtaskMap.put(4, (Subtask) taskManager.createSubtask(subtask2));
        testSubtaskMap.put(5, (Subtask) taskManager.createSubtask(subtask3));
        historyList.add(taskManager.showTaskById(2));
        historyList.add(taskManager.showEpicById(1));
        historyList.add(taskManager.showSubtaskById(3));

        HTTPTaskManager newHTTPTaskManager = new HTTPTaskManager(KV_SERVER_URL);
        newHTTPTaskManager.recoveryFromFile();
        assertEquals(testTaskMap, newHTTPTaskManager.showAllTasks(), "Списки не равны");
        assertEquals(testEpicMap, newHTTPTaskManager.showAllEpics(), "Списки не равны");
        assertEquals(testSubtaskMap, newHTTPTaskManager.showAllSubtasks(), "Списки не равны");
        assertEquals(historyList, newHTTPTaskManager.getHistory(), "Списки не равны");
    }
}
