package groupbase.vn.thn.baselibs.service.callback;

import android.graphics.Bitmap;

import com.android.volley.VolleyError;

/**
 * Created by nghiath on 4/7/15.
 */
public interface ImageRequestListener {
    public void onComplete(final Bitmap bitmap);

    public void onError(final VolleyError error);

}
