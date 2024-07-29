package org.Main;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class URLParser {
    public static void main(String[] args) {


        String url = "http://example.com?customerId=12345&email=customer@example.com";
        Map<String, String> queryParams = getQueryParams(url);

        String customerId = queryParams.get("customerId");
        String email = queryParams.get("email");

        //Testing if gathered correctly
        System.out.println("Customer ID: " + customerId);
        System.out.println("Email: " + email);

    }

    private static Map<String, String> getQueryParams(String url){
        try{
            Map<String, String> queryParams = new HashMap<>();
            String[] urlParts = url.split("\\?");
            if (urlParts.length > 1){
                String query = urlParts[1];
                String[] pairs = query.split("&");
                for(String pair : pairs){
                    String[] keyValue = pair.split("=");
                    String key = URLDecoder.decode(keyValue[0], "UTF-8");
                    String value = URLDecoder.decode(keyValue[1], "UTF-8");
                    queryParams.put(key, value);
                }
            }
            return queryParams;
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error parsing URL", e);
        }

    }
}
