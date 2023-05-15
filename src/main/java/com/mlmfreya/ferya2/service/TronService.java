package com.mlmfreya.ferya2.service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.security.interfaces.ECKey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.mlmfreya.ferya2.dto.AccountDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TronService {

    private static final String TRON_API_URL = "https://api.trongrid.io";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;


    @Autowired
    public TronService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();

    }


    public AccountDto createAccount() throws Exception {
        String endpoint = "/wallet/createaccount";
        HttpRequest request = buildPostRequest(TRON_API_URL + endpoint, "");
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        validateResponse(response);
        JsonNode rootNode = objectMapper.readTree(response.body());
        return AccountDto.builder()
                .privateKey(rootNode.get("privateKey").asText())
                .publicKey(rootNode.get("publicKey").asText())
                .base58Address(rootNode.get("base58Address").asText())
                .hexAddress(rootNode.get("hexAddress").asText())
                .build();
    }

    // Similar structure for other methods

    private HttpRequest buildPostRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
    }

    private void validateResponse(HttpResponse<String> response) throws Exception {
        if (response.statusCode() != 200) {
            throw new Exception("Invalid response from server: " + response.body());
        }
    }
}
