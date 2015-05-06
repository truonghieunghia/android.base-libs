package groupbase.vn.thn.baselibs.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nghiath on 5/6/15.
 */
public class SQLiteDB {

    private AtomicInteger mOpenCounter = new AtomicInteger();
    private SQLiteDatabase mDatabase;

    public SQLiteDB(SQLiteDatabase database) {
        this.mDatabase = database;
    }

    public synchronized void close() {
        if (mOpenCounter.decrementAndGet() == 0) {
            // Closing database
            mDatabase.close();

        }
    }
    public Cursor query(String sql,String[]selectionArgs){

       return mDatabase.rawQuery(sql,selectionArgs);

    }

    public long insert(String table, String nullColumnHack, ContentValues values){
        return mDatabase.insert( table,  nullColumnHack,  values);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs){
        return mDatabase.update( table,  values,  whereClause,  whereArgs);
    }

    public int delete(String table, String whereClause, String[] whereArgs){
        return mDatabase.delete( table,  whereClause,  whereArgs);
    }
}
