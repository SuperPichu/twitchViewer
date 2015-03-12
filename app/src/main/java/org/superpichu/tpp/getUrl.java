package org.superpichu.tpp;

import android.os.AsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

/**
 * Created by chris on 3/11/15.
 */
public class getUrl extends AsyncTask<String, Void, String>{
    @Override
    protected String doInBackground(String... params) {
        String channel = params[0];
        String playlist = "";
        try {
            // Create a new HTTP Client
            DefaultHttpClient defaultClient = new DefaultHttpClient();
            // Setup the get request
            HttpGet jsonGet = new HttpGet("http://api.twitch.tv/api/channels/" + channel + "/access_token");

            // Execute the request in the client
            HttpResponse httpResponse = defaultClient.execute(jsonGet);
            // Grab the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
            String result = reader.readLine();
            // Instantiate a JSON object from the request response
            JSONObject json = new JSONObject(result);
            String token = json.getString("token");
            String sig = json.getString("sig");
            token = URLEncoder.encode(token, "ISO-8859-1");
            HttpGet m3uGet = new HttpGet("http://usher.twitch.tv/api/channel/hls/" + channel + ".m3u8?token=" + token + "&sig=" + sig);

            // Execute the request in the client
            HttpResponse m3uResponse = defaultClient.execute(m3uGet);
            // Grab the response
            BufferedReader m3uReader = new BufferedReader(new InputStreamReader(m3uResponse.getEntity().getContent(), "UTF-8"));
            String line = null;
            playlist = null;
            while ((line = m3uReader.readLine()) != null) {
                if (line.contains("/high/py")) {
                    playlist = line;
                    break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return playlist;
    }
}
