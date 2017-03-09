package com.cug;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class ProjectDaoGenerator {
    public static void main(String[] args) throws Exception {

        // 正如你所见的，你创建了一个用于添加实体（Entity）的模式（Schema）对象。
        // 两个参数分别代表：数据库版本号与自动生成代码的包路径。
        Schema schema = new Schema(1, "com.cug.greendao");

        // 一旦你拥有了一个 Schema 对象后，你便可以使用它添加实体（Entities）了。
        addPeople(schema);
        addRegion(schema);
        addSheet(schema);
        addRecord(schema);
        addPeriod(schema);
        addRoute(schema);
        addNotices(schema);
        //设置目标文件路径
        new DaoGenerator().generateAll(schema, "E:/AndroidStudioProjects/GpsCheckGas/app/src/main/java-gen");
    }

    /**
     * @param schema
     */
    private static void addPeople(Schema schema) {
        Entity people = schema.addEntity("PeopleInfo");
        people.addIdProperty();
        people.addStringProperty("peopleName");
        people.addStringProperty("name");
        people.addIntProperty("teamId");
        people.addStringProperty("peoplePassword");
        people.addIntProperty("branchId");

        //增加字段
        people.addStringProperty("branchName");
        people.addStringProperty("branchType");
        people.addStringProperty("company");
        people.addStringProperty("teamName");
        people.addBooleanProperty("isLogin").notNull();
    }
    /**
     * @param schema
     */
    private static void addRegion(Schema schema) {
        // 一个实体（类）就关联到数据库中的一张表，此处表名为「RegionInfo」（既类名）
        Entity region = schema.addEntity("RegionInfo");

        // greenDAO 会自动根据实体类的属性值来创建表字段，并赋予默认值
        region.addIdProperty();
        region.addIntProperty("regionId").notNull();
        region.addIntProperty("sheetId");
        region.addStringProperty("branch");
        region.addStringProperty("regionName");
        region.addStringProperty("regionIntro");
        region.addStringProperty("regionGps");
        region.addStringProperty("regionQrcode");
        region.addIntProperty("regionSort");
        region.addStringProperty("regionType");
        region.addStringProperty("regionRange");
        region.addIntProperty("periodId");
        region.addIntProperty("ptrId");

        //增加字段
        region.addBooleanProperty("isDone");
    }

    /**
     * @param schema
     */
    private static void addSheet(Schema schema) {
        //创建表名
        Entity sheet = schema.addEntity("SheetInfo");
        //创建字段
        sheet.addIdProperty();
        sheet.addIntProperty("sheetId");
        sheet.addStringProperty("sheetName");
        sheet.addStringProperty("sheetIntro");
        sheet.addIntProperty("branchId");
        sheet.addBooleanProperty("isDownLoad").notNull();
        sheet.addIntProperty("doneTime");
        sheet.addIntProperty("length");
        sheet.addBooleanProperty("isDoing").notNull();
        sheet.addBooleanProperty("isDone").notNull();
    }

    /**
     * @param schema
     */
    private static void addRecord(Schema schema) {
        Entity record = schema.addEntity("RecordInfo");
        record.addIdProperty();
        record.addIntProperty("recordId");
        record.addStringProperty("recordGps");
        record.addStringProperty("recordAsws");
        record.addIntProperty("recordError");
        record.addStringProperty("recordPic");
        record.addStringProperty("recordStart");
        record.addStringProperty("recordEnd");
        record.addStringProperty("recordSub");
        record.addBooleanProperty("recordStatus");
        record.addIntProperty("checkerId");
        record.addStringProperty("recordNote");
        record.addStringProperty("checkTime");

        //增加字段
        record.addBooleanProperty("isChanged").notNull();
        record.addStringProperty("responseId");
    }

    /**
     * @param schema
     */
    private static void addPeriod(Schema schema) {
        Entity period = schema.addEntity("PeriodInfo");
        period.addIdProperty();
        period.addIntProperty("periodId");
        period.addIntProperty("sheetId");
        period.addStringProperty("periodShift");
        period.addStringProperty("periodTime");
    }

    /**
     * @param schema
     */
    private static void addRoute(Schema schema) {
        Entity route = schema.addEntity("HistoryRoute");
        route.addIdProperty();
        route.addIntProperty("sheetId");
        route.addIntProperty("periodId");
        route.addStringProperty("points");
        route.addStringProperty("startTime");
        route.addStringProperty("endTime");
        route.addIntProperty("generId");
    }

    private static void addNotices(Schema schema){
        Entity notices = schema.addEntity("Notices");
        notices.addIdProperty();
        notices.addStringProperty("title");
        notices.addStringProperty("description");
        notices.addStringProperty("sendTime");
        notices.addIntProperty("peopleId");
        notices.addBooleanProperty("isChecked").notNull();
    }
}
