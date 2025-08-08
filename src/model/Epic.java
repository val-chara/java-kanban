package model;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;

import manager.Manager;
import manager.TaskManager;

public class Epic extends Task {

    protected List<Integer> subtaskIds;
    private final TaskManager inMemoryTaskManager = Manager.getDefault();
    List<Subtask> subtaskList = inMemoryTaskManager.getAllSubtasks();

    public Epic(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(title, description, status, startTime, duration);
        this.subtaskIds = new ArrayList<>();
    }

    public List<Integer> getSubtaskIds() {

        return new ArrayList<>(subtaskIds);
    }

    public void addSubtask(int subtaskId) {
        subtaskIds.add(subtaskId);
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

    @Override
    public LocalDateTime getStartTime() {
        if (subtaskIds.isEmpty()) {
            return null;
        }
        LocalDateTime earliest = null;
        for (int id : subtaskIds) {
            Subtask subtask = inMemoryTaskManager.getSubtaskById(id);
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
        if (subtaskIds.isEmpty()) {
            return null;
        }
        LocalDateTime latest = null;
        for (int id : subtaskIds) {
            Subtask subtask = inMemoryTaskManager.getSubtaskById(id);
            if (subtask != null && subtask.getEndTime() != null) {
                if (latest == null || subtask.getEndTime().isAfter(latest)) {
                    latest = subtask.getEndTime();
                }
            }
        }
        return latest;
    }

    @Override
    public Duration getDuration() {
        if (subtaskIds.isEmpty()) {
            return Duration.ZERO;
        }
        long totalMinutes = 0;
        for (int id : subtaskIds) {
            Subtask subtask = inMemoryTaskManager.getSubtaskById(id);
            if (subtask != null && subtask.getDuration() != null) {
                totalMinutes  += subtask.getDuration().toMinutes();
            }
        }
        return Duration.ofMinutes(totalMinutes);
    }

}