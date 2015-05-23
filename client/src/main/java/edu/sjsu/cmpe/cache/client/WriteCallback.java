package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.async.Callback;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by aditi on 22/05/15.
 */
public class WriteCallback implements Callback<JsonNode> {

    private static AtomicInteger successCount = new AtomicInteger(0);
    private static AtomicInteger failureCount = new AtomicInteger(0);
    private static String singleSuccessUrl = "";
    private String serverUrl;
    private long key;

    public WriteCallback(String url, long keyVal){
        this.serverUrl = url;
        this.key = keyVal;
    }

    public void failed(UnirestException e) {
        int count = failureCount.incrementAndGet();
        if(count==2) {
            System.out.println("The request failed for key "+ key+" !! Try Again..");
            if(successCount.get()==1) {
                try {
                    HttpResponse<JsonNode> response = Unirest.delete(singleSuccessUrl + "/cache/{key}")
                            .header("accept", "application/json")
                            .routeParam("key", Long.toString(key)).asJson();

//                    if (response.getStatus() == 204)
//                        System.out.println("Deleted from server.. ");
                } catch (UnirestException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

    public void completed(HttpResponse<JsonNode> response) {

        int code = response.getStatus();
        int count = successCount.incrementAndGet();
        singleSuccessUrl = serverUrl;

        if(count == 2) {
            System.out.println("Request processed successfully for key "+ key+" !!");
        }

        if(count==1 && failureCount.get()==2){
            System.out.println("The request failed for key "+ key+" !! Try Again..");

            try {
                HttpResponse<JsonNode> res = Unirest.delete(serverUrl + "/cache/{key}")
                        .header("accept", "application/json")
                        .routeParam("key", Long.toString(key)).asJson();

//                if(res.getStatus() == 204)
//                    System.out.println("Deleted from server.. ");
            } catch (UnirestException ex) {
                System.err.println(ex);
            }
        }
    }

    public void cancelled() {
        System.out.println("The request has been cancelled");
    }
}
