package com.example.formproject.service;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;

//9796290E-0590-3531-B798-07A913B8A6A9
@Service
public class GeoService {

    public String[] getGeoPoint(String address) {
        String apiURL = "http://api.vworld.kr/req/address";

        try{
            int responseCode = 0;
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");

            String text_content =  URLEncoder.encode(address, "utf-8");

            // post request
            String postParams = "service=address";
            postParams += "&request=getcoord";
            postParams += "&version=2.0";
            postParams += "&crs=EPSG:4326";
            postParams += "&address="+text_content;
            postParams += "&arefine=true";
            postParams += "&simple=true";
            postParams += "&format=json";
            postParams += "&type=road";
            postParams += "&errorFormat=json";
            postParams += "&key=9796290E-0590-3531-B798-07A913B8A6A9";

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            responseCode = con.getResponseCode();
            BufferedReader br;

            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }else{  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            System.out.println(response);
            br.close();
            con.disconnect();

            String point = response.toString();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(point);
            JSONObject parse_response = (JSONObject) obj.get("response");
            System.out.println(parse_response);
            JSONObject parse_result = (JSONObject) parse_response.get("result");
            JSONObject parse_point = (JSONObject) parse_result.get("point");
            String x = parse_point.get("x").toString();
            String y = parse_point.get("y").toString();

            return new String[] {x,y};

        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

