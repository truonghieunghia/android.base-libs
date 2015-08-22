package groupbase.vn.thn.baselibs.service;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import groupbase.vn.thn.baselibs.service.callback.ProgressRequestCallBack;
import groupbase.vn.thn.baselibs.service.callback.RequestCallBack;
import groupbase.vn.thn.baselibs.service.callback.RequestErrorCallBack;
import groupbase.vn.thn.baselibs.service.callback.UIRequestCallBack;
import groupbase.vn.thn.baselibs.util.Param;
import groupbase.vn.thn.baselibs.util.Parse;

/**
 * Created by nghiath on 4/1/15.
 */
public class ConnectWS implements Response.Listener< String >, Response.ErrorListener {

    public int REQUEST_TYPE = 1;

    public interface RequestType{
        public int XML = 0;
        public int JSON = 1;
    }

    private String TAG = "ConnectWS";
    private ArrayList< Param > mParams;
    private ArrayList< Param > mHeaders;
    private String mKey;
    private RequestCallBack mRequestCallBack;
    private RequestErrorCallBack mRequestErrorCallBack;
    private Context mContext;
    private Class< ? > mObjectParser;
    private String mUrl;
    private Object mDataCache = null;
    private RequestQueue mRequestQueue;
    private ArrayList< ? > mListResult;
    private static ConnectWS mInstance;
    private ProgressRequestCallBack mProgressRequestCallBack;
    private UIRequestCallBack mUIRequestCallBack;

    public ConnectWS ( Context context ) {

        this.mContext = context;
        mRequestQueue = getRequestQueue();
    }
    public ConnectWS ( Context context ,ProgressRequestCallBack progressRequestCallBack ) {

        this.mContext = context;
        mRequestQueue = getRequestQueue();
    }
    public static synchronized ConnectWS getInstance ( Context context ) {

        if ( mInstance == null ) {
            mInstance = new ConnectWS( context );
        }
        return mInstance;
    }

    public ConnectWS ( String url, Context context ) {

        this.mRequestCallBack = null;
        this.mObjectParser = null;
        this.mUrl = url;
        this.mContext = context;
        this.mKey = url;
    }

    public void setUIRequestCallBack(UIRequestCallBack uiRequestCallBack){
        mUIRequestCallBack = uiRequestCallBack;
    }

    public void setRequestType(int requestType){
        REQUEST_TYPE = requestType;
    }
    public ConnectWS ( String url, Context context, ProgressRequestCallBack progressRequestCallBack ) {

        this.mRequestCallBack = null;
        this.mObjectParser = null;
        this.mUrl = url;
        this.mContext = context;
        this.mKey = url;
        this.mProgressRequestCallBack = progressRequestCallBack;
    }


    public < T > ConnectWS ( String url, RequestCallBack< T > requestCallBack, Context context ) {

        this.mContext = context;
        this.mUrl = url;
        this.mKey = url;
        this.setRequestCallBack( requestCallBack );
    }

    public void setHeader(ArrayList<Param> headers) {
        this.mHeaders = headers;
    }

    public void setProgressRequestCallBack ( ProgressRequestCallBack progressRequestCallBack ) {

        this.mProgressRequestCallBack = progressRequestCallBack;
    }

    public void setRequestErrorCallBack ( RequestErrorCallBack requestErrorCallBack ) {

        this.mRequestErrorCallBack = requestErrorCallBack;
    }

    public < T > void setRequestCallBack ( RequestCallBack< T > requestCallBack ) {

        this.mObjectParser = ( Class< T > ) (( ParameterizedType ) requestCallBack.getClass().getGenericInterfaces()[ 0 ]).getActualTypeArguments()[ 0 ];
        this.mRequestCallBack = requestCallBack;
    }

    public void setParams ( ArrayList< Param > params ) {

        this.mParams = params;
    }

    public void request ( int method ) {

        createKey();
        RequestJson requestJson = new RequestJson( method, mUrl, this, this );
        addToRequestQueue(requestJson, mKey);
    }

    public void onError ( VolleyError volleyError ) {

        mRequestErrorCallBack.onError(volleyError);
        if (mUIRequestCallBack != null){
            mUIRequestCallBack.end();
        }
    }
    public void postRequest (boolean isCache) {

        request(Request.Method.POST, isCache);
    }

    public void getRequest (boolean isCache) {

        request(Request.Method.GET, isCache);
    }

    public void request ( int method ,boolean isCache) {
        if (mUIRequestCallBack != null){
            mUIRequestCallBack.begin();
        }
        if (method == Request.Method.GET ){
            for (int i = 0 ;i< mParams.size();i++){
                if (i == 0){
                    mUrl = mUrl+"?"+mParams.get(i).getParamName()+"="+mParams.get(i).getParamValue();
                }else {
                    mUrl = mUrl+"&"+mParams.get(i).getParamName()+"="+mParams.get(i).getParamValue();
                }
            }

        }
        createKey();
        RequestJson requestJson = new RequestJson( method, mUrl, this, this );
        requestJson.setShouldCache(isCache);
        addToRequestQueue(requestJson, mKey);
    }

    public void start () {

        stop();
        mRequestQueue.start();
    }

    public void stop () {

        mRequestQueue.stop();
        mRequestQueue.cancelAll( TAG );
    }

    private void createKey () {

        String key_ext = "";
        if ( mParams != null && mParams.size() > 0 ) {
            for ( Param param : mParams ) {
                key_ext = key_ext + param.getParamName() + ":" + param.getParamValue();
            }
        }
        this.mKey = mKey + key_ext;
        this.mDataCache = getCache();
    }

    private RequestQueue getRequestQueue ( ProgressRequestCallBack progressRequestCallBack ) {

        if ( mRequestQueue == null ) {
            mRequestQueue = VolleyBase.newRequestQueue( mContext.getApplicationContext(), progressRequestCallBack );
        }
        return mRequestQueue;
    }

    private RequestQueue getRequestQueue () {

        if ( mRequestQueue == null ) {
            if (mProgressRequestCallBack != null){
                mRequestQueue = VolleyBase.newRequestQueue( mContext.getApplicationContext(), mProgressRequestCallBack );
            }else {
                mRequestQueue = VolleyBase.newRequestQueue(mContext.getApplicationContext());
            }
        }
        return mRequestQueue;
    }

    private < T > void addToRequestQueue ( Request< T > req, String tag ) {

        req.setTag( tag );
        if ( mProgressRequestCallBack != null ){
            getRequestQueue(mProgressRequestCallBack).add( req );
        }else {
            getRequestQueue().add( req );
        }
    }

    private < T > void addToRequestQueue ( Request< T > req ) {

        req.setTag( TAG );
        if ( mProgressRequestCallBack != null ){
            getRequestQueue(mProgressRequestCallBack).add( req );
        }else {
            getRequestQueue().add( req );
        }
    }

    private Object convetString ( String str ) {

        try {
            return new JSONObject( str );
        } catch ( JSONException ex ) {
            try {
                return new JSONArray( str );
            } catch ( JSONException e ) {
                return null;
            }
        }
    }

    public Object getCache () {

        Cache cache = this.getRequestQueue().getCache();
        Cache.Entry entry = cache.get( mKey );
        if ( entry != null ) {
            try {
                String data = new String( entry.data, "UTF-8" );
                return convetString( data );
            } catch ( UnsupportedEncodingException e ) {
                return null;
            }
        } else {
            return null;
        }
    }

    public < T > void loadCache ( RequestCallBack requestCallBack ) {

        createKey();
        if ( mDataCache != null ) {
            if (REQUEST_TYPE == RequestType.XML){
                try {
                    mDataCache = XML.toJSONObject(mDataCache.toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
            if ( mDataCache instanceof JSONObject ) {
                requestCallBack.onResult( Parse.FromJsonToObject( mDataCache.toString(), mObjectParser ) );
            } else {
                if ( mDataCache instanceof JSONArray ) {
                    ArrayList< T > lst = new ArrayList< T >();
                    lst = Parse.FromJsonArrayToArrayObject( mDataCache.toString(), mObjectParser );
                    requestCallBack.onResultArray( lst );
                }

            }
        }
    }

    @Override
    public void onErrorResponse ( VolleyError error ) {

        if ( mRequestErrorCallBack != null ) {
            mRequestErrorCallBack.onError( error );
        }
    }

    @Override
    public void onResponse ( String response ) {

        if (REQUEST_TYPE == RequestType.XML){
            try {
                response = XML.toJSONObject(response).toString();
            }catch (JSONException e){
                e.printStackTrace();
            }

        }
        if ( mRequestCallBack != null ) {
            if ( convetString( response ) instanceof JSONObject ) {
                mRequestCallBack.onResult( Parse.FromJsonToObject( response, mObjectParser ) );
            } else if ( convetString( response ) instanceof JSONArray ) {
                ArrayList< Object > lst = new ArrayList<>();
                lst = Parse.FromJsonArrayToArrayObject( response.toString(), mObjectParser );
                mRequestCallBack.onResultArray( lst );
            }
        }
        if (mUIRequestCallBack != null){
            mUIRequestCallBack.end();
        }
    }

    private class RequestJson extends StringRequest {

        private boolean isMultipart = false;
        private HttpEntity mHttpEntity;
        private String mPathFile;
        private String mFileKey;
        private File mFile;


        public RequestJson ( int method, String url, Response.Listener< String > listener, Response.ErrorListener errorListener ) {

            super( method, url, listener, errorListener );
        }


        @Override
        public String getCacheKey () {

            return mKey;
        }

        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            Map< String, String > Headers = new HashMap< String, String >();
            if ( mHeaders != null && mHeaders.size() > 0 ) {
                for ( Param param : mHeaders) {
                    Headers.put( param.getParamName(), param.getParamValue().toString() );
                }
            }else {
                return super.getHeaders();
            }
            return Headers;
        }

        @Override
        protected Map< String, String > getParams () throws AuthFailureError {

            Map< String, String > Params = new HashMap< String, String >();
            if ( mParams != null && mParams.size() > 0 ) {
                for ( Param param : mParams ) {
                    Params.put( param.getParamName(), param.getParamValue().toString() );
                }
            }else {
                return  super.getParams();
            }

            return Params;
        }

        @Override
        public String getBodyContentType () {

            if ( this.isMultipart ) {
                return mHttpEntity.getContentType().getValue();
            }
            return super.getBodyContentType();
        }

        @Override
        public byte[] getBody () throws AuthFailureError {

            if ( this.isMultipart ) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                try {
                    mHttpEntity.writeTo( bos );
                } catch ( Exception e ) {
                    VolleyLog.e( "IOException writing to ByteArrayOutputStream" );
                }
                return bos.toByteArray();
            }
            return super.getBody();
        }

        @Override
        protected Response< String > parseNetworkResponse ( NetworkResponse response ) {

            String parsed;
            try {
                parsed = new String( response.data, "UFT-8" );
            } catch ( UnsupportedEncodingException e ) {
                parsed = new String( response.data );
            }
            return Response.success( parsed, HttpHeaderParser.parseCacheHeaders( response ) );
        }

        public void setMultipart ( boolean Multipart ) {

            this.isMultipart = Multipart;
        }

        public void setPathFile ( String pathFile ) {

            this.mPathFile = pathFile;
        }

        public void setFile ( File file ) {

            this.mFile = file;
        }

        public void setFileKey ( String fileKey ) {

            this.mFileKey = fileKey;
        }

        private HttpEntity buildMultipartEntity () {

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            if ( this.mFile != null ) {
                FileBody fileBody = new FileBody( this.mFile );
                builder.addPart( this.mFileKey, fileBody );
            }
            for ( Param param : mParams ) {
                builder.addTextBody( param.getParamName(), param.getParamValue().toString() );
            }
            return builder.build();
        }
    }
}
