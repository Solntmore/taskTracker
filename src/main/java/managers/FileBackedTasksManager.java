package managers;

import exceptions.ManagerSaveException;
import interfaces.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskType;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static helpTools.Сonstants.*;
import static task.Task.Status.statusSet;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private final String backUpFile;


    public FileBackedTasksManager() {
        backUpFile = BACK_UP_FILE;
    }

    public FileBackedTasksManager(String path) {
        backUpFile = path;
    }


    public static void main(String[] args) {
        FileBackedTasksManager taskManager = Managers.getDefault(BACK_UP_FILE);
    }

    public static FileBackedTasksManager loadFromFile(String file) {
        try {
            FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
            taskManager.recoveryFromFile();
            return taskManager;
        } catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
            return null;
        }
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
        if (taskOverride == null) {
            System.out.println("Задачи с id " + id + " нет.");
            return null;
        } else {
            save();
            return taskOverride;
        }
    }

    @Override
    public Epic showEpicById(int id) throws IOException {
        Epic epicOverride = super.showEpicById(id);
        if (epicOverride == null) {
            System.out.println("Эпика с id " + id + " нет.");
            return null;
        } else {
            save();
            return epicOverride;
        }
    }

    @Override
    public Subtask showSubtaskById(int id) throws IOException {
        Subtask subtaskOverride = super.showSubtaskById(id);
        if (subtaskOverride == null) {
            System.out.println("Подзадачи с id " + id + " нет.");
            return null;
        } else {
            save();
            return subtaskOverride;
        }
    }

    @Override
    public HashMap<Integer, Subtask> showSubtasksByEpicId(int id) throws IOException {
        HashMap<Integer, Subtask> allSubtasksMap = super.showSubtasksByEpicId(id);
        save();
        return allSubtasksMap;
    }

    @Override
    public Task updateTask(int id, Task task) throws IOException {
        Task taskOverride = super.updateTask(id, task);
        save();
        return taskOverride;
    }

    @Override
    public Subtask updateSubtask(int id, Subtask subtask) throws IOException {
        Subtask subtaskOverride = super.updateSubtask(id, subtask);
        save();
        return subtaskOverride;
    }

    @Override
    public Epic updateEpic(int epicId) throws IOException {
        Epic epicOverride = super.updateEpic(epicId);
        save();
        return epicOverride;
    }

    protected void save() {
        try (Writer fileWriter = new FileWriter(backUpFile, UTF_8)) {
            fileWriter.write("id,type,name,status,description,startTime,duration,epic\n");

            for (int i = 0; i <= getTaskCounter(); i++) {
                if (taskMap.containsKey(i)) {
                    fileWriter.write(taskMap.get(i).toCsvString());
                }
            }
            for (int i = 0; i <= getTaskCounter(); i++) {
                if (epicMap.containsKey(i)) {
                    fileWriter.write(epicMap.get(i).toCsvString());
                }
            }
            for (int i = 0; i <= getTaskCounter(); i++) {
                if (subtaskMap.containsKey(i)) {
                    fileWriter.write(subtaskMap.get(i).toCsvString());
                }
            }

            fileWriter.write("\n");

            List<Task> taskIdHistoryList = inMemoryHistoryManager.getHistory();
            for (Task task : taskIdHistoryList) {
                int id = task.getMainTaskId();
                fileWriter.write(id + DELIMITER);
            }

        } catch (IOException e) {
            System.out.println("Ошибка сохранения" + Arrays.toString(e.getStackTrace()));
            throw new ManagerSaveException("Ошибка сохранения" + Arrays.toString(e.getStackTrace()));
        }
    }

    //вспомогательный внутренний метод класса, который из файла вытаскивает данные и раскладывает их по нужным мапам
    //и листам.
    protected void recoveryFromFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(backUpFile, UTF_8))) {

            while (br.ready()) {
                String line = br.readLine();
                String[] lineValue = line.split(DELIMITER);
                if ((!lineValue[0].isEmpty() && lineValue.length > 1)) {
                    if (lineValue[1].equals(TaskType.TASK.name()) || lineValue[1].equals(TaskType.SUBTASK.name())
                            || lineValue[1].equals(TaskType.EPIC.name())) {
                        fromString(line);
                        recoveryTaskCounter(Integer.parseInt(lineValue[0]));
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
            throw new ManagerSaveException("Произошла ошибка восстановления из файла, попробуйте еще раз.");
        }
    }

    private void recoveryTaskCounter(int newId) {
        if (newId > getTaskCounter()) {
            setTaskCounter(newId);
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

    private void fromString(String line) throws IOException {
        String[] taskArray = line.split(",");
        String taskType = taskArray[1];
        Task.Status status = statusSet(taskArray[3]);
        String name = taskArray[2];
        String description = taskArray[4];
        LocalDateTime startTime = backUpStartTime(taskArray[5]);
        Duration duration = backUpDuration(taskArray[6]);
        int mainTaskId = Integer.parseInt(taskArray[0]);

        try {
            if (taskType.equals(TaskType.TASK.name())) {
                Task task = new Task(name, description, mainTaskId, status, startTime, duration);
                backUpTask(task);
            } else if (taskType.equals(TaskType.SUBTASK.name())) {
                int epicId = Integer.parseInt(taskArray[7]);
                Subtask subtask = new Subtask(name, description, epicId, status, mainTaskId, startTime, duration);
                backUpSubtask(subtask);
            } else {
                Epic epic = new Epic(name, description, mainTaskId, status, startTime, duration);
                backUpEpic(epic);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла непредвиденная ошибка создания задачи, попробуйте еще раз.");
        }
    }

    //вспомогательный внутренний метод класса, который определяет статус задачи при восстановлении
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

    private Duration backUpDuration(String duration) {
        if (duration.equals("null")) {
            return null;
        } else {
            return Duration.parse(duration);
        }
    }

    private LocalDateTime backUpStartTime(String startTime) {
        if (startTime.equals("null")) {
            return null;
        } else {
            return LocalDateTime.parse(startTime);
        }
    }


}

