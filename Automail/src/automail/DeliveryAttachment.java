package automail;

import simulation.Simulation;

/**
 * The interface for Robot delivery attachments to carry different items
 */
public interface DeliveryAttachment {
    int INDIVIDUAL_MAX_WEIGHT = 2000;


    /**
     * Empty the attachment
     *
     * @param mailPool Gives the mail back to mail pool
     */
    void empty(MailPool mailPool);

    /**
     * @return true if a delivery can be started
     */
    boolean canStartDelivery();

    /**
     * @return the item to be delivered
     */
    DeliveryItem deliverItem();

    /**
     * @return the capacity of the attachment
     */
    int getCapacity();

    /**
     * @return true if the attachment is empty
     */
    boolean isEmpty();

    /**
     * @return peeks the next item to deliver without removing it
     */
    DeliveryItem nextToDeliver();

    /**
     * @param id The robot id
     * @return the composition of robot id and tube fullness
     */
    String getTubeId(String id);
}
