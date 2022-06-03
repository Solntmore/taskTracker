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
                    System.out.println("Введите ID задачи: ");
                    String userInput = scanner.nextLine();
                    System.out.println(manager.showTaskById(userInput));
                    System.out.println(manager.showEpicById(userInput));
                    System.out.println(manager.showSubtaskById(userInput));
                    break;
                case "4":
                    System.out.println("Введите название задачи:");
                    String name = scanner.nextLine();

                    System.out.println("Введите описание задачи:");
                    String description = scanner.nextLine();

                    System.out.println("1 - обычная задача; 2 - эпик с подзадачами.");
                    String number = scanner.nextLine();

                    if (number.equals("1")) {
                        System.out.println(manager.createTask(name, description));
                    } else if (number.equals("2")) {
                        System.out.println(manager.createEpic(name, description));
                        while (true) {
                            System.out.println("1 - добавить подзадачу, 2 - в главное меню");
                            userInput = scanner.nextLine();
                            if (userInput.equals("1")) {
                                System.out.println("Введите название задачи:");
                                String subTaskName = scanner.nextLine();

                                System.out.println("Введите описание задачи:");
                                String subTaskDescription = scanner.nextLine();

                                System.out.println(manager.createSubtask(subTaskName, subTaskDescription));
                            } else if(userInput.equals("2")) {
                                break;
                            } else {
                                System.out.println("Такой команды нет, попробуйте еще раз.");
                            }
                        }
                    } else {
                        System.out.println("Такой команды нет, попробуйте еще раз.");
                    }
                    break;
                case "5":
                    System.out.println("Вы хотите удалить: 1 - обычную задачу, 2 - эпик, 3.");
                    userInput = scanner.nextLine();
                    if (userInput.equals("1")) {
                        System.out.println("Введите ID:");
                        String id = scanner.nextLine();
                        manager.deleteTaskById(id);

                    } else if (userInput.equals("2")) {
                        System.out.println("Введите ID:");
                        String id = scanner.nextLine();
                        manager.deleteEpicById(id);

                    } else {
                        System.out.println("Такой команды нет.");
                    }
                    break;
                case "6":
                    System.out.println("Введите ID задачи: ");
                    userInput = scanner.nextLine();
                    manager.showSubtasksByEpicId(userInput);
                    break;
                case "7":
                    System.out.println("Вы хотите обновить статус: 1 - задачи, любая команда - подзадачи?");
                    userInput = scanner.nextLine();
                    if(userInput.equals("1")) {
                        System.out.println("Введите ID задачи: ");
                        userInput = scanner.nextLine();
                        System.out.println("Статус: любая команда - STATUS_IN_PROGRESS, 1 - STATUS_DONE");
                        String status = scanner.nextLine();
                        manager.updateTask(userInput, status);
                    } else {
                        System.out.println("Введите ID задачи: ");
                        userInput = scanner.nextLine();
                        System.out.println("Статус: любая команда - STATUS_IN_PROGRESS, 1 - STATUS_DONE");
                        String status = scanner.nextLine();
                        manager.updateSubtask(userInput, status);
                    }
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
