package task;

import task.Subtask;
import task.Task;

import java.util.HashMap;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();


    public Epic(String name, String description, int mainTaskId, Status status) {
        super(name, description, mainTaskId, status);
        this.subtaskMap = this.subtaskMap;
    }

    public HashMap<Integer, Subtask> getSubtaskMap() {
        return subtaskMap;
    }

    public void addSubtaskMap(int id, Subtask subtask) {
        subtaskMap.put(id, subtask);
    }

    public void clearSubtaskMap() {
        subtaskMap.clear();
    }

    public void removeFromSubtaskMap(int id) {
        subtaskMap.remove(id);
    }

    public Subtask getFromSubtaskMap(int id) {
        return subtaskMap.get(id);

    }

    public boolean containsSubtaskMap(int id) {
        return subtaskMap.containsKey(id);
    }

}