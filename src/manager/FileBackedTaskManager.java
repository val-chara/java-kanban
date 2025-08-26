package manager;

import model.TaskType;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.util.*;
import java.io.IOException;
import java.io.File;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;
    private static final String HEADER = "id,type,title,status,description,epic,startTime,duration";

    public FileBackedTaskManager(File file) {
        super();
        this.file = file;
        Epic.setTaskManager(this);
    }

    public FileBackedTaskManager() {
        this(new File("default_tasks.csv"));
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        Epic.setTaskManager(manager);
        int maxId = 0;
        List<Integer> historyIds = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line = reader.readLine();
            List<Subtask> subtasksToAddLater = new ArrayList<>();

            while ((line = reader.readLine()) != null && !line.isBlank()) {
                Task task = ConverterTask.fromString(line);
                if (task.getType() == TaskType.SUBTASK) {
                    subtasksToAddLater.add((Subtask) task);
                } else if (task.getType() == TaskType.EPIC) {
                    manager.putEpic((Epic) task);
                } else {
                    manager.putTask(task);
                }
                if (task.getId() > maxId) {
                    maxId = task.getId();
                }
            }
            for (Subtask subtask : subtasksToAddLater) {
                manager.putSubtask(subtask);
            }
            String historyLine = reader.readLine();
            if (historyLine != null && historyLine.isBlank()) {
                String history = reader.readLine();
                if (history != null && !history.isBlank()) {
                    historyIds = ConverterTask.historyFromString(history);
                    for (int id : historyIds) {
                        if (manager.tasks.containsKey(id)) {
                            manager.getTaskById(id);
                        } else if (manager.epics.containsKey(id)) {
                            manager.getEpicById(id);
                        } else if (manager.subtasks.containsKey(id)) {
                            manager.getSubtaskById(id);
                        }
                    }
                }
            }
            manager.setNextId(maxId + 1);

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла: " + file.getName(), e);
        }
        return manager;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(HEADER);
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(ConverterTask.taskToString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(ConverterTask.taskToString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(ConverterTask.taskToString(subtask));
                writer.newLine();
            }

            writer.newLine();
            writer.write(ConverterTask.historyToString(historyManager));

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении в файл: " + file.getName(), e);
        }
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

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }
}
