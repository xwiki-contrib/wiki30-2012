package fr.loria.score.client;

/**
 * @author: Bogdan.Flueras@inria.fr
 */
public interface ClientCallback {
    void onConnected();
    void onDisconnected();
}
