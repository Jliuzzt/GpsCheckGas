package com.cug.gpscheckgas.util;

import android.content.Context;
import android.util.Log;

import com.cug.greendao.DaoMaster;
import com.cug.greendao.DaoSession;
import com.cug.greendao.HistoryRoute;
import com.cug.greendao.HistoryRouteDao;
import com.cug.greendao.Notices;
import com.cug.greendao.NoticesDao;
import com.cug.greendao.PeopleInfo;
import com.cug.greendao.PeopleInfoDao;
import com.cug.greendao.PeriodInfo;
import com.cug.greendao.PeriodInfoDao;
import com.cug.greendao.RecordInfo;
import com.cug.greendao.RecordInfoDao;
import com.cug.greendao.RegionInfo;
import com.cug.greendao.RegionInfoDao;
import com.cug.greendao.SheetInfo;
import com.cug.greendao.SheetInfoDao;

import java.sql.Timestamp;
import java.util.List;

import de.greenrobot.dao.query.Query;

/**
 * Created by WF on 2016/3/29.
 */
public class DBHelper {

    private static final String TAG = DBHelper.class.getSimpleName();
    private static DBHelper instance;
    private static Context appContext;
    private static DaoSession daoSession;
    private static DaoMaster daoMaster;
    private PeopleInfoDao peopleInfoDao;
    private RecordInfoDao recordInfoDao;
    private RegionInfoDao regionInfoDao;
    private SheetInfoDao sheetInfoDao;
    private PeriodInfoDao periodInfoDao;
    private HistoryRouteDao historyRouteDao;
    private NoticesDao noticesDao;

    public static int SQL_VERSION = 1;

    public DBHelper() {
    }

    //单例模式，DBHelper只初始化一次
    public static DBHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DBHelper();
            if (appContext == null) {
                appContext = context.getApplicationContext();
            }
            instance.daoSession = getDaoSession(context);
            instance.peopleInfoDao = instance.daoSession.getPeopleInfoDao();
            instance.recordInfoDao = instance.daoSession.getRecordInfoDao();
            instance.regionInfoDao = instance.daoSession.getRegionInfoDao();
            instance.sheetInfoDao = instance.daoSession.getSheetInfoDao();
            instance.periodInfoDao = instance.daoSession.getPeriodInfoDao();
            instance.historyRouteDao = instance.daoSession.getHistoryRouteDao();
            instance.noticesDao = instance.daoSession.getNoticesDao();
        }

        return instance;

    }

    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            // OpenHelper helper = new DaoMaster.DevOpenHelper(context,
            // IDConstants.INFODATA_DB, null);
            DaoMaster.SCHEMA_VERSION = SQL_VERSION;
            DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(context,
                    Constants.DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }

        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

    public void insertPeopleInfo(PeopleInfo peopleInfo) {
        peopleInfoDao.insert(peopleInfo);
    }

    //插入数据
    public void insertSheetInfo(SheetInfo sheetInfo) {
        sheetInfoDao.insert(sheetInfo);
    }

    public void insertRegionInfo(RegionInfo regionInfo) {
        regionInfoDao.insert(regionInfo);
    }

    public void insertRecordInfo(RecordInfo recordInfo) {
        recordInfoDao.insert(recordInfo);
    }

    public void insertPeriodInfo(PeriodInfo periodInfo) {
        periodInfoDao.insert(periodInfo);
    }

    public void insertHistoryRoute(HistoryRoute historyRoute) {
        historyRouteDao.insert(historyRoute);
    }

    public void insertNoticesInfo(Notices notices) {
        noticesDao.insert(notices);
    }

    //修改数据
    public void updatePeopleInfo(PeopleInfo peopleInfo){
        peopleInfoDao.insertOrReplace(peopleInfo);
    }
    public void updateSheetInfo(SheetInfo sheetInfo) {
        sheetInfoDao.insertOrReplace(sheetInfo);
    }

    public void updateRegionInfo(RegionInfo regionInfo) {
        regionInfoDao.insertOrReplace(regionInfo);
        Log.e("updateRegionInfo", "updateRegionInfo");
    }

    public void updateRecordInfo(RecordInfo recordInfo) {
        recordInfoDao.insertOrReplace(recordInfo);
    }

    public void updateHistoryRoute(HistoryRoute historyRoute) {
        historyRouteDao.insertOrReplace(historyRoute);
    }

    public void updateNoticeInfo(Notices notices) {
        noticesDao.insertOrReplace(notices);
    }

    //取出表中所有数据
    public List<PeopleInfo> getAllPeopleInfo() {
        List<PeopleInfo> peopleInfos = null;
        peopleInfos = peopleInfoDao.loadAll();
        return peopleInfos;
    }

    public List<SheetInfo> getAllSheetInfo() {
        List<SheetInfo> sheetInfos = null;
        sheetInfos = sheetInfoDao.loadAll();
        return sheetInfos;
    }

    public List<RegionInfo> getAllRegionInfo() {
        List<RegionInfo> regionInfos = null;
        regionInfos = regionInfoDao.loadAll();
        return regionInfos;
    }

    public List<RecordInfo> getAllRecordInfo() {
        List<RecordInfo> recordInfos = null;
        recordInfos = recordInfoDao.loadAll();
        return recordInfos;
    }

    public List<PeriodInfo> getAllPeriodInfo() {
        List<PeriodInfo> periodInfos = null;
        periodInfos = periodInfoDao.loadAll();
        return periodInfos;
    }

    public List<Notices> getAllNotices() {
        List<Notices> notices = noticesDao.queryBuilder().where(NoticesDao.Properties.Id.isNotNull()).orderDesc(NoticesDao.Properties.Id).list();
        if (notices.size() > 0) {
            return notices;
        } else {
            return null;
        }
    }

    public List<HistoryRoute> getAllRoutes() {
        List<HistoryRoute> historyRoutes = historyRouteDao.loadAll();
        if (historyRoutes.size() > 0) {
            return historyRoutes;
        } else {
            return null;
        }
    }

    //查询

    public PeopleInfo getPeopleById(long peopleId) {
        List<PeopleInfo> peopleInfos = peopleInfoDao.queryBuilder().where(PeopleInfoDao.Properties.Id.eq(peopleId)).build().list();
        if (peopleInfos.isEmpty()) {
            return null;
        } else {
            return peopleInfos.get(0);
        }
    }

    public PeopleInfo getPeopleByNP(String userName, String passWord) {
        List<PeopleInfo> peopleInfos = peopleInfoDao.queryBuilder().where(PeopleInfoDao.Properties.PeopleName.eq(userName), PeopleInfoDao.Properties.PeoplePassword.eq(passWord)).build().list();
        if (peopleInfos.isEmpty()) {
            return null;
        } else {
            return peopleInfos.get(0);
        }
    }

    public int getBranchById(int peopleId) {
        List<PeopleInfo> peopleInfos = peopleInfoDao.queryBuilder().where(PeopleInfoDao.Properties.Id.eq(peopleId)).build().list();
        if (peopleInfos.isEmpty()) {
            return -1;
        } else {
            return peopleInfos.get(0).getBranchId();
        }
    }

    public String getSheetIntro(int sheetId) {
        List<SheetInfo> sheetInfos = sheetInfoDao.queryBuilder().where(SheetInfoDao.Properties.SheetId.eq(sheetId)).build().list();
        String sheetIntro = sheetInfos.get(0).getSheetIntro();
        return sheetIntro;
    }

    public List<SheetInfo> getSheetInfoByBId(int branchId) {
        List<SheetInfo> sheetInfos = sheetInfoDao.queryBuilder().where(SheetInfoDao.Properties.BranchId.eq(branchId)).build().list();
        if (sheetInfos.size() > 0) {
            return sheetInfos;
        } else {
            return null;
        }
    }

    public SheetInfo getSheetInfoById(int sheetId) {
        List<SheetInfo> sheetInfos = sheetInfoDao.queryBuilder().where(SheetInfoDao.Properties.SheetId.eq(sheetId)).build().list();
        if (sheetInfos.size() > 0) {
            return sheetInfos.get(0);
        } else {
            return null;
        }
    }

    public int getLengthById(int sheetId) {
        List<SheetInfo> sheetInfos = sheetInfoDao.queryBuilder().where(SheetInfoDao.Properties.SheetId.eq(sheetId)).build().list();
        if (sheetInfos.size() > 0) {
            int doneTime = sheetInfos.get(0).getLength();
            return doneTime;
        } else {
            return 0;
        }
    }

    public int getDoneTimeById(int sheetId) {
        List<SheetInfo> sheetInfos = sheetInfoDao.queryBuilder().where(SheetInfoDao.Properties.SheetId.eq(sheetId)).build().list();
        if (sheetInfos.size() > 0) {
            int doneTime = sheetInfos.get(0).getDoneTime();
            return doneTime;
        } else {
            return -1;
        }
    }

    public List<PeriodInfo> queryPeriod(int sheetId) {
        Query periodQuery = periodInfoDao.queryBuilder().where(
                PeriodInfoDao.Properties.SheetId.eq(sheetId)).build();
        List<PeriodInfo> periodInfos = periodQuery.list();
//        List<PeroidInfo> mList=new ArrayList<PeroidInfo>();
//        for(int i=0;i<peroidInfos.size();i++){
//            mList.add(peroidInfos.get(i).getPeroidShift());
//        }
        return periodInfos;
    }

    public List<RegionInfo> queryRegion(String periodShift, String periodtime, int sheetId) {

        List<PeriodInfo> periodInfos = periodInfoDao.queryBuilder().where(PeriodInfoDao.Properties.SheetId.eq(sheetId), PeriodInfoDao.Properties.PeriodShift.eq(periodShift), PeriodInfoDao.Properties.PeriodTime.eq(periodtime)).build().list();
//        List<Integer> mList = new ArrayList<Integer>();
//        for (int i = 0; i < peroidInfos.size(); i++) {
//            mList.add(peroidInfos.get(i).getPeroidId());
//        }
//        Log.e("sheetcount", mList.size() + "");

        List<RegionInfo> regionInfos = regionInfoDao.queryBuilder().where(RegionInfoDao.Properties.PeriodId.eq(periodInfos.get(0).getPeriodId())).build().list();

        return regionInfos;
    }

    public PeriodInfo getPeriodInfo(int periodId) {
        List<PeriodInfo> periodInfos = periodInfoDao.queryBuilder().where(PeriodInfoDao.Properties.PeriodId.eq(periodId)).build().list();
        if (periodInfos.size() > 0) {
            return periodInfos.get(0);
        } else {
            return null;
        }
    }

    public int queryPeriodId(String periodShift, String periodtime, int sheetId) {

        List<PeriodInfo> periodInfos = periodInfoDao.queryBuilder().where(PeriodInfoDao.Properties.SheetId.eq(sheetId), PeriodInfoDao.Properties.PeriodShift.eq(periodShift), PeriodInfoDao.Properties.PeriodTime.eq(periodtime)).build().list();
        List<RegionInfo> regionInfos = regionInfoDao.queryBuilder().where(RegionInfoDao.Properties.PeriodId.eq(periodInfos.get(0).getPeriodId())).build().list();
        int periodId = periodInfos.get(0).getPeriodId();
        return periodId;
    }

    public List<RegionInfo> getRegionByPeriod(int periodId) {
        List<RegionInfo> regionInfos = regionInfoDao.queryBuilder().where(RegionInfoDao.Properties.PeriodId.eq(periodId)).orderAsc(RegionInfoDao.Properties.RegionSort).list();
        return regionInfos;
    }

    public RegionInfo getOneRegionByPeriod(int periodId, int sort) {
        List<RegionInfo> regionInfos = regionInfoDao.queryBuilder().where(RegionInfoDao.Properties.PeriodId.eq(periodId), RegionInfoDao.Properties.RegionSort.eq(sort)).list();
        if (regionInfos.size() > 0) {
            return regionInfos.get(0);
        } else {
            return null;
        }
    }

    public RegionInfo getOneRegionByRegion(int periodId, int regionId) {
        List<RegionInfo> regionInfos = regionInfoDao.queryBuilder().where(RegionInfoDao.Properties.PeriodId.eq(periodId), RegionInfoDao.Properties.RegionId.eq(regionId)).list();
        if (regionInfos.size() > 0) {
            return regionInfos.get(0);
        } else {
            return null;
        }
    }

    public PeriodInfo getPeriodInfoBySheetId(int sheetId) {
        PeriodInfo periodInfo = null;
        if (sheetId >= 0) {
            periodInfo = periodInfoDao.load((long) sheetId);
        }
        return periodInfo;
    }

    public RecordInfo getRecordById(int recordId) {
        List<RecordInfo> recordInfos = recordInfoDao.queryBuilder().where(RecordInfoDao.Properties.RecordId.eq(recordId)).build().list();
        return recordInfos.get(0);
    }

    public int getPtrByPerId(int periodId, int regionId) {
        List<RegionInfo> regionInfos = regionInfoDao.queryBuilder().where(RegionInfoDao.Properties.PeriodId.eq(periodId), RegionInfoDao.Properties.RegionId.eq(regionId)).build().list();
        if (regionInfos.size() > 0) {
            int ptrId = regionInfos.get(0).getPtrId();
            return ptrId;
        } else {
            return -1;
        }
    }

    public String getStartTimeById(int recordId) {
        List<RecordInfo> recordInfos = recordInfoDao.queryBuilder().where(RecordInfoDao.Properties.RecordId.eq(recordId)).build().list();
        if (recordInfos != null) {
            String startTime = recordInfos.get(0).getRecordStart();
            return startTime;
        } else {
            return null;
        }
    }

    public String getEndTimeById(int recordId) {
        List<RecordInfo> recordInfos = recordInfoDao.queryBuilder().where(RecordInfoDao.Properties.RecordId.eq(recordId)).build().list();
        if (recordInfos != null) {
            String startTime = recordInfos.get(0).getRecordEnd();
            return startTime;
        } else {
            return null;
        }
    }


    public List<HistoryRoute> getRouteByGener(int generId, String start) {
        List<HistoryRoute> historyRoutes = historyRouteDao.queryBuilder().where(HistoryRouteDao.Properties.GenerId.eq(generId), HistoryRouteDao.Properties.StartTime.eq(start)).build().list();
        if (historyRoutes.size() > 0) {
            return historyRoutes;
        } else {
            return null;
        }
    }

    public HistoryRoute getRouteBySP(int sheetId, int perioidId) {
        List<HistoryRoute> historyRoutes = historyRouteDao.queryBuilder().where(HistoryRouteDao.Properties.SheetId.eq(sheetId), HistoryRouteDao.Properties.PeriodId.eq(perioidId)).build().list();
        if (historyRoutes.size() > 0) {
            return historyRoutes.get(0);
        } else {
            return null;
        }
    }

    public HistoryRoute getRouteBySPR(int sheetId, int perioidId, int regionId) {
        List<HistoryRoute> historyRoutes = historyRouteDao.queryBuilder().where(HistoryRouteDao.Properties.SheetId.eq(sheetId), HistoryRouteDao.Properties.PeriodId.eq(perioidId), HistoryRouteDao.Properties.EndTime.eq(regionId)).build().list();
        if (historyRoutes.size() > 0) {
            return historyRoutes.get(0);
        } else {
            return null;
        }
    }

    public HistoryRoute getRouteById(int routeId) {
        List<HistoryRoute> historyRoutes = historyRouteDao.queryBuilder().where(HistoryRouteDao.Properties.Id.eq(routeId)).build().list();
        if (historyRoutes.size() > 0) {
            return historyRoutes.get(0);
        } else {
            return null;
        }
    }

    public void deleteAllData() {
        recordInfoDao.deleteAll();
        regionInfoDao.deleteAll();
        sheetInfoDao.deleteAll();
        periodInfoDao.deleteAll();
    }

    public void deleteRecordInfo() {
        recordInfoDao.deleteAll();
    }

    public void deleteSheetInfo() {
        sheetInfoDao.deleteAll();
    }

    public void deletePeriodInfo() {
        periodInfoDao.deleteAll();
    }

    public void dailyDataClear() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        int hour = ts.getHours();
        int minutes = ts.getMinutes();
        int seconds = ts.getSeconds();
        if (hour == 23 && minutes == 59) {
            clearPeopleStatus(true);
            clearSheetStatus(true);
            clearRegionStatus(true);
            clearRecordStatus(true);
//            clearRouteStatus(true);
            clearNoticesStatus(true);
            Log.d("LongRunningService","已删除");
        }
    }

    public void initDataClear(){
        clearPeopleStatus(true);
        clearSheetStatus(true);
        clearRegionStatus(true);
        clearRecordStatus(true);
        clearNoticesStatus(true);
        Log.d("LongRunningService","已删除");
    }

    private void clearPeopleStatus(boolean timeOut) {
        if (timeOut) {
            List<PeopleInfo> peopleInfos = null;
            peopleInfos = peopleInfoDao.loadAll();
            if (peopleInfos != null) {
                for (int i = 0; i < peopleInfos.size(); i++) {
                    PeopleInfo peopleInfo = peopleInfos.get(i);
                    peopleInfo.setIsLogin(false);
                    peopleInfoDao.insertOrReplace(peopleInfo);
                }
            }
        }
    }

    private void clearSheetStatus(boolean timeOut) {
        if (timeOut) {
            List<SheetInfo> sheetInfos = sheetInfoDao.loadAll();
            if (sheetInfos != null) {
                for (int i = 0; i < sheetInfos.size(); i++) {
                    SheetInfo sheetInfo = sheetInfos.get(i);
                    sheetInfo.setIsDownLoad(false);
                    sheetInfo.setDoneTime(0);
                    sheetInfo.setLength(-1);
                    sheetInfo.setIsDoing(false);
                    sheetInfo.setIsDone(false);
                    sheetInfoDao.insertOrReplace(sheetInfo);
                }
            }
        }
    }

    private void clearRegionStatus(boolean timeOut) {
        if (timeOut) {
            List<RegionInfo> regionInfos = regionInfoDao.loadAll();
            if (regionInfos != null) {
                for (int i = 0; i < regionInfos.size(); i++) {
                    RegionInfo regionInfo = regionInfos.get(i);
                    regionInfo.setIsDone(false);
                    regionInfoDao.insertOrReplace(regionInfo);
                }
            }
        }
    }

    private void clearRecordStatus(boolean timeOut) {
        if (timeOut) {
            recordInfoDao.deleteAll();
        }
    }

    private void clearRouteStatus(boolean timeOut) {
        if (timeOut) {
            historyRouteDao.deleteAll();
        }
    }

    private void clearNoticesStatus(boolean timeOut) {
        if (timeOut) {
            noticesDao.deleteAll();
        }
    }
}