package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Building;
import simulation.Clock;
import simulation.IMailDelivery;


/**
 * The robot delivers mail!
 */
public class Robot {

    private boolean isMailMode = true;
    IMailDelivery delivery;
    protected final String id;
    /** Possible states the robot can be in */
    public enum RobotState { DELIVERING, WAITING, RETURNING }
    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private MailPool mailPool;
    public boolean isReceivedDispatch() {
        return receivedDispatch;
    }
    private boolean receivedDispatch;
    private int deliveryCounter;
    private DeliveryAttachment currentDeliveryAttachment = null;
    private FoodAttachment foodAttachment;
    private MailAttachment mailAttachment;


    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     * @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number governs selection of mail items for delivery and behaviour on priority arrivals
     */
    public Robot(IMailDelivery delivery, MailPool mailPool, int number){
    	this.id = "R" + number;

    	current_state = RobotState.RETURNING;
        current_floor = Building.MAILROOM_LOCATION;
        this.delivery = delivery;
        this.mailPool = mailPool;
        this.receivedDispatch = false;
        this.deliveryCounter = 0;
        foodAttachment = new FoodAttachment();
        mailAttachment = new MailAttachment();
        currentDeliveryAttachment = mailAttachment;
    }
    
    /**
     * This is called when a robot is assigned the mail items and ready to dispatch for the delivery 
     */
    public void dispatch() {
    	receivedDispatch = true;
    }


    public boolean isEmpty()   {
        return currentDeliveryAttachment.isEmpty();
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
                    currentDeliveryAttachment.empty(this.mailPool);
                    
                    deliveryCounter = 0;
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
                if(receivedDispatch && currentDeliveryAttachment.canStartDelivery())   {
                    receivedDispatch = false;
                    deliveryCounter = 0; // reset delivery counter
                    setDestination();
                    changeState(Robot.RobotState.DELIVERING);
                }
                break;

    		case DELIVERING:
    		    String floorLocked = FloorManager.getInstance().getLockedFloors().get(current_floor-1).peek();
    			if(current_floor == destination_floor && (this.id.equals(floorLocked) || floorLocked == null) ) { // If already here drop off either way
                    /** Delivery complete, report this to the simulator! */
                    delivery.deliver(currentDeliveryAttachment.deliverItem());

                    deliveryCounter++;
                    if(deliveryCounter > currentDeliveryAttachment.getCapacity()){  // Implies a simulation bug
                        throw new ExcessiveDeliveryException();
                    }

                    if(currentDeliveryAttachment.isEmpty()) {
                        changeState(RobotState.RETURNING);
                        currentDeliveryAttachment = mailAttachment;
                    }
                    else
                    {
                        setDestination();
                        changeState(RobotState.DELIVERING);
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

        destination_floor = currentDeliveryAttachment.nextToDeliver().destinationFloor;

        if(currentDeliveryAttachment.equals(foodAttachment))    {
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
    

    
    /**
     * Prints out the change in state
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState){
        if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), currentDeliveryAttachment.getTubeId(this.id), current_state, nextState);
        }
        current_state = nextState;
        if(nextState == RobotState.DELIVERING){
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), currentDeliveryAttachment.getTubeId(this.id), currentDeliveryAttachment.nextToDeliver().toString());
        }
    }




    public boolean inspectDeliveryItem(DeliveryItem item) throws ItemTooHeavyException {

        if (item instanceof FoodItem
                && foodAttachment.foodItemsLoaded() < foodAttachment.getCapacity()
                && mailAttachment.isEmpty()) {

            foodAttachment.addToFoodTube((FoodItem) item);

            if(currentDeliveryAttachment.equals(mailAttachment))    {
                RobotStatistics.foodTubeAttachedCount();
                this.currentDeliveryAttachment = foodAttachment;
            }

            if(foodAttachment.foodItemsLoaded() == foodAttachment.getCapacity())    {
                dispatch();
            }
            return true;

        } else if (item instanceof MailItem && foodAttachment.isEmpty()) {

            if(mailAttachment.nextToDeliver() == null)    {
                mailAttachment.addToHand((MailItem) item);
                currentDeliveryAttachment = mailAttachment;
                return true;
            } else if (mailAttachment.getTube() == null) {
                mailAttachment.addToTube((MailItem) item);
                dispatch();
                return true;
            }

        }
        return false;
    }



}
