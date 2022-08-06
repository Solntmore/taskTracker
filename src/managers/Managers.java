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

}