package Server;


import managers.FileBackedTasksManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
    private HttpTaskServer server;
    private FileBackedTasksManager fileBackedTasksManager;
    private HttpClient client;
    private static final String jsonTask = "{\"name\":\"Задача-1\",\"description\":\"описание\",\"mainTaskId\":0" +
            ",\"status\":\"NEW\",\"startTime\":\"2022-06-01T00:00:00\",\"duration\":{\"seconds\":86340,\"nanos\":0}}";
    private static final String jsonTask2 = "{\"name\":\"Обновленная задача-1\",\"description\":\"описание\"" +
            ",\"mainTaskId\":0,\"status\"" + ":\"NEW\",\"startTime\":\"2022-06-01T00:00:00\",\"duration\":{\"seconds\"" +
            ":86340,\"nanos\":0}}";

    private static final String jsonEpic = "{\"subtaskMap\":{},\"endTime\":\"2022-08-27T20:04:41.4196358\",\"name\"" +
            ":\"Эпик-1\",\"description\":\"описание\",\"mainTaskId\":1,\"status\":\"NEW\",\"startTime\"" +
            ":\"2022-08-27T20:04:41.4196358\",\"duration\":null}";
    private static final String updatedJsonSubtask1 = "{\"epicId\":1,\"name\":\"Измененная подзадача эпика-1\"" +
            ",\"description\":\"описание\",\"mainTaskId\":0,\"status\":\"NEW\",\"startTime\":\"2022-06-02T00:00:00\"" +
            ",\"duration\":{\"seconds\":86340,\"nanos\":0}}";
    private static final String jsonSubtask2 = "{\"epicId\":1,\"name\":\"Подзадача эпика-2\",\"description\":\"описание\"" +
            ",\"mainTaskId\":0,\"status\":\"IN_PROGRESS\",\"startTime\":\"2022-06-03T00:00:00\",\"duration\"" +
            ":{\"seconds\":86340,\"nanos\":0}}";
    private static final String jsonSubtask3 = "{\"epicId\":1,\"name\":\"Подзадача эпика-3\",\"description\":\"описание\"" +
            ",\"mainTaskId\":0,\"status\":\"DONE\",\"startTime\":\"2022-06-04T00:00:00\",\"duration\":{\"seconds\"" +
            ":86340,\"nanos\":0}}";

    @BeforeEach
    void beforeEach() throws IOException {
        client = HttpClient.newHttpClient();
        fileBackedTasksManager = new FileBackedTasksManager();
        fileBackedTasksManager.deleteAllTasks();
        fileBackedTasksManager.deleteAllEpics();
        fileBackedTasksManager.deleteAllSubtasks();
        server = new HttpTaskServer(fileBackedTasksManager);
        server.start();

    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void handleTasksTest() throws IOException, InterruptedException {
        /*Проверка создания задачи */
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("POST", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка просмотра всех задач */
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка просмотра задачи по id */
        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка удаления всех задач */
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("DELETE", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Повторное создание задачи, для проверки обновления и удаления по id */
        url = URI.create("http://localhost:8080/tasks/task");
        body = HttpRequest.BodyPublishers.ofString(jsonTask);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        /*Повторное создание задачи, для проверки удаления по id */
        url = URI.create("http://localhost:8080/tasks/task/?id=2");
        body = HttpRequest.BodyPublishers.ofString(jsonTask2);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("POST", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка удаления задачи по id */
        url = URI.create("http://localhost:8080/tasks/task/?id=2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("DELETE", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");


    }

    @Test
    void handleTasksAndSubtasksTest() throws IOException, InterruptedException {
        /*Проверка создания подзадачи без эпика */
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        String jsonSubtask1 = "{\"epicId\":1,\"name\":\"Подзадача эпика-1\",\"description\":\"описание\"" +
                ",\"mainTaskId\":0,\"status\":\"NEW\",\"startTime\":\"2022-06-02T00:00:00\",\"duration\":{\"seconds\"" +
                ":86340,\"nanos\":0}}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonSubtask1);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("POST", request.method(), "Неверный метод запроса.");
        assertEquals(400, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка создания эпика */
        url = URI.create("http://localhost:8080/tasks/epic");
        body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("POST", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка создания подзадачи с существующим эпиком */
        url = URI.create("http://localhost:8080/tasks/subtask");
        body = HttpRequest.BodyPublishers.ofString(jsonSubtask1);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("POST", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка просмотра всех эпиков */
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка просмотра всех подзадач */
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка просмотра эпика по id */
        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка просмотра подзадачи по id */
        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка просмотра подзадачи по EpicId */
        url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Создание дополнительных подзадач для дальнейших тестов тестов */
        url = URI.create("http://localhost:8080/tasks/subtask");
        body = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        body = HttpRequest.BodyPublishers.ofString(jsonSubtask3);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        /*Проверка обновления подзадач */
        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        body = HttpRequest.BodyPublishers.ofString(updatedJsonSubtask1);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("POST", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка удаления подзадачи по id */
        url = URI.create("http://localhost:8080/tasks/subtask/?id=2");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("DELETE", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка удаления всех подзадач*/
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("DELETE", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Проверка удаления эпика по id*/
        url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("DELETE", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
        /*Создание эпика для проверки удаления*/
        url = URI.create("http://localhost:8080/tasks/epic");
        body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        /*Проверка удаления всех эпиков*/
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder().uri(url).DELETE().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("DELETE", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");

    }

    /*Создание эпика протестировано в общем методе handleTasksAndSubtasksTest()*/
    @Disabled
    void handleEpicsTest() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("POST", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
    }

    @Test
    void handleHistoryTest() throws IOException, InterruptedException {
        /*Создание задач*/
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonTask);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/epic");
        body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        /*Просмотр задач для добавления в историю*/
        url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=2");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/task/?id=1");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        /*Проверка просмотра истории*/
        url = URI.create("http://localhost:8080/tasks/history");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
    }

    @Test
    void handlePrioritizedTasks() throws IOException, InterruptedException {
        /*Создание задач*/
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(jsonEpic);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/task");
        body = HttpRequest.BodyPublishers.ofString(jsonTask);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        url = URI.create("http://localhost:8080/tasks/subtask");
        body = HttpRequest.BodyPublishers.ofString(jsonSubtask2);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        body = HttpRequest.BodyPublishers.ofString(jsonSubtask3);
        request = HttpRequest.newBuilder().uri(url).POST(body).build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());

        /*Проверка просмотра списка задач по приоритету*/
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder().uri(url).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals("GET", request.method(), "Неверный метод запроса.");
        assertEquals(200, response.statusCode(), "Код обработки запроса неверен.");
    }


}

