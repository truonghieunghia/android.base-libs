package groupbase.vn.thn.baselibs.service;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import groupbase.vn.thn.baselibs.service.callback.RequestCallBack;
import groupbase.vn.thn.baselibs.service.callback.RequestErrorCallBack;
import groupbase.vn.thn.baselibs.util.Param;
import groupbase.vn.thn.baselibs.util.Parse;

/**
 * Created by nghiath on 4/1/15.
 */
public class ConnectWS {

    private ArrayList< Param > mParams;
    private String mKey;
    private RequestCallBack mRequestCallBack;
    private RequestErrorCallBack mRequestErrorCallBack;
    private Context mContext;
    private Class< ? > mObjectParser;
    private String mUrl;
    private Object mDataCache = null;
    private RequestQueue mRequestQueue;
    public ConnectWS ( String url, Context context ) {

        this.mRequestCallBack = null;
        this.mObjectParser = null;
        this.mUrl = url;
        this.mContext = context;
        this.mKey = url;
    }

    public < T > ConnectWS ( String url, RequestCallBack< T > requestCallBack, Context context ) {

        this.mContext = context;
        this.mUrl = url;
        this.mKey = url;
        this.setRequestCallBack( requestCallBack );
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
    private RequestQueue getRequestQueue () {

        mRequestQueue = Volley.newRequestQueue( mContext );
        return mRequestQueue;
    }

    private < T > void addToRequestQueue ( Request< T > req, String tag ) {

        req.setTag( tag );
        getRequestQueue().add( req );
    }

    private < T > void addToRequestQueue ( Request< T > req ) {

        req.setTag( "Service Connect" );
        getRequestQueue().add( req );
    }

    private Object ConvetString ( String str ) {

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
                return ConvetString( data );
            } catch ( UnsupportedEncodingException e ) {
                return null;
            }
        } else {
            return null;
        }
    }

    public <T> void loadCache(RequestCallBack requestCallBack){
        createKey();
        if ( mDataCache !=null ){
            try {
                if ( mDataCache instanceof JSONObject ){
                    requestCallBack.onResult( Parse.FromJsonToObject( new JSONObject( mDataCache.toString() ),mObjectParser ) );
                }else {
                    if ( mDataCache instanceof JSONArray ){
                        JSONArray jsonArray = new JSONArray( mDataCache.toString() );
                        ArrayList<T> lst = new ArrayList<T>();
                        for (  int i = 0; i < jsonArray.length() ; i++ ){
                         lst.add( (T)Parse.FromJsonToObject( jsonArray.getJSONObject( i ),mObjectParser ) );
                        }
                        requestCallBack.onResultArray( lst );
                    }

                }
            }catch ( Exception e ){
                requestCallBack.onResult( null );
            }
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
        protected Map< String, String > getParams () throws AuthFailureError {

            Map< String, String > Params = new HashMap< String, String >();
            if ( mParams != null && mParams.size() > 0 ) {
                for ( Param param : mParams ) {
                    Params.put( param.getParamName(), param.getParamValue().toString() );
                }
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
