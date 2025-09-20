package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected final Gson gson = new Gson();

    public abstract void handle(HttpExchange exchange) throws IOException;

    protected String readText(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        return new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendOk(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 200);
    }

    protected void sendCreated(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(201, -1);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String response = "Объект с таким id не найден.";
        sendText(exchange, response, 404);
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        String response = "Задача пересекается по времени с уже существующей.";
        sendText(exchange, response, 406);
    }

    protected void sendInternalServerError(HttpExchange exchange) throws IOException {
        String response = "Внутренняя ошибка сервера.";
        sendText(exchange, response, 500);
    }

    protected void sendBadRequest(HttpExchange exchange, String message) throws IOException {
        String response = "Некорректный запрос. " + message;
        sendText(exchange, response, 400);
    }

    protected int parseId(String idString) {
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}