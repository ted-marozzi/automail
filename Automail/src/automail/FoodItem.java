package automail;

// import java.util.UUID;

/**
 * Represents a mail item
 */
public class FoodItem extends DeliveryItem {
	

    /**
     * Constructor for a MailItem
     * @param dest_floor the destination floor intended for this mail item
     * @param arrival_time the time that the mail arrived
     * @param weight the weight of this mail item
     */
    public FoodItem(int dest_floor, int arrival_time, int weight){
        super(dest_floor, arrival_time, weight);
    }

    @Override
    public String toString() {
        return String.format("Food Item:: ID: %6s | Arrival: %4d | Destination: %2d | Weight: %4d", id, arrivalTime, destinationFloor, weight);
    }



}
