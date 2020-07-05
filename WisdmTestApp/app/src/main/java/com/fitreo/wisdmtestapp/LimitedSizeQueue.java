package com.fitreo.wisdmtestapp;

import java.util.ArrayList;

/**
 * Created by vanya on 24-Apr-18.
 *
 *  A queue with limited size, when elements are added beyond its size, older elements are removed to make way
 */

public class LimitedSizeQueue<K> extends ArrayList<K> {

    private int maxSize;
    public boolean isQueueFilled = false;

    public LimitedSizeQueue(int size){
        this.maxSize = size;
    }

    public boolean add(K k){
        //adds an element to the array superclass and removes older elements if over the queue size limit
        boolean r = super.add(k);
        if (size() > maxSize){
            removeRange(0, size() - maxSize - 1);
            isQueueFilled = true;
        }
        return r;
    }

    public boolean isQueueFilled(){
        return isQueueFilled;
    }

    public K getYongest() {
        return get(size() - 1);
    }

    public K getOldest() {
        return get(0);
    }
}
