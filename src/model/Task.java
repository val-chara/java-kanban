package model;

public class Task {
    protected String title;
    protected String description;
    protected int id;
    protected Status status;

    public Task(String title, String description, Status status) {
        this.title = title;
        this.description = description;
        //this.id = id;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title){
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description){
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
}
