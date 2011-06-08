package fr.loria.score;

import fr.loria.score.jupiter.model.Message;
import fr.loria.score.server.ServerJupiterAlg;

/**
 * @author: Bogdan Flueras (email: Bogdan.Flueras@inria.fr)
 */
public class MockServerJupiterAlg extends ServerJupiterAlg {
    public MockServerJupiterAlg(String initialData, int siteId) {
        super(initialData, siteId);
    }

    @Override
    protected void execute(Message m) {
        //do nothing
    }
}
