package com.ihren.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//TODO: create a monolith application (not multi-module app with one sub-module)
//TODO: finish uml diagram
//TODO: add diagram in README as a screenshot or so
@SpringBootApplication
public class ProcessorApplication {
	public static void main(String[] args) {
		SpringApplication.run(ProcessorApplication.class, args);
	}
}