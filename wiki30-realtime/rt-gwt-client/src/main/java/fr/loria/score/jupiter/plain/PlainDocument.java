package fr.loria.score.jupiter.plain;

import fr.loria.score.jupiter.model.AbstractOperation;
import fr.loria.score.jupiter.model.Document;
import fr.loria.score.jupiter.plain.operation.DeleteOperation;
import fr.loria.score.jupiter.plain.operation.InsertOperation;

import java.util.logging.Logger;

/**
 * Plain text document. A simple wrapper for a String
 * @author Bogdan.Flueras@inria.fr
 */
public class PlainDocument implements Document {
    public transient static final String POSITION_GREATER_THAN_DATA_LENGTH =
            "Position: {0} is greater than document length: {1} ";
    private transient static final Logger logger = Logger.getLogger(PlainDocument.class.getName());

    private String content;

    public PlainDocument() {
    }

    public PlainDocument(String content) {
        this.content = content;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void apply(AbstractOperation op) {
        logger.fine("\tExecuting " + op + " on content = " + content);

        int length = content.length();
        int position = op.getPosition();
        StringBuilder sb = new StringBuilder(content);

        //won't delegate to operation because the code is almost the same ;)
        if (op instanceof InsertOperation) {
            if (position > length) {
                handleError(position, length);
            }

            sb = new StringBuilder(content);
            sb.insert(position, ((InsertOperation) op).getChr());
        } else if (op instanceof DeleteOperation) {
            if (position >= length) {
               handleError(position, length);
            }
            sb.deleteCharAt(position);
        }
        content = sb.toString();
    }

    @Override
    public Document deepCloneDocument() {
        return new PlainDocument(content);
    }

    @Override
    public String toString() {
        return getContent();
    }

    /**
     * Custom formatter for message error
     */
    private void handleError(int position, int length) {
        String errMsg = POSITION_GREATER_THAN_DATA_LENGTH.
                        replace("{0}", String.valueOf(position)).
                        replace("{1}", String.valueOf(length));
        logger.severe(errMsg);
        throw new IllegalArgumentException(errMsg);
    }
}
