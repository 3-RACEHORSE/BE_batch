package com.meetplus.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BatchApplication {

	public static void main(String[] args) {
		// application 전체 timezone을 UTC로 설정
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

		SpringApplication.run(BatchApplication.class, args);
	}

}
