package com.example.munkoback.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
@Configuration
@EnableScheduling
public class WakeUpService {
    private static final Logger logger = LoggerFactory.getLogger(WakeUpService.class);
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
