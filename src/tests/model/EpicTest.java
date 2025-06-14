package tests.model;

import model.Epic;
import model.Status;

import static org.junit.jupiter.api.Assertions.*;

import model.Subtask;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

class EpicTest {
    private Epic epic;

    @BeforeEach
    void setUp() {
        epic = new Epic("Эпик ", "Описание эпика", Status.NEW);
    }

    @Test
    void testAddSubtask() {
        epic.addSubtask(1);
        epic.addSubtask(2);

        List<Integer> subtasks = epic.getSubtaskIds();
        assertEquals(2, subtasks.size());
        assertTrue(subtasks.contains(1));
        assertTrue(subtasks.contains(2));
    }

    @Test
    void testRemoveSubtask() {
        epic.addSubtask(1);
        epic.addSubtask(2);
        epic.removeSubtask(1);

        List<Integer> subtasks = epic.getSubtaskIds();
        assertEquals(1, subtasks.size());
        assertFalse(subtasks.contains(1));
        assertTrue(subtasks.contains(2));
    }

    @Test
    void testClearSubtasks() {
        epic.addSubtask(1);
        epic.addSubtask(2);
        epic.clearSubtasks();

        List<Integer> subtasks = epic.getSubtaskIds();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void testToString() {
        epic.addSubtask(1);
        epic.addSubtask(2);

        String expectedString = "Epic{" +
                "id=" + epic.getId() +
                ", title='" + epic.getTitle() + '\'' +
                ", description='" + epic.getDescription() + '\'' +
                ", status=" + epic.getStatus() +
                ", subtasks=[1, 2]" +
                '}';
        assertEquals(expectedString, epic.toString());
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
}


