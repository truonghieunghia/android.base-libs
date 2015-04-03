package groupbase.vn.thn.baselibs.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by nghiath on 4/3/15.
 */
public class Parse {

    public static < T > T FromJsonToObject ( JSONObject jsonObject, Class< T > object ) throws Exception {

        int field_index = 0;
        List< Field > fields = new ArrayList< Field >();
        fields.addAll( Arrays.asList( object.getDeclaredFields() ) );
        Object result = object.getConstructors();
        JsonAnnotation[] jsonAnnotation = ( JsonAnnotation[] ) object.getAnnotations();
        for ( JsonAnnotation field : jsonAnnotation ) {
            fields.get( field_index ).setAccessible( true );
            try {
                Object valueJson = jsonObject.getJSONArray( field.FieldName() );
                if ( valueJson != null ) {
                    if ( valueJson instanceof JSONArray ) {
                        if ( List.class.isAssignableFrom( field.FieldType() ) ) {
                            ParameterizedType pType = ( ParameterizedType ) fields.get( field_index ).getGenericType();
                            Type castType = pType.getActualTypeArguments()[ 0 ];
                            fields.get( field_index ).set( object, FromJsonArrayToArrayObject( ( JSONArray ) valueJson, castType ) );
                        }
                    } else if ( valueJson instanceof JSONObject ) {
                        if ( field.isObject() ) {
                            fields.get( field_index ).set( result, FromJsonToObject( ( JSONObject ) valueJson, field.FieldType() ) );
                        } else {
                            fields.get( field_index ).set( result, valueJson.getClass().cast( field.FieldType() ) );
                        }
                    }
                } else {
                    fields.get( field_index ).set( result, null );
                }
                field_index = field_index + 1;
            } catch ( JSONException e ) {
                e.printStackTrace();
            }
        }
        return ( T ) result;
    }

    public static < T > ArrayList< T > FromJsonArrayToArrayObject ( JSONArray jsonArray, Type typeObject ) throws Exception {

        Class< ? > clazz = ( Class< ? > ) typeObject;
        ArrayList< T > result = new ArrayList< T >();
        if ( String.class.isAssignableFrom( clazz ) ) {
            for ( int i = 0; i < jsonArray.length(); i++ ) {
                result.add( ( T ) jsonArray.get( i ) );
            }
        } else {
            if ( JSONArray.class.isAssignableFrom( clazz ) ) {
                for ( int i = 0; i < jsonArray.length(); i++ ) {
                    result.add( ( T ) jsonArray.get( i ) );
                }
            } else {
                for ( int i = 0; i < jsonArray.length(); i++ ) {
                    Object value = jsonArray.get( i );
                    if ( value instanceof JSONArray ) {
                        return FromJsonArrayToArrayObject( jsonArray.getJSONArray( i ), clazz );
                    } else {
                        result.add( ( T ) FromJsonToObject( jsonArray.getJSONObject( i ), clazz ) );
                    }
                }
            }
        }
        return result;
    }
}