package com.example.webclient;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Person {

  String name;
  String job;

  @JsonCreator
  public Person(@JsonProperty("name") String name, @JsonProperty("job") String job) {
    this.job = job;
    this.name = name;

  }

}
