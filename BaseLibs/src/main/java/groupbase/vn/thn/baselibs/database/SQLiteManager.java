package groupbase.vn.thn.baselibs.database;

import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nghiath on 5/6/15.
 */
public class SQLiteManager {

    private AtomicInteger mOpenCounter = new AtomicInteger();
    private static SQLiteManager instance;
    private static SQLiteOpenHelper mDatabaseHelper;
    private SQLiteDB mDatabase;

    public static synchronized void initializeInstance(OpenSQLiteBase helper) {
        if (instance == null) {
            instance = new SQLiteManager();
            mDatabaseHelper = helper;
        }
    }

    public static synchronized SQLiteManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException(SQLiteManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }

        return instance;
    }

    public synchronized SQLiteDB openDatabase() {
        if(mOpenCounter.incrementAndGet() == 1) {
            // Opening new database
            mDatabase = new SQLiteDB( mDatabaseHelper.getWritableDatabase());
        }
        return mDatabase;
    }

}
