package groupbase.vn.thn.baselibs.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

import groupbase.vn.thn.baselibs.service.callback.ProgressRequestCallBack;

/**
 * Created by nghiath on 4/6/15.
 */
public class VolleyBase {

    private static final String DEFAULT_CACHE_DIR = "volley";

    public static RequestQueue newRequestQueue ( Context context ) {

        return newRequestQueue( context, null );
    }

    public static RequestQueue newRequestQueue ( final Context context, final ProgressRequestCallBack aProgress ) {

        final File cacheDir = new File( context.getCacheDir(), DEFAULT_CACHE_DIR );
        String userAgent = "volley/0";
        try {
            String packageName = context.getPackageName();
            PackageInfo info = context.getPackageManager().getPackageInfo( packageName, 0 );
            userAgent = packageName + "/" + info.versionCode;
        } catch ( PackageManager.NameNotFoundException e ) {
        }
        final HttpStack stack;
        if ( Build.VERSION.SDK_INT >= 9 ) {
            stack = new HurlStack();
        } else {
            // Prior to Gingerbread, HttpUrlConnection was unreliable.
            // See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
            stack = new HttpClientStack( AndroidHttpClient.newInstance( userAgent ) );
        }

        final Network network = new NetworkBase( stack, aProgress );

        final RequestQueue queue = new RequestQueue( new DiskBasedCache( cacheDir ), network );

        queue.start();

        return queue;
    }
}
