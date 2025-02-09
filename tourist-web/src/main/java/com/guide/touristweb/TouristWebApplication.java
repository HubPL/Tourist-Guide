package com.guide.touristweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
        "com.guide.touristweb",
        "com.guide.touristmodel",
        "com.guide.touristrepository",
        "com.guide.touristservice"
})
@EnableJpaRepositories(basePackages = "com.guide.touristrepository.repository")
@EntityScan(basePackages = "com.guide.touristmodel.entity")
public class TouristWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(TouristWebApplication.class, args);
    }
}
