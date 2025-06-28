package model;

public class Subtask extends Task {

    private int epicId;

    public Subtask(String title, String description, Status status, int epicId) {

        super(title, description, status);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override

    public String toString() {

        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId=" + getEpicId() +
                '}';
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

}