package automail;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Keeps track of the stats of the robots.
 * Can be used for global robot stats or individual robots.
 * Extend statistic functionality by adding to this class.
 */
public class RobotStatistics {

    private int timesFoodTubeAttached = 0;
    private List<DeliveryItem> items_delivered = new ArrayList<>();

    /**
     * @param deliveryItem Add an item to list of items delivered
     */
    public void itemDelivered(DeliveryItem deliveryItem)    {
        items_delivered.add(deliveryItem);
    }

    /**
     * @return Get the number of items delivered
     */
    public List<DeliveryItem> getItemsDelivered() {
        return items_delivered;
    }

    /**
     * @return Return the number of times a food tube is attached.
     */
    public int getTimesFoodTubeAttached() {
        return timesFoodTubeAttached;
    }

    /**
     * Increment the counter on times food tube attached
     */
    public void foodTubeAttachedCount() {
        timesFoodTubeAttached++;
    }



    /**
     * @return Total mail items delivered
     */
    public List<DeliveryItem> getMailItemsDelivered()   {
        /* Get the mail delivered */
        return items_delivered.stream()
                .filter(deliveryItem -> deliveryItem instanceof MailItem)
                .collect(Collectors.toList());

    }


    /**
     * @return Total food items delivered
     */
    public List<DeliveryItem> getFoodItemsDelivered()   {
        /* Get the mail delivered */
        return items_delivered.stream()
                .filter(deliveryItem -> deliveryItem instanceof FoodItem)
                .collect(Collectors.toList());

    }

    /**
     * @return Total weight of the items delivered
     */
    public double getTotalWeight()  {
        return items_delivered.stream().mapToDouble(mail -> mail.getWeight()).sum();
    }

    /**
     * @return Total weight of the food
     */
    public double getTotalFoodWeight()  {
        return getFoodItemsDelivered().stream().mapToDouble(mail -> mail.getWeight()).sum();
    }

    /**
     * @return Total weight of the mail items
     */
    public double getTotalMailWeight()  {
        return getMailItemsDelivered().stream().mapToDouble(mail -> mail.getWeight()).sum();
    }

    /**
     * @return Number of mail items delivered
     */
    public int getNumberMailDelivered() {
        return getMailItemsDelivered().size();
    }


    /**
     * @return Number of food items delivered
     */
    public int getNumberFoodDelivered() {
        return getFoodItemsDelivered().size();
    }

    /**
     * @return Get total items delivered
     */
    public int getTotalItemsDelivered()  {
        return items_delivered.size();
    }
}