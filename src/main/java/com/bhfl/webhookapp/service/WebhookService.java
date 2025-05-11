package com.bhfl.webhookapp.service;

import com.bhfl.webhookapp.model.WebhookRequest;
import com.bhfl.webhookapp.model.WebhookResponse;
import com.bhfl.webhookapp.model.SqlSolutionRequest;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {

    private final RestTemplate restTemplate;
    private static final String WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    private String webhookUrl;
    private String accessToken;

    public WebhookService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void generateWebhook() {
        WebhookRequest request = new WebhookRequest();
        request.setName("Tilak Neema");
        request.setRegNo("0827CI221138");
        request.setEmail("tilakneema220818@acropolis.in");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);

        try {
            WebhookResponse response = restTemplate.postForObject(WEBHOOK_URL, entity, WebhookResponse.class);
            if (response != null) {
                this.webhookUrl = response.getWebhook();
                this.accessToken = response.getAccessToken();
                System.out.println("Webhook URL received: " + webhookUrl);
                System.out.println("Access Token received: " + accessToken);

                // Submit the SQL solution immediately after receiving the webhook
                submitSqlSolution();
            }
        } catch (Exception e) {
            System.err.println("Error generating webhook: " + e.getMessage());
        }
    }

    private void submitSqlSolution() {
        if (webhookUrl == null || accessToken == null) {
            System.err.println("Webhook URL or Access Token not available");
            return;
        }
        SqlSolutionRequest solution = new SqlSolutionRequest();
        solution.setFinalQuery(
                "SELECT E1.EMP_ID, E1.FIRST_NAME, E1.LAST_NAME, D.DEPARTMENT_NAME, COUNT(E2.EMP_ID) AS YOUNGER_EMPLOYEES_COUNT FROM EMPLOYEE E1 JOIN DEPARTMENT D ON E1.DEPARTMENT = D.DEPARTMENT_ID LEFT JOIN EMPLOYEE E2 ON E1.DEPARTMENT = E2.DEPARTMENT AND E1.DOB < E2.DOB GROUP BY E1.EMP_ID, E1.FIRST_NAME, E1.LAST_NAME, D.DEPARTMENT_NAME ORDER BY E1.EMP_ID DESC");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<SqlSolutionRequest> entity = new HttpEntity<>(solution, headers);
        System.out.println("Sending request to: " + webhookUrl);
        System.out.println("With headers: " + headers);
        System.out.println("With body: " + solution.getFinalQuery());

        try {
            String response = restTemplate.postForObject(webhookUrl, entity, String.class);
            System.out.println("Solution submission response: " + response);
        } catch (Exception e) {
            System.err.println("Error submitting solution: " + e.getMessage());
        }
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public String getAccessToken() {
        return accessToken;
    }
}