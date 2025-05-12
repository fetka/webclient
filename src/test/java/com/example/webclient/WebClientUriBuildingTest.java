package com.example.webclient;

import com.fasterxml.jackson.databind.JsonNode;
import io.netty.handler.logging.LogLevel;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.support.HttpComponentsHeadersAdapter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

public class WebClientUriBuildingTest {

  @Test
  public void test1() {
    // https://dummyjson.com/products/search?q=phone
    String segment = "search";
//    UriBuilder uriBuilder = UriComponentsBuilder.fromPath("/products/");
//    uriBuilder.path("path");
    HttpClient httpClient = HttpClient
        .create()
        .wiretap("reactor.netty.http.client.HttpClient",
            LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL);
    // Create an empty MultiValueMap
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("q", "phone");
    map.add("limit", "1");
//    uriBuilder.queryParams(map);
//    System.err.println(uriBuilder.toUriString());
    WebClient client = WebClient.builder()
        .clientConnector(new ReactorClientHttpConnector(httpClient))
        .baseUrl("https://dummyjson.com").build();

    JsonNode block = client.get()
        .uri(uriBuilder1 ->
            uriBuilder1
                .path("products/{segment}")
                .queryParams(map)
                .build(segment))
        .retrieve()
        .bodyToMono(JsonNode.class)
        .map(this::deserialize)
        .block();
    System.err.println("block: \n" + block);
  }

  private JsonNode deserialize(JsonNode response) {
    return response;
  }

}
