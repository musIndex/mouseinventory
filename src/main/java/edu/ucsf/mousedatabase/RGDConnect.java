package edu.ucsf.mousedatabase;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.ucsf.mousedatabase.objects.RGDResult;
import org.json.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class RGDConnect {
    public static String executeGet(String request) {
        URL url;
        HttpURLConnection connection = null;
        try {
            url = new URL("https://rest.rgd.mcw.edu/rgdws/" + request);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                return "Error: " + responseCode;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuffer response = new StringBuffer();
            String line;

            while ((line = in.readLine()) != null) {
                response.append(line);
            }


            return response.toString();
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }
    }

    public static RGDResult getGeneQuery(String geneId) {
        RGDResult result = new RGDResult();
        String request = "genes/" + geneId;
        String response = executeGet(request);
        try {
            JSONObject jsonResponse = new JSONObject(response);
            result.setName(jsonResponse.getString("name"));
            result.setSymbol(jsonResponse.getString("symbol"));
            result.setComment(jsonResponse.getString("mergedDescription"));
            result.setValid(true);
        } catch (JSONException err) {
            Log.Error(err);
            result.setValid(false);
            result.setErrorString(response);
        }

        return result;
    }
}