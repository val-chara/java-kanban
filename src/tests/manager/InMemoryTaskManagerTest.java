package tests.manager;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import manager.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void shouldAddAndFindTaskById() {
        Task task = new Task("Задача 1", "Описание первой задачи", Status.NEW);
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertNotNull(retrievedTask, "Задача должна существовать");
        assertEquals(task, retrievedTask, "Задача после добавления и извлечения должна оставаться неизменной");
    }

    @Test
    void shouldNotConflictWithGeneratedAndManualId() {
        Task task1 = new Task("Задача 1", "Описание первой задачи", Status.NEW);
        task1.setId(5);
        taskManager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание второй задачи", Status.NEW);
        taskManager.createTask(task2);

        assertNotEquals(5, task2.getId(), "Сгенерированный ID не должен конфликтовать с установленным вручную");
    }

    @Test
    void shouldPreserveTaskDataWhenAdded() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW);
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertEquals("Задача", retrievedTask.getTitle());
        assertEquals("Описание задачи", retrievedTask.getDescription());
    }

}