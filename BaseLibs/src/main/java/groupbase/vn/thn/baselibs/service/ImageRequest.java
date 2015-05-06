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
    public ImageRequest(final Context context) {

        mContext = context;
        mCache = DiskImageCacheBase.getInstance(mContext);
    }

    /**
     * Request images
     *
     * @param aUrl url that get image
     * @param listener WidgetImageRequestListener
     */
    public void request(final String aUrl, final ImageRequestListener listener) {

        // If have cache, run response in cache of bitmap

        final Bitmap bitmap = mCache.getBitmap(aUrl);

        if (bitmap != null) {

            listener.onComplete(bitmap);
            return;
        }

        // Start request queue
        final RequestQueue queue = VolleyBase.newRequestQueue(mContext);
        queue.start();

        final ImageLoader imageLoader = new ImageLoader(queue, mCache);

        imageLoader.get(aUrl, new ImageLoader.ImageListener() {

            @Override
            public void onResponse(final ImageLoader.ImageContainer response, final boolean isImmediate) {

                if (response != null && response.getBitmap() != null) {

                    queue.stop();
                    mCache.putBitmap(response.getRequestUrl(), response.getBitmap());

                    if (listener != null) {

                        listener.onComplete(response.getBitmap());
                    }
                }
            }

            @Override
            public void onErrorResponse(final VolleyError error) {

                queue.stop();

                if (listener != null) {

                    listener.onError(error);
                }
            }
        });
    }
}
