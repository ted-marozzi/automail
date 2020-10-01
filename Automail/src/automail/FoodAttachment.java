package automail;

import exceptions.ItemTooHeavyException;
import simulation.Clock;

import java.util.Stack;



public class FoodAttachment extends DeliveryAttachment {

    private int capacity = 3;
    private Stack<FoodItem> foodTube = new Stack<>();
    private int heatingStarted;



    public void addToFoodTube(FoodItem food) throws ItemTooHeavyException {

        if (food.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();




        heatingStarted = Clock.Time();

        foodTube.push(food);
    }


    public int foodItemsLoaded()    {
        return foodTube.size();
    }


    public void empty(MailPool mailPool) {



        while(!foodTube.isEmpty()) {
            FoodItem food = foodTube.pop();
            mailPool.addToPool(food);
            System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), food.toString());

        }

    }

    @Override
    public boolean canStartDelivery() {
        return !foodTube.isEmpty() && Clock.Time() - heatingStarted >= 5;

    }

    @Override
    public DeliveryItem deliverItem() {
        FloorManager.getInstance().getLockedFloors().get(nextToDeliver().destinationFloor-1).poll();

        return foodTube.pop();

    }

    @Override
    public boolean isEmpty() {
        return foodTube.size() == 0 ? true : false;
    }

    @Override
    public DeliveryItem nextToDeliver() {
        return foodTube.peek();
    }

    @Override
    public String getTubeId(String id) {
        return String.format("%s(%1d)", id, foodTube.size());
    }

    @Override
    public int getCapacity() {
        return capacity;
    }
}
