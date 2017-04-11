package com.cug.gpscheckgas.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by WF on 2016/3/29.
 */
public class Constants {
    public final static String DB_NAME = "MyDataBase.db";

    public static final String BASE_URL = "http://1.gpsrouting.applinzi.com/";

    public static final String LOGIN_DATA_STRING = BASE_URL + "WorkerLoginServlet";
    public static final String SHEET_DATA_STRING = BASE_URL + "GetAllSheetServlet";
    public static final String QUESTION_DATA_STRING = BASE_URL + "GetAllQuestionServlet";
    public static final String PERIOD_DATA_STRING = BASE_URL + "GetAllPeriodServlet";
    public static final String REGION_DATA_STRING = BASE_URL + "GetAllRegionServlet";
    public static final String RECORD_DATA_STRING = BASE_URL + "GetAllRecordServlet";
    public static final String UPLOADFILE_DATA_STRING = BASE_URL + "UploadFileServlet";
    public static final String UPLOADRECORD_DATA_STRING = BASE_URL + "AddRecordServlet";
    public static final String CHANGERECORD_DATA_STRING = BASE_URL + "ChangeRecordServlet";
    public static final String CHANGEGPS_DATA_STRING = BASE_URL + "ChangeRegionServlet";
    public static final String ADDREGION_DATA_STRING = BASE_URL + "AddRegionServlet";
    public static final String POST_ROUTE_DATA_STRING = BASE_URL + "PostRouteServlet";
    public static final String LOGOUT_POSITION_DATA_STRING = BASE_URL + "LogoutPosition";
    public static final String ROUTE_DATA_STRING = BASE_URL + "GetRouteServlet";
    public static final String FAULT_DATA_STRING = BASE_URL + "ManageFaultmsgServlet?action=newfault";
    public static final String ALARM_DATA_STRING = BASE_URL + "PushAlarmServlet";

    public static int peopleId = -1;
    public static int branchId = -1;
    public static int team_id = -1;
    public static String teamName = null;
    public static String gpsInfo;


//    public static int sheetId = -1;
//    public static int regioinId = -1;
    public static int checkTime = -1;
//    public static int doneTime = 0;

//    public static int sid = -1;
//    public static int length = -2;
//    public static int doneTime2 = -3;

    public static String recordStart = null, recordEnd = null, recordSub = null;

//    public static HashMap<Integer, Boolean> isDownload = new HashMap<Integer, Boolean>();
    public static Map<Integer, Integer> isSelected = new HashMap<Integer, Integer>();
    public static Map<String, String> rtr = new HashMap<String, String>();

//    public static List<PeroidInfo> peroidInfos;

//    public static List<String> title = new ArrayList<String>();
//    public static List<String> possasws= new ArrayList<String>();
//    public static List<String> normalasws= new ArrayList<String>();
//    public static int  resultLen= -1;

    public static Map<Integer,List<String>> title = new HashMap<Integer,List<String>>();
    public static Map<Integer,List<String>> possasws = new HashMap<Integer,List<String>>();
    public static Map<Integer,List<String>> normalasws = new HashMap<Integer,List<String>>();
    public static Map<Integer,List<String>> type = new HashMap<Integer,List<String>>();
    public static Map<Integer,Integer> resultLen = new HashMap<Integer,Integer>();

//    public static Boolean[] flag = {false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false, false};
//    public static String tempRecordId = "0000";
    public static Boolean isUploadOver = false;
    public static Boolean isMapLoad = false;
    public static Boolean isLogout = false;

    public static List<Integer> ScanRegionids = new ArrayList<Integer>();//巡检区域扫描记录id，防止重复巡检，在提交巡检表时清空。

    public static void clear(){
        isLogout = true;
        peopleId = -1;
        branchId = -1;
        teamName = null;
        gpsInfo = null;
        recordStart = null;
        recordEnd = null;
        recordSub = null;
        isUploadOver = false;
        isMapLoad = false;
    }

    public static void logout(){
        peopleId = -1;
        branchId = -1;
        teamName = null;
        recordStart = null;
        recordEnd = null;
        recordSub = null;
        isUploadOver = false;
        isMapLoad = false;
    }

    public static void clearQuestion(){
        title.clear();
        possasws.clear();
        normalasws.clear();
    }
}
