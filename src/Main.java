import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();
        System.out.println("Поехали!");
        Scanner scanner = new Scanner(System.in);

        label:
        while (true) {

            printMenu();

            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    System.out.println(manager.showAllTasks());
                    System.out.println(manager.showAllEpic());
                    System.out.println(manager.showAllSubtasks());
                    break;

                case "2":
                    System.out.println(manager.deleteAllTasks());
                    System.out.println(manager.deleteAllEpics());
                    System.out.println(manager.deleteAllSubtasks());
                    break;

                case "3":
                    System.out.println(manager.showTaskById(1));
                    System.out.println(manager.showEpicById(3));
                    System.out.println(manager.showSubtaskById(1));
                    break;

                case "4":
                    Task task = new Task("Задача-1", "описание", 0, Manager.Status.NEW);
                    manager.createTask(task);
                    task = new Task("Задача-2", "описание", 0, Manager.Status.NEW);
                    manager.createTask(task);
                    Epic epic = new Epic("Эпик-1", "описание", 0, Manager.Status.NEW);
                    manager.createEpic(epic);
                    Subtask subtask = new Subtask("Подзадача эпика-1", "описание", 3, Manager.Status.NEW, 0);
                    manager.createSubtask(subtask);
                    subtask = new Subtask("Подзадача эпика-2", "описание", 3, Manager.Status.NEW, 0);
                    manager.createSubtask(subtask);
                    break;

                case "5":
                    System.out.println("Вы хотите удалить: 1 - обычную задачу, 2 - эпик, 3 - подзадачу.");
                    String userInput = scanner.nextLine();
                    if (userInput.equals("1")) {
                        manager.deleteTaskById(1);
                    } else if (userInput.equals("2")) {
                        manager.deleteEpicById(3);
                    } else if (userInput.equals("3")) {
                        manager.deleteSubtaskById(4);
                    } else {
                        System.out.println("Такой команды нет.");
                    }
                    break;

                case "6":
                    System.out.println(manager.showSubtasksByEpicId(3));
                    break;

                case "7":
                    manager.updateTask(1, "1");
                    manager.updateSubtask(4, "1");
                    manager.updateSubtask(5, "1");
                    break;

                case "0":

                    break label;


                default:

                    System.out.println("Такой команды нет, попробуйте еще раз.\n");
                    break;
            }

        }

    }

    private static void printMenu() {

        System.out.println("Какое действие вы хотите выполнить?\n");
        System.out.println("1 – Получить список всех задач.");
        System.out.println("2 – Удалить все задачи.");
        System.out.println("3 – Получить информацию по идентификатору.");
        System.out.println("4 – Создать задачу.");
        System.out.println("5 – Удалить информацию по идентификатору.");
        System.out.println("6 – Получение списка всех подзадач определённого эпика.");
        System.out.println("7 – Обновление статуса задачи.");
        System.out.println("0 – Завершить работу.\n");

    }

}
