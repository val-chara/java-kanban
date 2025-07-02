package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtaskIds;

    public Epic(String title, String description, Status status) {
        super(title, description, status);
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
                '}';
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

}