package http;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (method.equals("GET") && pathParts.length == 2) {
                handleGetAllTasks(exchange);
                return;
            }
            if (method.equals("GET") && pathParts.length == 3) {
                int id = parseId(pathParts[2]);
                if (id != -1) {
                    handleGetTaskById(exchange, id);
                } else {
                    sendBadRequest(exchange, "Некорректный ID задачи.");
                }
                return;
            }
            if (method.equals("POST") && pathParts.length == 2) {
                handleCreateOrUpdateTask(exchange, Optional.empty());
                return;
            }
            if (method.equals("DELETE") && pathParts.length == 2) {
                handleDeleteAllTasks(exchange);
                return;
            }
            if (method.equals("DELETE") && pathParts.length == 3) {
                int id = parseId(pathParts[2]);
                if (id != -1) {
                    handleDeleteTaskById(exchange, id);
                } else {
                    sendBadRequest(exchange, "Некорректный ID задачи.");
                }
                return;
            }
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getAllTasks();
        String response = gson.toJson(tasks);
        sendOk(exchange, response);
    }

    private void handleGetTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            String response = gson.toJson(task);
            sendOk(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange, Optional<Integer> id) throws IOException {
        String requestBody = readText(exchange);
        Task task = gson.fromJson(requestBody, Task.class);

        if (task == null) {
            sendBadRequest(exchange, "Тело запроса не может быть пустым.");
            return;
        }

        id.ifPresent(task::setId);

        try {
            if (id.isPresent()) {
                taskManager.updateTask(task);
            } else {
                taskManager.createTask(task);
            }
            sendCreated(exchange);
        } catch (Exception e) {
            if (e.getMessage().contains("пересекается")) {
                sendHasOverlaps(exchange);
            } else {
                sendInternalServerError(exchange);
            }
        }
    }

    private void handleDeleteAllTasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        sendCreated(exchange);
    }

    private void handleDeleteTaskById(HttpExchange exchange, int id) throws IOException {
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            taskManager.deleteTaskById(id);
            sendCreated(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}