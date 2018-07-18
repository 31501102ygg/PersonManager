package com.zucc.ygg31501102.personmanager.plug_in;

import android.util.Log;

import com.zucc.ygg31501102.personmanager.modal.Exchange;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class JSONParser {
    static InputStream sInputStream = null;
    static JSONObject sReturnJSONObject = null;
    static String sRawJSONString = "";
    public List<Exchange> getsReturnJSONObject(String url) {
        List<Exchange> objectArray = new ArrayList<Exchange>() ;
        try{
            URL urlObj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection)urlObj.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            int resonseCode = connection.getResponseCode();
            if (resonseCode == HttpURLConnection.HTTP_OK){
                sInputStream = connection.getInputStream();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        try{
            BufferedReader reader = new BufferedReader(new InputStreamReader(sInputStream,"UTF-8"),8);
            StringBuffer stringBuffer = new StringBuffer();
            String line = null;
            while((line=reader.readLine())!=null){
                stringBuffer.append(line+"\n");
            }
            sInputStream.close();
            sRawJSONString = stringBuffer.toString();
        }catch(Exception e ){
            Log.e("JsonParser","数据接收失败");
        }
        try{
            sReturnJSONObject = new JSONObject(sRawJSONString);
        }catch(Exception e){
            Log.e("JsonParser","生成JSON对象失败");
        }
        try {
            JSONArray jsonArray = sReturnJSONObject.getJSONArray("result");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            for (int i=1;i<=jsonObject.length();i++){
                String dataName = "data";
                dataName = dataName+i;
                JSONObject jsonObject1 = jsonObject.getJSONObject(dataName);
                Exchange exchange = new Exchange();
                exchange.setName(jsonObject1.getString("name"));
                exchange.setBankConversionPri(jsonObject1.getDouble("bankConversionPri"));
                exchange.setDate(jsonObject1.getString("date"));
                exchange.setTime(jsonObject1.getString("time"));
                objectArray.add(exchange);
            }
        } catch (JSONException e) {
            Log.e("JsonParser","JSON对象解析失败");
        }
        return objectArray;
    }
}
