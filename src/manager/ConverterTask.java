package manager;

import model.*;

public class ConverterTask {
    public static String taskToString(Task task) {
        String epicId = "";
        if (task.getType() == TaskType.SUBTASK) {
            epicId = String.valueOf(((Subtask) task).getEpicId());
        }
        return String.format("%d,%s,%s,%s,%s,%s",
                task.getId(),
                task.getType(),
                task.getTitle(),
                task.getStatus(),
                task.getDescription(),
                epicId
        );
    }

    public static Task fromString(String line) {
        String[] parts = line.split(",", 6);
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        Task task;
        switch (type) {
            case TASK:
                task = new Task(title, description, status);
                break;
            case EPIC:
                task = new Epic(title, description, status);
                break;
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                task = new Subtask(title, description, status, epicId);
                break;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
        task.setId(id);
        return task;
    }
}
