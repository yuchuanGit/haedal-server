package com.sui.haedal.common;

import com.sui.haedal.model.vo.BorrowRateLineVo;
import com.sui.haedal.model.vo.TimePeriodStatisticsVo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtil {
    public static final String YMD_HMS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_YMD = "yyyy-MM-dd";
    public static final String DATE_FORMAT_YMD_HM = "yyyy-MM-dd HH:mm";
    public static final String DATE_FORMAT_MD = "MM/dd";
    public static final String DATE_FORMAT_MD_H = "MM/dd HH";


    public static List<TimePeriodStatisticsVo> timePeriodDayGenerateNew(LocalDateTime start, LocalDateTime end, boolean isHours,int createPoolTimeHours) {
        List<TimePeriodStatisticsVo> lines = new ArrayList<>();
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_YMD));
        int idx = 0;
        int generateHoursStartQuantity = 0;//生成小时起始数量,默认一天0点-23点
        while (!start.isAfter(end)) {
            if (isHours) {
                if(idx==0){
                    generateHoursStartQuantity = createPoolTimeHours;
                }else{
                    generateHoursStartQuantity = 0;
                }
                String startDate = start.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YMD));
                if (currentDate.equals(startDate)) {
                    lines.addAll(dayGenerate24HoursNew(startDate, true,generateHoursStartQuantity));
                } else {
                    lines.addAll(dayGenerate24HoursNew(startDate, false,generateHoursStartQuantity));
                }
            } else {
                TimePeriodStatisticsVo b = initTimePeriodStatistics(start, false);
                lines.add(b);
            }
            // 日期加1天
            start = start.plusDays(1);
            idx+=1;
        }

        return lines;
    }


    /**
     * 生成时间区间内的BorrowLine列表
     * @param start 开始时间
     * @param end 结束时间
     * @param isHours 是否按小时生成
     * @return BorrowLine列表
     */
    public static List<BorrowRateLineVo> timePeriodDayGenerate(LocalDateTime start, LocalDateTime end, boolean isHours) {
        List<BorrowRateLineVo> lines = new ArrayList<>();
        String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT_YMD));

        while (!start.isAfter(end)) {
            if (isHours) {
                String startDate = start.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YMD));
                if (currentDate.equals(startDate)) {
                    lines.addAll(dayGenerate24Hours(startDate, true));
                } else {
                    lines.addAll(dayGenerate24Hours(startDate, false));
                }
            } else {
                BorrowRateLineVo b = initBorrowLineTime(start, false);
                lines.add(b);
            }

            // 日期加1天
            start = start.plusDays(1);
        }

        return lines;
    }

    /**
     * 生成一天内的24小时BorrowLine
     * @param yyyyMMdd 日期（格式：yyyy-MM-dd）
     * @param isCurrentDate 是否是当天
     * @return 小时粒度的BorrowLine列表
     */
    public static List<BorrowRateLineVo> dayGenerate24Hours(String yyyyMMdd, boolean isCurrentDate) {
        List<BorrowRateLineVo> lines = new ArrayList<>();
        String dateTimeStr = yyyyMMdd + " 00:00";

        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(DATE_FORMAT_YMD_HM));
        lines.add(initBorrowLineTime(dateTime, true));

        int length = 23;
        if (isCurrentDate) {
            LocalDateTime now = LocalDateTime.now();
            length = now.getHour();
        }

        for (int i = 0; i < length; i++) {
            dateTime = dateTime.plusHours(1);
            lines.add(initBorrowLineTime(dateTime, true));
        }

        return lines;
    }

    public static List<TimePeriodStatisticsVo> dayGenerate24HoursNew(String yyyyMMdd, boolean isCurrentDate,int generateHoursStartQuantity) {
        List<TimePeriodStatisticsVo> lines = new ArrayList<>();
        String dateTimeStr = yyyyMMdd + " 00:00";

        if (generateHoursStartQuantity > 0) {
            dateTimeStr = yyyyMMdd + " "+generateHoursStartQuantity+":00";
        }
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(DATE_FORMAT_YMD_HM));
        lines.add(initTimePeriodStatistics(dateTime, true));

        int length = 23;
        if (isCurrentDate) {
            LocalDateTime now = LocalDateTime.now();
            length = now.getHour();
        }

        for (int i = generateHoursStartQuantity; i < length; i++) {
            dateTime = dateTime.plusHours(1);
            lines.add(initTimePeriodStatistics(dateTime, true));
        }

        return lines;
    }

    /**
     * 初始化BorrowLine对象
     * @param dateTime 时间
     * @param isHours 是否小时粒度
     * @return 初始化后的BorrowLine
     */
    private static BorrowRateLineVo initBorrowLineTime(LocalDateTime dateTime, boolean isHours) {
        BorrowRateLineVo m = new BorrowRateLineVo();
        m.setTransactionTime(dateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YMD_HM)));
        m.setDateUnit(dateTime.format(getDateGroupFormatter(isHours)));
        return m;
    }

    /**
     * 初始化BorrowLine对象
     * @param dateTime 时间
     * @param isHours 是否小时粒度
     * @return 初始化后的BorrowLine
     */
    private static TimePeriodStatisticsVo initTimePeriodStatistics(LocalDateTime dateTime, boolean isHours) {
        TimePeriodStatisticsVo m = new TimePeriodStatisticsVo();
        if(isHours){
            m.setTransactionTime(dateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YMD_HM)));
        }else{
            m.setTransactionTime(dateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YMD))+" 00:00");
        }
        m.setDateUnit(dateTime.format(getDateGroupFormatter(isHours)));
        return m;
    }


    /**
     * 获取日期分组格式化器
     * @param isHours 是否小时粒度
     * @return 格式化器
     */
    private static DateTimeFormatter getDateGroupFormatter(boolean isHours) {
        return isHours ? DateTimeFormatter.ofPattern(DATE_FORMAT_MD_H):DateTimeFormatter.ofPattern(DATE_FORMAT_MD) ;
    }

    public static String LocalDateTimeFormat(LocalDateTime localDateTime,String format){
        return localDateTime.format(DateTimeFormatter.ofPattern(format));
    }

    public static String dateFormat(Date date,String format){
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public static String dateGroupFormat(boolean isHours,Date date){
        String format = isHours?DATE_FORMAT_MD_H:DATE_FORMAT_MD;
        return dateFormat(date, format);
    }

    public static Date dateStrFormatDate(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat(YMD_HMS);
        try {
            Date date = sdf.parse(dateStr);
            return date;
        } catch (ParseException e) {
            e.printStackTrace(); // 格式不匹配时抛出 ParseException
        }
        return null;
    }
}
