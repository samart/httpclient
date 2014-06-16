# Vert.x Example Showing HttpClient issue

i uploaded a sample project that shows the issue here.  2.1.1-snapshot didnt seem to fix,

https://github.com/samart/httpclient

if you do a mvn runMod and hit http://localhost:9903/api/doPost from your browser u will see.

then in Bootstrap, change the HttpClientVerticle to multithreaded=false.  it then works, but sometimes gets 'stuck' - you'll see your browser waiting.  (note, i dont think i actually need a multthreaded verticle, but there is this periodic sticking issue with the single threaded one)

This example is written in the spirit of how my app is constructed.  the client api is to be used standalone as well.  (note, i'm also using SSL with my client (which works great btw), but i was able to recreate the issue with this simple example)


## Latest Findings
 - When the httpclient is used in a single threaded worker verticle, httpClient.post() can be blocked by a countdownlatch
 - when run as a multi-threaded worker verticle, responses simply nullpointer.