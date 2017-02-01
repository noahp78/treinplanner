package me.noahp78.travel.http;

import java.util.Date;

/**
 * Created by noahp on 23/jan/2017 for TravelApp
 */
public class CacheItem {
    String content;
    long createdTime;

    public CacheItem(String content) {
        this.content = content;
        this.createdTime = new Date().getTime();
    }
    public boolean isValid(){
        return (this.createdTime + MAX_STORE > new Date().getTime());
    }

    public String getContent() {
        return content;
    }

    //We keep most items cached for 8 hours.
    public static final long MAX_STORE = 14400000L;

}
