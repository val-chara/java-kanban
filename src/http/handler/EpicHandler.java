package http.handler;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if (method.equals("GET") && pathParts.length == 2) {
                handleGetAllEpics(exchange);
                return;
            }
            if (method.equals("GET") && pathParts.length == 3) {
                int id = parseId(pathParts[2]);
                if (id != -1) {
                    handleGetEpicById(exchange, id);
                } else {
                    sendBadRequest(exchange, "Некорректный ID эпика.");
                }
                return;
            }
            if (method.equals("GET") && pathParts.length == 4 && "subtasks".equals(pathParts[3])) {
                int id = parseId(pathParts[2]);
                if (id != -1) {
                    handleGetEpicSubtasks(exchange, id);
                } else {
                    sendBadRequest(exchange, "Некорректный ID эпика.");
                }
                return;
            }
            if (method.equals("POST") && pathParts.length == 2) {
                handleCreateOrUpdateEpic(exchange, Optional.empty());
                return;
            }
            if (method.equals("DELETE") && pathParts.length == 2) {
                handleDeleteAllEpics(exchange);
                return;
            }
            if (method.equals("DELETE") && pathParts.length == 3) {
                int id = parseId(pathParts[2]);
                if (id != -1) {
                    handleDeleteEpicById(exchange, id);
                } else {
                    sendBadRequest(exchange, "Некорректный ID эпика.");
                }
                return;
            }
            sendNotFound(exchange);
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalServerError(exchange);
        }
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getAllEpics();
        String response = gson.toJson(epics);
        sendOk(exchange, response);
    }

    private void handleGetEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            String response = gson.toJson(epic);
            sendOk(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int epicId) throws IOException {
        Epic epic = taskManager.getEpicById(epicId);
        if (epic != null) {
            List<Subtask> subtasks = taskManager.getSubtasksOfEpic(epicId);
            String response = gson.toJson(subtasks);
            sendOk(exchange, response);
        } else {
            sendNotFound(exchange);
        }
    }

    private void handleCreateOrUpdateEpic(HttpExchange exchange, Optional<Integer> id) throws IOException {
        String requestBody = readText(exchange);
        Epic epic = gson.fromJson(requestBody, Epic.class);

        if (epic == null) {
            sendBadRequest(exchange, "Тело запроса не может быть пустым.");
            return;
        }

        id.ifPresent(epic::setId);

        try {
            if (id.isPresent()) {
                taskManager.updateEpic(epic);
            } else {
                taskManager.createEpic(epic);
            }
            sendCreated(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleDeleteAllEpics(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        sendCreated(exchange);
    }

    private void handleDeleteEpicById(HttpExchange exchange, int id) throws IOException {
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            taskManager.deleteEpicById(id);
            sendCreated(exchange);
        } else {
            sendNotFound(exchange);
        }
    }
}