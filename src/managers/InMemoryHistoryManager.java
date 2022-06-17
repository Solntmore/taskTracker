package managers;

import interfaces.HistoryManager;
import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    public static List<Task> browsingList = new ArrayList<>();

    @Override
    public void addTask(Task task) {
        browsingList.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return browsingList;
    }

    public void checkSizeBrowsingList() {
        if (browsingList.size() > 10) {
            browsingList.remove(0);
        }
    }
}
