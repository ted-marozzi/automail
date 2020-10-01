package automail;

public abstract class DeliveryAttachment {
    static public final int INDIVIDUAL_MAX_WEIGHT = 2000;
    private int capacity;

    public abstract void empty(MailPool mailPool);

    public abstract boolean canStartDelivery();

    public abstract DeliveryItem deliverItem();

    public abstract int getCapacity();

    public abstract boolean isEmpty();

    public abstract DeliveryItem nextToDeliver();

    public abstract String getTubeId(String id);
}
