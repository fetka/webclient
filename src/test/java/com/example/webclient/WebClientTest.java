package com.example.webclient;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.logging.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

@Slf4j
public class WebClientTest {

  WebClient webClient;

  @BeforeEach
  public void setup() {
//    HttpClient httpClient = HttpClient.create().wiretap(true);
    HttpClient httpClient = HttpClient
        .create()
        .wiretap("reactor.netty.http.client.HttpClient",
            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
    webClient = WebClient
        .builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
//        .filters(exchangeFilterFunctions -> {
//          exchangeFilterFunctions.add(logRequest());
//          exchangeFilterFunctions.add(logResponse());
//        })
        .build();
  }

  @Test
  public void testJsonServer() {
    String posts = webClient.get().uri("http://localhost:3000/posts")
        .retrieve()
        .bodyToMono(String.class).block();
//    System.err.println("posts: \n" + posts);

    Person person = Person.builder().name("Andras").job("IT").build();

    String response = webClient.post().uri("https://echo.free.beeceptor.com")
        .bodyValue(person)
        .retrieve()
        .bodyToMono(String.class)
        .block();

  }

  @Test
  public void test1() {

    WebClient webClient = WebClient
        .builder()
        .filters(exchangeFilterFunctions -> {
          exchangeFilterFunctions.add(logRequest());
          exchangeFilterFunctions.add(logResponse());
        })
        .build();
    TourRating rating = webClient
        .get()
        .uri("http://localhost:8089/tours/1/ratings")
        .retrieve()
        .bodyToMono(TourRating.class).block();
    System.err.println(rating);
  }

  public static ExchangeFilterFunction logRequest() {
    return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
      // Use a logging level check to avoid unnecessary string building
      if (log.isDebugEnabled()) {
        StringBuilder sb = new StringBuilder("Response: \n");

        // Log headers
        sb.append("Headers:\n");
        clientRequest.headers().forEach((name, values) ->
            values.forEach(
                value -> sb.append("\t").append(name).append(": ").append(value).append("\n"))
        );
//        log.debug(sb.toString()); // Use the logger
      }
      // IMPORTANT: Return the original response wrapped in a Mono
      // so that the downstream consumers can process it.

      return Mono.just(clientRequest);
    });
  }


  public static ExchangeFilterFunction logResponse() {
    return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
      // Use a logging level check to avoid unnecessary string building
      if (log.isDebugEnabled()) {
        StringBuilder sb = new StringBuilder("Response: \n");
        // Log status code
        sb.append("Status: ").append(clientResponse.statusCode()).append("\n");
        // Log headers
        sb.append("Headers:\n");
        clientResponse.headers().asHttpHeaders().forEach((name, values) ->
            values.forEach(
                value -> sb.append("\t").append(name).append(": ").append(value).append("\n"))
        );
        sb.append(clientResponse.statusCode());
        sb.append("Response body:: ");
        sb.append(clientResponse.bodyToMono(String.class));

//        log.debug("RESPONSE: \n{}", sb); // Use the logger
        return Mono.just(clientResponse);
      }
      // IMPORTANT: Return the original response wrapped in a Mono
      // so that the downstream consumers can process it.
      return Mono.just(clientResponse);
    });
  }

  String json = """
      [
        {
          "id": "1",
          "title": "a title",
          "views": 100
        },
        {
          "id": "2",
          "title": "another title",
          "views": 200
        }
      ]
      """;
}
