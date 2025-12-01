package com.sui.haedal;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.sui.haedal.mapper")
public class HaedalServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(HaedalServerApplication.class, args);
	}

}
