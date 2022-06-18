package interfaces;

import task.*;

import java.util.HashMap;
import java.util.List;

public interface TaskManager {

    Object createTask(Task task);

    Object createEpic(Epic epic);

    Object createSubtask(Subtask subtask);

    HashMap<Integer, Task> showAllTasks();

    HashMap<Integer, Epic> showAllEpic();

    HashMap<Integer, Subtask> showAllSubtasks();

    HashMap<Integer, Task> deleteAllTasks();

    HashMap<Integer, Epic> deleteAllEpics();

    HashMap<Integer, Subtask> deleteAllSubtasks();

    HashMap<Integer, Task> deleteTaskById(int id);

    HashMap<Integer, Epic> deleteEpicById(int id);

    HashMap<Integer, Subtask> deleteSubtaskById(int id);

    Task showTaskById(int id);

    Epic showEpicById(int id);

    Subtask showSubtaskById(int id);

    HashMap<Integer, Subtask> showSubtasksByEpicId(int id);

    Task updateTask(int id, Task.Status status);

    Subtask updateSubtask(int id, Task.Status status);

    Epic updateEpic(int epicId);

    List<Task> getHistory();


}
