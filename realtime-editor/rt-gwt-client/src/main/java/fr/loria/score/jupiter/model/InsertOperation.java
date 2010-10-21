package fr.loria.score.jupiter.model;

/**
 * Inserts a character at a given position
 */
public class InsertOperation extends Operation {
    public static final String POSITION_GREATER_THAN_DATA_LENGTH = "Position is greater than data length: ";

    // the character to be inserted
    private char character;

    public InsertOperation(int position, char character) {
        super(position);
        this.character = character;
    }

    public InsertOperation(int position, char character, int siteId) {
        super(position, siteId);
        this.character = character;
    }

    public InsertOperation() {
    }

    /**
     * {@inheritDoc}
     */
    public String execute(String data) {
        System.out.println("\tExecuting " + this + " on data = " + data);
        int length = data.length();

        if (position > length) {
            throw new IllegalArgumentException(POSITION_GREATER_THAN_DATA_LENGTH + length);
        }

        StringBuilder sb = new StringBuilder(length + 1);
        if (length == 0) {
            sb = sb.append(character);
        } else if (position == 0) {
            sb = sb.append(character).append(data);
        } else if (position == length) {
            sb = sb.append(data).append(character);
        } else {
            sb = sb.append(data.substring(0, position)).append(character).append(data.substring(position, length));
        }
        return sb.toString();
    }

    public char getCharacter() {
        return character;
    }

    @Override
    public String toString() {
        return "InsertOperation(" + position + ", " + character + ")";
    }
}
