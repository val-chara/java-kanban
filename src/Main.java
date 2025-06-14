import manager.*;
import model.*;

public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = Manager.getDefault();

        // Создаём обычные задачи

        Task task1 = new Task("Задача 1", "Описание первой задачи", Status.NEW);
        taskManager.createTask(task1);
        int task1Id = task1.getId();

        Task task2 = new Task("Задача 2", "Описание второй задачи", Status.IN_PROGRESS);
        taskManager.createTask(task2);
        int task2Id = task1.getId();

        Task task3 = new Task("Задача 3", "Описание третьей задачи", Status.DONE);
        taskManager.createTask(task3);
        int task3Id = task1.getId();

        // Создаём эпики
        Epic epic1 = new Epic("Эпик 1", "Описание первого эпика", Status.NEW);
        taskManager.createEpic(epic1);

        Epic epic2 = new Epic("Эпик 2", "Описание второго эпика", Status.NEW);
        taskManager.createEpic(epic2);

        // Раскрываем эпики на подзадачи
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи №1", Status.NEW, epic1.getId());
        taskManager.createSubtask(subtask1);
        int subtask1id = subtask1.getId();

        Subtask subtask2 = new Subtask("Подзадача 1", "Описание подзадачи №1", Status.NEW, epic2.getId());
        taskManager.createSubtask(subtask2);
        int subtask2id = subtask2.getId();


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
        taskManager.getSubtaskById(subtask1id).setStatus(Status.DONE);

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

        taskManager.deleteTaskById(task1.getId());
        System.out.println("Задачи после удаления одной:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getTitle() + ": " + task.getStatus());
        }

        taskManager.deleteSubtaskById(subtask1id);
        System.out.println("Подзадачи после удаления одной:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask.getTitle() + ": " + subtask.getStatus());
        }

        System.out.println("Эпики после удаления подзадачи:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getTitle() + ": " + epic.getStatus());
        }

        taskManager.deleteEpicById(epic1.getId());
        System.out.println("Эпики после удаления одного эпика:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getTitle() + ": " + epic.getStatus());
        }

        System.out.println("Подзадачи после удаления эпика:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask.getTitle() + ": " + subtask.getStatus());
        }

        taskManager.deleteAllTasks();
        System.out.println("Все задачи после полной очистки:");
        for (Task task : taskManager.getAllTasks()) {
            System.out.println(task.getTitle() + ": " + task.getStatus());
        }

        taskManager.deleteAllSubtasks();
        System.out.println("Все подзадачи после полной очистки:");
        for (Subtask subtask : taskManager.getAllSubtasks()) {
            System.out.println(subtask.getTitle() + ": " + subtask.getStatus());
        }

        System.out.println("Эпики после удаления всех подзадач:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getTitle() + ": " + epic.getStatus());
        }

        taskManager.deleteAllEpics();
        System.out.println("Все эпики после полной очистки:");
        for (Epic epic : taskManager.getAllEpics()) {
            System.out.println(epic.getTitle() + ": " + epic.getStatus());
        }

        System.out.println("Финальная история просмотров:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task.getTitle() + ": " + task.getStatus());
        }
    }
}
