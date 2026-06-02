package com.marruky.Http;


public class CircuitBreaker {
    private enum State {CLOSED, OPEN, HALF_OPEN}

    private State state;
    private int failureCount;
    private int failureThreshold;
    private long lastFailureTime;
    private int timeout;
    private boolean probeSent;

    public CircuitBreaker(){
        state = State.CLOSED;
        failureCount = 0;
        failureThreshold = 5;
        lastFailureTime = 0;
        timeout = 30;
        probeSent = false;
    }

    public synchronized boolean isAllowed(){
        long now = System.currentTimeMillis()/1000;
        if(state == State.CLOSED) {
            return true;
        }
        if(state == State.OPEN) {
            if(now > lastFailureTime + timeout) {
                state = State.HALF_OPEN;
                probeSent = true;
                return true;
            }else{
                return false;
            }
        }
        if(state == State.HALF_OPEN) {
            if(probeSent == false){
                probeSent = true;
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    public synchronized void recordSuccess() {
        if(state == State.HALF_OPEN){
            state = State.CLOSED;
            failureCount = 0;
            probeSent = false;
        }else if(state == State.CLOSED){
            return;
        }
    }

    public synchronized void recordFailure() {

        lastFailureTime = System.currentTimeMillis()/1000;

        if(state == State.HALF_OPEN){
            state = State.OPEN;
            probeSent = false;
            return;
        }
        failureCount++;
        if(failureCount >= failureThreshold) {
            state = State.OPEN;
        }
    }
}
