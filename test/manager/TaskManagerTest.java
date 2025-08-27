package manager;

import model.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

abstract class TaskManagerTest<T extends TaskManager> {

        protected T manager;

        protected abstract T createManager();

        @Test
        void testEpicStatus_AllNew() {
            manager = createManager();
            Epic epic = manager.createEpic(new Epic("Epic1", "Desc", Status.NEW));
            Subtask sub1 = manager.createSubtask(new Subtask("Sub1", "Desc", Status.NEW, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));
            Subtask sub2 = manager.createSubtask(new Subtask("Sub2", "Desc", Status.NEW, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 12, 0), Duration.ofMinutes(60)));

            assertEquals(Status.NEW, manager.getEpicById(epic.getId()).getStatus());
        }

        @Test
        void testEpicStatus_AllDone() {
            manager = createManager();
            Epic epic = manager.createEpic(new Epic("Epic2", "Desc", Status.NEW));
            Subtask sub1 = manager.createSubtask(new Subtask("Sub1", "Desc", Status.DONE, epic.getId(),
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));
            Subtask sub2 = manager.createSubtask(new Subtask("Sub2", "Desc", Status.DONE, epic.getId(),
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
        void testHistoryManager() {
            manager = createManager();
            Task task = manager.createTask(new Task("Task1", "Desc", Status.NEW,
                    LocalDateTime.of(2025, 8, 27, 10, 0), Duration.ofMinutes(60)));

            manager.getTaskById(task.getId());
            manager.getTaskById(task.getId()); // дублирование

            List<Task> history = manager.getHistory();
            assertEquals(1, history.size());
            assertEquals(task, history.get(0));
        }
}
