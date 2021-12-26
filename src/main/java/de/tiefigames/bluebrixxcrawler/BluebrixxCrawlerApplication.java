package de.tiefigames.bluebrixxcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BluebrixxCrawlerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BluebrixxCrawlerApplication.class, args);
    }

}
