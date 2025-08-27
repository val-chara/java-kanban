package manager;

import model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.time.Duration;
import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {

        protected T manager;

        void setUp() throws IOException {
            manager = createManager();
        }

        protected abstract T createManager();

        @Test
        void testEpicStatus_AllNew() {
            manager = createManager();
            Epic epic = manager.createEpic(new Epic("Epic1", "Desc", Status.NEW));
            manager.createSubtask(new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));
            manager.createSubtask(new Subtask("Sub2", "Desc", Status.NEW, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 12, 0), Duration.ofMinutes(60)));

            assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
        }

        @Test
        void testEpicStatus_AllDone() {
            manager = createManager();
            Epic epic = manager.createEpic(new Epic("Epic2", "Desc", Status.NEW));
            manager.createSubtask(new Subtask("Sub1", "Desc", Status.DONE, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));
            manager.createSubtask(new Subtask("Sub2", "Desc", Status.DONE, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 12, 0), Duration.ofMinutes(60)));

            assertEquals(Status.DONE, manager.getEpicById(epic.getId()).getStatus());
        }

        @Test
        void testEpicStatus_NewAndDone() {
            manager = createManager();
            Epic epic = manager.createEpic(new Epic("Epic3", "Desc", Status.NEW));
            manager.createSubtask(new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));
            manager.createSubtask(new Subtask("Sub2", "Desc", Status.DONE, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 12, 0), Duration.ofMinutes(60)));

            assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
        }

        @Test
        void testEpicStatus_InProgress() {
            manager = createManager();
            Epic epic = manager.createEpic(new Epic("Epic4", "Desc", Status.NEW));
            manager.createSubtask(new Subtask("Sub1", "Desc", Status.IN_PROGRESS, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));
            manager.createSubtask(new Subtask("Sub2", "Desc", Status.NEW, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 12, 0), Duration.ofMinutes(60)));

            assertEquals(Status.IN_PROGRESS, manager.getEpicById(epic.getId()).getStatus());
        }

        @Test
        void testTaskTimeOverlap() {
            manager = createManager();
            Task task1 = manager.createTask(new Task("Task1", "Desc", Status.NEW,
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));

            assertThrows(InMemoryTaskManager.TimeConflictException.class, () -> {
                manager.createTask(new Task("Task2", "Desc", Status.NEW,
                        LocalDateTime.of(2025, 8, 27, 10, 30), Duration.ofMinutes(60)));
            });
        }

        @Test
        void testTaskTimeNoOverlap() {
            manager = createManager();
            Task task1 = manager.createTask(new Task("Task1", "Desc", Status.NEW,
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));

            assertDoesNotThrow(() -> {
                manager.createTask(new Task("Task2", "Desc", Status.NEW,
                    LocalDateTime.of(2025, 8, 27, 11, 0), Duration.ofMinutes(60)));
            });
        }

        @Test
        void testEmptyHistory() {
            manager = createManager();
            assertTrue(manager.getHistory().isEmpty(), "История должна быть пустой при отсутствии вызовов");
        }

        @Test
        void testHistoryManager() {
            manager = createManager();
            Task task = manager.createTask(new Task("Task1", "Desc", Status.NEW,
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));

            manager.getTaskById(task.getId());

            List<Task> history = manager.getHistory();
            assertEquals(1, history.size());
            assertEquals(task, history.get(0));
        }

        @Test
        void testHistoryDuplication() {
            manager = createManager();
            Task task = manager.createTask(new Task("Task1", "Desc", Status.NEW,
                LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));
            manager.getTaskById(task.getId());
            manager.getTaskById(task.getId());

            List<Task> history = manager.getHistory();
            assertEquals(1, history.size(), "Дублирование задач в истории должно быть предотвращено");
            assertEquals(task, history.get(0));
        }

        @Test
        void testRemoveFromHistory_beginMiddleEnd() {
            manager = createManager();
            Task task1 = manager.createTask(new Task("Task1", "Desc", Status.NEW,
                LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));
            Task task2 = manager.createTask(new Task("Task2", "Desc", Status.NEW,
                LocalDateTime.of(2025, 8, 27, 11, 0), Duration.ofMinutes(60)));
            Task task3 = manager.createTask(new Task("Task3", "Desc", Status.NEW,
                LocalDateTime.of(2025, 8, 27, 12, 0), Duration.ofMinutes(60)));

        // добавляем все задачи в историю
            manager.getTaskById(task1.getId());
            manager.getTaskById(task2.getId());
            manager.getTaskById(task3.getId());

        // удаляем первую
            manager.removeTask(task1.getId());
            assertFalse(manager.getHistory().contains(task1));

        // удаляем середину
            manager.removeTask(task2.getId());
            assertFalse(manager.getHistory().contains(task2));

        // удаляем конец
            manager.removeTask(task3.getId());
            assertFalse(manager.getHistory().contains(task3));
        }

        @Test
        void testExceptionOnInvalidFile() {
            File invalidFile = new File("/invalid/path/tasks.csv");
            FileBackedTaskManager manager = new FileBackedTaskManager(invalidFile);

            assertThrows(RuntimeException.class, () -> {
                FileBackedTaskManager.loadFromFile(invalidFile);
            });
        }
}
