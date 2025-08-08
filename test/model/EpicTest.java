package model;

import model.*;
import manager.*;
import java.time.LocalDateTime;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

class EpicTest {
    private Epic epic;
    private TaskManager inMemoryTaskManager;

    @BeforeEach
    void setUp() {
        inMemoryTaskManager = Manager.getDefault();

        epic = new Epic("Эпик ", "Описание эпика", Status.NEW, LocalDateTime.of(2025, 7, 18, 10, 0),
                Duration.ofMinutes(0));
        epic.setId(100);

        inMemoryTaskManager.createEpic(epic);
    }

    private void addSubtaskToManager(Subtask subtask) {
        inMemoryTaskManager.createSubtask(subtask);
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
    void epicShouldNotContainItselfAsSubtask() {
        Epic epic = new Epic("Эпик 1", "Описание первого эпика", Status.NEW, LocalDateTime.of(2025, 7, 18, 10, 0),
                Duration.ofMinutes(0));
        epic.setId(1);

        assertFalse(epic.getSubtaskIds().contains(1), "Эпик не может содержать сам себя в подзадачах");
    }

    @Test
    void epicStartTimeShouldBeEarliestOfSubtasks() {
        Epic epic = inMemoryTaskManager.createEpic(new Epic("Epic", "desc", Status.NEW, null, null));

        Subtask sub1 = new Subtask("Подзадача 1", "desc", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 18, 9, 0), Duration.ofMinutes(30));
        Subtask sub2 = new Subtask("Подзадача 2", "desc", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 18, 11, 0), Duration.ofMinutes(60));

        inMemoryTaskManager.createSubtask(sub1);
        inMemoryTaskManager.createSubtask(sub2);

        Epic updatedEpic = inMemoryTaskManager.getEpicById(epic.getId());

        assertEquals(LocalDateTime.of(2025, 7, 18, 9, 0), updatedEpic.getStartTime());
    }

    @Test
    void epicEndTimeShouldBeLatestOfSubtasks() {
        Epic epic = inMemoryTaskManager.createEpic(new Epic("Epic", "desc", Status.NEW, null, null));

        Subtask sub1 = new Subtask("Подзадача 1", "desc", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 18, 9, 0), Duration.ofMinutes(30));
        Subtask sub2 = new Subtask("Подзадача 2", "desc", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 18, 11, 0), Duration.ofMinutes(90));

        inMemoryTaskManager.createSubtask(sub1);
        inMemoryTaskManager.createSubtask(sub2);

        Epic updatedEpic = inMemoryTaskManager.getEpicById(epic.getId());

        assertEquals(LocalDateTime.of(2025, 7, 18, 12, 30), updatedEpic.getEndTime());
    }

    @Test
    void epicDurationShouldBeSumOfSubtaskDurations() {
        Epic epic = inMemoryTaskManager.createEpic(new Epic("Epic", "desc", Status.NEW, null, null));

        Subtask sub1 = new Subtask("Подзадача 1", "desc", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 18, 9, 0), Duration.ofMinutes(40));
        Subtask sub2 = new Subtask("Подзадача 2", "desc", Status.NEW, epic.getId(),
                LocalDateTime.of(2025, 7, 18, 10, 0), Duration.ofMinutes(50));

        inMemoryTaskManager.createSubtask(sub1);
        inMemoryTaskManager.createSubtask(sub2);

        Epic updatedEpic = inMemoryTaskManager.getEpicById(epic.getId());

        assertEquals(Duration.ofMinutes(90), updatedEpic.getDuration());
    }

}


