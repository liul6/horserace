package com.horserace;

import com.horserace.domain.Users;
import org.apache.ibatis.type.MappedTypes;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@MappedTypes(Users.class)
@MapperScan("com.horserace.mapper")
@SpringBootApplication
@EnableScheduling
public class HorseRaceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HorseRaceApplication.class, args);
    }
}
