package com.mlmfreya.ferya2.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;

@Service
public class TronWebService {


    @Value("${tronApiAdress}")
    private String TronApiAddress;

    public String makeApiRequest(String apiUrl, String method) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpResponse response;

        if (method.equalsIgnoreCase("POST")) {
            HttpPost postRequest = new HttpPost(apiUrl);
            StringEntity entity = new StringEntity("{}");  // Sending an empty json body
            postRequest.setEntity(entity);
            postRequest.setHeader("Content-type", "application/json");
            response = client.execute(postRequest);
        } else {
            HttpGet getRequest = new HttpGet(apiUrl);
            response = client.execute(getRequest);
        }

        HttpEntity responseEntity = response.getEntity();
        String responseString = EntityUtils.toString(responseEntity, "UTF-8");
        return responseString;
    }

    public BigDecimal checkWalletBalance(String walletAddress) {
      //  String apiUrl = TronApiAddress+"/getTokenBalance/" + walletAddress;
        String apiUrl = TronApiAddress+"/getTokenBalance/" + walletAddress;
        String apiResponse;
        try {
            apiResponse = makeApiRequest(apiUrl,"GET");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode response = mapper.readTree(apiResponse);
            JsonNode dataNode = response.get("data");
            if (dataNode == null || dataNode.isNull()) {
                throw new RuntimeException("No data field in the response");
            }
            double balance = dataNode.asDouble();
            return BigDecimal.valueOf(balance);
        } catch (IOException e) {
            // handle exception when calling the API
            e.printStackTrace();
            throw new RuntimeException("Failed to check wallet balance");
        }
    }


}
