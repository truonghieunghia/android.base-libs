package groupbase.vn.thn.baselibs.service;

import com.android.volley.Network;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;

import groupbase.vn.thn.baselibs.service.callback.ProgressRequestCallBack;

/**
 * Created by nghiath on 4/6/15.
 */
public class CustomNetwork implements Network{

    private final ProgressRequestCallBack mProgressRequestCallBack;

    public CustomNetwork (  HttpStack httpStack, ProgressRequestCallBack progressRequestCallBack ){

        mProgressRequestCallBack = progressRequestCallBack;
    }
    public CustomNetwork (  HttpStack httpStack ){

        this(httpStack,null);
    }
    @Override
    public NetworkResponse performRequest ( Request< ? > request ) throws VolleyError {

        return null;
    }
}
