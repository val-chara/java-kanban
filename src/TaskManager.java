import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks;
    private Map<Integer, Epic> epics;
    private Map<Integer, Subtask> subtasks;
    private int nextId;

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.nextId = 1;
    }

    private int generateId() {
        return nextId++;
    }

    public void createTask(String title, String description, Status status) {
        Task task = new Task(title, description, generateId(), status);
        tasks.put(task.getId(), task);
    }

    public int createEpic(String title, String description, Status status) {
        Epic epic = new Epic(title, description, generateId(), status);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void createSubtask(String title, String description, Status status, int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Ошибка: эпик с id " + epicId + " не найден.");
            return;
        }

        Subtask subtask = new Subtask(title, description, generateId(), status, epicId);
        subtasks.put(subtask.getId(), subtask);

        epic.addSubtask(subtask.getId());
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Ошибка: эпик с id " + epicId + " не найден.");
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            Status status = subtask.getStatus();

            if (status != Status.NEW) {
                allNew = false;
            }
            if (status != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    public void deleteTaskById(int id) {
        tasks.remove(id);
        subtasks.remove(id);
        epics.remove(id);
    }


    public void printAllTasks() {
        tasks.forEach((id, task) -> System.out.println(task.getTitle() + " : " + task.getStatus()));
        epics.forEach((id, epic) -> System.out.println(epic.getTitle() + " : " + epic.getStatus()));
        subtasks.forEach((id, subtask) -> System.out.println(subtask.getTitle() + " : " + subtask.getStatus()));
    }
}
