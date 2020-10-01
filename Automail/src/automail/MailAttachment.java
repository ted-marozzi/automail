package automail;

import exceptions.ItemTooHeavyException;
import simulation.Clock;

public class  MailAttachment extends DeliveryAttachment {

    private MailItem deliveryItem = null;
    private MailItem tube = null;
    private int capacity = 2;

    @Override
    public void empty(MailPool mailPool) {
        if(tube != null)    {
            mailPool.addToPool(tube);
            System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), tube.toString());
            tube = null;


        }


    }

    public boolean isEmpty() {
        return (deliveryItem == null && tube == null);
    }

    @Override
    public boolean canStartDelivery() {

        return !isEmpty();

    }

    @Override
    public DeliveryItem deliverItem() {
        DeliveryItem tmp = deliveryItem;
        deliveryItem = null;

        if(tube != null)    {
            deliveryItem = tube;
            tube = null;
        }

        return tmp;
    }



    @Override
    public DeliveryItem nextToDeliver() {
        return deliveryItem;
    }

    public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
        assert(deliveryItem == null);
        deliveryItem = mailItem;

        if (deliveryItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
    }

    public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
        assert(tube == null);
        tube = mailItem;
        if (tube.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
    }

    @Override
    public String getTubeId(String id) {
        return String.format("%s(%1d)", id, (tube == null ? 0 : 1));
    }

    public MailItem getTube() {
        return tube;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
