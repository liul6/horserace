package com.horserace.service;

import com.horserace.domain.Users;
import com.horserace.mapper.UsersMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class HorseRaceDataRetriever {
    private Logger logger = LoggerFactory.getLogger(HorseRaceDataRetriever.class);

    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private HKJCResponseParser hkjcResponseParser;

    @Scheduled(cron = "0 0/1 * * * ?") //Start the job every 1 minute, we should make it configurable
    public void runScheduledTask() {
        logger.info("Cron Task :: Execution Time - {}", LocalDateTime.now().toString());

        Users users = new Users();

        users.setName("Joey Liu");
        users.setSalary(20000L);

        usersMapper.insert(users);
    }

    private String retrieveDataFromHKJC() {
        //launch a HTTP call to HKJC horsing racing website

        return "something";
    }
}
