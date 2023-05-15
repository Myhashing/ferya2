package com.mlmfreya.ferya2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mlmfreya.ferya2.dto.TronAPIResponse;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class TronAPIService {
    private HttpClient client;
    private ObjectMapper objectMapper;

    public TronAPIService() {
        this.client = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public <T> TronAPIResponse<T> sendRequest(String uri, String body, Class<T> responseType) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode jsonNode = objectMapper.readTree(httpResponse.body());

        TronAPIResponse<T> response = new TronAPIResponse<>();
        if (jsonNode.has("success") && jsonNode.get("success").asBoolean()) {
            T result = objectMapper.treeToValue(jsonNode.get("result"), responseType);
            response.setSuccess(true);
            response.setResult(result);
        } else {
            String error = jsonNode.has("error") ? jsonNode.get("error").asText() : "Unknown error";
            response.setSuccess(false);
            response.setError(error);
        }

        return response;
    }
}
