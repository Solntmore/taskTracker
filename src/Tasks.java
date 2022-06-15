import java.util.Objects;

public interface Tasks {

    public Task.Status getStatus();

    public void setStatus(Task.Status status);

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public int getMainTaskId();

    public void setMainTaskId(int mainTaskId);
}
