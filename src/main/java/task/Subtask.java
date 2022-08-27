package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String name, String description, int epicId, Task.Status status, int mainTaskId,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, mainTaskId, status, startTime, duration);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Название задачи: " + name + '\'' +
                " описание задачи: " + description + '\'' +
                " ID основной задачи: " + epicId + '\'' +
                " ID подзадачи: " + mainTaskId + " статус задачи: "
                + status + " дата начала: " + startTime +
                " продолжительность задачи: " + duration + '.';
    }
@Override
    public String toCsvString() {
        return getMainTaskId() + "," + "SUBTASK," + getName() + ","
                + getStatus() + "," + getDescription() + "," + getStartTime()
                + "," + getDuration() + "," + getEpicId() + "\n";
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return getEpicId() == subtask.getEpicId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getEpicId());
    }
}


