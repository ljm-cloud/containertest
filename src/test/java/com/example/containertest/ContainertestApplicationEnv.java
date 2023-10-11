package com.example.containertest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ContainertestApplicationEnv extends BaseTestEnv{

	public static void main(String[] args) {
		SpringApplication.run(ContainertestApplicationEnv.class, args);
	}

}
