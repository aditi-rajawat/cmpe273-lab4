package edu.sjsu.cmpe.cache.client;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by aditi on 22/05/15.
 */
public class ReadRepairCallback implements Callback<JsonNode>  {
    private static AtomicInteger noOfCalls = new AtomicInteger(0);
    private static List<String> mapOfUrls = new ArrayList<String>();
    private static List<String> mapOfVals = new ArrayList<String>();
    public static String consistentValue = "";

    private String serverLocUrl;
    private long key;

    public ReadRepairCallback(String serverUrl, long keyVal){
        serverLocUrl = serverUrl;
        key = keyVal;
    }

    @Override
    public void completed(HttpResponse<JsonNode> response) {
        int noOfResponses;

        String value = "";
        if(response.getBody()!=null){
            if(response.getBody().getObject()!=null){
                value = response.getBody().getObject().getString("value");
            }
            else{
                value = "";
            }
        }

        mapOfUrls.add(serverLocUrl);
        mapOfVals.add(value);

        noOfResponses = noOfCalls.incrementAndGet();

        if(noOfResponses == 3){

        //    String correctValue = "";
            String callbackServer = "";
            int index=0;
            int noOfCorrectInstances = 0;
            int ind = 0;

            for(String str: mapOfVals){

                int countOfVal = 0;
                for(int j=0; j< mapOfVals.size(); j++){
                    if(mapOfVals.get(j).equals(str)){
                        countOfVal++;
                    }
                    else
                        ind = j;
                }
                if(countOfVal>=2) {
                    consistentValue = str;
                    noOfCorrectInstances = countOfVal;
                    break;
                }

                index++;
            }

            callbackServer = mapOfUrls.get(ind);

            if(noOfCorrectInstances != 3){
                System.out.println("There are not 3 correct values...so correcting on server "+ callbackServer);
                Future<HttpResponse<JsonNode>> future = Unirest.put(callbackServer + "/cache/{key}/{value}")
                        .header("accept", "application/json")
                        .routeParam("key", Long.toString(key))
                        .routeParam("value", value)
                        .asJsonAsync();
            }
        }

    }

    @Override
    public void failed(UnirestException e) {

    }

    @Override
    public void cancelled() {

    }
}
