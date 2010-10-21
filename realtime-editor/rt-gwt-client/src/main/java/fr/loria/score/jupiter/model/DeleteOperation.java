package fr.loria.score.jupiter.model;

/**
 * Deletes a character at the given position
 */
public class DeleteOperation extends Operation {
    public static final String POSITION_GREATER_THAN_DATA_LENGTH = "Position is greater than data length";

    public DeleteOperation(int position) {
        super(position);
    }

    public DeleteOperation(int position, int siteId) {
        super(position, siteId);
    }

    public DeleteOperation() {
    }

    /**
     * {@inheritDoc}
     */
    public String execute(String data) {
        System.out.println("\tExecuting " + this + " on data = " + data);
        int length = data.length();

        if (position >= length) {
            throw new IllegalArgumentException(POSITION_GREATER_THAN_DATA_LENGTH);
        }

        StringBuilder sb = new StringBuilder(length - 1);
        if (position == 0) {
            sb = sb.append(data.substring(1, length));
        } else if (position == length - 1) {
            sb = sb.append(data.substring(0, position));
        } else {
            sb = sb.append(data.substring(0, position)).append(data.substring(position + 1, length));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "DeleteOperation(" + position + ")";
    }
}
