package test.model;

import model.Status;
import model.Task;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Задача 1", "Описание первой задачи", Status.NEW);
        Task task2 = new Task("Задача 1", "Описание первой задачи", Status.NEW);
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

}