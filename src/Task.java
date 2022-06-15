import java.util.Objects;

public class Task implements Tasks {
    protected String name;
    protected String description;
    protected int mainTaskId;
    protected Status status;

    public Task(String name, String description, int mainTaskId, Status status) {
        this.name = name;
        this.description = description;
        this.mainTaskId = mainTaskId;
        this.status = status;
    }

    public enum Status {
        NEW, IN_PROGRESS, DONE
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

    @Override
    public Task.Status getStatus() {
        return status;
    }

    @Override
    public void setStatus(Task.Status status) {
        this.status = status;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getMainTaskId() {
        return mainTaskId;
    }

    @Override
    public void setMainTaskId(int mainTaskId) {
        this.mainTaskId = mainTaskId;
    }
}

