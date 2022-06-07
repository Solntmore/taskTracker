import java.util.HashMap;

class Epic extends Task {
    /*При работе с ArrayList столкнулся с проблемой, что не могу положить сабтаски к нужные ячейки(по их id),
     отсюда вытекало ряд проблем, по дальнейшему доставанию/удалению/обновлению, поэтому заменил ArrayList на HashMap.*/
    public HashMap<Integer, Subtask> subtaskMap;

    public Epic(String name, String description, int mainTaskId, Status status, HashMap<Integer, Subtask> subtaskMap) {
        super(name, description, mainTaskId, status);
        this.subtaskMap = subtaskMap;
    }
}