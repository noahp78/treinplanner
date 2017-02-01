package me.noahp78.travel.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import me.noahp78.travel.TravelAppTest;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by noahp on 23/jan/2017 for TravelApp
 */
public class HTTPIcalServer implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = queryToMap(t.getRequestURI().getQuery());
        String response = "";
        if(!params.containsKey("rooster")){
            response = "Geen rooster";
            t.sendResponseHeaders(404, response.length());
        }else if(!params.containsKey("start")){
            response = "Geen start";
            t.sendResponseHeaders(404, response.length());
        }else if(!params.containsKey("end")){
            response = "Geen rooster";
            t.sendResponseHeaders(404, response.length());
        }else if(params.containsKey("uk")) {
            //Verwacht start en eind in postcode
            String start = params.get("start");
            String end = params.get("end");
            if(!start.startsWith("postcode") || !end.startsWith("postcode")){
                response = "UK start and end should start with a postcode";
                t.sendResponseHeaders(404,response.length());
            }

        }else{
            String rooster = params.get("rooster");
            String start = params.get("start");
            String end = params.get("end");
            String url = "ICAL/" + rooster + "/" + start + "/" + end;
            if(CacheServer.haveAllowedWebCache(url)){
                response = CacheServer.getWebCachedItem(url);
                System.out.println("Saved compute because cache");
            }else {
                response = TravelAppTest.makeICAL(rooster, start, end);
                CacheServer.saveWEBRequest(url,response);
            }
            t.sendResponseHeaders(200, response.length());
        }
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
    public static Map<String, String> queryToMap(String query){
        Map<String, String> result = new HashMap<String, String>();
        for (String param : query.split("&")) {
            String pair[] = param.split("=");
            if (pair.length>1) {
                result.put(pair[0], pair[1]);
            }else{
                result.put(pair[0], "");
            }
        }
        return result;
    }
}

