package com.example.munkoback.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@Configuration
@EnableScheduling
public class WakeUpServiceConfig {

    @Scheduled(fixedRate = 300000) // 5 min
    public void keepAlive() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        int itemId = 1;

        String graphqlQuery = "{ \"query\": \"{ getItem(id: " + itemId + ") { id, name, description } }\" }";


        String graphqlUrl = "https://funkopop.onrender.com/graphql";

        HttpEntity<String> request = new HttpEntity<>(graphqlQuery, headers);

         new RestTemplate().exchange(
                graphqlUrl,
                HttpMethod.POST,
                request,
                String.class
        );

    }
}
