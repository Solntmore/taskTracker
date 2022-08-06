package managers;

import task.Subtask;
import task.Task;

import javax.naming.directory.InvalidAttributesException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;

public class Validator {
    public static void validator(LocalDateTime startTime, Duration duration, HashMap<Integer, Task> taskMap,
                          HashMap<Integer, Subtask> subtaskMap) throws InvalidAttributesException {

        LocalDateTime endTime = startTime.plus(duration);
        int counter = 0;
        if (!taskMap.isEmpty()) {
            for (Task task : taskMap.values()) {
                if (startTime.isBefore(task.getStartTime()) && endTime.isBefore(task.getStartTime()))
                    counter++;
                if (startTime.isAfter(task.getEndTime()) && endTime.isAfter(task.getEndTime()))
                    counter++;
            }
        }

        if (!subtaskMap.isEmpty()) {
            for (Subtask subtask : subtaskMap.values()) {
                if (startTime.isBefore(subtask.getStartTime()) && endTime.isBefore(subtask.getStartTime()))
                    counter++;
                if (startTime.isAfter(subtask.getEndTime()) && endTime.isAfter(subtask.getEndTime()))
                    counter++;
            }
        }
        if (counter == taskMap.size() + subtaskMap.size())
            return;
        if(taskMap.isEmpty() && subtaskMap.isEmpty())
            return;

        throw new InvalidAttributesException();
    }
}
