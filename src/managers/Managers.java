package managers;

import interfaces.HistoryManager;


public final class Managers {
    private static final FileBackedTasksManager taskManager = new FileBackedTasksManager(null);
    private static final HistoryManager historyManager = new InMemoryHistoryManager();


    public static FileBackedTasksManager getDefault(String path) {
        return new FileBackedTasksManager(path);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    //сделал метод статик, согласно ТЗ, хотя до конца пока не понимаю, почему для восстановления нельзя такой же
    //способ, что и при создании новых экземпляров.
    public static FileBackedTasksManager loadFromFile(String file) {
        FileBackedTasksManager taskManager = new FileBackedTasksManager(file);
        taskManager.recoveryFromFile();
        return taskManager;
    }
}