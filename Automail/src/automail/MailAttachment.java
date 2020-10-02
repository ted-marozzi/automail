package automail;

import exceptions.ItemTooHeavyException;
import simulation.Clock;

/**
 * Represents a Mail attachment to the robot to deliver regular mail
 */
public class MailAttachment implements DeliveryAttachment {

    private MailItem deliveryItem = null;
    private MailItem tube = null;
    private final int capacity = 2;

    /**
     * @param mailPool Gives the mail back to mail pool
     */
    @Override
    public void empty(MailPool mailPool) {
        if (tube != null) {
            mailPool.addToPool(tube);
            System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), tube.toString());
            tube = null;
        }
    }

    /**
     * @return if the attachments are empty
     */
    public boolean isEmpty() {
        return (deliveryItem == null && tube == null);
    }

    /**
     * @return checks conditions to be able to start delivery
     */
    @Override
    public boolean canStartDelivery() {

        return !isEmpty();

    }

    /**
     * @return Returns the delivery item to be delivered.
     */
    @Override
    public DeliveryItem deliverItem() {
        DeliveryItem tmp = deliveryItem;
        deliveryItem = null;

        if (tube != null) {
            deliveryItem = tube;
            tube = null;
        }

        return tmp;
    }


    /**
     * @return Peeks the next item to deliver
     */
    @Override
    public DeliveryItem nextToDeliver() {
        return deliveryItem;
    }

    /**
     * @param mailItem Item to be added to hand
     * @throws ItemTooHeavyException If item exceeds max weight
     */
    public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
        assert (deliveryItem == null);
        deliveryItem = mailItem;

        if (deliveryItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
    }

    /**
     * @param mailItem Item to be added to tube
     * @throws ItemTooHeavyException If item exceeds max weight
     */
    public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
        assert (tube == null);
        tube = mailItem;
        if (tube.weight > INDIVIDUAL_MAX_WEIGHT)
        {
            throw new ItemTooHeavyException();
        }
    }

    /**
     * @param id The robot id
     * @return The robot id and fullness of tube together
     */
    @Override
    public String getTubeId(String id) {
        return String.format("%s(%1d)", id, (tube == null ? 0 : 1));
    }

    /**
     * @return The item in the tube
     */
    public MailItem getTube() {
        return tube;
    }

    /**
     * @return The capacity of the attachments
     */
    @Override
    public int getCapacity() {
        return capacity;
    }
}
