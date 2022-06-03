import java.util.Objects;

public class Task {
    public String name;
    public String description;
    public int mainTaskId;
    protected String status;

    public Task(String name, String description, int mainTaskId, String status) {
        this.name = name;
        this.description = description;
        this.mainTaskId = mainTaskId;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return mainTaskId == task.mainTaskId && Objects.equals(name, task.name) &&
                Objects.equals(description, task.description) && Objects.equals(status, task.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, mainTaskId, status);
    }

    @Override
    public String toString() {
        return "Название задачи: " + name + '\'' +
                " описание задачи: " + description + '\'' +
                " ID основной задачи: " + mainTaskId +
                " статус задачи: " + status +
                '.';
    }
}

class Subtask extends Task {
    public int subtaskID;

    public Subtask(String name, String description, int mainTaskId, String status, int subtaskID) {
        super(name, description, mainTaskId, status);
        this.subtaskID = subtaskID;
    }

    @Override
    public String toString() {
        return "Название задачи: " + name + '\'' +
                " описание задачи: " + description + '\'' +
                " ID основной задачи: " + mainTaskId + '\'' +
                " ID подзадачи: " + subtaskID + " статус задачи: "
                + status + '.';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return subtaskID == subtask.subtaskID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskID);
    }
}

class Epic extends Task {

    public Epic(String name, String description, int mainTaskId, String status) {
        super(name, description, mainTaskId, status);
    }
}
