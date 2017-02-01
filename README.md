# treinplanner
Automaticly put NS travel information in your calendar (ICAL webservice)

## How it works
This program exposes a service on port 8000. (/ical). When you pass the following arguments

| Argument | What does it do?                                                                    |
|----------|-------------------------------------------------------------------------------------|
| start    | The start "NS station" where you start your journey from every day.                 |
| end      | The "end" station where you have to be at the start of every event in your calendar |
| rooster  | ICAL url that points to your calendar                                               |

It will respond with travel information in ICAL format that you can add to your calendar.

## Why?
I hated looking in the NS app every single evening to see how early my train departs. and AUTOMATION!

## DEMO
You can use this service at http://stream1-nas.cloudapp.net:8000/ical


## Self hosting
First you have to get the NS API login details (https://www.ns.nl/ews-aanvraagformulier/?1)
When you have those you need to create a class "Config" in me.noahp78.travel that contains the following:

  public class Config {
      public static final String NS_USER="YOURUSERNAME";
      public static final String NS_PASS="YOURPASSWORD";
  }

Now use maven to compile the jar with dependencies:
   mvn package

And now you are ready to run the created jar file (target/travelapi-jar-with-dependencies.jar)
  java - jar travelapi-jar-with-dependencies.jar
  
Tada it works!
