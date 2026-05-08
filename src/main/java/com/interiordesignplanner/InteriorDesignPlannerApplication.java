package com.interiordesignplanner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class InteriorDesignPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(InteriorDesignPlannerApplication.class, args);
	}

}
