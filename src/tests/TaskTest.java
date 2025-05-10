package tests;

import model.Status;
import model.Task;
import model.Epic;
import model.Subtask;



import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksWithSameIdShouldBeEqual() {
        Task task1 = new Task("Задача 1", "Описание первой задачи", Status.NEW);
        Task task2 = new Task("Задача 2", "Описание второй задачи", Status.NEW);
        task1.setId(1);
        task2.setId(1);

        assertEquals(task1, task2, "Задачи с одинаковым ID должны быть равны");
    }

    @Test
    void epicAndSubtaskWithSameIdShouldBeEqual() {
        Epic epic = new Epic("Эпик 1", "Описание первого эпика", Status.NEW);
        Subtask subtask = new Subtask("Подзадача 1", "Описание подзадачи №1", Status.NEW, epic.getId());
        epic.setId(2);
        subtask.setId(2);

        assertEquals(epic, subtask, "Объекты Epic и Subtask с одинаковым ID должны быть равны");
    }

    @Test
    void epicShouldNotContainItselfAsSubtask() {
        Epic epic = new Epic("Эпик 1", "Описание первого эпика", Status.NEW);
        epic.setId(1);

        assertFalse(epic.getSubtaskIds().contains(1), "Эпик не может содержать сам себя в подзадачах");
    }

    @Test
    void subtaskShouldNotBeItsOwnEpic() {
        Epic epic = new Epic("Эпик 2", "Описание второго эпика", Status.NEW);
        Subtask subtask = new Subtask("Подзадача 2", "Описание подзадачи №2", Status.NEW, epic.getId());
        subtask.setId(2);

        assertNotEquals(subtask.getId(), subtask.getEpicId(), "Подзадача не должна быть своим же эпиком");
    }

}