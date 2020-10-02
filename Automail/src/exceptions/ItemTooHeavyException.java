package exceptions;

/**
 * This exception is thrown when a robot takes a MailItem from its StorageTube which is too heavy for that robot
 */
public class ItemTooHeavyException extends Exception {

    private static final long serialVersionUID = 1L;

    public ItemTooHeavyException() {
        super("Item too heavy! Dropped by robot.");
    }
}
