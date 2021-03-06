package automail;

import exceptions.ItemTooHeavyException;
import simulation.Clock;

import java.util.Stack;


/**
 * A type of delivery attachment used for delivering food.
 */
public class FoodAttachment implements DeliveryAttachment {

    private final int capacity = 3;
    private final Stack<FoodItem> foodTube = new Stack<>();
    private int heatingStarted;
    private boolean isHeatingStarted = false;
    private static final int TIME_TO_HEAT = 5;


    /**
     * @param food Adds to the food tube
     * @throws ItemTooHeavyException If item exceeds max weight
     */
    public void addToFoodTube(FoodItem food) throws ItemTooHeavyException {

        if (food.weight > INDIVIDUAL_MAX_WEIGHT)
        {
            throw new ItemTooHeavyException();
        }



        foodTube.push(food);
    }



    /**
     * @return the number of food items loaded
     */
    public int foodItemsLoaded() {
        return foodTube.size();
    }


    /**
     * @param mailPool Gives the mail back to mail pool
     */
    public void empty(MailPool mailPool) {


        while (!foodTube.isEmpty()) {
            FoodItem food = foodTube.pop();
            mailPool.addToPool(food);
            System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), food.toString());

        }

    }

    /**
     * @return boolean to check if allowed to start delivering
     */
    @Override
    public boolean canStartDelivery() {

        if(!isHeatingStarted)   {

            startHeating();
        }

        return !foodTube.isEmpty() && Clock.Time() - heatingStarted >= TIME_TO_HEAT;

    }

    private void startHeating() {
        isHeatingStarted = true;
        heatingStarted = Clock.Time();
    }

    public void stopHeating() {
        isHeatingStarted = false;
    }

    /**
     * @return The next item to deliver
     */
    @Override
    public DeliveryItem deliverItem() {

        return foodTube.pop();
    }

    /**
     * @return True if the food tube is empty
     */
    @Override
    public boolean isEmpty() {
        return foodTube.size() == 0;
    }

    /**
     * @return Peeks the next item to deliver
     */
    @Override
    public DeliveryItem nextToDeliver() {
        return foodTube.peek();
    }

    /**
     * @param id The robot id
     * @return The composition of the robot id and the fullness of the food tube
     */
    @Override
    public String getTubeId(String id) {
        return String.format("%s(%1d)", id, foodTube.size());
    }

    /**
     * @return The max capacity of the food tube
     */
    @Override
    public int getCapacity() {
        return capacity;
    }
}
