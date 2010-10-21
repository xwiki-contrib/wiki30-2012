package fr.loria.score.jupiter.model;

import java.io.Serializable;

/**
 * The generic operation that has to be executed on the server or client side.
 * Currently it is only an insert/delete character
 */
public abstract class Operation implements Serializable {
    public static final String THE_POSITION_IS_NEGATIVE = "The position is negative: ";

    //the position (0 indexed) at which the operation should be applied on the data
    protected int position;

    //the id of every remote site that performs some operations
    protected int siteId;


    public Operation(int position) {
        if (position < 0) {
            throw new IllegalArgumentException(THE_POSITION_IS_NEGATIVE + position);
        }
        this.position = position;
    }

    public Operation(int position, int siteId) {
        this(position);
        this.siteId = siteId;
    }

    //todo remove copy ctor

    public Operation(Operation o) {
        this(o.getPosition(), o.getSiteId());
    }

    public Operation() {
    }

    public int getPosition() {
        return position;
    }

    public int getSiteId() {
        return siteId;
    }

    /**
     * Actually performs the execution of this operation
     *
     * @param data the data for this operation should be applied on
     * @return the modified data, after the operation was executed
     */
    public abstract String execute(String data);
}
