package groupbase.vn.thn.baselibs.service;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by nghiath on 4/7/15.
 */
public class VolleyQueueBase {
    private String TAG = "NewsVolleyQueue";

    private static VolleyQueueBase mInstance = null;
    private RequestQueue mQueue = null;

    private Context mContext;

    private VolleyQueueBase( final Context context ) {

        mContext = context.getApplicationContext();
        mQueue = Volley.newRequestQueue( mContext );
    }

    public static VolleyQueueBase getInstance( final Context context ) {

        if ( mInstance == null ) {

            mInstance = new VolleyQueueBase( context );
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {

        return mQueue;
    }

    public void add( final Request<?> request ) {

        request.setTag( TAG );
        mQueue.add( request );
    }

    public void start() {

        stop();
        mQueue.start();
    }

    public void stop() {

        mQueue.stop();
        mQueue.cancelAll( TAG );
    }
}
