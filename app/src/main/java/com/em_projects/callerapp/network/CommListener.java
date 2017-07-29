package com.em_projects.callerapp.network;

/**
 * Created by eyalmuchtar on 29/07/2017.
 */

public interface CommListener {
    public void newDataArrived(String response);

    public void exceptionThrown(Throwable throwable);
}
