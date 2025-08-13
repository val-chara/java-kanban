package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import manager.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();

        task = new Task("Задача 1", "Описание задачи", Status.NEW, LocalDateTime.of(2025, 7, 18, 10, 0), Duration.ofMinutes(90));
        task.setId(1);

        task1 = new Task("Задача 1", "Описание задачи t1", Status.NEW, LocalDateTime.of(2025, 7, 19, 11, 11), Duration.ofMinutes(90));
        task1.setId(1);

        task2 = new Task("Задача 2", "Описание задачи t2", Status.NEW, LocalDateTime.of(2025, 7, 20, 20, 22), Duration.ofMinutes(90));
        task2.setId(2);

        task3 = new Task("Задача 3", "Описание задачи t3", Status.NEW, LocalDateTime.of(2025, 7, 21, 21, 22), Duration.ofMinutes(90));
        task3.setId(3);
    }

    @Test
    void shouldAddTaskToHistory() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertNotNull(history, "История не может быть пустой");
        assertEquals(1, history.size(), "История должна содержать одну задачу");
        assertEquals(task, history.get(0), "Задача в истории должна совпадать с добавленной");
    }

    @Test
    void shouldPreservePreviousTaskDataInHistory() {
        task.setDescription("Обновленное описание");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertEquals("Обновленное описание", history.get(0).getDescription(),
                "В истории должны сохранятся обновленные задачи");
    }

    @Test
    void addAndGetHistory_simple() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> h = historyManager.getHistory();

        assertEquals(List.of(task1, task2), h);
    }

    @Test
    void addDuplicate_movesToEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> h = historyManager.getHistory();
        assertEquals(List.of(task2, task1), h, "t1 должен переместиться в конец");
    }

    @Test
    void remove_existingNode_removesCorrectly() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);
        List<Task> h = historyManager.getHistory();

        assertEquals(List.of(task1, task3), h);
        assertFalse(h.contains(task2));
    }

    @Test
    void remove_nonExisting_doesNothing() {
        historyManager.add(task1);
        historyManager.remove(99);
        assertEquals(List.of(task1), historyManager.getHistory());
    }

    @Test
    void history_withNullTask_doesNotAdd() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }
}