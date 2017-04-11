package com.cug.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import com.cug.greendao.PeopleInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "PEOPLE_INFO".
*/
public class PeopleInfoDao extends AbstractDao<PeopleInfo, Long> {

    public static final String TABLENAME = "PEOPLE_INFO";

    /**
     * Properties of entity PeopleInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property PeopleName = new Property(1, String.class, "peopleName", false, "PEOPLE_NAME");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property TeamId = new Property(3, Integer.class, "teamId", false, "TEAM_ID");
        public final static Property PeoplePassword = new Property(4, String.class, "peoplePassword", false, "PEOPLE_PASSWORD");
        public final static Property BranchId = new Property(5, Integer.class, "branchId", false, "BRANCH_ID");
        public final static Property BranchName = new Property(6, String.class, "branchName", false, "BRANCH_NAME");
        public final static Property BranchType = new Property(7, String.class, "branchType", false, "BRANCH_TYPE");
        public final static Property Company = new Property(8, String.class, "company", false, "COMPANY");
        public final static Property TeamName = new Property(9, String.class, "teamName", false, "TEAM_NAME");
        public final static Property IsLogin = new Property(10, boolean.class, "isLogin", false, "IS_LOGIN");
    };


    public PeopleInfoDao(DaoConfig config) {
        super(config);
    }
    
    public PeopleInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"PEOPLE_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: id
                "\"PEOPLE_NAME\" TEXT," + // 1: peopleName
                "\"NAME\" TEXT," + // 2: name
                "\"TEAM_ID\" INTEGER," + // 3: teamId
                "\"PEOPLE_PASSWORD\" TEXT," + // 4: peoplePassword
                "\"BRANCH_ID\" INTEGER," + // 5: branchId
                "\"BRANCH_NAME\" TEXT," + // 6: branchName
                "\"BRANCH_TYPE\" TEXT," + // 7: branchType
                "\"COMPANY\" TEXT," + // 8: company
                "\"TEAM_NAME\" TEXT," + // 9: teamName
                "\"IS_LOGIN\" INTEGER NOT NULL );"); // 10: isLogin
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"PEOPLE_INFO\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, PeopleInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String peopleName = entity.getPeopleName();
        if (peopleName != null) {
            stmt.bindString(2, peopleName);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
 
        Integer teamId = entity.getTeamId();
        if (teamId != null) {
            stmt.bindLong(4, teamId);
        }
 
        String peoplePassword = entity.getPeoplePassword();
        if (peoplePassword != null) {
            stmt.bindString(5, peoplePassword);
        }
 
        Integer branchId = entity.getBranchId();
        if (branchId != null) {
            stmt.bindLong(6, branchId);
        }
 
        String branchName = entity.getBranchName();
        if (branchName != null) {
            stmt.bindString(7, branchName);
        }
 
        String branchType = entity.getBranchType();
        if (branchType != null) {
            stmt.bindString(8, branchType);
        }
 
        String company = entity.getCompany();
        if (company != null) {
            stmt.bindString(9, company);
        }
 
        String teamName = entity.getTeamName();
        if (teamName != null) {
            stmt.bindString(10, teamName);
        }
        stmt.bindLong(11, entity.getIsLogin() ? 1L: 0L);
    }

    /** @inheritdoc */
    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public PeopleInfo readEntity(Cursor cursor, int offset) {
        PeopleInfo entity = new PeopleInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // peopleName
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3), // teamId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // peoplePassword
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // branchId
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // branchName
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // branchType
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // company
            cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9), // teamName
            cursor.getShort(offset + 10) != 0 // isLogin
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, PeopleInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setPeopleName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setTeamId(cursor.isNull(offset + 3) ? null : cursor.getInt(offset + 3));
        entity.setPeoplePassword(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setBranchId(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setBranchName(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setBranchType(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setCompany(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setTeamName(cursor.isNull(offset + 9) ? null : cursor.getString(offset + 9));
        entity.setIsLogin(cursor.getShort(offset + 10) != 0);
     }
    
    /** @inheritdoc */
    @Override
    protected Long updateKeyAfterInsert(PeopleInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    /** @inheritdoc */
    @Override
    public Long getKey(PeopleInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
