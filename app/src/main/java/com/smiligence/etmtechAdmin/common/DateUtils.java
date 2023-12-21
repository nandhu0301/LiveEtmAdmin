package com.smiligence.etmtechAdmin.common;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.smiligence.etmtechAdmin.common.Constant.DATE_AND_TIME;
import static com.smiligence.etmtechAdmin.common.Constant.DATE_FORMAT;
import static com.smiligence.etmtechAdmin.common.Constant.DATE_FORMAT_YYYYMD;
import static com.smiligence.etmtechAdmin.common.Constant.DATE_TIME_FORMAT;
import static com.smiligence.etmtechAdmin.common.Constant.DATE_TIME_FORMAT_NEW;


public class DateUtils
{
    static Calendar calendar = Calendar.getInstance();

   /* public static String fetchCurrentDate (){
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YYYYMD);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }*/
    public static String fetchCurrentDate (){
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

    public static String fetchCurrentDateAndTime (){
        DateFormat dateFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetch7days(){
        LocalDate now= LocalDate.now();
        LocalDate lastSeven =now.plusDays(7);
        String lastSevenDays = lastSeven.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
        System.out.println("lastSevenDays"+lastSevenDays);
        return lastSevenDays;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchNextDay(){
        LocalDate now= LocalDate.now();
        LocalDate lastSeven =now.plusDays(1);
        String lastSevenDays = lastSeven.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
        System.out.println("lastSevenDays"+lastSevenDays);
        return lastSevenDays;
    }


    public static String fetchDayOfTheWeek(){
        SimpleDateFormat dateFormat = new SimpleDateFormat( "EEEE" );
        String dayOfTheWeek = dateFormat.format ( new Date() );

        return dayOfTheWeek;
    }

    public static String fetchCurrentDateTime (){
        DateFormat dateFormat = new SimpleDateFormat(DATE_AND_TIME);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

    public static String fetchTommrow(){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMD);
        calendar.add(Calendar.DATE, 1);
        String yesterdayDate = sdf.format(calendar.getTime());
        return yesterdayDate;
    }
    public static String fetchDateAfterWeek(){
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM dd, YYYY");
        calendar.add(Calendar.DATE, 7);
        String sevendays = sdf.format(calendar.getTime());
        System.out.println("FGHN"+sevendays);
        return sevendays;
    }


    public static String fetchTime(){
        SimpleDateFormat timeFormat = new SimpleDateFormat( "hh:mm a" );
        String time = timeFormat.format ( calendar.getTime() );
        return time;
    }

    public static String fetchDateAndTimeInMilliSecond(){

        //Pattern for showing milliseconds in the time "SSS"
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String stringDate = sdf.format(new Date());
        //String dateTime = sdf.format(calendar.getTime());
        return stringDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String addDatesToDate(int noOfDatesTobeAdded){
        //Adding one Day to the current date
        LocalDate date =  LocalDate.now().plusDays(noOfDatesTobeAdded);

        //Adding one Day to the given date
        // date = LocalDate.of(2016, 10, 14).plusDays(1);
        // System.out.println("Adding one day to the given date: "+date);
        return date.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getPastDate(int noOfDatesTobeMinused){
        //Adding one Day to the current date
        LocalDate date =  LocalDate.now().plusDays(noOfDatesTobeMinused);
        return date.toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchYesterdayDate(){
        LocalDate now=LocalDate.now();
        LocalDate yesterday =now.minusDays(1);
        String yesterdayDate = yesterday.format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        return yesterdayDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchLast7days(){
        LocalDate now=LocalDate.now();
        LocalDate lastSeven =now.minusDays(7);
        String lastSevenDays = lastSeven.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));

        return lastSevenDays;
    }
    public static String fetchTommorow(){
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_YYYYMD);
        calendar.add(Calendar.DATE, +1);
        String yesterdayDate = sdf.format(calendar.getTime());

        return yesterdayDate;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  static String fetchLast30Days(){
        LocalDate now=LocalDate.now();
        LocalDate thirty=now.minusDays(33);
        String thirthDate = thirty.format(DateTimeFormatter.ofPattern(DATE_FORMAT_YYYYMD));
        Log.e("thirthDate",thirthDate);
        return thirthDate;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean pastDate(String inputDate) {
        // inputDate = "17/6/2021";
        // inputDate = "17/06/2020";
        //inputDate = "17/05/2020";
        Date date= null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // boolean date=LocalDate.now().isAfter(inputDate);
        return new Date().before(date);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static boolean ValidatePastDate(String inputDate) {
        // inputDate = "17/6/2021";
        // inputDate = "17/06/2020";
        //inputDate = "17/05/2020";
        Date date= null;
        try {
            date = new SimpleDateFormat("dd/MM/yyyy").parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // boolean date=LocalDate.now().isAfter(inputDate);
        return new Date().after(date);
    }
    public static ArrayList<String> fetchTimeInterval() {

        int begin = 0; // Starts at mid nigth 12 AM
        int end = 1439; //23*60 + 59; // till mid nigth 11:59 PM
        int interval = 120; // every 2 hours

        ArrayList<String> timeArrayList = new ArrayList<>();

        for (int time = begin; time <= end; time += interval) {
            timeArrayList.add(String.format("%02d:%02d", time / 60, time % 60));
        }
        return timeArrayList;
    }

    /**
     * @param  target  hour to check
     * @param  start   interval start
     * @param  end     interval end
     * @return true    true if the given hour is between
     */
    public static boolean isHourInInterval(String target, String start, String end) {
        return ((target.compareTo(start) >= 0)
                && (target.compareTo(end) <= 0));
    }
    public static String fetchTimewithSeconds(String dateTime){
        SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm:ss"  );
        Date date = null;
        try {
            date = new SimpleDateFormat(DATE_TIME_FORMAT_NEW).parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = timeFormat.format(date);
        return time;
    }

    public static String fetchTime(String dateTime){
        SimpleDateFormat timeFormat = new SimpleDateFormat( "HH:mm"  );
        Date date = null;
        try {
            date = new SimpleDateFormat(DATE_TIME_FORMAT_NEW).parse(dateTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String time = timeFormat.format(date);
        return time;
    }


    public static String fetchCurrentTime (){
        DateFormat dateFormat = new SimpleDateFormat(Constant.DATE_TIME_FORMAT_NEW);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

    public static String fetchFormatedCurrentDate ()
    {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_YYYYMD);
        String currentDateAndTime = dateFormat.format(new Date());
        return currentDateAndTime;
    }

}
