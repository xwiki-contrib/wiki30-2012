package fr.loria.score.jupiter.model;

import fr.loria.score.client.Editor;

import java.io.Serializable;

/**
 * The operation that has to be executed on the server or client side.
 * Currently it is only an insert/delete character/string
 */
public abstract class Operation implements Serializable {
    public static final String THE_POSITION_IS_NEGATIVE = "The position is negative: ";
    public static final String POSITION_GREATER_THAN_DATA_LENGTH = "Position is greater than data length: ";
    
    //the position (0 indexed) at which the operation should be applied on the linear data model content
    protected int position;

    //the id of every remote site that performs some operations
    protected int siteId;

    protected Operation() {
    }
    
    private Operation(int position) {
        if (position < 0) {
            throw new IllegalArgumentException(THE_POSITION_IS_NEGATIVE + position);
        }
        this.position = position;
    }

    public Operation(int position, int siteId) {
        this(position);
        this.siteId = siteId;
    }

    public Operation(Operation o) {
        this(o.getPosition(), o.getSiteId());
    }

    public int getPosition() {
        return position;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    /**
     * Actually performs the execution of this operation
     *
     * @param data the data that this operation should be applied on
     * @return the modified data, after the operation was executed
     */
    public abstract String execute(String data); // FIXME: parameter and return value should be of same type

    /**
     * Updates the UI(the editor in fact) according to the semantics of the operation (<code>this</code>).<br/>
     * <strong>Nb:</strong>This could be embedded in <code>execute()</code> method, but I choose to follow the OC principle.
     */
    public abstract void updateUI(Editor editor);

    public String toString() {
        return " siteId: " + siteId;
    }
}
