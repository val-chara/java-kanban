package manager;

import manager.InMemoryTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

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
        Task task = new Task("Задача 1", "Описание первой задачи", Status.NEW, LocalDateTime.of(2025, 7, 18, 10, 0), Duration.ofMinutes(90));
        task.setId(1);
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertNotNull(retrievedTask, "Задача должна существовать");
        assertEquals(task, retrievedTask, "Задача после добавления и извлечения должна оставаться неизменной");
    }

    @Test
    void shouldNotConflictWithGeneratedAndManualId() {
        Task task1 = new Task("Задача 1", "Описание задачи", Status.NEW, LocalDateTime.of(2025, 7, 18, 10, 0), Duration.ofMinutes(90));
        taskManager.createTask(task1);

        Task task2 = new Task("Задача 2", "Описание второй задачи", Status.NEW, LocalDateTime.of(2025, 7, 19, 10, 10), Duration.ofMinutes(45));
        taskManager.createTask(task2);

        assertNotEquals(task1.getId(),task2.getId(),"Сгенерированный ID не должен конфликтовать с установленным вручную");

    }

    @Test
    void shouldPreserveTaskDataWhenAdded() {
        Task task = new Task("Задача", "Описание задачи", Status.NEW, LocalDateTime.of(2025, 7, 18, 10, 0), Duration.ofMinutes(90));
        task.setId(1);
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertEquals("Задача", retrievedTask.getTitle());
        assertEquals("Описание задачи", retrievedTask.getDescription());
    }

    @Test
    void shouldReturnTasksSortedByStartTime() {
        Task task1 = new Task("Task 1", "desc", Status.NEW,
                LocalDateTime.of(2025, 1, 1, 12, 0), Duration.ofHours(1));
        Task task2 = new Task("Task 2", "desc", Status.NEW,
                LocalDateTime.of(2025, 1, 1, 10, 0), Duration.ofHours(1));

        taskManager.createTask(task1);
        taskManager.createTask(task2);

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(task2, prioritized.get(0)); // task2 должен быть первым (раньше по времени)
    }
}