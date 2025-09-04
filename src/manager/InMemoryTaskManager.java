package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import exception.TimeConflictException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    protected  Map<Integer, Task> tasks;
    protected  Map<Integer, Epic> epics;
    protected  Map<Integer, Subtask> subtasks;
    protected  int nextId;
    protected final HistoryManager historyManager;

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>(
            Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder()))
    );


    public InMemoryTaskManager() {

        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.nextId = 1;
        this.historyManager = Manager.getDefaultHistory();
    }

    private int generateId() {

        return nextId++;
    }


    @Override
    public Task createTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new TimeConflictException("Задача '" + task.getTitle() +
                    "' пересекается по времени с существующей задачей");
        }

        task.setId(generateId());
        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (hasTimeOverlap(subtask)) {
            throw new TimeConflictException("Задача '" + subtask.getTitle() +
                    "' пересекается по времени с существующей задачей");
        }

        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);

        if (epic == null) {
            System.out.println("Ошибка: эпик с id " + epicId + " не найден.");
            return null;
        }

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());

        if (subtask.getStartTime() != null) {
            prioritizedTasks.add(subtask);
        }

        updateEpicStatus(epic.getId());
        updateEpicFields(epic.getId());

        return subtask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    private void updateEpicFields(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }

        List<Subtask> epicSubtasks = getAllSubtasks().stream()
                .filter(subtask -> subtask.getEpicId() == epicId)
                .collect(Collectors.toList());

        epic.updateEpicFields(epicSubtasks);
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Ошибка: эпик с id " + epicId + " не найден.");
            return;
        }

        List<Subtask> epicSubtasks = getSubtasksOfEpic(epicId);
        updateEpicStatus(epic, epicSubtasks);
    }


    private void updateEpicStatus(Epic epic, List<Subtask> epicSubtasks) {
        if (epic == null || epicSubtasks == null) {
            return;
        }

        if (epicSubtasks.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        if (epicSubtasks.stream().allMatch(s -> s.getStatus() == Status.NEW)) {
            epic.setStatus(Status.NEW);
        } else if (epicSubtasks.stream().allMatch(s -> s.getStatus() == Status.DONE)) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtaskIds().forEach(subtaskId -> {
                Subtask subtask = subtasks.remove(subtaskId);
                if (subtask != null) {
                    prioritizedTasks.remove(subtask);
                    historyManager.remove(subtaskId);
                }
            });
            historyManager.remove(id);
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);

        if (subtask != null) {
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);

            Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.removeSubtask(id);
                    updateEpicStatus(epic.getId());
                    updateEpicFields(subtask.getEpicId());
                }
        }
    }

    @Override
    public void updateTask(Task task) {
        if (hasTimeOverlap(task)) {
            throw new TimeConflictException("Задача '" + task.getTitle() +
                    "' пересекается по времени с существующей задачей");
        }

        if (tasks.containsKey(task.getId())) {
            Task oldTask = tasks.get(task.getId());
            prioritizedTasks.remove(oldTask);

            tasks.put(task.getId(), task);

            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        }
    }

    @Override
    public void updateEpic(Epic epic) {

        if (epics.containsKey(epic.getId())) {
            Epic stored = epics.get(epic.getId());
            stored.setTitle(epic.getTitle());
            stored.setDescription(epic.getDescription());
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (hasTimeOverlap(subtask)) {
            throw new TimeConflictException("Задача '" + subtask.getTitle() +
                    "' пересекается по времени с существующей задачей");
        }

        if (subtasks.containsKey(subtask.getId())) {
            Subtask oldSubtask = subtasks.get(subtask.getId());
            prioritizedTasks.remove(oldSubtask);

            subtasks.put(subtask.getId(), subtask);

            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
            }

            updateEpicStatus(subtask.getEpicId());
            updateEpicFields(subtask.getEpicId());
        }
    }

    @Override
    public void deleteAllTasks() {
        prioritizedTasks.removeIf(task -> tasks.containsKey(task.getId()));
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        prioritizedTasks.removeIf(task -> epics.containsKey(task.getId()) ||
                subtasks.containsKey(task.getId()));

        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteAllSubtasks() {
            prioritizedTasks.removeIf(task -> subtasks.containsKey(task.getId()));
            subtasks.clear();

            epics.values().forEach(epic -> {
                epic.clearSubtasks();
                updateEpicFields(epic.getId());
            });
        }

    @Override
    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            return List.of();
        }

        return epic.getSubtaskIds().stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    protected void putTask(Task task) {
        tasks.put(task.getId(), task);
    }

    protected void putEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    protected void putSubtask(Subtask subtask) {
        subtasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.addSubtask(subtask.getId());
        }
    }

    protected void setNextId(int nextId) {
        this.nextId = nextId;
    }

    public boolean hasTimeOverlap(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }

        //LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newTask.getEndTime();

        if (newEnd == null) {
            return false;
        }

        return prioritizedTasks.stream()
                .filter(task -> task.getId() != newTask.getId())
                .filter(task -> task.getStartTime() != null)
                .filter(task -> task.getEndTime() != null)
                .anyMatch(existingTask -> isTimeOverlap(newTask, existingTask));
    }

    private boolean isTimeOverlap(Task task1, Task task2) {

        return task1.isTimeOverlap(task2);
    }
}

