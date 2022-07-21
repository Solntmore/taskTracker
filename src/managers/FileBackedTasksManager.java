package managers;

import interfaces.TaskManager;
import task.*;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    String backUpFile;

    public FileBackedTasksManager(String path) {
        backUpFile = path;
    }

    @Override
    public Object createTask(Task task) throws IOException {
        Task taskOverride = (Task) super.createTask(task);
        save();
        return taskOverride;
    }

    @Override
    public Object createEpic(Epic epic) throws IOException {
        Epic epicOverride = (Epic) super.createEpic(epic);
        save();
        return epicOverride;
    }

    @Override
    public Object createSubtask(Subtask subtask) throws IOException {
        Subtask subtaskOverride = (Subtask) super.createSubtask(subtask);
        save();
        return subtaskOverride;
    }

    @Override
    public HashMap<Integer, Task> deleteAllTasks() throws IOException {
        HashMap<Integer, Task> allTasksMap = super.deleteAllTasks();
        save();
        return allTasksMap;
    }

    @Override
    public HashMap<Integer, Epic> deleteAllEpics() throws IOException {
        HashMap<Integer, Epic> allEpicsMap = super.deleteAllEpics();
        save();
        return allEpicsMap;
    }

    @Override
    public HashMap<Integer, Subtask> deleteAllSubtasks() throws IOException {
        HashMap<Integer, Subtask> allSubtasksMap = super.deleteAllSubtasks();
        save();
        return allSubtasksMap;
    }

    @Override
    public HashMap<Integer, Task> deleteTaskById(int id) throws IOException {
        HashMap<Integer, Task> allTasksMap = super.deleteTaskById(id);
        save();
        return allTasksMap;
    }

    @Override
    public HashMap<Integer, Epic> deleteEpicById(int id) throws IOException {
        HashMap<Integer, Epic> allEpicsMap = super.deleteEpicById(id);
        save();
        return allEpicsMap;
    }

    @Override
    public HashMap<Integer, Subtask> deleteSubtaskById(int id) throws IOException {
        HashMap<Integer, Subtask> allSubtasksMap = super.deleteSubtaskById(id);
        save();
        return allSubtasksMap;
    }

    @Override
    public Task showTaskById(int id) throws IOException {
        Task taskOverride = super.showTaskById(id);
        save();
        return taskOverride;
    }

    @Override
    public Epic showEpicById(int id) throws IOException {
        Epic epicOverride = super.showEpicById(id);
        save();
        return epicOverride;
    }

    @Override
    public Subtask showSubtaskById(int id) throws IOException {
        Subtask subtaskOverride = super.showSubtaskById(id);
        save();
        return subtaskOverride;
    }

    @Override
    public HashMap<Integer, Subtask> showSubtasksByEpicId(int id) throws IOException {
        HashMap<Integer, Subtask> allSubtasksMap = super.showSubtasksByEpicId(id);
        save();
        return allSubtasksMap;
    }

    @Override
    public Task updateTask(int id, Task.Status status) throws IOException {
        Task taskOverride = super.updateTask(id, status);
        save();
        return taskOverride;
    }

    @Override
    public Subtask updateSubtask(int id, Task.Status status) throws IOException {
        Subtask subtaskOverride = super.updateSubtask(id, status);
        save();
        return subtaskOverride;
    }

    @Override
    public Epic updateEpic(int epicId) throws IOException {
        Epic epicOverride = super.updateEpic(epicId);
        save();
        return epicOverride;
    }

    private void save() {
        try (Writer fileWriter = new FileWriter(backUpFile)) {
            fileWriter.write("id,type,name,status,description,epic\n");

            for (int i = 0; i <= getTaskCounter(); i++) {
                if (taskMap.containsKey(i)) {
                    fileWriter.write(toString(taskMap.get(i)));
                }
            }
            for (int i = 0; i <= getTaskCounter(); i++) {
                if (epicMap.containsKey(i)) {
                    fileWriter.write(toString(epicMap.get(i)));
                }
            }
            for (int i = 0; i <= getTaskCounter(); i++) {
                if (subtaskMap.containsKey(i)) {
                    fileWriter.write(toString(subtaskMap.get(i)));
                }
            }

            fileWriter.write("\n");

            List<Task> taskIdHistoryList = inMemoryHistoryManager.getHistory();
            for (Task task : taskIdHistoryList) {
                int id = task.getMainTaskId();
                fileWriter.write(id + ",");
            }

        } catch (IOException e) {
            System.out.println("Ошибка сохранения" + Arrays.toString(e.getStackTrace()));
        }
    }

    //вспомогательный внутренний метод класса, который из файла вытаскивает данные и раскладывает их по нужным мапам
    //и листам.
    public void recoveryFromFile() {
        try (FileReader reader = new FileReader(backUpFile); BufferedReader br = new BufferedReader(reader)) {

            while (br.ready()) {
                String line = br.readLine();
                String[] lineValue = line.split(",");
                if ((!lineValue[0].isEmpty() && lineValue.length > 1)) {
                    if (lineValue[1].equals("TASK") || lineValue[1].equals("SUBTASK") || lineValue[1].equals("EPIC")) {
                        fromString(line);
                    }
                    if (isNumeric(lineValue[0]) && isNumeric(lineValue[1])) {
                        makeTaskById(lineValue);
                    }
                }
                if (isNumeric(lineValue[0]) && lineValue.length == 1) {
                    makeTaskById(lineValue);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //вспомогательный внутренний метод класса, который превращает ID в задачу в истории просмотров, используется
    //в методе recoveryFromFile()
    private void makeTaskById(String[] value) {
        for (String number : value) {
            int id = Integer.parseInt(number);
            if (taskMap.containsKey(id)) {
                inMemoryHistoryManager.addTask(taskMap.get(id));
            } else if (epicMap.containsKey(id)) {
                inMemoryHistoryManager.addTask(epicMap.get(id));
            } else {
                inMemoryHistoryManager.addTask(subtaskMap.get(id));
            }
        }
    }

    //вспомогательный внутренний метод класса, который определяет передано ли в него число или нет.
    // Используется в методе recoveryFromFile() при считывании данных, чтобы корректно определять, какая строка
    //прочитана(с задачами или только с их id
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    //использовал перегрузку метода toString, так как параметры task и subtask отличаются
    private String toString(Task task) {
        return task.getMainTaskId() + "," + "TASK," + task.getName() + ","
                + task.getStatus() + "," + task.getDescription() + "," + "\n";
    }

    private String toString(Subtask subtask) {
        return subtask.getMainTaskId() + "," + "SUBTASK," + subtask.getName() + ","
                + subtask.getStatus() + "," + subtask.getDescription() + "," + subtask.getEpicId() + "\n";
    }

    private String toString(Epic epic) {
        return epic.getMainTaskId() + "," + "EPIC," + epic.getName() + ","
                + epic.getStatus() + "," + epic.getDescription() + "," + "\n";
    }

    private void fromString(String line) {
        String[] taskArray = line.split(",");
        if (taskArray[1].equals("TASK")) {
            Task.Status status = statusSet(taskArray[3]);
            String name = taskArray[2];
            String description = taskArray[4];
            int mainTaskId = Integer.parseInt(taskArray[0]);
            Task task = new Task(name, description, mainTaskId, status);
            try {
                backUpTask(task);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (taskArray[1].equals("SUBTASK")) {
            Task.Status status = statusSet(taskArray[3]);
            String name = taskArray[2];
            String description = taskArray[4];
            int mainTaskId = Integer.parseInt(taskArray[0]);
            int epicId = Integer.parseInt(taskArray[5]);
            Subtask subtask = new Subtask(name, description, epicId, status, mainTaskId);
            try {
                backUpSubtask(subtask);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            Task.Status status = statusSet(taskArray[3]);
            String name = taskArray[2];
            String description = taskArray[4];
            int mainTaskId = Integer.parseInt(taskArray[0]);
            Epic epic = new Epic(name, description, mainTaskId, status);
            try {
                backUpEpic(epic);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //вспомогательный внутренний метод класса, который определяет статус задачи при восстановлении
    private Task.Status statusSet(String status) {
        switch (status) {
            case "DONE":
                return Task.Status.DONE;
            case "NEW":
                return Task.Status.NEW;
            default:
                return Task.Status.IN_PROGRESS;
        }
    }

    private void backUpTask(Task task) throws IOException {
        taskMap.put(task.getMainTaskId(), task);
    }


    private void backUpEpic(Epic epic) throws IOException {
        epicMap.put(epic.getMainTaskId(), epic);
    }


    private void backUpSubtask(Subtask subtask) throws IOException {
        int id = subtask.getEpicId();
        subtaskMap.put(subtask.getMainTaskId(), subtask);
        Epic epic = epicMap.get(id);
        epic.addSubtaskMap(subtask.getMainTaskId(), subtask);
        epicMap.put(id, epic);
    }


}

