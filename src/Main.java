import manager.TaskManager;
import model.Status;
import model.Epic;
import model.Subtask;
import model.Task;


public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        // Создаём обычные задачи
        taskManager.createTask(new Task("Задача 1", "Описание первой задачи", Status.NEW));
        taskManager.createTask(new Task("Задача 2", "Описание второй задачи", Status.IN_PROGRESS));
        taskManager.createTask(new Task("Задача 3", "Описание третьей задачи", Status.DONE));

        // Создаём эпики
        Epic epic1 = new Epic("Эпик 1", "Описание первого эпика", Status.NEW);
        taskManager.createEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "Описание второго эпика", Status.NEW);
        taskManager.createEpic(epic2);

        // Раскрываем эпики на подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи №1", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        int subtask1_id = subtask1.getId();

        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи №1", Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask2);
        int subtask2_id = subtask2.getId();


        System.out.println("Обычные задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getTitle() + ": " + task.getStatus());
        }

        System.out.println("Эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getTitle() + ": " + epic.getStatus());
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask.getTitle() + ": " + subtask.getStatus());
        }

        //taskManager.getTaskById(subtask1_id).setStatus(Status.DONE);
        taskManager.getSubtaskById(subtask1_id).setStatus(Status.DONE);

        System.out.println("Обновленные статусы задач:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getTitle() + ": " + task.getStatus());
        }

        //taskManager.deleteTaskById(epic1.getId());
        taskManager.deleteEpicById(epic1.getId());

        System.out.println("Незакрытые задачи:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getTitle() + ": " + task.getStatus());
        }

        System.out.println("Незакрытые эпики:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getTitle() + ": " + epic.getStatus());
        }
    }
}
