package com.marruky.Http;

import java.util.HashMap;
import java.util.Map;

public class RateLimiter {
    //int[] = {counter, windowStart}
    private Map<String, int[]> counters;

    public RateLimiter(){
        counters = new HashMap<>();
    }
    public boolean isAllowed(String ip) {
       int now = (int) (System.currentTimeMillis()/1000);

        if(!counters.containsKey(ip)) {
            int[] i = {1,  now};
            counters.put(ip, i);
            return true;
            }
        if(now > (counters.get(ip)[1] + 60)) {
            counters.get(ip)[0] = 1;
            counters.get(ip)[1] = now;
            return true;
        }
        if(counters.get(ip)[0] >= 60){
            return false;
        }else{
            counters.get(ip)[0]++;
            return true;
        }
    }
}
