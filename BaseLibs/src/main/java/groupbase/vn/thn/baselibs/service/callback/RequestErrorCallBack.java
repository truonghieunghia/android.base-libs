package groupbase.vn.thn.baselibs.service.callback;

import com.android.volley.VolleyError;

/**
 * Created by nghiath on 4/3/15.
 */
public interface RequestErrorCallBack {

    public void onError ( VolleyError volleyError );

    public void onException ( Exception e );
}
