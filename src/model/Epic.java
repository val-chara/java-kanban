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

    public void setStartTimeInternal(LocalDateTime startTime) {
        super.setStartTimeInternal(startTime);
    }

    public void setDurationInternal(Duration duration) {
        super.setDurationInternal(duration);
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

    public void updateEpicFields(List<Subtask> subtasks) {
        updateStatus(subtasks);
        updateTime(subtasks);
    }

    private void updateStatus(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setStatus(Status.NEW);
            return;
        }
        if (subtasks.stream().allMatch(s -> s.getStatus() == Status.NEW)) {
            setStatus(Status.NEW);
        } else if (subtasks.stream().allMatch(s -> s.getStatus() == Status.DONE)) {
            setStatus(Status.DONE);
        } else {
            setStatus(Status.IN_PROGRESS);
        }
    }

    private void updateTime(List<Subtask> subtasks) {
        if (subtasks == null || subtasks.isEmpty()) {
            setStartTimeInternal(null);
            setDurationInternal(Duration.ZERO);
            setEndTimeInternal(null);
            return;
        }

        Optional<LocalDateTime> earliestStart = subtasks.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> latestEnd = subtasks.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        if (earliestStart.isPresent() && latestEnd.isPresent()) {
            setStartTimeInternal(earliestStart.get());
            setEndTimeInternal(latestEnd.get());
            setDurationInternal(Duration.between(earliestStart.get(), latestEnd.get()));
        } else {
            setStartTimeInternal(null);
            setDurationInternal(Duration.ZERO);
            setEndTimeInternal(null);
        }
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