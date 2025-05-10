package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;
import manager.*;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Задача 1", "Описание первой задачи", Status.NEW);
        task.setId(1);
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
}