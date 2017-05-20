package com.sun.recordway.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sun.recordway.bean.RecordBean;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by SUN on 2016/12/19.
 */
public class Database extends SQLiteOpenHelper {

    private static Database instance;
    private static final AtomicInteger openCounter = new AtomicInteger();
    private final static String DB_NAME = "record";
    private final static int DB_VERSION = 1;
    public Database(final Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + DB_NAME + " (title varchar(45), duration varchar(45), distance varchar(45) )");
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    public static synchronized Database getInstance(final Context c) {
        if (instance == null) {
            instance = new Database(c.getApplicationContext());
        }
        openCounter.incrementAndGet();
        return instance;
    }
    @Override
    public void close() {
        if (openCounter.decrementAndGet() == 0) {
            super.close();
        }
    }

    /**
     * 对数据库操作
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @param limit
     * @return
     */
    public Cursor query(final String[] columns, final String selection,
                        final String[] selectionArgs, final String groupBy, final String having,
                        final String orderBy, final String limit) {
        return getReadableDatabase()
                .query(DB_NAME, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    /**
     * 通过一个路径记录为数据库添加一条新的记录
     * @param recordBean
     */
    public boolean addItem(RecordBean recordBean){
        String title = recordBean.getTitle();
        String distance = recordBean.getDistance();
        String duration = recordBean.getDuration();
        getWritableDatabase().beginTransaction();
        boolean success = true ;
        try {
            Cursor c = getReadableDatabase().query(DB_NAME, new String[]{"title"}, "title = ?",
                    new String[]{title}, null, null, null);
            if(c.getCount() > 0)//说明用户标题存在
            success = false ;
            else//向数据库中添加数据
            {
                ContentValues values = new ContentValues();
                values.put("title",title);
                values.put("distance", distance);
                values.put("duration", duration);
                getWritableDatabase().insert(DB_NAME, null, values);
            }
            c.close();
            getWritableDatabase().setTransactionSuccessful();
        } finally {
                getWritableDatabase().endTransaction();
        }
        return success;
    }
    /**
     * 删除标题为 title的数据
     */
    public void deleteItem(String title){
        getWritableDatabase().beginTransaction();
        getWritableDatabase().execSQL("delete from "+DB_NAME+" where title='"+title+"'");
        getWritableDatabase().setTransactionSuccessful();
        getWritableDatabase().endTransaction();
        return ;
    }
    /**
     * 通过标题获取日记数据
     */
    public RecordBean getItem(String title){
        getWritableDatabase().beginTransaction();
        RecordBean recordBean = null;
        try {
            Cursor c = getReadableDatabase().query(DB_NAME, new String[]{"title","distance","duration"}, "title = ?",
                    new String[]{title}, null, null, null);
            if(c!=null){
                recordBean =new RecordBean();
                recordBean.setTitle(c.getString(0));
                recordBean.setDistance(c.getString(1));
                recordBean.setDuration(c.getString(2));
            }
            c.close();
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
        return recordBean;
    }
    /**
     * 获取所有数据库的所有项
     */
    public LinkedList<RecordBean> getAllItems(){
        LinkedList<RecordBean> linkedList = new LinkedList<>();
        getWritableDatabase().beginTransaction();
        try {
            String sql = "select * from " + DB_NAME;
            Cursor c = getReadableDatabase().rawQuery(sql,null);
            if(c.getCount() == 0)//查不到数据
                return linkedList;
            else//向数据库中添加数据
            {
                if(c!=null){
                    RecordBean recordBean;
                    while (c.moveToNext()){
                        recordBean = new RecordBean();
                        recordBean.setTitle(c.getString(0));
                        recordBean.setDuration(c.getString(1));
                        recordBean.setDistance(c.getString(2));
                        linkedList.add(recordBean);
                    }
                }
            }
            c.close();
            getWritableDatabase().setTransactionSuccessful();
        } finally {
            getWritableDatabase().endTransaction();
        }
        System.out.println(linkedList);
        return linkedList;
    }

}
