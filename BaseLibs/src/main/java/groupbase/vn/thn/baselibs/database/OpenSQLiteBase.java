package groupbase.vn.thn.baselibs.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by nghiath on 5/6/15.
 */
public class OpenSQLiteBase extends SQLiteOpenHelper {

    private boolean mIsOpenFromFile = false;
    private String mPathFileDataBase = null;
    private String mDataBaseName = null;
    private ArrayList<String> mListEntry = new ArrayList();
    private Context mContext;
    public OpenSQLiteBase(Context context, String dataBaseName, int version) {
        super(context, dataBaseName, null, version);
        mContext = context;
        mDataBaseName = dataBaseName;
    }

    public OpenSQLiteBase(Context context, String dataBaseName, int version, boolean isOpenFromFile, String pathFileDataBase) {
        super(context, dataBaseName, null, version);
        this.mIsOpenFromFile = isOpenFromFile;
        this.mPathFileDataBase = pathFileDataBase;
        mDataBaseName = dataBaseName;
        mContext = context;
        if (checkDataBase(pathFileDataBase+dataBaseName)){
            SQLiteDatabase.openDatabase(pathFileDataBase+dataBaseName, null, SQLiteDatabase.OPEN_READWRITE);
        }else {
            createDataBase(pathFileDataBase+dataBaseName,dataBaseName);
        }
    }

    public void setListEntry(ArrayList<String> listEntry) {
        this.mListEntry = listEntry;
    }

    private boolean checkDataBase(String path){

        boolean checkBD= false;
        try {
            File dbFile = new File(path);
            checkBD = dbFile.exists();
        }catch (SQLiteException e){
            Log.i("OpenSqlite",e.getMessage());
        }
        return checkBD;
    }

    private void copyDataBase(String path,String fileName){

        try {
            InputStream inputStream = mContext.getAssets().open(fileName);
            OutputStream outputStream =new FileOutputStream(path);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer))>0) {
                outputStream.write(buffer,0,length);
            }
            //Close the streams
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }catch (IOException e){
            Log.i("OpenSqlite",e.getMessage());
        }
    }

    private void createDataBase(String path,String fileName){
        boolean dbExist = checkDataBase(path);
        if (!dbExist){
            getReadableDatabase();
            copyDataBase(path,fileName);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (!mIsOpenFromFile){
            for (String sql_create : mListEntry) {
                db.execSQL(sql_create);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if (oldVersion != newVersion){
            if (!mIsOpenFromFile){
                for (String sql_create : mListEntry) {
                    db.execSQL(sql_create);
                }
            }else {
                copyDataBase(this.mPathFileDataBase+mDataBaseName,mDataBaseName);
            }
        }
    }
}
