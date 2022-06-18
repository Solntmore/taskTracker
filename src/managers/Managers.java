package managers;

import interfaces.HistoryManager;
import interfaces.TaskManager;

public final class Managers {
    private static final TaskManager taskManager = new InMemoryTaskManager();
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}