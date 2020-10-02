package automail;

import java.util.TreeMap;
import java.util.Map;

/**
 * This is the abstract version of the delivery item, mail and food items inherits from here
 */
public abstract class DeliveryItem {


    /**
     * @param destFloor   The floor the delivery needs to be delivered too
     * @param arrivalTime The time the delivery item appears in the mail pool
     * @param weight      The weight of the item to be delivered
     */
    public DeliveryItem(int destFloor, int arrivalTime, int weight) {
        this.destinationFloor = destFloor;
        this.id = String.valueOf(hashCode());
        this.arrivalTime = arrivalTime;
        this.weight = weight;
    }


    /**
     * Represents the destination floor to which the delivery is intended to go
     */
    protected final int destinationFloor;
    /**
     * The delivery identifier
     */
    protected final String id;
    /**
     * The time the delivery item arrived
     */
    protected final int arrivalTime;
    /**
     * The weight in grams of the delivery item
     */
    protected final int weight;


    /**
     * @return the destination floor of the delivery item
     */
    public int getDestFloor() {
        return destinationFloor;
    }

    /**
     * @return the ID of the delivery item
     */
    public String getId() {
        return id;
    }

    /**
     * @return the arrival time of the delivery item
     */
    public int getArrivalTime() {
        return arrivalTime;
    }

    /**
     * @return the weight of the delivery item
     */
    public int getWeight() {
        return weight;
    }

    private static int count = 0;
    private static Map<Integer, Integer> hashMap = new TreeMap<>();


    @Override
    public int hashCode() {
        Integer hash0 = super.hashCode();
        Integer hash = hashMap.get(hash0);
        if (hash == null) {
            hash = count++;
            hashMap.put(hash0, hash);
        }
        return hash;
    }

}
