package com.cloudstuff.tictactoe.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class GsonUtils {

    private final Gson mGson;

    @Inject
    GsonUtils(Gson gson) {
        this.mGson = gson;
    }

    public JSONObject createJsonObjectFromPOJO(Object object) throws JSONException {
        return new JSONObject(mGson.toJson(object));
    }

    public JSONArray createJsonArrayFromList(List listOfObjects) throws JSONException {
        return new JSONArray(mGson.toJson(listOfObjects));
    }

    /**
     * add json object  as param also add pojo.class as param so will clare object with that class
     * you must type cast object to your object
     */
    public Object createPOJOFromJsonObject(JSONObject jsonObject, Class pojoClass) {
        return mGson.fromJson(jsonObject.toString(), pojoClass);
    }

    /**
     * add json object  as param also add pojo.class as param so will clare object with that class
     * you must type cast object to your object
     */
    public Object createPOJOFromString(String jsonString, Class pojoClass) throws Exception {
        return mGson.fromJson(jsonString, pojoClass);
    }

    public String toJsonString(Object object) {
        return mGson.toJson(object);
    }
}
