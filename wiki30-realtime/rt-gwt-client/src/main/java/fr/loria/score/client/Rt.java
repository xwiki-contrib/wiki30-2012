package fr.loria.score.client;

import com.google.gwt.core.client.EntryPoint;

//TODO what happens if session expires?
//TODO Remove unused client server pairs

//TODO multiple carets
//TODO use log API instead of sout

/**
 * Expose the "real-time API"
 */
public class Rt implements EntryPoint {

    public void onModuleLoad() {
        RtApi.publish();
    }
}
