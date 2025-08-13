package model;

import model.Status;
import model.Task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;

class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Задача 1", "Описание первой задачи", Status.NEW, LocalDateTime.of(2025, 7, 18, 10, 0), Duration.ofMinutes(90));
        Task task2 = new Task("Задача 1", "Описание первой задачи", Status.NEW, LocalDateTime.of(2025, 7, 19, 11, 0), Duration.ofMinutes(90));
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

}