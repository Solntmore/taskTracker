package task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private HashMap<Integer, Subtask> subtaskMap = new HashMap<>();

    private LocalDateTime endTime;

    public Epic(String name, String description, int mainTaskId, Status status, LocalDateTime startTime, Duration duration) {
        super(name, description, mainTaskId, status, startTime, duration);
    }

    @Override
    public String toCsvString() {
        return getMainTaskId() + "," + "EPIC," + getName() + ","
                + getStatus() + "," + getDescription() + "," + getStartTime() + "," + getDuration() + "\n";
    }

    @Override
    public LocalDateTime getStartTime() {
        if (!subtaskMap.isEmpty()) {
            for (Subtask subtask : subtaskMap.values()) {
                if (startTime == null) {
                    startTime = subtask.getStartTime();
                } else {
                    if (startTime.isAfter(subtask.getStartTime())) {
                        startTime = subtask.getStartTime();
                    }
                }
            }
            return startTime;
        } else {
            return startTime;
        }
    }

    @Override
    public Duration getDuration() {
        if (startTime != null && endTime != null) {
            return Duration.between(endTime, startTime);
        }
        return null;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (!subtaskMap.isEmpty()) {
            for (Subtask subtask : subtaskMap.values()) {
                if (endTime == null) {
                    return endTime = subtask.getStartTime().plus(subtask.getDuration());
                } else {
                    if (endTime.isBefore(subtask.getEndTime()))
                        return endTime = subtask.getStartTime().plus(subtask.getDuration());
                }
            }
        }
        return null;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Epic)) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(getSubtaskMap(), epic.getSubtaskMap());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getSubtaskMap());
    }
}