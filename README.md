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

## How to host
It should all work if you simply compile the jar and run the travelapi-jar-with-dependencies.jar.
