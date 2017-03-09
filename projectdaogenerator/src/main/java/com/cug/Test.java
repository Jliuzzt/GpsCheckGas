package com.cug;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by WF on 2016/4/14.
 */
public class Test {
    public static boolean isOver=false;



    public static void main(String[] args){
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        System.out.println(System.currentTimeMillis());
        System.out.println(ts);
        System.out.println(timestampToString(new Timestamp(System.currentTimeMillis())));
        System.out.println(ts.getTime());
        System.out.println(ts.getHours());
        System.out.println(ts.getMinutes());
        System.out.println(ts.getSeconds());
    }
    private static String timestampToString(Timestamp ts) {
        String tsStr = "";
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            tsStr = sdf.format(ts);
            return tsStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public static void timer(){
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if(!isOver) {
                    System.out.println("--------------klgjgljlk-----------");
                }else{
                    timer.cancel();
                }
            }
        },1000,2000);
    }

}
