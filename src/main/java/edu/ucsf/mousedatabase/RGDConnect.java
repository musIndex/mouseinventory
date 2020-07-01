package edu.ucsf.mousedatabase;

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
            StringBuilder response = new StringBuilder();
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
    
    
}