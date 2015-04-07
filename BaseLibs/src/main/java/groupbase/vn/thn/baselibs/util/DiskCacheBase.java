package groupbase.vn.thn.baselibs.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by nghiath on 4/7/15.
 */
public class DiskCacheBase {
    public static final int SIZE_UNKNOWN = -1;

    private static final String TAG = "NewsDiskCache";
    private static final int DEFAULT_QUALITY = 100;
    private static final Bitmap.CompressFormat DEFAULT_FILE_EXTENTION = Bitmap.CompressFormat.PNG;
    //private static final int DISK_CACHE_MAX = 3 * 1024 * 1024; // 3MB

    //private int _mMaxSize = DISK_CACHE_MAX;
    private int _mQuality = DEFAULT_QUALITY;
    private Bitmap.CompressFormat _mExtention = DEFAULT_FILE_EXTENTION;

    private File _mCacheDir = null;

    /**
     * コンストラクタ
     * @param aCacheDir Context#getCacheDir()の値想定。　保存ディレクトリのルートを指定
     * @param aDirName  ルート以下に作成するディレクトリ
     * @param aMaxSize  保存する最大サイズ（現在未使用）
     */
    public DiskCacheBase( final File aCacheDir, final String aDirName, final int aMaxSize ) {

        final StringBuilder dir = new StringBuilder();
        dir.append( aCacheDir.getAbsolutePath() );
        dir.append( File.separator );
        dir.append( aDirName );

        _mCacheDir = new File( dir.toString() );

        if ( !_mCacheDir.exists() ) {

            //ディレクトリが存在しなければ作成
            _mCacheDir.mkdir();
        }

        //後々最大キャッシュ量を指定できるようにする
        //_mMaxSize = aMaxSize;

    }

    /**
     * 画質のクオリティを指定する（ 1 - 100 ）<br>
     * デフォルト：100
     * @param aQuality : クオリティ（%）
     */
    public void setQuality( final int aQuality ) {

        int quality = aQuality;

        if ( aQuality < 1 ) {

            quality = 1;
        } else if ( aQuality > 100 ) {

            quality = 100;
        }

        _mQuality = quality;
    }

    /**
     * 保存拡張子の指定をする <br>
     * デフォルト : PNG
     * @param aExtention : Bitmap.CompressFormat　で指定できる拡張子
     */
    public void setFileExtension( final Bitmap.CompressFormat aExtention ) {

        if ( aExtention == null ) {

            return;
        }

        _mExtention = aExtention;
    }

    /**
     * ファイルを出力する
     * @param aKey  :url
     * @param aValue
     * @return
     */
    public boolean put( final String aKey, final Bitmap aValue ) {

        final File file = new File( _mCacheDir, _keyMD5( aKey ) );

        _restore();

        try {

            if ( !file.exists() ) {

                file.createNewFile();
            } else {

                return false;
            }

            final ByteArrayOutputStream bos = new ByteArrayOutputStream();
            aValue.compress( _mExtention, _mQuality, bos );

            final FileOutputStream output = new FileOutputStream( file );
            output.write( bos.toByteArray() );

            output.close();

        } catch ( IOException e ) {

            Log.e( TAG, "Error! Output image " + e.getMessage() );
            return false;
        } catch ( Exception e ) {

            Log.e( TAG, e.getMessage() );
            return false;
        }

        return true;

    }

    /**
     * bmpファイルを取得する
     * @param aKey
     * @return bmp : ファイルが存在しない場合はnull
     */
    public Bitmap get( final String aKey ) {

        final File file = new File( _mCacheDir, _keyMD5( aKey ) );
        Bitmap bmp = null;

        _restore();

        if ( !file.exists() ) {

            return bmp;
        }

        try {

            final FileInputStream inputStream = new FileInputStream( file );
            bmp = BitmapFactory.decodeStream( inputStream );

            inputStream.close();

        } catch ( Exception e ) {

            Log.e( TAG, "Error! Input Image" );
        }

        return bmp;
    }

    public boolean isExist( final String aKey ) {

        final File file = new File( _mCacheDir, _keyMD5( aKey ) );

        return file.exists();
    }

    /**
     * キー指定でファイルを削除する
     * @param aKey
     */
    public void remove( final String aKey ) {

        final File file = new File( _mCacheDir, _keyMD5( aKey ) );
        _remove( file );
    }

    /**
     * ファイルを指定削除する
     * @param aFile : 削除対象File
     */
    private void _remove( final File aFile ) {

        if ( aFile.exists() ) {

            aFile.delete();
        }

    }

    /**
     * キャッシュを全削除する
     */
    public void clear() {

        _removeAll( _mCacheDir );
    }

    /**
     * このインスタンスで生成したディレクトリ以下のファイルを全削除する <br>
     * ディレクトリが存在している場合は再帰的によぶ
     * スレッドたてるべきかどうかあとで
     * @param aDir
     */
    private void _removeAll( final File aDir ) {

        if ( aDir == null ) {

            return;
        }

        if ( aDir.isDirectory() ) {

            String[] children = aDir.list();    //ディレクトリにあるすべてのファイルを処理する

            for ( String fileName : children ) {

                final File file = new File( aDir, fileName );

                if ( !file.exists() ) {

                    //存在しないファイル(dir)であれば無視
                    continue;
                }

                if ( file.isDirectory() ) {

                    //ディレクトリならば再帰的に呼ぶ
                    _removeAll( file );
                } else {

                    //削除する
                    _remove( file );
                }
            }
        }

    }


    /**
     * システムにキャッシュが回収された場合などに再度ディレクトリを生成
     *
     */
    private void _restore() {

        if ( _mCacheDir == null ) {

            return;
        }

        if ( !_mCacheDir.exists() ) {

            //ディレクトリが存在しなければ作成
            _mCacheDir.mkdir();
        }

    }

    /**
     * キーを変換する
     * ファイル名として利用する
     * @return
     */
    private String _keyMD5( final String aKey ) {

        String key = aKey;
        try {

            key = MD5.crypt( aKey );
        } catch ( NoSuchAlgorithmException e ) {
            //変換失敗したらそのままで
        }

        return key;
    }

    /**
     * デバッグ用　：現在の容量(byte)
     * @return
     */
    public long currentCapacity() {

        long totalSize = 0;

        if ( _mCacheDir == null || _mCacheDir.list() == null ) {

            return -1;
        }

        for ( String fileName : _mCacheDir.list() ) {

            final File file = new File( _mCacheDir, fileName );

            if ( file.exists() ) {

                totalSize += file.length();
            }
        }

        return totalSize;
    }

    /**
     * とりあえずデバッグ用 保存されているファイル数とサイズを返す
     * @return
     */
    public String debug() {

        if( _mCacheDir == null || _mCacheDir.list() == null ) {

            return "DEBUG MODE:: CACHE = Null or file = 0 ";
        }

        return " total file :" + _mCacheDir.list().length + "- total size : "+ ( currentCapacity() / 1000 ) +"KB";
    }

}
