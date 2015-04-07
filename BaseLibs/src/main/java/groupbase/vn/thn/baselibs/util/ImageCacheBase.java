package groupbase.vn.thn.baselibs.util;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.util.HashMap;

/**
 * Created by nghiath on 4/7/15.
 */
public class ImageCacheBase implements ImageLoader.ImageCache {

    private static final int MEGA_BYTE = 1024 * 1024;
    private static final int CACHE_MAX_SIZE = 4 * MEGA_BYTE;

    private static ImageCacheBase mInstance;
    private LruCache< String, Bitmap > _mCache;
    private DiskCacheBase _mDiskCache = null;
    private HashMap< String, ImageDetailData > _mImageDetailMap = new HashMap< String, ImageDetailData >();

    public static ImageCacheBase getInstance () {

        if ( mInstance == null ) {

            mInstance = new ImageCacheBase( CACHE_MAX_SIZE );
        }

        return mInstance;
    }

    public static ImageCacheBase getInstance ( final Context context ) {


        if ( mInstance == null && context != null ) {

            //アプリメモリ使用量の8分の１を利用する
            final int memory = (( ActivityManager ) context.getSystemService( Context.ACTIVITY_SERVICE )).getMemoryClass();
            int cacheSize = 0;

            if ( memory == 0 ) {

                cacheSize = CACHE_MAX_SIZE;
            } else {

                //アプリ使用量の８分の１をキャッシュサイズとする
                cacheSize = memory * MEGA_BYTE / 8;
                Log.i( "NewsImageCache", "MAKE MemoryCache News :" + memory + "MB / 8" );
            }

            mInstance = new ImageCacheBase( cacheSize, context.getCacheDir() );
        }

        return mInstance;
    }

    private ImageCacheBase ( final int size, final File cacheDir ) {

        this( size );

        _mDiskCache = new DiskCacheBase( cacheDir, "baseimage", size );
    }

    private ImageCacheBase ( final int size ) {

        _mCache = new LruCache< String, Bitmap >( size ) {

            @Override
            protected int sizeOf ( final String aUrl, final Bitmap aBitmap ) {

                return aBitmap.getByteCount();
            }

            @Override
            protected void entryRemoved ( final boolean aEvicted, final String aKey, Bitmap aOldValue, final Bitmap aNewValue ) {

                removeDetailData( aKey );

                if ( aEvicted && ! aOldValue.isRecycled() ) {

                    //bmp解放
                    aOldValue.recycle();
                    aOldValue = null;
                }
            }
        };

    }

    @Override
    public Bitmap getBitmap ( String url ) {

        final ImageDetailData data = _mImageDetailMap.get( url );

        if ( data == null || ! data.getIsUseDiscCache() ) {

            //メモリキャッシュ使用
            return _mCache.get( url );
        }

        //ディスクキャッシュ使用
        return _mDiskCache.get( url );
    }

    @Override
    public void putBitmap ( final String url, final Bitmap bitmap ) {

        new Thread( new Runnable() {

            @Override
            public void run () {

                final ImageDetailData data = _mImageDetailMap.get( url );

                if ( data != null ) {

                    Bitmap resizeBitmap = bitmap;

                    //画像がViewより大きい時だけリサイズを行う
                    if ( data.getWidth() <= bitmap.getWidth() ) {

                        try {

                            final int bmpWidth = bitmap.getWidth();
                            final int bmpHeight = bitmap.getHeight();
                            final float scale = Float.valueOf( data.getWidth() ) / Float.valueOf( bmpWidth );

                            final Matrix matrix = new Matrix();
                            matrix.postScale( scale, scale );

                            //リサイズ
                            resizeBitmap = Bitmap.createBitmap( bitmap, 0, 0, bmpWidth, bmpHeight, matrix, true );
                        } catch ( Exception e ) {

                            Log.e( "ImageCacheBase", e.getMessage() );
                        }
                    }

                    if ( data.getIsUseDiscCache() && _mDiskCache != null ) {

                        //ディスクキャッシュを利用
                        _mDiskCache.put( url, resizeBitmap );
                    } else {

                        //メモリキャッシュ
                        _mCache.put( url, resizeBitmap );
                    }

                    return;
                }

                //画像詳細がセットされていなければメモリキャッシュを利用する
                _mCache.put( url, bitmap );
            }

        } ).start();
    }

    public void setImageDetail ( final String url, final int width, final boolean IsUseDiscCache ) {

        final ImageDetailData data = new ImageDetailData();
        data.setImageWidth( width );
        data.setIsUseDiscCache( IsUseDiscCache );

        _mImageDetailMap.put( generateKey( url ), data );
    }

    private void removeDetailData ( final String aKey ) {

        if ( ! _mImageDetailMap.isEmpty() && _mImageDetailMap.containsKey( aKey ) ) {

            final ImageDetailData data = _mImageDetailMap.get( aKey );

            if ( ! data.getIsUseDiscCache() ) {

                //discCacheをつかっていなければ削除する
                _mImageDetailMap.remove( aKey );
            }
        }
    }

    private static String generateKey ( final String url ) {

        return new StringBuilder( url.length() + 12 ).append( "#W" ).append( 0 ).append( "#H" ).append( 0 ).append( url ).toString();
    }

    public void clear () {

        _mCache.evictAll();
        _mDiskCache.clear();
        _mImageDetailMap.clear();
    }

    /**
     * 終了時に解放をする
     */
    public void release () {

        _mCache.evictAll();
        _mImageDetailMap.clear();

        _mCache = null;
        _mImageDetailMap = null;

    }


    public void debug () {

        Log.d( "debug", _mDiskCache.debug() );
    }

    private class ImageDetailData {

        private int _mWidth = 0;
        private boolean _mUseDiscCache = true;

        public int getWidth () {

            return _mWidth;
        }

        public boolean getIsUseDiscCache () {

            return _mUseDiscCache;
        }

        public void setImageWidth ( final int aWidth ) {

            _mWidth = aWidth;

        }

        public void setIsUseDiscCache ( final boolean aIsUse ) {

            _mUseDiscCache = aIsUse;
        }

    }
}
