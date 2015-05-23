package edu.sjsu.cmpe.cache.client;

public class Client {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting Cache Client...");

        CacheServiceInterface cache = new DistributedCacheService(
                "http://localhost:300");

        // STEP I: HTTP PUT call to store “a” to key 1
        System.out.println("STEP I: HTTP PUT call to store a to key 1");
        cache.put(1, "a");
        System.out.println("put(1 => a)");

        System.out.println("Sleeping to bring down Server A");
        Thread.sleep(30000); // Sleeping to bring down Server A

        // STEP II: HTTP PUT call to update key 1 value to “b”
        System.out.println("STEP II: HTTP PUT call to update key 1 value to b");
        cache.put(1,"b");
        System.out.println("put(1 => b)");

        System.out.println("Sleeping to bring up Server A");
        Thread.sleep(45000); // Sleeping to bring up Server A

        // STEP III: HTTP GET call to retrieve key “1” value
        System.out.println("STEP III: HTTP GET call to retrieve key 1 value");
        cache.get(1);
        Thread.sleep(10000);
        String value = ReadRepairCallback.consistentValue;
        System.out.println("get(1 =>"+ value +")");

        System.out.println("Existing Cache Client...");
    }

}
