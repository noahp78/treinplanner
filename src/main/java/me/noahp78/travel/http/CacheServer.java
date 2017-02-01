package me.noahp78.travel.http;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by noahp on 23/jan/2017 for TravelApp
 */
public class CacheServer {
    //Save Requests from NS;
    private static Map<String, CacheItem> NSRequests= Collections.synchronizedMap(new WeakHashMap<String, CacheItem>());
    private static Map<String, CacheItem> WEBRequests= Collections.synchronizedMap(new WeakHashMap<String, CacheItem>());

    public static int NSCacheSize(){
        return NSRequests.size();
    }
    public static int WEBCacheSize(){
        return WEBRequests.size();
    }
    public static boolean haveAllowedNSCache(String url){
        if(NSRequests.containsKey(url)){
            if(NSRequests.get(url).isValid()){
                return true;
            }else{
                System.out.println("NSRequest(Removed Outdated Request)");
                NSRequests.remove(url);
                return false;
            }
        }
        return false;
    }
    public static void saveNSRequest(String url, String response){
        NSRequests.put(url,new CacheItem(response));
    }
    public static boolean haveAllowedWebCache(String url){
        if(WEBRequests.containsKey(url)){
            if(WEBRequests.get(url).isValid()){
                return true;
            }else{
                System.out.println("WEBRequest(Removed Outdated Request)");
                WEBRequests.remove(url);
                return false;
            }
        }
        return false;
    }
    public static String getNSCachedItem(String url){
        return NSRequests.get(url).getContent();
    }
    public static String getWebCachedItem(String url){
        return WEBRequests.get(url).getContent();
    }
    public static void saveWEBRequest(String url, String response){
        WEBRequests.put(url,new CacheItem(response));
    }

}
