package manager;

import model.*;

import java.io.*;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,title,status,description,epic";

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public FileBackedTaskManager(){
        this(new File("default_tasks.csv"));
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine(); // пропустить заголовок
            while ((line = reader.readLine()) != null && !line.isBlank()) {
                Task task = fromString(line);
                if (task instanceof Subtask) {
                    manager.createSubtask((Subtask) task);
                } else if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                } else {
                    manager.createTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + file.getName(), e);
        }
        return manager;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();
            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + file.getName(), e);
        }
    }

    private static String toString(Task task) {
        String epicId = "";
        if (task instanceof Subtask) {
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

    private static Task fromString(String line) {
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

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        save();
        return created;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask created = super.createSubtask(subtask);
        save();
        return created;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }
}
