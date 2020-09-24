package automail;

import java.util.TreeMap;
import java.util.Map;

public abstract class DeliveryItem {
    

    public DeliveryItem(int destFloor, int arrivalTime, int weight){
        this.destinationFloor = destFloor;
        this.id = String.valueOf(hashCode());
        this.arrivalTime = arrivalTime;
        this.weight = weight;
    }


    /** Represents the destination floor to which the mail is intended to go */
    protected final int destinationFloor;
    /** The mail identifier */
    protected final String id;
    /** The time the mail item arrived */
    protected final int arrivalTime;
    /** The weight in grams of the mail item */
    protected final int weight;
    
    /**
     *
     * @return the destination floor of the mail item
     */
    public int getDestFloor() {
        return destinationFloor;
    }
    
    /**
     *
     * @return the ID of the mail item
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @return the arrival time of the mail item
     */
    public int getArrivalTime(){
        return arrivalTime;
    }

    /**
    *
    * @return the weight of the mail item
    */
   public int getWeight(){
       return weight;
   }

   private static int count = 0;
   private static Map<Integer, Integer> hashMap = new TreeMap<Integer, Integer>();

   @Override
   public int hashCode() {
       Integer hash0 = super.hashCode();
       Integer hash = hashMap.get(hash0);
       if (hash == null) { hash = count++; hashMap.put(hash0, hash); }
       return hash;
   }
   
}
