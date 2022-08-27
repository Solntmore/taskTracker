package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.FileBackedTasksManager;
import task.Epic;
import task.Subtask;
import task.Task;
import helpTools.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.regex.Pattern;

import static helpTools.Сonstants.PORT8080;
import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    private final HttpServer server;
    private final Gson gson;
    FileBackedTasksManager fileBackedTasksManager;

    /**
     * Команды, которые доступны в HttpTaskServer
     * createTask(Task task); - создание задачи - POST http://localhost:8080/tasks/task (body)
     * createEpic(Epic epic);  - создание эпика - POST http://localhost:8080/tasks/epic (body)
     * createSubtask(Subtask subtask);  - создание подзадачи - POST http://localhost:8080/tasks/subtask (body)
     * showAllTasks(); - показать все задачи - GET http://localhost:8080/tasks/task
     * showAllEpics(); - показать все эпики - GET http://localhost:8080/tasks/epic
     * showAllSubtasks();  - показать все подзадачи - GET http://localhost:8080/tasks/subtask
     * deleteAllTasks(); - удалить все задачи - DELETE http://localhost:8080/tasks/task
     * deleteAllEpics(); - удалить все эпики - DELETE http://localhost:8080/tasks/epic
     * deleteAllSubtasks();  - удалить все подзадачи - DELETE http://localhost:8080/tasks/subtask
     * deleteTaskById(int id); - удалить задачу по ID - DELETE http://localhost:8080/tasks/task/?id=1
     * deleteEpicById(int id); - удалить эпик по ID - DELETE http://localhost:8080/tasks/epic/?id=1
     * deleteSubtaskById(int id); - удалить подзадачу по ID - DELETE http://localhost:8080/tasks/subtask/?id=1
     * showTaskById(int id); - показать задачу по ID - GET http://localhost:8080/tasks/task/?id=1
     * showEpicById(int id); - показать эпик по ID - GET http://localhost:8080/tasks/epic/?id=1
     * showSubtaskById(int id); - показать подзадачу по ID - GET http://localhost:8080/tasks/subtask/?id=1
     * showSubtasksByEpicId(int id); - показать все подзадачи по EPIC ID - GET http://localhost:8080/tasks/subtask/epic/?id=1
     * updateTask(int id, Task task); - изменение задачи - POST http://localhost:8080/tasks/task/?id=1 (body)
     * updateSubtask(int id, Subtask subtask); - изменение подзадачи - POST http://localhost:8080/tasks/subtask/?id=1 (body)
     * getHistory(); - показать историю просмотров - GET http://localhost:8080/tasks/history
     * getPrioritizedSet(); - показать задачи по приоритету - GET http://localhost:8080/tasks
     */

    public HttpTaskServer(FileBackedTasksManager fileBackedTasksManager) throws IOException {
        this.fileBackedTasksManager = fileBackedTasksManager;
        gson = Utils.getGson();
        server = HttpServer.create(new InetSocketAddress("localhost", PORT8080), 0);
        server.createContext("/tasks/task", this::handleTasks);
        server.createContext("/tasks/epic", this::handleEpics);
        server.createContext("/tasks/subtask", this::handleSubtasks);
        server.createContext("/tasks/history", this::handleHistory);
        server.createContext("/tasks", this::handlePrioritizedTasks);
    }

    public static void main(String[] args) {
        HttpTaskServer httpUserServer;
        try {
            httpUserServer = new HttpTaskServer(new FileBackedTasksManager("taskManager.csv"));
        } catch (IOException e) {
            System.out.println("Ошибка" + Arrays.toString(e.getStackTrace()) + e.getMessage());
            throw new RuntimeException(e);
        }
        httpUserServer.start();
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {

        try {
            System.out.println("\nhttp://localhost:8080" + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes());
            String query = httpExchange.getRequestURI().getQuery();
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        final String response = gson.toJson(fileBackedTasksManager.showAllTasks());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllTasks().containsKey(id)) {
                            final String response = gson.toJson(fileBackedTasksManager.showTaskById(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        fileBackedTasksManager.deleteAllTasks();
                        String response = "Список эпиков очищен";
                        sendText(httpExchange, response);
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllTasks().containsKey(id)) {
                            fileBackedTasksManager.deleteTaskById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                            String jsonString = "Удалили задачу с идентификатором -" + id;
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                    }
                    break;
                case "POST":
                    try {
                        if (query.contains("id")) {
                            String[] split = query.split("=");
                            Task task = gson.fromJson(body, Task.class);
                            if (fileBackedTasksManager.showAllTasks().containsKey(Integer.parseInt(split[1]))) {
                                fileBackedTasksManager.updateTask(Integer.parseInt(split[1]), task);
                                String jsonString = "Задача изменена!";
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            } else {
                                httpExchange.sendResponseHeaders(404, 0);
                                String jsonString = "";
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        if (!body.isEmpty()) {
                            Task task = gson.fromJson(body, Task.class);
                            String jsonString = gson.toJson(fileBackedTasksManager.createTask(task));
                            if (!jsonString.equals("null")) {
                                jsonString = "Задача создана!";
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            } else {
                                httpExchange.sendResponseHeaders(400, 0);
                                jsonString = "";
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            }
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                    }
                    break;
                default:
                    httpExchange.sendResponseHeaders(405, 0);
                    String jsonString = "/ ждем GET-запрос, POST-запрос или DELETE-запрос, а получили - " + requestMethod;
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(jsonString.getBytes());
                    }
                    break;
            }
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(405, 0);
            String jsonString = "";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(jsonString.getBytes());
            }
        } finally {
            httpExchange.close();
        }
    }

    private void handleSubtasks(HttpExchange httpExchange) throws IOException {

        try {
            System.out.println("\nhttp://localhost:8080" + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            System.out.println(path);
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes());
            String query = httpExchange.getRequestURI().getQuery();
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        final String response = gson.toJson(fileBackedTasksManager.showAllSubtasks());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/$", path)) {

                        System.out.println("Сработало");
                        if (query.contains("id")) {
                            String[] split = query.split("=");
                            int id = Integer.parseInt(split[1]);
                            if (fileBackedTasksManager.showAllEpics().containsKey(id)) {
                                final String response = gson.toJson(fileBackedTasksManager.showSubtasksByEpicId(id));
                                sendText(httpExchange, response);
                                return;
                            } else {
                                System.out.println("Нет эпика с идентификатором -" + id);
                                httpExchange.sendResponseHeaders(404, 0);
                                String jsonString = "";
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            }
                            break;
                        }
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllSubtasks().containsKey(id)) {
                            final String response = gson.toJson(fileBackedTasksManager.showSubtaskById(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                        break;
                    }
                case "DELETE":
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        fileBackedTasksManager.deleteAllSubtasks();
                        String response = "Список подзадач очищен.";
                        sendText(httpExchange, response);
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllSubtasks().containsKey(id)) {
                            fileBackedTasksManager.deleteSubtaskById(id);
                            System.out.println();
                            httpExchange.sendResponseHeaders(200, 0);
                            String jsonString = "Удалили подзадачу с идентификатором " + id;
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                    }
                    break;
                case "POST":
                    try {
                        if (query.contains("id")) {
                            String[] split = query.split("=");
                            int id = Integer.parseInt(split[1]);
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            if (fileBackedTasksManager.showAllSubtasks().containsKey(id)) {
                                fileBackedTasksManager.updateSubtask(Integer.parseInt(split[1]), subtask);
                                String jsonString = "Подзадача изменена!";
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            } else {
                                httpExchange.sendResponseHeaders(404, 0);
                                String jsonString = "";
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        if (!body.isEmpty()) {
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            String jsonString = gson.toJson(fileBackedTasksManager.createSubtask(subtask));
                            if (!jsonString.equals("null")) {
                                jsonString = "Подзадача создана!";
                                httpExchange.sendResponseHeaders(200, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            } else {
                                httpExchange.sendResponseHeaders(400, 0);
                                jsonString = "";
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            }
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                    }
                    break;
                default:
                    System.out.println("/ ждем GET-запрос, POST-запрос или DELETE-запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
            }
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(405, 0);
            String jsonString = "";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(jsonString.getBytes());
            }
        } finally {
            httpExchange.close();
        }
    }

    private void handleEpics(HttpExchange httpExchange) throws IOException {

        try {
            System.out.println("\nhttp://localhost:8080" + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            InputStream inputStream = httpExchange.getRequestBody();
            String body = new String(inputStream.readAllBytes());
            String query = httpExchange.getRequestURI().getQuery();
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        final String response = gson.toJson(fileBackedTasksManager.showAllEpics());
                        sendText(httpExchange, response);
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllEpics().containsKey(id)) {
                            final String response = gson.toJson(fileBackedTasksManager.showEpicById(id));
                            sendText(httpExchange, response);
                            return;
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        fileBackedTasksManager.deleteAllEpics();
                        httpExchange.sendResponseHeaders(200, 0);
                        final String jsonString = "Список эпиков очищен.";
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(jsonString.getBytes());
                        }
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllEpics().containsKey(id)) {
                            fileBackedTasksManager.deleteEpicById(id);
                            httpExchange.sendResponseHeaders(200, 0);
                            String jsonString = "Удалили эпик с идентификатором -" + id;
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        } else {
                            httpExchange.sendResponseHeaders(404, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                    }
                    break;
                case "POST":
                    try {
                        if (Pattern.matches("^/tasks/epic$", path)) {
                            Epic epic = gson.fromJson(body, Epic.class);
                            if (fileBackedTasksManager.showAllEpics().containsKey(epic.getMainTaskId())) {
                                String jsonString = "";
                                httpExchange.sendResponseHeaders(400, 0);
                                try (OutputStream os = httpExchange.getResponseBody()) {
                                    os.write(jsonString.getBytes());
                                }
                            } else {
                                if (!body.isEmpty()) {
                                    epic = gson.fromJson(body, Epic.class);
                                    fileBackedTasksManager.createEpic(epic);
                                    String jsonString = "Задача создана!";
                                    httpExchange.sendResponseHeaders(200, 0);
                                    try (OutputStream os = httpExchange.getResponseBody()) {
                                        os.write(jsonString.getBytes());
                                    }
                                } else {
                                    httpExchange.sendResponseHeaders(404, 0);
                                    String jsonString = "";
                                    try (OutputStream os = httpExchange.getResponseBody()) {
                                        os.write(jsonString.getBytes());
                                    }
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        if (!body.isEmpty()) {
                            httpExchange.sendResponseHeaders(412, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        } else {
                            httpExchange.sendResponseHeaders(400, 0);
                            String jsonString = "";
                            try (OutputStream os = httpExchange.getResponseBody()) {
                                os.write(jsonString.getBytes());
                            }
                        }
                    }
                    break;
                default: {
                    System.out.println("/ ждем GET-запрос, POST-запрос или DELETE-запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                    break;
                }
            }
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(405, 0);
            String jsonString = "";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(jsonString.getBytes());
            }
        } finally {
            httpExchange.close();
        }
    }

    private void handleHistory(HttpExchange httpExchange) throws IOException {

        try {
            System.out.println("\nhttp://localhost:8080" + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/history$", path)) {
                        final String response = gson.toJson(fileBackedTasksManager.getHistory());
                        sendText(httpExchange, response);
                        return;
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                        String jsonString = "";
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(jsonString.getBytes());
                        }
                    }
                    break;
                default:
                    System.out.println("/ ждем GET-запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                    String jsonString = "";
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(jsonString.getBytes());
                    }
                    break;
            }
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(405, 0);
            String jsonString = "";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(jsonString.getBytes());
            }
        } finally {
            httpExchange.close();
        }
    }

    private void handlePrioritizedTasks(HttpExchange httpExchange) throws IOException {

        try {
            System.out.println("\nhttp://localhost:8080" + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks$", path)) {
                        final String response = gson.toJson(fileBackedTasksManager.getPrioritizedSet());
                        sendText(httpExchange, response);
                        return;
                    } else {
                        httpExchange.sendResponseHeaders(405, 0);
                        String jsonString = "";
                        try (OutputStream os = httpExchange.getResponseBody()) {
                            os.write(jsonString.getBytes());
                        }
                    }
                    break;
                default:
                    System.out.println("/ ждем GET-запрос, а получили - " + requestMethod);
                    httpExchange.sendResponseHeaders(405, 0);
                    String jsonString = "";
                    try (OutputStream os = httpExchange.getResponseBody()) {
                        os.write(jsonString.getBytes());
                    }
                    break;
            }
        } catch (IOException e) {
            httpExchange.sendResponseHeaders(405, 0);
            String jsonString = "";
            try (OutputStream os = httpExchange.getResponseBody()) {
                os.write(jsonString.getBytes());
            }
        } finally {
            httpExchange.close();
        }
    }

    public void start() {
        System.out.println("Started TaskServer " + PORT8080);
        System.out.println("http://localhost:" + PORT8080 + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили сервер на порту " + PORT8080);
    }

    private String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
    }
}
