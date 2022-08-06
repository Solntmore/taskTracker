package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Comparable<Task> {
    protected String name;
    protected String description;
    protected int mainTaskId;
    protected Status status;
    protected LocalDateTime startTime;
    protected Duration duration;

    public Task(String name, String description, int mainTaskId, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.mainTaskId = mainTaskId;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(getDuration());
    }

    public Duration getDuration() {
        return duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task task = (Task) o;
        if (startTime == null && duration == null) {
            return getMainTaskId() == task.getMainTaskId() && getName().equals(task.getName())
                    && getDescription().equals(task.getDescription()) && getStatus() == task.getStatus();
        }
        return getMainTaskId() == task.getMainTaskId() && getName().equals(task.getName())
                && getDescription().equals(task.getDescription()) && getStatus() == task.getStatus()
                && getStartTime().equals(task.getStartTime()) && getDuration().equals(task.getDuration());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getDescription(), getMainTaskId(), getStatus(), getStartTime(), getDuration());
    }

    @Override
    public int compareTo(Task o) {
        return this.getStartTime().compareTo(o.getStartTime());
    }


    public enum Status {
        NEW, IN_PROGRESS, DONE;

        public static Status statusSet(String status) {
            switch (status) {
                case "DONE":
                    return Status.DONE;
                case "NEW":
                    return Status.NEW;
                default:
                    return Status.IN_PROGRESS;
            }
        }
    }


    @Override
    public String toString() {
        return "Название задачи: " + name + '\'' +
                " описание задачи: " + description + '\'' +
                " ID основной задачи: " + mainTaskId +
                " статус задачи: " + status + " дата начала: " +
                startTime + " продолжительность задачи: " + duration +
                '.';
    }

    public String toCsvString() {
        return getMainTaskId() + "," + "TASK," + getName() + ","
                + getStatus() + "," + getDescription() + "," + getStartTime() + "," + getDuration() + "\n";
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(Task.Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMainTaskId() {
        return mainTaskId;
    }

    public void setMainTaskId(int mainTaskId) {
        this.mainTaskId = mainTaskId;
    }
}

