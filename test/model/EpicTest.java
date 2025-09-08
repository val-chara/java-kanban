package model;

import exception.TimeConflictException;
import manager.*;
import java.time.LocalDateTime;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.List;

class EpicTest {
    private Epic epic;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Manager.getDefault();
        epic = new Epic("Эпик", "Описание эпика", Status.NEW);
        taskManager.createEpic(epic);
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

        String result = epic.toString();
        assertTrue(result.contains("id=" + epic.getId()));
        assertTrue(result.contains("title='" + epic.getTitle() + "'"));
        assertTrue(result.contains("subtasks=[1, 2]"));

    }

    @Test
    void epicShouldNotContainItselfAsSubtask() {
        Epic epic = new Epic("Эпик 1", "Описание первого эпика", Status.NEW);
        epic.setId(1);

        assertFalse(epic.getSubtaskIds().contains(1), "Эпик не может содержать сам себя в подзадачах");
    }

    @Test
    void testEpicTimeCalculationWithNoSubtasks() {
        assertNull(epic.getStartTime(), "У эпика без подзадач startTime должен быть null");
        assertNull(epic.getEndTime(), "У эпика без подзадач endTime должен быть null");
        assertNull(epic.getDuration(), "У эпика без подзадач duration должен быть null");
    }

    @Test
    void testEpicDurationCalculationThroughManager() {
        Epic testEpic = new Epic("Test", "Desc", Status.NEW);
        taskManager.createEpic(testEpic);

        LocalDateTime now = LocalDateTime.of(2024, 1, 1, 10, 0);

        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, testEpic.getId(),
                now, Duration.ofHours(3));
        Subtask subtask2 = new Subtask("Sub2", "Desc", Status.NEW, testEpic.getId(),
                now.plusHours(3), Duration.ofHours(2));

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        Epic updatedEpic = taskManager.getEpicById(testEpic.getId());

        assertEquals(Duration.ofHours(5), updatedEpic.getDuration());
    }

    @Test
    void testEpicStatusUpdateThroughManager() {
        Epic testEpic = new Epic("Test", "Desc", Status.NEW);
        taskManager.createEpic(testEpic);

        Subtask subtask = new Subtask("Sub", "Desc", Status.NEW, testEpic.getId(),
                LocalDateTime.now(), Duration.ofHours(1));

        taskManager.createSubtask(subtask);

        Subtask existingSubtask = taskManager.getSubtaskById(subtask.getId());
        existingSubtask.setStatus(Status.DONE);
        taskManager.updateSubtask(existingSubtask);

        Epic updatedEpic = taskManager.getEpicById(testEpic.getId());
        assertEquals(Status.DONE, updatedEpic.getStatus());
    }

    @Test
    void testNoTimeOverlap_ValidBefore() {
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Task1", "Desc", Status.NEW, baseTime, Duration.ofHours(2));
        Task task2 = new Task("Task2", "Desc", Status.NEW, baseTime.plusHours(3), Duration.ofHours(2));

        taskManager.createTask(task1);
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }

    @Test
    void testNoTimeOverlap_ValidAfter() {

        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Task1", "Desc", Status.NEW, baseTime.plusHours(5), Duration.ofHours(2));
        Task task2 = new Task("Task2", "Desc", Status.NEW, baseTime, Duration.ofHours(2));

        taskManager.createTask(task1);
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }

    @Test
    void testTimeOverlap_ExactSameTime() {

        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Task1", "Desc", Status.NEW, baseTime, Duration.ofHours(2));
        Task task2 = new Task("Task2", "Desc", Status.NEW, baseTime, Duration.ofHours(2));

        taskManager.createTask(task1);
        assertThrows(TimeConflictException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void testTimeOverlap_StartInsideExisting() {

        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Task1", "Desc", Status.NEW, baseTime, Duration.ofHours(4));
        Task task2 = new Task("Task2", "Desc", Status.NEW, baseTime.plusHours(2), Duration.ofHours(2));

        taskManager.createTask(task1);
        assertThrows(TimeConflictException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void testTimeOverlap_EndInsideExisting() {

        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Task1", "Desc", Status.NEW, baseTime.plusHours(2), Duration.ofHours(4));
        Task task2 = new Task("Task2", "Desc", Status.NEW, baseTime, Duration.ofHours(3));

        taskManager.createTask(task1);
        assertThrows(TimeConflictException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void testTimeOverlap_ExistingInsideNew() {

        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Task1", "Desc", Status.NEW, baseTime.plusHours(2), Duration.ofHours(2));
        Task task2 = new Task("Task2", "Desc", Status.NEW, baseTime, Duration.ofHours(6));

        taskManager.createTask(task1);
        assertThrows(TimeConflictException.class, () -> taskManager.createTask(task2));
    }

    @Test
    void testTimeOverlap_EdgeCase_StartAtEnd() {

        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Task1", "Desc", Status.NEW, baseTime, Duration.ofHours(2));
        Task task2 = new Task("Task2", "Desc", Status.NEW, baseTime.plusHours(2), Duration.ofHours(2));

        taskManager.createTask(task1);
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }

    @Test
    void testTimeOverlap_EdgeCase_EndAtStart() {
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);
        Task task1 = new Task("Task1", "Desc", Status.NEW, baseTime.plusHours(2), Duration.ofHours(2));
        Task task2 = new Task("Task2", "Desc", Status.NEW, baseTime, Duration.ofHours(2));

        taskManager.createTask(task1);
        assertDoesNotThrow(() -> taskManager.createTask(task2));
    }

    @Test
    void testSubtaskTimeOverlapWithOtherSubtasks() {
        Epic testEpic = new Epic("Test", "Desc", Status.NEW);
        taskManager.createEpic(testEpic);

        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);

        Subtask subtask1 = new Subtask("Sub1", "Desc", Status.NEW, testEpic.getId(),
                baseTime, Duration.ofHours(2));
        taskManager.createSubtask(subtask1);

        Subtask overlappingSubtask = new Subtask("Sub2", "Desc", Status.NEW, testEpic.getId(),
                baseTime.plusHours(1), Duration.ofHours(2));

        assertThrows(TimeConflictException.class, () -> taskManager.createSubtask(overlappingSubtask));
    }

    @Test
    void testSubtaskTimeOverlapWithTasks() {
        LocalDateTime baseTime = LocalDateTime.of(2024, 1, 1, 10, 0);

        Task task = new Task("Task", "Desc", Status.NEW, baseTime, Duration.ofHours(2));
        taskManager.createTask(task);

        Epic testEpic = new Epic("Test", "Desc", Status.NEW);
        taskManager.createEpic(testEpic);

        Subtask overlappingSubtask = new Subtask("Sub", "Desc", Status.NEW, testEpic.getId(),
                baseTime.plusHours(1), Duration.ofHours(2));

        assertThrows(TimeConflictException.class, () -> taskManager.createSubtask(overlappingSubtask));
    }
}