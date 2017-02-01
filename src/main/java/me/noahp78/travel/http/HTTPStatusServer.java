package me.noahp78.travel.http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.Map;

/**
 * Created by noahp on 24/jan/2017 for TravelApp
 */
public class HTTPStatusServer implements HttpHandler {
    @Override
    public void handle(HttpExchange t) throws IOException {
        Map<String, String> params = HTTPIcalServer.queryToMap(t.getRequestURI().getQuery());
        String response = "";
        response = "Cache(" + CacheServer.NSCacheSize() + CacheServer.WEBCacheSize() + " items cached.";
    }

}
