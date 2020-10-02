package exceptions;

/**
 * An exception thrown when the robot tries to deliver more items than its tube capacity without refilling.
 */
public class ExcessiveDeliveryException extends Throwable {

	private static final long serialVersionUID = 1L;

	public ExcessiveDeliveryException() {
		super("Attempting to deliver to many items in a single trip!");
	}
}
