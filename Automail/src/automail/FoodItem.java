package automail;

/**
 * Represents a food item
 */
public class FoodItem extends DeliveryItem {


    /**
     * Constructor for a FoodItem
     *
     * @param dest_floor   the destination floor intended for this food item
     * @param arrival_time the time that the food arrived
     * @param weight       the weight of this food item
     */
    public FoodItem(int dest_floor, int arrival_time, int weight) {
        super(dest_floor, arrival_time, weight);
    }

    /**
     * @return A string log
     */
    @Override
    public String toString() {
        return String.format("Food Item:: ID: %6s | Arrival: %4d | Destination: %2d | Weight: %4d", id, arrivalTime, destinationFloor, weight);
    }


}
