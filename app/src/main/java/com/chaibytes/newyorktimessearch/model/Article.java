package com.chaibytes.newyorktimessearch.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by pdebadarshini on 10/22/16.
 */

public class Article implements Serializable {

    public String getThumbNail() {
        return thumbNail;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline;
    }

    String webUrl;
    String headline;
    String thumbNail;

    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if (multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Factory method
    public static ArrayList<Article> fromJSONArray (JSONArray array) {
        ArrayList<Article> results = new ArrayList<>(array.length());

        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }

}
