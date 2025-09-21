package http.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (method.equals("GET") && pathParts.length == 2) {
                handleGetAllSubtasks(exchange);
                return;
            }
            if (method.equals("GET") && pathParts.length == 3) {
                int id = parseId(pathParts[2]);
                if (id != -1) {
                    handleGetSubtaskById(exchange, id);
                } else {
                    sendBadRequest(exchange, "Некорректный ID подзадачи.");
                }
                return;
            }
            if (method.equals("POST") && pathParts.length == 2) {
                handleCreateOrUpdateSubtask(exchange, Optional.empty());
                return;
            }
            if (method.equals("DELETE") && pathParts.length == 2) {
                handleDeleteAllSubtasks(exchange);
                return;
            }
            if (method.equals("DELETE") && pathParts.length == 3) {
                int id = parseId(pathParts[2]);
                if (id != -1) {
                    handleDeleteSubtaskById(exchange, id);
                } else {
                    sendBadRequest(exchange, "Некорректный ID подзадачи.");
                }
                return;
            }
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getAllSubtasks();
        String response = gson.toJson(subtasks);
        sendOk(exchange, response);
    }

    private void handleGetSubtaskById(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask != null) {
            String response = gson.toJson(subtask);
            sendOk(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange, Optional<Integer> id) throws IOException {
        String requestBody = readText(exchange);
        Subtask subtask = gson.fromJson(requestBody, Subtask.class);

        if (subtask == null) {
            sendBadRequest(exchange, "Тело запроса не может быть пустым.");
            return;
        }

        id.ifPresent(subtask::setId);

        try {
            if (id.isPresent()) {
                taskManager.updateSubtask(subtask);
            } else {
                taskManager.createSubtask(subtask);
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

    private void handleDeleteAllSubtasks(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtasks();
        sendCreated(exchange);
    }

    private void handleDeleteSubtaskById(HttpExchange exchange, int id) throws IOException {
        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask != null) {
            taskManager.deleteSubtaskById(id);
            sendCreated(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}