package me.noahp78.travel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import static org.apache.http.protocol.HTTP.USER_AGENT;

/**
 * Created by noahp on 23/jan/2017 for TravelApp
 */
public class NetUtil {
    public static String sendGet(String url, Boolean shouldNewLine) throws Exception {


        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        String response= "";
        while ((inputLine = in.readLine()) != null) {
            response+=(inputLine);
            if(shouldNewLine){
                response+=("\n");
            }
        }
        in.close();

        //print result
        return response;

    }
}
