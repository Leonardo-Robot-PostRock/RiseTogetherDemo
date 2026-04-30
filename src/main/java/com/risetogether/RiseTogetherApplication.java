package com.risetogether;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RiseTogetherApplication {

  public static void main(String[] args) {
    SpringApplication.run(RiseTogetherApplication.class, args);
  }
}
