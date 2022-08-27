package managers;

import client.KVTaskClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import task.Epic;
import task.Subtask;
import task.Task;
import helpTools.Utils;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;


public class HTTPTaskManager extends FileBackedTasksManager {
    static KVTaskClient kvTaskClient;
    private Type taskType;
    private final Gson gson = Utils.getGson();


    public HTTPTaskManager(String serverUrl) {
        kvTaskClient = new KVTaskClient(serverUrl);
    }

    @Override
    protected void save() {
        Gson gson = Utils.getGson();
        String taskMapJson = gson.toJson(taskMap);
        String subtaskMapJson = gson.toJson(subtaskMap);
        String epicMapJson = gson.toJson(epicMap);
        String historyMapJson = gson.toJson(getHistory());
        kvTaskClient.put("taskMap", taskMapJson);
        kvTaskClient.put("subtaskMap", subtaskMapJson);
        kvTaskClient.put("epicMap", epicMapJson);
        kvTaskClient.put("historyMap", historyMapJson);
    }

    @Override
    protected void recoveryFromFile() {
        recoveryTasks();
        recoveryEpics();
        recoverySubtasks();
        recoveryHistory();
    }

    private void recoveryTasks() {
        Type taskType = new TypeToken<HashMap<Integer, Task>>() {
        }.getType();
        HashMap<Integer, Task> tasks = gson.fromJson(kvTaskClient.load("taskMap"), taskType);
        if (!tasks.isEmpty()) {
            for (Task task : tasks.values()) {
                int id = task.getMainTaskId();
                taskMap.put(id, task);
                addTaskToPrioritizedSet(task);
                checkTaskCounter(id);
            }
        }
    }

    private void recoveryEpics() {
        taskType = new TypeToken<HashMap<Integer, Epic>>() {
        }.getType();
        HashMap<Integer, Epic> epics = gson.fromJson(kvTaskClient.load("epicMap"), taskType);
        if (!epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                int id = epic.getMainTaskId();
                epicMap.put(id, epic);
                checkTaskCounter(id);
            }
        }
    }

    private void recoverySubtasks() {
        taskType = new TypeToken<HashMap<Integer, Subtask>>() {
        }.getType();
        HashMap<Integer, Subtask> subtasks = gson.fromJson(kvTaskClient.load("subtaskMap"), taskType);
        if (!subtasks.isEmpty()) {
            for (Subtask subtask : subtasks.values()) {
                int id = subtask.getMainTaskId();
                subtaskMap.put(id, subtask);
                addTaskToPrioritizedSet(subtask);
                checkTaskCounter(id);
            }
        }
    }

    private void recoveryHistory() {
        taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(kvTaskClient.load("historyMap"), taskType);
        for (Task task : history) {
            int id = task.getMainTaskId();
            if (taskMap.containsKey(id)) {
                inMemoryHistoryManager.addTask(taskMap.get(id));
            } else if (epicMap.containsKey(id)) {
                inMemoryHistoryManager.addTask(epicMap.get(id));
            } else {
                inMemoryHistoryManager.addTask(subtaskMap.get(id));
            }
        }
    }

    private void checkTaskCounter(int id) {
        if (getTaskCounter() < id) {
            setTaskCounter(id);
        }
    }
}
