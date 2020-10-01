package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Building;
import simulation.Clock;
import simulation.IMailDelivery;

import java.util.*;

/**
 * The robot delivers mail!
 */
public class Robot {

    private boolean isMailMode = true;

    public boolean isMailMode() {
        return isMailMode;
    }

    static public final int INDIVIDUAL_MAX_WEIGHT = 2000;

    IMailDelivery delivery;
    protected final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private MailPool mailPool;
    private boolean receivedDispatch;
    
    private DeliveryItem deliveryItem = null;
    private MailItem tube = null;
    
    private int deliveryCounter;

    private int heatingStarted;

    private Stack<FoodItem> foodTube = new Stack<>();

    private static int timesFoodTubeAttached = 0;



    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number governs selection of mail items for delivery and behaviour on priority arrivals
     */
    public Robot(IMailDelivery delivery, MailPool mailPool, int number){
    	this.id = "R" + number;
        // current_state = RobotState.WAITING;
    	current_state = RobotState.RETURNING;
        current_floor = Building.MAILROOM_LOCATION;
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
    }
    
    /**
     * This is called when a robot is assigned the mail items and ready to dispatch for the delivery 
     */
    public void dispatch() {
    	receivedDispatch = true;
    }



    /**
     * This is called on every time step
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void operate() throws ExcessiveDeliveryException {   
    	switch(current_state) {
    		/** This state is triggered when the robot is returning to the mailroom after a delivery */
    		case RETURNING:
    			/** If its current position is at the mailroom, then the robot should change state */
                if(current_floor == Building.MAILROOM_LOCATION){
                    if(isMailMode) {

                        if (tube != null) {
                            mailPool.addToPool(tube);
                            System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), tube.toString());
                            tube = null;
                            deliveryCounter = 0;
                        }

                    } else if (!isMailMode)    {
                        isMailMode = true;

                        while(!foodTube.isEmpty()) {
                            FoodItem food = foodTube.pop();
                            mailPool.addToPool(food);
                            System.out.printf("T: %3d > old addToPool [%s]%n", Clock.Time(), food.toString());
                            deliveryCounter = 0;
                        }
                    }
                    /** Tell the sorter the robot is ready */
                    mailPool.registerWaiting(this);
                    changeState(RobotState.WAITING);
                } else {
                	/** If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(Building.MAILROOM_LOCATION);
                	break;
                }
    		case WAITING:
                /** If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if(isMailMode)    {
                    if(!isEmpty() && receivedDispatch){
                        receivedDispatch = false;
                        deliveryCounter = 0; // reset delivery counter
                        setDestination();
                        changeState(RobotState.DELIVERING);
                    }
                    break;

                } else if (!isMailMode)   {
                    if(!foodTube.isEmpty() && receivedDispatch && Clock.Time() - heatingStarted >= 5){
                        receivedDispatch = false;
                        deliveryCounter = 0; // reset delivery counter
                        setDestination();

                        changeState(RobotState.DELIVERING);
                    }
                    break;
                }

    		case DELIVERING:
    			if(current_floor == destination_floor && (FloorManager.getInstance().getLockedFloors().get(current_floor-1).peek() == this.id || FloorManager.getInstance().getLockedFloors().get(current_floor-1).peek() == null)) { // If already here drop off either way
                    /** Delivery complete, report this to the simulator! */

                    if(isMailMode)   {
                        delivery.deliver(deliveryItem);
                        deliveryItem = null;
                        /** Check if want to return, i.e. if there is no item in the tube*/
                        if(tube == null){

                            changeState(RobotState.RETURNING);
                        }
                        else{
                            /** If there is another item, set the robot's route to the location to deliver the item */
                            deliveryItem = tube;
                            tube = null;
                            setDestination();
                            changeState(RobotState.DELIVERING);
                        }

                        deliveryCounter++;
                        if(deliveryCounter > 2){  // Implies a simulation bug
                            throw new ExcessiveDeliveryException();
                        }


                    } else if (!isMailMode)   {
                        delivery.deliver(foodTube.pop());
                        FloorManager.getInstance().getLockedFloors().get(destination_floor-1).remove();
                        if(foodItemsLoaded() == 0)  {
                            isMailMode = true;
                            changeState(RobotState.RETURNING);
                        } else  {
                            setDestination();
                            changeState(RobotState.DELIVERING);
                        }

                        deliveryCounter++;
                        if(deliveryCounter > 3){  // Implies a simulation bug
                            throw new ExcessiveDeliveryException();
                        }

                    }

    			} else {
	        		/** The robot is not at the destination yet, move towards it! */
	                moveTowards(destination_floor);
    			}
                break;
    	}
    }



    /**
     * Sets the route for the robot
     */
    private void setDestination() {

        if(isMailMode)    {
            /** Set the destination floor */
            destination_floor = deliveryItem.getDestFloor();

        } else if (!isMailMode)   {
            destination_floor = foodTube.peek().getDestFloor();
            FloorManager.getInstance().lockFloor(destination_floor, this.id);


        }

    }

    /**
     * Generic function that moves the robot towards the destination
     * @param destination the floor towards which the robot is moving
     */
    private void moveTowards(int destination) {
        if(current_floor < destination){
            current_floor++;
        } else if(current_floor > destination) {
            current_floor--;
        }
    }
    
    private String getIdTube() {
    	return String.format("%s(%1d)", this.id, (tube == null ? 0 : 1));
    }

    private String getIdFoodTube() {
        return String.format("%s(%1d)", this.id, foodTube.size());
    }
    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
        if(this.isMailMode)
        {
            assert(!(deliveryItem == null && tube != null));
            if (current_state != nextState) {
                System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdTube(), current_state, nextState);
            }
            current_state = nextState;
            if(nextState == RobotState.DELIVERING){
                System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdTube(), deliveryItem.toString());
            }

        } else if(!this.isMailMode)   {

            if (current_state != nextState) {
                System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), getIdFoodTube(), current_state, nextState);
            }
            current_state = nextState;
            if(nextState == RobotState.DELIVERING){
                System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), getIdFoodTube(), foodTube.peek().toString());
            }

        }

    }

	public MailItem getTube() {
		return tube;
	}


	public boolean istubeEmpty()  {
        return tube == null ? true : false;
    }


	public boolean isEmpty() {
		return (deliveryItem == null && tube == null);
	}

	public void addToHand(MailItem mailItem) throws ItemTooHeavyException {
		assert(deliveryItem == null);
		deliveryItem = mailItem;
		if (deliveryItem.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}

	public void addToTube(MailItem mailItem) throws ItemTooHeavyException {
		assert(tube == null);
		tube = mailItem;
		if (tube.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();
	}


    private static void foodTubeAttachedCount()   {
        timesFoodTubeAttached++;
    }


    private void addToFoodTube(FoodItem food) throws ItemTooHeavyException {
        if (food.weight > INDIVIDUAL_MAX_WEIGHT) throw new ItemTooHeavyException();




        if(isMailMode == true)    {
            foodTubeAttachedCount();
            this.isMailMode = false;
        }





        heatingStarted = Clock.Time();

        foodTube.push(food);
    }

    public static int getTimesFoodTubeAttached() {
        return timesFoodTubeAttached;
    }



    public int foodItemsLoaded()    {
        return foodTube.size();
    }



    public boolean inspectDeliveryItem(DeliveryItem item) throws ItemTooHeavyException {






        if (item.getItemType().equals("Food")) {


            addToFoodTube((FoodItem) item);
            j.remove();

            while (j.hasNext() && robot.foodItemsLoaded() < 3) {
                DeliveryItem item2 = j.next().deliveryItem;
                if (item2.getItemType().equals("Food")) {
                    robot.addToFoodTube((FoodItem) item2);
                    j.remove();
                }
            }

            robot.dispatch(); // send the robot off if it has any items to
            // deliver
            i.remove();



        } else if (item.getItemType().equals("Mail")) {
            assert (robot.isEmpty());
            robot.addToHand((MailItem) item);
            j.remove();

            while (j.hasNext() && robot.istubeEmpty()) {
                DeliveryItem item2 = j.next().deliveryItem;
                if (item2.getItemType().equals("Mail")) {
                    robot.addToTube((MailItem)j.previous().deliveryItem);
                    j.remove();
                }
            }
            robot.dispatch(); // send the robot off if it has any items to deliver
            i.remove();
        }

    }

}
