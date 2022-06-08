import java.util.HashMap;

class Epic extends Task {
    public HashMap<Integer, Subtask> subtaskMap;

    public Epic(String name, String description, int mainTaskId, Status status, HashMap<Integer, Subtask> subtaskMap) {
        super(name, description, mainTaskId, status);
        this.subtaskMap = subtaskMap;
    }
}