package fr.loria.score;

import fr.loria.score.jupiter.JupiterAlg;
import fr.loria.score.jupiter.model.Message;
import fr.loria.score.jupiter.plain.PlainDocument;

/**
 * @author: Bogdan.Flueras@inria.fr
 */
public class MockClientJupiter extends JupiterAlg {

    public MockClientJupiter(int siteId) {
        super(siteId, new PlainDocument(""));
    }

    @Override
    protected void execute(Message receivedMsg) {
    }

    @Override
    protected void send(Message m) {
    }
}
