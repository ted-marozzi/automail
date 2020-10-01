package automail;

public interface DeliveryAttachment {
    int INDIVIDUAL_MAX_WEIGHT = 2000;


    void empty(MailPool mailPool);

    boolean canStartDelivery();

    DeliveryItem deliverItem();

    int getCapacity();

    boolean isEmpty();

    DeliveryItem nextToDeliver();

    String getTubeId(String id);
}
