package task;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, Task.Status status, int mainTaskId) {
        super(name, description, mainTaskId, status);
        this.epicId = epicId;
    }


    @Override
    public String toString() {
        return "Название задачи: " + name + '\'' +
                " описание задачи: " + description + '\'' +
                " ID основной задачи: " + epicId + '\'' +
                " ID подзадачи: " + mainTaskId + " статус задачи: "
                + status + '.';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return mainTaskId == subtask.mainTaskId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), mainTaskId);
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}


