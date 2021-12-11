package com.example.imagetranslation;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

public class OcrProc {
    public static String main(String ocrApiUrl, String ocrSecretKey, String uri) {

        String ocrMessage = "";

        try {

            String apiURL = ocrApiUrl;
            String secretKey = ocrSecretKey;

            String objectStorageURL = uri;
            Log.d("OCR", "uri : "+objectStorageURL);

            URL url = new URL(apiURL);

            String message = getReqMessage(objectStorageURL);
            System.out.println("##" + message);

            long timestamp = new Date().getTime();

            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json;UTF-8");
            con.setRequestProperty("X-OCR-SECRET", secretKey);

            // post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(message.getBytes("UTF-8"));
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();

            if(responseCode==200) { // 정상 호출
                System.out.println(con.getResponseMessage());

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                con.getInputStream()));
                String decodedString;
                while ((decodedString = in.readLine()) != null) {
                    ocrMessage = decodedString;
                }
                in.close();

            } else {  // 에러 발생
                ocrMessage = con.getResponseMessage();
                Log.e("OCR", "에러코드"+responseCode);

            }
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println(">>>>>>>>>>"+ ocrMessage);
        return ocrMessage;
    }

    public static String getReqMessage(String objectStorageURL) {

        String requestBody = "";

        try {

            long timestamp = new Date().getTime();

            JSONObject json = new JSONObject();
            json.put("version", "V1");
            json.put("requestId", UUID.randomUUID().toString());
            json.put("timestamp", Long.toString(timestamp));
            JSONObject image = new JSONObject();
            image.put("format", "png");
            image.put("url", objectStorageURL);
            image.put("name", "test_ocr");
            JSONArray images = new JSONArray();
            images.put(image);
            json.put("images", images);

            requestBody = json.toString();

        } catch (Exception e){
            System.out.println("# Exception : " + e);
        }

        return requestBody;

    }

}
