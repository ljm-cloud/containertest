package com.example.containertest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.containertest.mapper")
@ServletComponentScan("com.example.containertest")
@ComponentScan("com.example.containertest")
public class ContainertestApplicationEnv extends BaseTestEnv{

	public static void main(String[] args) {
		SpringApplication.run(ContainertestApplicationEnv.class, args);
	}

}
