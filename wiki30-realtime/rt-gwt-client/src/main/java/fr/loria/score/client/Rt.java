package fr.loria.score.client;

import com.google.gwt.core.client.EntryPoint;

/**
 * Expose the "real-time API"
 */
public class Rt implements EntryPoint {

    public void onModuleLoad() {
        RtApi.publish();
    }
}
