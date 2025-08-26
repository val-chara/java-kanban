package model;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

import manager.Manager;
import manager.TaskManager;

public class Epic extends Task {

    protected List<Integer> subtaskIds;
    private LocalDateTime endTime;
    private static TaskManager taskManager;

    public Epic(String title, String description, Status status) {
        super(title, description, status, null, null);
        this.subtaskIds = new ArrayList<>();
    }

    public static void setTaskManager(TaskManager manager) {
        taskManager = manager;
    }

    public List<Integer> getSubtaskIds() {
        return new ArrayList<>(subtaskIds);
    }

    public void addSubtask(int subtaskId) {
        if (!subtaskIds.contains(subtaskId)) {
            subtaskIds.add(subtaskId);
        }
    }

    public void removeSubtask(int subtaskId) {
        subtaskIds.remove(Integer.valueOf(subtaskId));
    }

    public void clearSubtasks() {
        subtaskIds.clear();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtaskIds +
                ", startTime=" + getStartTime() +
                ", endTime=" + getEndTime() +
                ", duration=" + getDuration() +
                '}';
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    public void updateEpicFields(List<Subtask> subtasks) {
        getDuration();
        getStartTime();
        getEndTime();
        updateStatus(subtasks); // Обновлю статус
    }

    private void updateStatus(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }
        boolean allNew = true;
        boolean allDone = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != Status.NEW) allNew = false;
            if (subtask.getStatus() != Status.DONE) allDone = false;
        }

        if (allDone) setStatus(Status.DONE);
        else if (allNew) setStatus(Status.NEW);
        else setStatus(Status.IN_PROGRESS);
    }

    @Override
    public void setStartTime(LocalDateTime startTime) {
        throw new UnsupportedOperationException("Время запуска рассчитается автоматически.");
    }

    @Override
    public void setDuration(Duration duration) {
        throw new UnsupportedOperationException("Продолжительность рассчитается автоматически.");
    }



    @Override
    public LocalDateTime getStartTime() {
        if (subtaskIds.isEmpty() || taskManager == null) {
            return null;
        }
        LocalDateTime earliest = null;
        for (int id : subtaskIds) {
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null && subtask.getStartTime() != null) {
                if (earliest == null || subtask.getStartTime().isBefore(earliest)) {
                    earliest = subtask.getStartTime();
                }
            }
        }
        return earliest;
    }

    @Override
    public LocalDateTime getEndTime() {
        if (subtaskIds.isEmpty() || taskManager == null) {
            return null;
        }
        LocalDateTime latest = null;
        for (int id : subtaskIds) {
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null && subtask.getEndTime() != null) {
                if (latest == null || subtask.getEndTime().isAfter(latest)) {
                    latest = subtask.getEndTime();
                }
            }
        }
        return latest;
    }

    @Override
    public Duration getDuration()  {
        if (subtaskIds.isEmpty() || taskManager == null) {
            return Duration.ZERO;
        }
        long totalMinutes = 0;
        for (int id : subtaskIds) {
            Subtask subtask = taskManager.getSubtaskById(id);
            if (subtask != null && subtask.getDuration() != null) {
                totalMinutes += subtask.getDuration().toMinutes();
            }
        }
        return Duration.ofMinutes(totalMinutes);
    }
}