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

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpUserServer;
        httpUserServer = new HttpTaskServer(new FileBackedTasksManager("taskManager.csv"));
        httpUserServer.start();
    }

    private void handleTasks(HttpExchange httpExchange) throws IOException {

        try {
            System.out.println("\nhttp://localhost:8080" + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String body = readText(httpExchange);
            String query = httpExchange.getRequestURI().getQuery();
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        final String response = gson.toJson(fileBackedTasksManager.showAllTasks());
                        sendText(httpExchange, response, 200);
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllTasks().containsKey(id)) {
                            final String response = gson.toJson(fileBackedTasksManager.showTaskById(id));
                            sendText(httpExchange, response, 200);
                            return;
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 404);
                        }
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/task$", path)) {
                        fileBackedTasksManager.deleteAllTasks();
                        String response = "Список эпиков очищен";
                        sendText(httpExchange, response, 200);
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllTasks().containsKey(id)) {
                            fileBackedTasksManager.deleteTaskById(id);
                            String response = "Удалили задачу с идентификатором -" + id;
                            sendText(httpExchange, response, 200);
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 404);
                        }
                    }
                    break;
                case "POST":
                    try {
                        if (query.contains("id")) {
                            int id = getIdFromQuery(query);
                            Task task = gson.fromJson(body, Task.class);
                            if (fileBackedTasksManager.showAllTasks().containsKey(id)) {
                                fileBackedTasksManager.updateTask(id, task);
                                String response = "Задача изменена!";
                                sendText(httpExchange, response, 200);
                            } else {
                                String response = "";
                                sendText(httpExchange, response, 404);
                            }
                        }
                    } catch (NullPointerException e) {
                        if (!body.isEmpty()) {
                            Task task = gson.fromJson(body, Task.class);
                            String response = gson.toJson(fileBackedTasksManager.createTask(task));
                            if (!response.equals("null")) {
                                response = "Задача создана!";
                                sendText(httpExchange, response, 200);
                            } else {
                                response = "";
                                sendText(httpExchange, response, 400);
                            }
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 404);
                        }
                    }
                    break;
                default:
                    String response = "/ ждем GET-запрос, POST-запрос или DELETE-запрос, а получили - " + requestMethod;
                    sendText(httpExchange, response, 405);
                    break;
            }
        } catch (IOException e) {
            String response = "";
            sendText(httpExchange, response, 405);
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
            String body = readText(httpExchange);
            String query = httpExchange.getRequestURI().getQuery();
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        final String response = gson.toJson(fileBackedTasksManager.showAllSubtasks());
                        sendText(httpExchange, response, 200);
                        return;
                    }
                    if (Pattern.matches("^/tasks/subtask/epic/$", path)) {

                        System.out.println("Сработало");
                        if (query.contains("id")) {
                            int id = getIdFromQuery(query);
                            if (fileBackedTasksManager.showAllEpics().containsKey(id)) {
                                final String response = gson.toJson(fileBackedTasksManager.showSubtasksByEpicId(id));
                                sendText(httpExchange, response, 200);
                                return;
                            } else {
                                System.out.println("Нет эпика с идентификатором -" + id);
                                String response = "";
                                sendText(httpExchange, response, 404);
                            }
                            break;
                        }
                        return;
                    }
                    if (query.contains("id")) {
                        int id = getIdFromQuery(query);
                        if (fileBackedTasksManager.showAllSubtasks().containsKey(id)) {
                            final String response = gson.toJson(fileBackedTasksManager.showSubtaskById(id));
                            sendText(httpExchange, response, 200);
                            return;
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 404);
                        }
                        break;
                    }
                case "DELETE":
                    if (Pattern.matches("^/tasks/subtask$", path)) {
                        fileBackedTasksManager.deleteAllSubtasks();
                        String response = "Список подзадач очищен.";
                        sendText(httpExchange, response, 200);
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllSubtasks().containsKey(id)) {
                            fileBackedTasksManager.deleteSubtaskById(id);
                            String response = "Удалили подзадачу с идентификатором " + id;
                            sendText(httpExchange, response, 200);
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 404);
                        }
                    }
                    break;
                case "POST":
                    try {
                        if (query.contains("id")) {
                            int id = getIdFromQuery(query);
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            if (fileBackedTasksManager.showAllSubtasks().containsKey(id)) {
                                fileBackedTasksManager.updateSubtask(id, subtask);
                                String response = "Подзадача изменена!";
                                sendText(httpExchange, response, 200);
                            } else {
                                String response = "";
                                sendText(httpExchange, response, 404);
                            }
                        }
                    } catch (NullPointerException e) {
                        if (!body.isEmpty()) {
                            Subtask subtask = gson.fromJson(body, Subtask.class);
                            String response = gson.toJson(fileBackedTasksManager.createSubtask(subtask));
                            if (!response.equals("null")) {
                                response = "Подзадача создана!";
                                sendText(httpExchange, response, 200);
                            } else {
                                response = "";
                                sendText(httpExchange, response, 400);
                            }
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 404);
                        }
                    }
                    break;
                default:
                    String response = "/ ждем GET-запрос, POST-запрос или DELETE-запрос, а получили - " + requestMethod;
                    sendText(httpExchange, response, 405);
            }
        } catch (IOException e) {
            String response = "";
            sendText(httpExchange, response, 405);
        } finally {
            httpExchange.close();
        }
    }

    private void handleEpics(HttpExchange httpExchange) throws IOException {

        try {
            System.out.println("\nhttp://localhost:8080" + httpExchange.getRequestURI());
            String requestMethod = httpExchange.getRequestMethod();
            String path = httpExchange.getRequestURI().getPath();
            String body = readText(httpExchange);
            String query = httpExchange.getRequestURI().getQuery();
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        final String response = gson.toJson(fileBackedTasksManager.showAllEpics());
                        sendText(httpExchange, response, 200);
                        return;
                    }
                    if (query.contains("id")) {
                        String[] split = query.split("=");
                        int id = Integer.parseInt(split[1]);
                        if (fileBackedTasksManager.showAllEpics().containsKey(id)) {
                            final String response = gson.toJson(fileBackedTasksManager.showEpicById(id));
                            sendText(httpExchange, response, 200);
                            return;
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 404);
                        }
                    }
                    break;
                case "DELETE":
                    if (Pattern.matches("^/tasks/epic$", path)) {
                        fileBackedTasksManager.deleteAllEpics();
                        final String response = "Список эпиков очищен.";
                        sendText(httpExchange, response, 200);
                        return;
                    }
                    if (query.contains("id")) {
                        int id = getIdFromQuery(query);
                        if (fileBackedTasksManager.showAllEpics().containsKey(id)) {
                            fileBackedTasksManager.deleteEpicById(id);
                            String response = "Удалили эпик с идентификатором -" + id;
                            sendText(httpExchange, response, 200);
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 404);
                        }
                    }
                    break;
                case "POST":
                    try {
                        if (Pattern.matches("^/tasks/epic$", path)) {
                            Epic epic = gson.fromJson(body, Epic.class);
                            if (fileBackedTasksManager.showAllEpics().containsKey(epic.getMainTaskId())) {
                                String response = "";
                                sendText(httpExchange, response, 400);
                            } else {
                                if (!body.isEmpty()) {
                                    epic = gson.fromJson(body, Epic.class);
                                    fileBackedTasksManager.createEpic(epic);
                                    String response = "Задача создана!";
                                    sendText(httpExchange, response, 200);
                                } else {
                                    String response = "";
                                    sendText(httpExchange, response, 404);
                                }
                            }
                        }
                    } catch (NullPointerException e) {
                        if (!body.isEmpty()) {
                            String response = "";
                            sendText(httpExchange, response, 412);
                        } else {
                            String response = "";
                            sendText(httpExchange, response, 400);
                        }
                    }
                    break;
                default: {
                    String response = "/ ждем GET-запрос, POST-запрос или DELETE-запрос, а получили - " + requestMethod;
                    sendText(httpExchange, response, 405);
                    break;
                }
            }
        } catch (IOException e) {
            String response = "";
            sendText(httpExchange, response, 405);
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
                        sendText(httpExchange, response, 200);
                        return;
                    } else {
                        String response = "";
                        sendText(httpExchange, response, 405);
                    }
                    break;
                default:
                    String response = "/ ждем GET-запрос, а получили - " + requestMethod;
                    sendText(httpExchange, response, 405);
                    break;
            }
        } catch (IOException e) {
            String response = "";
            sendText(httpExchange, response, 405);
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
                        sendText(httpExchange, response, 200);
                        return;
                    } else {
                        String response = "";
                        sendText(httpExchange, response, 405);
                    }
                    break;
                default:
                    String response = "/ ждем GET-запрос, а получили - " + requestMethod;
                    sendText(httpExchange, response, 405);
                    break;
            }
        } catch (IOException e) {
            String response = "";
            sendText(httpExchange, response, 405);
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

    private void sendText(HttpExchange h, String text, int rCode) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(rCode, resp.length);
        h.getResponseBody().write(resp);
    }

    private int getIdFromQuery(String query) {
        String[] split = query.split("=");
        return Integer.parseInt(split[1]);
    }
}
