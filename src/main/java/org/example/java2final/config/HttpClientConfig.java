package org.example.java2final.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
public class HttpClientConfig {

    @Value("${stack-exchange.base_url}")
    private String stackExchangeBaseUrl;

    @Value("${stack-exchange.key}")
    private String apiKey;

    @Bean
    public HttpClient httpClient() {
        return HttpClient.create()
                .responseTimeout(Duration.ofSeconds(10)); // Set a connection timeout
    }

    @Bean
    public WebClient webClient(HttpClient httpClient) {
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(stackExchangeBaseUrl) // Replace with StackExchange base URL
                .defaultHeader("Accept", "application/json") // Set Accept header to JSON
                .defaultHeader("Authorization", "Bearer " + apiKey) // Set API Key for Authorization (if needed)
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))  // 10 MB
                .build();
    }
}
