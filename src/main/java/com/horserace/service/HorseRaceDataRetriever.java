package com.horserace.service;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Calendar.MONDAY;
import static java.util.Calendar.TUESDAY;

@Component
public class HorseRaceDataRetriever {
    private Logger logger = LoggerFactory.getLogger(HorseRaceDataRetriever.class);

    @Autowired
    private MsAccessDBConnection msAccessDBConnection;

    @Scheduled(cron = "0 0/1 * * * ?") //Start the job every 1 minute, we should make it configurable
    public void runScheduledTask() {
        logger.info("Cron Task :: Execution Time - {}", LocalDateTime.now().toString());

        retrieveWinplaodd(getNextRacingDate());
    }

    private LocalDate getNextRacingDate() {
        LocalDate racingDate = LocalDate.now();

        for (int i = 0; i < 7; i++) {
            racingDate = racingDate.plusDays(i);
            if (racingDate.getDayOfWeek() == 3 || racingDate.getDayOfWeek() == 7)
                return racingDate;
        }

        return null;
    }

    private void retrieveWinplaodd(LocalDate date) {
        String urlPattern = null;

        if (date.getDayOfWeek() == 7)
            urlPattern = "https://bet.hkjc.com/racing/getJSON.aspx?type=winplaodds&date=%s&venue=HV&start=%d&end=%d";
        else
            urlPattern = "https://bet.hkjc.com/racing/getJSON.aspx?type=winplaodds&date=%s&venue=ST&start=%d&end=%d";

        for (int i = 1; i <= 10; i++) {
            try {
                URL url = new URL(String.format(urlPattern, date.toString(), i, i));
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                System.out.println(response.toString());

                JSONObject myResponse = new JSONObject(response.toString());
                String peiLvStr = (String) myResponse.get("OUT");
                String realPeiLvStr = peiLvStr.substring(peiLvStr.indexOf(";") + 1, peiLvStr.indexOf("#"));

                String[] elements = realPeiLvStr.split(";");
                for (int j = 0; j < elements.length; j++) {
                    String[] subElements = elements[i].split("=");
                    if (subElements.length < 2)
                        continue;

                    org.joda.time.LocalDateTime ldt = date.toLocalDateTime(new LocalTime());

                    Map<String, Object> racingDataMap = new HashMap<String, Object>();
                    racingDataMap.put("tag", new java.sql.Date(ldt.toDateTime(DateTimeZone.UTC).getMillis()));//toString("yyyy-MM-dd HH:mm:ss")
                    racingDataMap.put("type1", 1);
                    racingDataMap.put("date1", date.toString("dd/MM/yyyy"));
                    racingDataMap.put("weekday", getWeekDayStr(date));
                    racingDataMap.put("race", String.valueOf(i));
                    racingDataMap.put("horse_number", Integer.valueOf(subElements[0]));
                    racingDataMap.put("pair1", 0);
                    racingDataMap.put("pair2", 0);
                    racingDataMap.put("rate", Double.valueOf(subElements[1]));

                    msAccessDBConnection.addRow("result", racingDataMap);
                }
                System.out.println(myResponse.toString());
            } catch (Exception e) {
            }

        }
    }

    private String getWeekDayStr(LocalDate date) {
        if (date.getDayOfWeek() == 1)
            return "星期一";
        if (date.getDayOfWeek() == 2)
            return "星期二";
        if (date.getDayOfWeek() == 3)
            return "星期三";
        if (date.getDayOfWeek() == 4)
            return "星期四";
        if (date.getDayOfWeek() == 5)
            return "星期五";
        if (date.getDayOfWeek() == 6)
            return "星期六";
        return "星期日";
    }
}
