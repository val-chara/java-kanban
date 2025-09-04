package model;

import java.util.ArrayList;
import java.util.List;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;


public class Epic extends Task {

    protected List<Integer> subtaskIds;
    private LocalDateTime endTime;

    public void setEpicTimes(LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super.updateStartTime(startTime);
        super.updateDuration(duration);
        setEndTimeInternal(endTime);
    }

    public Epic(String title, String description, Status status) {
        super(title, description, status, null, null);
        this.subtaskIds = new ArrayList<>();
        this.endTime = null;
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

    public void setEndTimeInternal(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
    public void setStartTime(LocalDateTime startTime) {
        throw new UnsupportedOperationException("Время запуска рассчитается автоматически.");
    }

    @Override
    public void setDuration(Duration duration) {
        throw new UnsupportedOperationException("Продолжительность рассчитается автоматически.");
    }

    public void setEndTime(LocalDateTime endTime) {
        throw new UnsupportedOperationException("Время окончания эпика рассчитывается автоматически на основе подзадач.");
    }

    @Override
    public LocalDateTime getStartTime() {
        return super.getStartTime();
    }

    @Override
    public Duration getDuration() {
        return super.getDuration();
    }
}