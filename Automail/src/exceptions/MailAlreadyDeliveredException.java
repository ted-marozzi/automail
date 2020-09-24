package exceptions;

/**
 * An exception thrown when a mail that is already delivered attempts to be delivered again.
 */
public class MailAlreadyDeliveredException extends Throwable    {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MailAlreadyDeliveredException() {
        super("This mail has already been delivered!");
    }
}
