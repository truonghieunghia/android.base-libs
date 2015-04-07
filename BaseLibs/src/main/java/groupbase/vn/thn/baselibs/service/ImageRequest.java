package groupbase.vn.thn.baselibs.service;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import groupbase.vn.thn.baselibs.service.callback.ImageRequestListener;
import groupbase.vn.thn.baselibs.util.DiskImageCacheBase;

/**
 * Created by nghiath on 4/6/15.
 */
public class ImageRequest {

    private Context mContext;
    private DiskImageCacheBase mCache;

    /**
     * Constructor
     */
    public ImageRequest(final Context aContext) {

        mContext = aContext;
        mCache = DiskImageCacheBase.getInstance(mContext);
    }

    /**
     * Request images
     *
     * @param aUrl url that get image
     * @param aListener WidgetImageRequestListener
     */
    public void request(final String aUrl, final ImageRequestListener aListener) {

        // If have cache, run response in cache of bitmap

        final Bitmap bitmap = mCache.getBitmap(aUrl);

        if (bitmap != null) {

            aListener.onComplete(bitmap);
            return;
        }

        // Start request queue
        final RequestQueue queue = VolleyBase.newRequestQueue(mContext);
        queue.start();

        final ImageLoader imageLoader = new ImageLoader(queue, mCache);

        imageLoader.get(aUrl, new ImageLoader.ImageListener() {

            @Override
            public void onResponse(final ImageLoader.ImageContainer aResponse, final boolean aIsImmediate) {

                if (aResponse != null && aResponse.getBitmap() != null) {

                    queue.stop();
                    mCache.putBitmap(aResponse.getRequestUrl(), aResponse.getBitmap());

                    if (aListener != null) {

                        aListener.onComplete(aResponse.getBitmap());
                    }
                }
            }

            @Override
            public void onErrorResponse(final VolleyError aError) {

                queue.stop();

                if (aListener != null) {

                    aListener.onError(aError);
                }
            }
        });
    }
}
