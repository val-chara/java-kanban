package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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

    public void createTask(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
    }

    public int createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    public void createSubtask(Subtask subtask) {
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Ошибка: эпик с id " + epicId + " не найден.");
            return;
        }

        subtask.setId(generateId());
        subtasks.put(subtask.getId(), subtask);
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic.getId());
    }

//    public Task getTaskById(int id) {
//        return tasks.get(id);
//    }
//
//    public Epic getEpicById(int id) {
//        return epics.get(id);
//    }
//
//    public Subtask getSubtaskById(int id) {
//        return subtasks.get(id);
//    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) {
            System.out.println("Ошибка: эпик с id " + epicId + " не найден.");
            return;
        }

        boolean allNew = true;
        boolean allDone = true;
        boolean hasInProgress = false;

        for (int subtaskId : epic.getSubtaskIds()) {
            Subtask subtask = subtasks.get(subtaskId);
            Status status = subtask.getStatus();

            if (status != Status.NEW) {
                allNew = false;
            }
            if (status != Status.DONE) {
                allDone = false;
            }
            if (status == status.IN_PROGRESS){
                hasInProgress = true;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else if (hasInProgress) {
            epic.setStatus(Status.IN_PROGRESS);
        } else{
            epic.setStatus(Status.NEW);
        }
    }

    public void deleteTaskById(int id) {
        tasks.remove(id);
    }
    public void deleteEpicById(int id){
        Epic epic = epics.remove(id);
        if (epic !=null){
            for (int subtaskId: epic.getSubtaskIds()){
                subtasks.remove(subtaskId);
            }
        }
    }
    public void deleteSubtaskById(int id){
        Subtask subtask = subtasks.remove(id);
        if (subtask != null){
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null){
                epic.removeSubtask(id);
                updateEpicStatus(epic.getId());
            }
        }
    }

    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            epics.put(epic.getId(), epic);
            updateEpicStatus(epic.getId());
        }

    }
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            updateEpicStatus(subtask.getEpicId());
        }
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        for (Epic epic : epics.values()){
            for(int subtaskId : epic.getSubtaskIds()){
                subtasks.remove(subtaskId);
            }
        }
        epics.clear();

    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic.getId());
        }
        subtasks.clear();
    }

    public List<Subtask> getSubtasksOfEpic(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return new ArrayList<>();
        }
        List<Subtask> result = new ArrayList<>();
        for (int subtaskId : epic.getSubtaskIds()){
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null){
                result.add(subtask);
            }
        }
        return result;
    }

    public List<Task> getAllTasks(){
        return new ArrayList<>(tasks.values());
    }
    public List<Epic> getAllEpics(){
        return new ArrayList<>(epics.values());
    }
    public List<Subtask> getAllSubtasks(){
        return new ArrayList<>(subtasks.values());
    }

}
