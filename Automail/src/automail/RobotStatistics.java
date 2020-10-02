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

    private static int timesFoodTubeAttached = 0;
    private static List<DeliveryItem> ITEMS_DELIVERED = new ArrayList<>();

    /**
     * @param deliveryItem Add an item to list of items delivered
     */
    public static void itemDelivered(DeliveryItem deliveryItem)    {
        ITEMS_DELIVERED.add(deliveryItem);
    }

    /**
     * @return Get the number of items delivered
     */
    public static List<DeliveryItem> getItemsDelivered() {
        return ITEMS_DELIVERED;
    }

    /**
     * @return Return the number of times a food tube is attached.
     */
    public static int getTimesFoodTubeAttached() {
        return timesFoodTubeAttached;
    }

    /**
     * Incrememnt the counter on times food tube attached
     */
    public static void foodTubeAttachedCount() {
        timesFoodTubeAttached++;
    }

    /**
     * @return Total mail items delivered
     */
    public static List<DeliveryItem> getMailItemsDelivered()   {
        /* Get the mail delivered */
        return ITEMS_DELIVERED.stream()
                .filter(deliveryItem -> deliveryItem instanceof MailItem)
                .collect(Collectors.toList());

    }


    /**
     * @return Total food items delivered
     */
    public static List<DeliveryItem> getFoodItemsDelivered()   {
        /* Get the mail delivered */
        return ITEMS_DELIVERED.stream()
                .filter(deliveryItem -> deliveryItem instanceof FoodItem)
                .collect(Collectors.toList());

    }

    /**
     * @return Total weight of the items delivered
     */
    public double getTotalWeight()  {
        return ITEMS_DELIVERED.stream().mapToDouble(mail -> mail.getWeight()).sum();
    }

    /**
     * @return Total weight of the food
     */
    public static double getTotalFoodWeight()  {
        return getFoodItemsDelivered().stream().mapToDouble(mail -> mail.getWeight()).sum();
    }

    /**
     * @return Total weight of the mail items
     */
    public static double getTotalMailWeight()  {
        return getMailItemsDelivered().stream().mapToDouble(mail -> mail.getWeight()).sum();
    }

    public static int getNumberMailDelivered() {
        return getMailItemsDelivered().size();
    }


    public static int getNumberFoodDelivered() {
        return getFoodItemsDelivered().size();
    }

    public static int getTotalItemsDelivered()  {
        return ITEMS_DELIVERED.size();
    }
}