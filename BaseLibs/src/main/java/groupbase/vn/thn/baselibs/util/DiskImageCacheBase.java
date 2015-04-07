package groupbase.vn.thn.baselibs.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by nghiath on 4/7/15.
 */
public class DiskImageCacheBase implements ImageLoader.ImageCache {

    private static DiskImageCacheBase mInstance = null;

    private DiskCacheBase mDiskCache = null;

    public static DiskImageCacheBase getInstance( final Context context ) {

        if ( mInstance == null ) {

            mInstance = new DiskImageCacheBase( context );
        }

        return mInstance;
    }

    private DiskImageCacheBase( final Context context ) {

        mDiskCache = new DiskCacheBase( context.getCacheDir(), "baseimage", DiskCacheBase.SIZE_UNKNOWN );
    }

    public void clear() {

        mDiskCache.clear();
    }

    public void release() {

        mInstance = null;
        mDiskCache = null;
    }
    private static String generateKey( final String aUrl ) {

        return new StringBuilder( aUrl.length() + 12 ).append("#W").append( 0 )
                .append("#H").append( 0 ).append( aUrl ).toString();
    }
    @Override
    public Bitmap getBitmap ( String url ) {

        return mDiskCache.get( url );
    }

    @Override
    public void putBitmap ( String url, Bitmap bitmap ) {
        mDiskCache.put( url, bitmap );
    }
}
