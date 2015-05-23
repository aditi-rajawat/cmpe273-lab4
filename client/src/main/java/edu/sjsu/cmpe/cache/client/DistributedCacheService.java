package edu.sjsu.cmpe.cache.client;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.http.async.Callback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Distributed cache service
 * 
 */
public class DistributedCacheService implements CacheServiceInterface {

    private final String cacheServerUrl;

    public DistributedCacheService(String serverUrl) {
        this.cacheServerUrl = serverUrl;
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#get(long)
     */
    @Override
    public String get(long key) {

        try {

            for(int i=0; i<3; i++){
                ReadRepairCallback readCallback = new ReadRepairCallback(this.cacheServerUrl+i, key);
                Future<HttpResponse<JsonNode>> future = Unirest.get(this.cacheServerUrl + i + "/cache/{key}")
                        .header("accept", "application/json")
                        .routeParam("key", Long.toString(key))
                        .asJsonAsync(readCallback);
            }

        } catch (Exception e) {
            System.err.println(e);
        }

    return "";
    }

    /**
     * @see edu.sjsu.cmpe.cache.client.CacheServiceInterface#put(long,
     *      java.lang.String)
     */
    @Override
    public void put(long key, String value) {

        try {

            for(int i=0; i<3; i++) {
                WriteCallback writeCallback = new WriteCallback(this.cacheServerUrl+i, key);
                Future<HttpResponse<JsonNode>> future = Unirest.put(this.cacheServerUrl + i+ "/cache/{key}/{value}")
                        .header("accept", "application/json")
                        .routeParam("key", Long.toString(key))
                        .routeParam("value", value)
                        .asJsonAsync(writeCallback);
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }


}
