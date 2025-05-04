public class Main {
    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        // Создаём обычные задачи
        taskManager.createTask("Задача 1", "Описание первой задачи", Status.NEW);
        taskManager.createTask("Задача 2", "Описание второй задачи", Status.IN_PROGRESS);
        taskManager.createTask("Задача 3", "Описание третьей задачи", Status.DONE);

        // Создаём эпики
        int epicId1 = taskManager.createEpic("Эпик 1", "Описание первого эпика", Status.NEW);
        int epicId2 = taskManager.createEpic("Эпик 2", "Описание второго эпика", Status.NEW);
        int epicId3 = taskManager.createEpic("Эпик 3", "Описание третьего эпика", Status.NEW);
        int epicId4 = taskManager.createEpic("Эпик 4", "Описание четвертого эпика", Status.NEW);

        // Раскрываем эпики на подзадачи
        taskManager.createSubtask("Подзадача 1", "Описание подзадачи №1", Status.NEW, epicId1);
        taskManager.createSubtask("Подзадача 2", "Описание подзадачи №2", Status.IN_PROGRESS, epicId1);
        taskManager.createSubtask("Подзадача 3", "Описание подзадачи №3", Status.DONE, epicId2);
        taskManager.createSubtask("Подзадача 4", "Описание подзадачи №4", Status.NEW, epicId3);
        taskManager.createSubtask("Подзадача 5", "Описание подзадачи №5", Status.IN_PROGRESS, epicId4);
        taskManager.createSubtask("Подзадача 6", "Описание подзадачи №6", Status.NEW, epicId4);

        // Печать списка задач
        taskManager.printAllTasks();

        // Обновляем статус
        taskManager.updateEpicStatus(epicId1);
        taskManager.updateEpicStatus(epicId2);
        taskManager.updateEpicStatus(epicId3);
        taskManager.updateEpicStatus(epicId4);

        System.out.println("Обновленный список:");
        taskManager.printAllTasks();
    }
}
