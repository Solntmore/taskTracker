package managers;

public final class Managers {
    private static final InMemoryTaskManager inMemoryTaskManager = new InMemoryTaskManager();
    private static final InMemoryHistoryManager inMemoryHistoryManager = new InMemoryHistoryManager();

    public static InMemoryTaskManager getDefault() {
        return inMemoryTaskManager;
    }

    public static InMemoryHistoryManager getDefaultHistory() {
        return inMemoryHistoryManager;
    }
}