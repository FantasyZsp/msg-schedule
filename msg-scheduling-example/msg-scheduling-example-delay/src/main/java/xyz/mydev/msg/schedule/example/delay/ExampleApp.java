package xyz.mydev.msg.schedule.example.delay;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ZSP
 */
@SpringBootApplication
@MapperScan
public class ExampleApp {
  public static void main(String[] args) {
    SpringApplication.run(ExampleApp.class, args);
  }
}
