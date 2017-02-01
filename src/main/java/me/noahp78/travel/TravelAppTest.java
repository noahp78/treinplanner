package me.noahp78.travel;


import biweekly.Biweekly;
import biweekly.ICalVersion;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import biweekly.io.chain.ChainingTextStringParser;
import biweekly.io.text.ICalWriter;
import biweekly.util.ICalDate;
import com.sun.net.httpserver.HttpServer;
import me.noahp78.travel.http.CacheServer;
import me.noahp78.travel.http.HTTPIcalServer;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.HttpClientBuilder;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.IsoFields;
import java.time.temporal.TemporalField;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by noahp on 23/jan/2017 for TravelApp
 */
public class TravelAppTest {

    public static final String SERVICE = "http://webservices.ns.nl/ns-api-treinplanner?fromStation={from}&toStation={to}&dateTime={time}&Departure={dep}";
    /**
     * Hoeveel dagen kijken we vooruit?
     */
    public static final int MAX_LOOK_AHEAD=5;

    public static void main(String[] args) {
        System.out.println("CurrentZone = " + ZoneId.systemDefault().toString());
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/ical", new HTTPIcalServer());
            server.setExecutor(null); // creates a default executor
            server.start();
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static String makeICALUK(String ROOSTER, String START_LOCATION, String END_LOCATION){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        try{
            String rooster = NetUtil.sendGet(ROOSTER,true);

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static String makeICAL(String ROOSTER, String START_LOCATION, String END_LOCATION){
        //Eerst lezen we het rooster in
        //En maken we ons eigen "ICAL" bestand aan
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        Date yesterday = cal.getTime();

        try {
            String rooster= NetUtil.sendGet(ROOSTER,true);
            ChainingTextStringParser thing = Biweekly.parse(rooster);

            ICalDate firstOfToday = null;
            ICalDate lastOfToday = null;
            ICalendar ical = new ICalendar();
            int lookedahead= 0;
            ical.setDescription("ReisPlanning gebaseerd op " + ROOSTER + " tussen " + START_LOCATION + " en " + END_LOCATION);
            for(VEvent event : thing.first().getEvents()) {
                if(event.getDateStart().getValue().getTime()< yesterday.getTime()){
                    continue;
                }
                //First error check if it isn't null
                if (firstOfToday == null || lastOfToday == null) {
                    firstOfToday = (event.getDateStart().getValue());
                    lastOfToday = event.getDateEnd().getValue();
                }
                //check if we still are looking at today
                if (event.getDateStart().getValue().getDay()== firstOfToday.getDay()) {
                    lastOfToday = event.getDateEnd().getValue();
                    //Check if this event is before the firstOfToday
                    if(event.getDateStart().getValue().toInstant().toEpochMilli() < firstOfToday.toInstant().toEpochMilli()){
                        firstOfToday = event.getDateStart().getValue();
                        System.out.println("Found a time where FirstOfToday was behind the current event");
                    }
                } else {
                    //Heenreis
                    VEvent heenreis = new VEvent();
                    NSReis heen = plan(START_LOCATION,END_LOCATION,firstOfToday.toInstant(),"false");

                    heenreis.setDateStart(toDate(heen.vertrek),true);
                    heenreis.setDateEnd(toDate(heen.aankomst),true);
                    heenreis.setSummary("NS TreinReis van " + START_LOCATION + " naar " + END_LOCATION);
                    heenreis.setDescription("AUTOMATISCH GEPLAND DOOR JE PLANSERVICE\nDeze reis wordt gemaakt omdat je vandaag je eerst les hebt om " +firstOfToday);
                    ical.addEvent(heenreis);

                    VEvent terug = new VEvent();
                    NSReis terugreis = plan(END_LOCATION,START_LOCATION,lastOfToday.toInstant(),"true");

                    terug.setDateStart(toDate(terugreis.vertrek),true);
                    terug.setDateEnd(toDate(terugreis.aankomst),true);
                    terug.setSummary("NS TreinReis van " + END_LOCATION +" naar " + START_LOCATION);
                    terug.setDescription("AUTOMATISCH GEPLAND DOOR JE PLANSERVICE\nDeze reis wordt gemaakt omdat je vandaag je laatste les hebt om " +lastOfToday);

                    ical.addEvent(terug);
                    lookedahead++;
                    if(lookedahead > MAX_LOOK_AHEAD) {
                        break;
                    }else{
                        firstOfToday=event.getDateStart().getValue();
                        lastOfToday=event.getDateEnd().getValue();
                    }
                }
            }
            File file = new File("reis.ical");
            ical.addName("ReisPlanning voor rooster");
            ical.setVersion(ICalVersion.V2_0);
            ICalWriter writer = null;
            ByteArrayOutputStream target =new ByteArrayOutputStream();
            try {
                writer = new ICalWriter(target, ICalVersion.V2_0);
                writer.write(ical);
            } finally {
                if (writer != null) writer.close();
            }
            return target.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    public static Date toDate(LocalDateTime time){
        Date in = new Date();
        Date out = Date.from(time.atZone(ZoneId.of("Europe/Berlin")).toInstant());
        return out;
    }
    public static NSReis plan(String from, String to, Instant instant, String dep){
        String url = SERVICE.replace("{from}",from).replace("{to}",to).replace("{time}",instant.toString()).replace("{dep}",dep);
        try{
            String resp = sendAuthenticatedRequest(url,me.noahp78.travel.Config.NS_USER,me.noahp78.travel.Config.NS_PASS);
            NSReis reis = vindtOptimaal(resp);
            System.out.println("De beste reis vertrekt om " + reis.vertrek+ " en komt aan op " + reis.aankomst);
            return reis;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static String read(InputStream input) throws IOException {
        try (BufferedReader buffer = new BufferedReader(new InputStreamReader(input))) {
            return buffer.lines().collect(Collectors.joining("\n"));
        }
    }

    public static NSReis vindtOptimaal(String nsResponse) throws Exception{
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        ByteArrayInputStream input =  new ByteArrayInputStream(
                nsResponse.getBytes("UTF-8"));
        Document doc = builder.parse(input);
        doc.getDocumentElement().normalize();
        Element root = doc.getDocumentElement();
        NodeList nList = doc.getElementsByTagName("ReisMogelijkheid");
        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE ) {
                Element eElement = (Element) nNode;
                String optimaal = eElement.getElementsByTagName("Optimaal").item(0).getTextContent();
                if(optimaal.equalsIgnoreCase("true")){
                    //Vindt vertrek en aankomsttijd
                    String vertrek= eElement.getElementsByTagName("ActueleVertrekTijd").item(0).getTextContent();
                    String aankomst = eElement.getElementsByTagName("ActueleAankomstTijd").item(0).getTextContent();
                    System.out.println("VERTREK:" + vertrek + "\nAANKOMST:" + aankomst );
                    //Shitty hack todo find better way
                    vertrek = vertrek.replace("+0100","+01:00").replace("+0200","+02:00");
                    aankomst= aankomst.replace("+0100","+01:00").replace("+0200","+02:00");

                    LocalDateTime localDate = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

                    return new NSReis(localDate.parse(vertrek,formatter),localDate.parse(aankomst,formatter));
                }
            }
        }
        return null;
    }
    public static String sendAuthenticatedRequest(String url, String user, String pass)throws Exception{
        if(CacheServer.haveAllowedNSCache(url)){
            System.out.println("Saved NS API request because we have it cached.");
            return CacheServer.getNSCachedItem(url);
        }else{
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader(BasicScheme.authenticate(
                    new UsernamePasswordCredentials(user, pass),
                    "UTF-8", false));

            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity responseEntity = httpResponse.getEntity();
            String resp = read(responseEntity.getContent());
            CacheServer.saveNSRequest(url,resp);
            return resp;
        }

    }



}
