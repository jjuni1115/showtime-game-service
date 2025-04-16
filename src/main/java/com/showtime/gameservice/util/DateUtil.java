package com.showtime.gameservice.util;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@Component
public class DateUtil {


    /**
     * 현재시간 가져오는 메소드
     * format 입력 ex) "yyyyMMdd"
     *
     * @param format
     * @return
     */
    public static String getNow(String format) {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul")).truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * 현재시간 LocalDateTime
     *
     * @return
     */
    public static LocalDateTime getNow() {
        return LocalDateTime.now(ZoneId.of("Asia/Seoul")).truncatedTo(ChronoUnit.SECONDS);
    }

    /**
     * 날짜 format 변경 메소드
     * "yyyyMMdd" -> "yyyy-MM-dd"
     *
     * @param date
     * @param baseFormat
     * @param targetFormat
     * @return
     */
    public static String converDateString(String date, String baseFormat, String targetFormat) {

        if (date == null) {
            return "";
        }

        try {
            DateTimeFormatter inpuFormatter = DateTimeFormatter.ofPattern(baseFormat);
            LocalDate date1 = LocalDate.parse(date, inpuFormatter);

            DateTimeFormatter ouputFormatter = DateTimeFormatter.ofPattern(targetFormat);
            return date1.format(ouputFormatter);
        } catch (Exception e) {
            return "";
        }


    }


    public static LocalDateTime convertStringToLocalDateTime(String date, String format){

        DateTimeFormatter formatter = null;

        if(format == null){
            formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        }else{
            formatter = DateTimeFormatter.ofPattern(format);
        }

        return LocalDateTime.parse(date, formatter);




    }

    /**
     * 시간 포맷 변경
     * "HHmmss" -> "HH:mm:ss"
     *
     * @param time
     * @param baseFormat
     * @param targetFormat
     * @return
     */
    public static String convertTimeString(String time, String baseFormat, String targetFormat) {

        if (time == null) {
            return "";
        }

        try {
            DateTimeFormatter inpuFormatter = DateTimeFormatter.ofPattern(baseFormat);
            LocalTime time1 = LocalTime.parse(time, inpuFormatter);

            DateTimeFormatter ouputFormatter = DateTimeFormatter.ofPattern(targetFormat);

            return time1.format(ouputFormatter);
        } catch (Exception e) {
            return "";
        }

    }

    /**
     * 기준일자에서 날짜 빼기
     *
     * @param inputDate
     * @param day
     * @return
     */
    public static String minusDay(String inputDate, int day) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(inputDate, formatter);
        LocalDate resultDate = date.minus(day, ChronoUnit.DAYS);
        return resultDate.format(formatter);


    }


}
