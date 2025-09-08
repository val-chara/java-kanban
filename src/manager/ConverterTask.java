package manager;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ConverterTask {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String taskToString(Task task) {
        String epicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        String startTime = "";
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(formatter);
        }

        String duration = "";
        if (task.getDuration() != null) {
            duration = String.valueOf(task.getDuration().toMinutes());
        }

        return String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicId,
                startTime,
                duration
        );
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        String epicIdStr = parts[5];
        String startTimeStr = parts[6];
        String durationStr = parts[7];

        LocalDateTime startTime = null;
        if (!startTimeStr.isEmpty()) {
            startTime = LocalDateTime.parse(startTimeStr, formatter);
        }

        Duration duration = null;
        if (!durationStr.isEmpty()) {
            duration = Duration.ofMinutes(Long.parseLong(durationStr));
        }

        Task task;
        switch (type) {
            case TASK:
                task = new Task(title, description, status, startTime, duration);
                break;
            case EPIC:
                task = new Epic(title, description, status);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                task = new Subtask(title, description, status, epicId, startTime, duration);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setId(id);
        return task;
    }

    public static String historyToString(HistoryManager manager) {
        List<String> ids = new ArrayList<>();
        for (Task task : manager.getHistory()) {
            ids.add(String.valueOf(task.getId()));
        }
        return String.join(",", ids);
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        if (value == null || value.isBlank()) {
            return history;
        }

        for (String id : value.split(",")) {
            try {
                history.add(Integer.parseInt(id.trim()));
            } catch (NumberFormatException e) {
                System.err.println("Ошибка при преобразовании истории: " + e.getMessage());
            }
        }
        return history;
    }
}
