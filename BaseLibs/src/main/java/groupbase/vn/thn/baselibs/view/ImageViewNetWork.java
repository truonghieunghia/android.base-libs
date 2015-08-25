package groupbase.vn.thn.baselibs.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import groupbase.vn.thn.baselibs.util.ImageCacheBase;

/**
 * Created by nghiath on 4/7/15.
 */
public class ImageViewNetWork extends ImageView {

    private String TAG = "ImageViewNetWork";
    private static final int DELAY_LOAD = 10;
    private static final int NO_IMAGE_INVISIBLE = 0;
    private String mRequestURL = null;
    private ImageLoader mImageLoader = null;
    private ImageLoader.ImageListener mListener = null;
    private RequestQueue mQueue = null;

    public ImageViewNetWork ( Context context ) {

        super( context );
    }

    public ImageViewNetWork ( Context context, AttributeSet attrs ) {

        super( context, attrs );
    }

    /**
     * requestImage
     *
     * @param imageURL String
     */
    public void requestImage ( final String imageURL ,RequestQueue requestQueue) {

        mQueue = requestQueue;
        final ImageCacheBase cache = ImageCacheBase.getInstance( getContext() );
        if ( mImageLoader == null ) {
            mImageLoader = new ImageLoader( mQueue, cache );
            mImageLoader.setBatchedResponseDelay( DELAY_LOAD );
        }
        mRequestURL = imageURL;
        setTag( imageURL );
        mListener = getImageListener( this, mQueue );
        mImageLoader.get( imageURL, mListener );
    }

    public void requestImage ( final String imageURL, final int errorImageResId,RequestQueue requestQueue ) {

        mQueue = requestQueue;
        final ImageCacheBase cache = ImageCacheBase.getInstance( getContext() );
        if ( mImageLoader == null ) {
            mImageLoader = new ImageLoader( mQueue, cache );
            mImageLoader.setBatchedResponseDelay( DELAY_LOAD );
        }
        mRequestURL = imageURL;
        setTag( imageURL );
        mListener = getImageListener( this, errorImageResId, mQueue );
        mImageLoader.get( imageURL, mListener );
    }

    private static ImageLoader.ImageListener getImageListener ( final ImageView view, final int errorImageResId, final RequestQueue requestQueue ) {

        return new ImageLoader.ImageListener() {

            @Override
            public void onResponse ( ImageLoader.ImageContainer response, boolean isImmediate ) {

                if ( response.getBitmap() != null ) {
                    view.setVisibility( View.VISIBLE );
                    view.setImageBitmap( response.getBitmap() );
                } else {
                    view.setVisibility( View.INVISIBLE );
                }
//                requestQueue.stop();
//                requestQueue.cancelAll( "ImageViewNetWork" );
            }

            @Override
            public void onErrorResponse ( VolleyError error ) {

                if ( errorImageResId != 0 ) {
                    view.setImageResource( errorImageResId );
                }
//                requestQueue.stop();
//                requestQueue.cancelAll( "ImageViewNetWork" );
            }
        };
    }

    private static ImageLoader.ImageListener getImageListener ( final ImageView view, final RequestQueue requestQueue ) {

        return new ImageLoader.ImageListener() {

            @Override
            public void onResponse ( ImageLoader.ImageContainer response, boolean isImmediate ) {

                if ( response.getBitmap() != null ) {
                    view.setVisibility( View.VISIBLE );
                    view.setImageBitmap( response.getBitmap() );
                } else {
                    view.setVisibility( View.INVISIBLE );
                }
//                requestQueue.stop();
//                requestQueue.cancelAll( "ImageViewNetWork" );
            }

            @Override
            public void onErrorResponse ( VolleyError error ) {

//                requestQueue.stop();
//                requestQueue.cancelAll( "ImageViewNetWork" );
            }
        };
    }
}
