package test;

import interfaces.TaskManager;
import org.junit.jupiter.api.Test;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static task.Task.Status.*;

abstract class BaseTaskManagerTest<T extends TaskManager> {

    T taskManager;
    static Task task;
    static Epic epic;
    static Subtask subtask1;
    static Subtask subtask2;
    static Subtask subtask3;
    final static LocalDateTime DAY_1 = LocalDateTime.of(2022, 6, 1, 0, 0);
    final static LocalDateTime DAY_2 = LocalDateTime.of(2022, 6, 2, 0, 0);
    final static LocalDateTime DAY_3 = LocalDateTime.of(2022, 6, 3, 0, 0);
    final static LocalDateTime DAY_4 = LocalDateTime.of(2022, 6, 4, 0, 0);

    //строчка 38 тестирует создание задачи с пересечением во времени с другой, возвращается null, значит задача не
    //создана из-за пересечения
    @Test
    void createTaskTest() throws IOException {

        Task newTask = (Task) taskManager.createTask(task);
        Task savedTask = taskManager.showTaskById(newTask.getMainTaskId());
        assertNull(taskManager.createTask(task), "Задача не создана, так как пересекается во времени с другой");
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(newTask, savedTask, "Задачи не совпадают.");

        final HashMap<Integer, Task> tasks = taskManager.showAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void createEpicTest() throws IOException {
        Epic newEpic = (Epic) taskManager.createEpic(epic);
        Epic savedEpic = taskManager.showEpicById(newEpic.getMainTaskId());
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(newEpic, savedEpic, "Задачи не совпадают.");

        final HashMap<Integer, Epic> epics = taskManager.showAllEpics();

        assertNotNull(epics, "Задачи на возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(1), "Задачи не совпадают.");
    }

    @Test
    void createSubtaskTest() throws IOException {
        assertNull(taskManager.createSubtask(subtask1));
        Epic newEpic = (Epic) taskManager.createEpic(epic);
        Subtask newSubtask = (Subtask) taskManager.createSubtask(subtask1);
        Subtask savedSubtask = taskManager.showSubtaskById(newSubtask.getMainTaskId());
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(newSubtask, savedSubtask, "Задачи не совпадают.");

        final HashMap<Integer, Subtask> subtasks = taskManager.showAllSubtasks();

        assertNotNull(subtasks, "Задачи на возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(newSubtask, subtasks.get(2), "Задачи не совпадают.");
    }

    @Test
    void showAllTasksTest() throws IOException {
        HashMap<Integer, Task> taskMap = taskManager.showAllTasks();
        HashMap<Integer, Task> newTaskMap = new HashMap<>();
        assertEquals(taskMap, newTaskMap, "Список задач не совпадает.");
        taskManager.createTask(task);
        newTaskMap.put(taskMap.get(1).getMainTaskId(), task);
        assertEquals(taskMap, newTaskMap, "Список задач не совпадает.");
    }

    @Test
    void showAllEpicsTest() throws IOException {
        HashMap<Integer, Epic> epicMap = taskManager.showAllEpics();
        HashMap<Integer, Epic> newEpicMap = new HashMap<>();
        assertEquals(epicMap, newEpicMap, "Список задач не совпадает.");
        taskManager.createEpic(epic);
        newEpicMap.put(epicMap.get(1).getMainTaskId(), epic);
        assertEquals(epicMap, newEpicMap, "Список задач не совпадает.");
    }

    @Test
    void showAllSubtasksTest() throws IOException {
        HashMap<Integer, Subtask> subtaskMap = taskManager.showAllSubtasks();
        HashMap<Integer, Subtask> newSubtaskMap = new HashMap<>();
        assertEquals(subtaskMap, newSubtaskMap, "Список задач не совпадает.");
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        newSubtaskMap.put(subtaskMap.get(2).getMainTaskId(), subtask1);
        assertEquals(subtaskMap, newSubtaskMap, "Список задач не совпадает.");
    }

    @Test
    void deleteAllTasksTest() throws IOException {
        taskManager.createTask(task);
        HashMap<Integer, Task> taskMap = taskManager.showAllTasks();
        assertEquals(1, taskMap.size(), "Список задач заполнен неправильно.");
        HashMap<Integer, Task> isEmptyTaskMap = taskManager.deleteAllTasks();
        assertEquals(0, isEmptyTaskMap.size(), "Список задач не пустой");

    }

    @Test
    void deleteAllEpicsTest() throws IOException {
        taskManager.createEpic(epic);
        HashMap<Integer, Epic> epicMap = taskManager.showAllEpics();
        assertEquals(1, epicMap.size(), "Список задач заполнен неправильно.");
        HashMap<Integer, Epic> isEmptyEpicMap = taskManager.deleteAllEpics();
        assertEquals(0, isEmptyEpicMap.size(), "Список задач не пустой");
    }

    @Test
    void deleteAllSubtasksTest() throws IOException {
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask1);
        HashMap<Integer, Subtask> subtaskMap = taskManager.showAllSubtasks();
        assertEquals(1, subtaskMap.size(), "Список задач заполнен неправильно.");
        HashMap<Integer, Subtask> isEmptySubtaskMap = taskManager.deleteAllSubtasks();
        assertEquals(0, isEmptySubtaskMap.size(), "Список задач не пустой");
    }

    @Test
    void deleteTaskByIdTest() throws IOException {
        assertNull(taskManager.deleteTaskById(1), "Нет задачи с таким id");
        HashMap<Integer, Task> taskMap = new HashMap<>();
        Task newTask = (Task) taskManager.createTask(task);
        int id = newTask.getMainTaskId();
        taskMap.put(id, task);
        taskMap.remove(id);
        HashMap<Integer, Task> newTaskMap = taskManager.deleteTaskById(id);
        assertEquals(taskMap, newTaskMap, "Списки задач не равны");
    }

    @Test
    void deleteSubtaskByIdTest() throws IOException {
        assertNull(taskManager.deleteSubtaskById(1), "Нет задачи с таким id");
        taskManager.createEpic(epic);

        HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
        Subtask newSubtask = (Subtask) taskManager.createSubtask(subtask1);
        int id = newSubtask.getMainTaskId();
        subtaskMap.put(id, subtask1);
        subtaskMap.remove(id);
        HashMap<Integer, Subtask> newSubtaskMap = taskManager.deleteSubtaskById(id);
        assertEquals(subtaskMap, newSubtaskMap, "Списки задач не равны");
    }

    @Test
    void deleteEpicByIdTest() throws IOException {
        assertNull(taskManager.deleteEpicById(1), "Нет задачи с таким id");
        HashMap<Integer, Epic> epicMap = new HashMap<>();
        Epic newEpic = (Epic) taskManager.createEpic(epic);
        int id = newEpic.getMainTaskId();
        epicMap.put(id, epic);
        epicMap.remove(id);
        HashMap<Integer, Epic> newEpicMap = taskManager.deleteEpicById(id);
        assertEquals(epicMap, newEpicMap, "Списки задач не равны");
    }

    @Test
    void showTaskByIdTest() throws IOException {
        assertNull(taskManager.showTaskById(1), "Нет задачи с таким id");
        Task newTask = (Task) taskManager.createTask(task);
        assertEquals(newTask, taskManager.showTaskById(newTask.getMainTaskId()), "Задачи не совпадают");
    }

    @Test
    void showEpicByIdTest() throws IOException {
        assertNull(taskManager.showEpicById(1), "Нет задачи с таким id");
        Epic newEpic = (Epic) taskManager.createEpic(epic);
        assertEquals(newEpic, taskManager.showEpicById(newEpic.getMainTaskId()), "Задачи не совпадают");
    }

    @Test
    void showSubtaskByIdTest() throws IOException {
        assertNull(taskManager.showSubtaskById(1), "Нет задачи с таким id");
        taskManager.createEpic(epic);
        Subtask newSubtask = (Subtask) taskManager.createSubtask(subtask1);
        assertEquals(newSubtask, taskManager.showSubtaskById(newSubtask.getMainTaskId()), "Задачи не совпадают");
    }

    @Test
    void showSubtasksByEpicIdTest() throws IOException {
        assertNull(taskManager.showSubtaskById(1), "Нет задачи с таким id");
        Epic newEpic = (Epic) taskManager.createEpic(epic);
        Subtask newSubtask1 = (Subtask) taskManager.createSubtask(subtask1);
        Subtask newSubtask2 = (Subtask) taskManager.createSubtask(subtask2);
        HashMap<Integer, Subtask> subtaskMap = new HashMap<>();
        subtaskMap.put(newSubtask1.getMainTaskId(), subtask1);
        subtaskMap.put(newSubtask2.getMainTaskId(), subtask2);
        assertEquals(subtaskMap, taskManager.showSubtasksByEpicId(newEpic.getMainTaskId()));
    }

    @Test
    void updateTaskTest() throws IOException {
        assertNull(taskManager.updateTask(1, Task.Status.DONE), "Нет задачи с таким id");
        Task newTask = (Task) taskManager.createTask(task);
        assertEquals(NEW, newTask.getStatus(), "Статусы задач не совпадают");
        newTask = taskManager.updateTask(1, Task.Status.DONE);
        assertEquals(DONE, newTask.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    void updateSubtaskTest() throws IOException {
        assertNull(taskManager.updateSubtask(1, Task.Status.DONE), "Нет задачи с таким id");
        Epic newEpic = (Epic) taskManager.createEpic(epic);
        Subtask newSubtask = (Subtask) taskManager.createSubtask(subtask1);
        assertEquals(NEW, newSubtask.getStatus(), "Статусы задач не совпадают");
        newSubtask = taskManager.updateSubtask(2, Task.Status.DONE);
        assertEquals(DONE, newSubtask.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    void updateEpicTest() throws IOException {
        Epic newEpic = (Epic) taskManager.createEpic(epic);
        assertEquals(NEW, newEpic.getStatus(), "Статусы задач не совпадают");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        newEpic = taskManager.showEpicById(1);
        assertEquals(IN_PROGRESS, newEpic.getStatus(), "Статусы задач не совпадают");
        taskManager.updateSubtask(2, DONE);
        taskManager.updateSubtask(3, DONE);
        newEpic = taskManager.showEpicById(1);
        assertEquals(DONE, newEpic.getStatus(), "Статусы задач не совпадают");
    }

    @Test
    void getHistoryTest() throws IOException {
        List<Task> newHistoryList = new ArrayList<>();
        assertEquals(newHistoryList, taskManager.getHistory(), "Списки не равны");
        newHistoryList.add((Task) taskManager.createEpic(epic));
        newHistoryList.add((Task) taskManager.createTask(task));
        newHistoryList.add((Task) taskManager.createSubtask(subtask3));

        taskManager.showEpicById(1);
        taskManager.showTaskById(2);
        taskManager.showSubtaskById(3);

        List<Task> historyList = taskManager.getHistory();

        assertEquals(newHistoryList, historyList, "Списки не равны");
    }
}
