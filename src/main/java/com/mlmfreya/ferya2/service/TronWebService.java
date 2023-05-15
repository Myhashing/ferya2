package com.mlmfreya.ferya2.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class TronWebService {

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


}
