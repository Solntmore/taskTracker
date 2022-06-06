import java.util.Objects;

class Subtask extends Task {
    public int subtaskId;

    public Subtask(String name, String description, int mainTaskId, Manager.Status status, int subtaskID) {
        super(name, description, mainTaskId, status);
        this.subtaskId = subtaskID;
    }

    @Override
    public String toString() {
        return "Название задачи: " + name + '\'' +
                " описание задачи: " + description + '\'' +
                " ID основной задачи: " + mainTaskId + '\'' +
                " ID подзадачи: " + subtaskId + " статус задачи: "
                + status + '.';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return subtaskId == subtask.subtaskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskId);
    }

    public int getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(int subtaskID) {
        this.subtaskId = subtaskID;
    }
}


