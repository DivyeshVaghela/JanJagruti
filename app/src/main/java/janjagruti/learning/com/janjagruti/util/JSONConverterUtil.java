package janjagruti.learning.com.janjagruti.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class JSONConverterUtil {

    public static final String TAG = JSONConverterUtil.class.getSimpleName();

    public static class DateDeserializer implements JsonDeserializer<Date>{

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            String date = json.getAsString();
            SimpleDateFormat formtter = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");

            try {
                return formtter.parse(date);
            } catch (ParseException e) {
                Log.d(TAG, "Date Conversion Failed");
                return null;
            }
        }
    }

    public static Gson getGson(){

        GsonBuilder gsonBuilder = new GsonBuilder();
        //gsonBuilder.registerTypeAdapter(Date.class, new DateDeserializer());
        return gsonBuilder.create();
    }

    public static <T> JSONObject toJSONObject(T object){
        Gson gson = getGson();
        try {
            return new JSONObject(gson.toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJSONObject(JSONObject jsonObject, Class<T> clazz){
        return getGson().fromJson(jsonObject.toString(), clazz);
    }

    public static <T> T fromJSONObject(String jsonObject, Class<T> clazz){
        return getGson().fromJson(jsonObject, clazz);
    }

    public static <T> T fromJSONObject(JSONObject jsonObject, Type type){
        return getGson().fromJson(jsonObject.toString(), type);
    }

    public static <T> List<T> fromJSONArray(JSONArray jsonArray, Type type){
        return getGson().fromJson(jsonArray.toString(), type);
    }
}
