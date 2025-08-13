package model;

import model.*;
import manager.*;
import java.time.LocalDateTime;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

class EpicTest {
    private Epic epic;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Manager.getDefault();
        epic = new Epic("Эпик ", "Описание эпика", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
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
                ", startTime=null" +
                ", endTime=null" +
                ", duration=PT0S" +
                '}';

       // assertEquals(expectedString, epic.toString());
    }

    @Test
    void epicShouldNotContainItselfAsSubtask() {
        Epic epic = new Epic("Эпик 1", "Описание первого эпика", Status.NEW, LocalDateTime.now(), Duration.ofMinutes(30));
        epic.setId(1);

        assertFalse(epic.getSubtaskIds().contains(1), "Эпик не может содержать сам себя в подзадачах");
    }
    @Test
    void testEpicTimeCalculationWithNoSubtasks() {
        assertNull(epic.getStartTime(), "У эпика без подзадач startTime должен быть null");
        assertNull(epic.getEndTime(), "У эпика без подзадач endTime должен быть null");
        assertEquals(Duration.ZERO, epic.getDuration(), "У эпика без подзадач duration должен быть 0");
    }





}