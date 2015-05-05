package groupbase.vn.thn.baselibs.util;

import android.util.Log;
import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by nghiath on 4/3/15.
 */
public class Parse {

    public static <T> T FromJsonToObject(String stringJsonObject, Class<T> object) {

        try {
            JSONObject jsonObject = new JSONObject(stringJsonObject);
            List<Field> fields = new ArrayList<Field>();
            fields.addAll(Arrays.asList(object.getDeclaredFields()));
            Object result = object.newInstance();
            for (Field field : fields) {
                field.setAccessible(true);
                JsonAnnotation jsonAnnotation = field.getAnnotation(JsonAnnotation.class);
                if (jsonAnnotation != null) {
                    try {
                        Object valueJson = jsonObject.get(jsonAnnotation.FieldName());
                        if (valueJson != null) {
                            if (valueJson instanceof JSONObject) {
                                if (jsonAnnotation.isObject()) {
                                    field.set(result, FromJsonToObject(valueJson.toString(), jsonAnnotation.FieldType()));
                                }
                            } else {
                                if (valueJson instanceof JSONArray) {
                                    if (jsonAnnotation.isObject()) {
                                        field.set(result, FromJsonArrayToArrayObject(valueJson.toString(), jsonAnnotation.FieldType()));
                                    }
                                } else {
                                    field.set(result, jsonAnnotation.FieldType().cast(valueJson));
                                }
                            }
                        } else {
                            field.set(result, null);
                        }
                    } catch (JSONException e) {
                        Log.e("JsonParse",jsonAnnotation.FieldName()+": "+ e.getMessage());
                    } catch (Exception e) {
                        Log.e("ObjectParse",jsonAnnotation.FieldName()+": "+ e.getMessage());
                    }
                }
            }
            return (T) result;
        } catch (Exception e) {
            Log.e("ObjectParse", e.getMessage());
            return null;
        }

    }

    public static <T> ArrayList<T> FromJsonArrayToArrayObject(String stringJsonArray, Type typeObject) {

        try {
            JSONArray jsonArray = new JSONArray(stringJsonArray);
            Class<?> clazz = (Class<?>) typeObject;
            ArrayList<T> result = new ArrayList<T>();
            if (String.class.isAssignableFrom(clazz)) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        result.add((T) jsonArray.get(i));
                    } catch (JSONException e) {
                        Log.e("JsonParse", e.getMessage());
                        result = null;
                    }
                }
            } else {
                if (JSONArray.class.isAssignableFrom(clazz)) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            result.add((T) jsonArray.get(i));
                        } catch (JSONException e) {
                            Log.e("JsonParse", e.getMessage());
                            result = null;
                        }
                    }
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Object value = null;
                        try {
                            value = jsonArray.get(i);
                            if (value instanceof JSONArray) {
                                return FromJsonArrayToArrayObject(jsonArray.getString(i), clazz);
                            } else {
                                result.add((T) FromJsonToObject(jsonArray.getString(i), clazz));
                            }
                        } catch (JSONException e) {
                            Log.e("JsonParse", e.getMessage());
                            result = null;
                        }

                    }
                }
            }
            return result;
        } catch (JSONException e) {
            Log.e("JsonParse", e.getMessage());
            return null;
        }

    }
}