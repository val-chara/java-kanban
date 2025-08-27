package manager;

import manager.FileBackedTaskManager;
import model.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    protected FileBackedTaskManager createManager() {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
            return new FileBackedTaskManager(tempFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File tempFile;
    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void shouldSaveAndLoadTaskWithTimeFields() throws IOException {
        LocalDateTime start = LocalDateTime.of(2025, 7, 18, 10, 0);
        Duration duration = Duration.ofMinutes(90);

        Task task = new Task("Test task", "Desc", Status.NEW, start, duration);
        manager.createTask(task);
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loaded.getTaskById(task.getId());

        Assertions.assertNotNull(loadedTask, "Задача должна загрузиться");
        Assertions.assertEquals(start, loadedTask.getStartTime(), "startTime должен сохраниться");
        Assertions.assertEquals(duration, loadedTask.getDuration(), "duration должен сохраниться");
        Assertions.assertEquals(start.plus(duration), loadedTask.getEndTime(), "endTime должен корректно вычисляться");
    }

    @Test
    void shouldDetectIntersectionBetweenTasks() {
        LocalDateTime start1 = LocalDateTime.of(2025, 7, 18, 10, 0);
        Duration duration1 = Duration.ofMinutes(60);
        Task task1 = new Task("Task 1", "desc", Status.NEW, start1, duration1);

        LocalDateTime start2 = LocalDateTime.of(2025, 7, 18, 10, 30);
        Duration duration2 = Duration.ofMinutes(60);
        Task task2 = new Task("Task 2", "desc", Status.NEW, start2, duration2);

        boolean isIntersect = !(task1.getEndTime().isBefore(task2.getStartTime()) ||
                task2.getEndTime().isBefore(task1.getStartTime()));

        assertTrue(task1.isTimeOverlap(task2), "Задачи должны пересекаться по времени");
    }

    @Test
    void shouldNotDetectIntersectionBetweenNonOverlappingTasks() {
        LocalDateTime start1 = LocalDateTime.of(2025, 7, 18, 10, 0);
        Task task1 = new Task("Task 1", "desc", Status.NEW, start1, Duration.ofMinutes(60));

        LocalDateTime start2 = LocalDateTime.of(2025, 7, 18, 11, 30); // Через 1.5 часа
        Task task2 = new Task("Task 2", "desc", Status.NEW, start2, Duration.ofMinutes(60));

        assertFalse(task1.isTimeOverlap(task2), "Задачи не должны пересекаться по времени");
    }
}