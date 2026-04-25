package com.ITJobsBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ITJobsBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(ITJobsBackendApplication.class, args);
  }
}
