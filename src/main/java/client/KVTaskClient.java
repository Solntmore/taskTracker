package client;

import exceptions.ServerLoadException;
import exceptions.ServerRegisterException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.nio.charset.StandardCharsets.UTF_8;

public class KVTaskClient {

    private final String url;
    private final String apiToken;

    public KVTaskClient(String serverUrl) {
        url = serverUrl;
        apiToken = register(url);
    }

    private String register(String url) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/register"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ServerRegisterException("Нельзя зарегистрировать запрос, статус = " + response.statusCode());
            }
            return response.body();
        } catch (Exception e) {
            throw new ServerRegisterException("Не удалось зарегистрировать на сервере. " + e.getMessage());
        }
    }

    public String load(String key) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ServerRegisterException("Нельзя получить запрос, статус = " + response.statusCode());
            }
            return response.body();
        } catch (Exception e) {
            throw new ServerLoadException("Не удалось получить данные с сервера. " + e.getMessage());
        }
    }

    public String put(String key, String json) {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken))
                    .POST(HttpRequest.BodyPublishers.ofString(json, UTF_8))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new ServerRegisterException("Нельзя получить запрос, статус = " + response.statusCode());
            }
            return response.body();
        } catch (Exception e) {
            throw new ServerLoadException("Не удалось получить данные с сервера. " + e.getMessage());
        }
    }
}

