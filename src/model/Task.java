package model;

import java.util.Objects;
import java.time.Duration;
import java.time.LocalDateTime;


public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;
    private LocalDateTime startTime;
    private Duration duration;


    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Task(String title, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public int getId() {

        return id;
    }

    public Status getStatus() {

        return status;
    }

    public void setStatus(Status status) {

        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id &&
                Objects.equals(title, task.title) &&
                Objects.equals(description, task.description) && status == task.status &&
                Objects.equals(duration, task.duration) &&
                Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, id, status, duration, startTime);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id = " + id +
                ", title = " + title +
                ", description = " + description +
                ", status = " + status +
                ", startTime = " + startTime +
                ", duration=" + duration +
                ", endTime = " + getEndTime() +
                '}';
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    protected void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    protected void updateDuration(Duration duration) {
        this.duration = duration;
    }
}
