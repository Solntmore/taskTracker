package interfaces;

import task.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    Object createTask(Task task) throws IOException;

    Object createEpic(Epic epic) throws IOException;

    Object createSubtask(Subtask subtask) throws IOException;

    HashMap<Integer, Task> showAllTasks();

    HashMap<Integer, Epic> showAllEpics();

    HashMap<Integer, Subtask> showAllSubtasks();

    HashMap<Integer, Task> deleteAllTasks() throws IOException;

    HashMap<Integer, Epic> deleteAllEpics() throws IOException;

    HashMap<Integer, Subtask> deleteAllSubtasks() throws IOException;

    HashMap<Integer, Task> deleteTaskById(int id) throws IOException;

    HashMap<Integer, Epic> deleteEpicById(int id) throws IOException;

    HashMap<Integer, Subtask> deleteSubtaskById(int id) throws IOException;

    Task showTaskById(int id) throws IOException;

    Epic showEpicById(int id) throws IOException;

    Subtask showSubtaskById(int id) throws IOException;

    HashMap<Integer, Subtask> showSubtasksByEpicId(int id) throws IOException;

    Task updateTask(int id, Task task) throws IOException;

    Subtask updateSubtask(int id, Subtask subtask) throws IOException;

    Epic updateEpic(int epicId) throws IOException;

    List<Task> getHistory();

    Set<Task> getPrioritizedSet();


}
