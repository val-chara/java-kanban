package tests.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import manager.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task, t1, t2, t3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Задача 1", "Описание первой задачи", Status.NEW);
        task.setId(1);

        t1 = new Task("Задача 1", "Описание задачи t1", Status.NEW);
        t1.setId(1);
        t2 = new Task("Задача 2", "Описание задачи t2", Status.NEW);
        t2.setId(2);
        t3 = new Task("Задача 3", "Описание задачи t3", Status.NEW);
        t3.setId(3);
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
        historyManager.add(t1);
        historyManager.add(t2);
        List<Task> h = historyManager.getHistory();

        assertEquals(List.of(t1, t2), h);
    }

    @Test
    void addDuplicate_movesToEnd() {
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t1); // повтор просмотра t1

        List<Task> h = historyManager.getHistory();
        assertEquals(List.of(t2, t1), h, "t1 должен переместиться в конец");
    }

    @Test
    void remove_existingNode_removesCorrectly() {
        historyManager.add(t1);
        historyManager.add(t2);
        historyManager.add(t3);

        historyManager.remove(2);
        List<Task> h = historyManager.getHistory();

        assertEquals(List.of(t1, t3), h);
        assertFalse(h.contains(t2));
    }

    @Test
    void remove_nonExisting_doesNothing() {
        historyManager.add(t1);
        historyManager.remove(99); // нет задачи с id
        assertEquals(List.of(t1), historyManager.getHistory());
    }

    @Test
    void history_withNullTask_doesNotAdd() {
        historyManager.add(null);
        assertTrue(historyManager.getHistory().isEmpty());
    }
}