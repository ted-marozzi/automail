package automail;

import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import simulation.Building;
import simulation.Clock;
import simulation.IMailDelivery;


/**
 * The robot delivers mail and food!
 */
public class Robot {

    IMailDelivery delivery;
    protected final String id;

    /**
     * Possible states the robot can be in
     */
    public enum RobotState {DELIVERING, WAITING, RETURNING}

    public RobotState current_state;
    private int current_floor;
    private int destination_floor;
    private MailPool mailPool;
    private boolean receivedDispatch;
    private int deliveryCounter;
    /* Current Delivery Attachment points to either food or mail attachment depending on which it is using at
     * any point in time */
    private DeliveryAttachment currentDeliveryAttachment;
    private final FoodAttachment foodAttachment;
    private final MailAttachment mailAttachment;
    private RobotStatistics stats;
    private FloorManager floorManager;



    /**
     * Initiates the robot's location at the start to be at the mailroom
     * also set it to be waiting for mail.
     *  @param delivery governs the final delivery
     * @param mailPool is the source of mail items
     * @param number   the robot number
     * @param floorManager
     */
    public Robot(IMailDelivery delivery, MailPool mailPool, int number, RobotStatistics stats, FloorManager floorManager) {
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
        this.stats = stats;
        this.floorManager = floorManager;

    }

    /**
     * This is called on every time step
     *
     * @throws ExcessiveDeliveryException if robot delivers more than the capacity of the tube without refilling
     */
    public void operate() throws ExcessiveDeliveryException {
        switch (current_state) {
            /* This state is triggered when the robot is returning to the mailroom after a delivery */
            case RETURNING:
                /* If its current position is at the mailroom, then the robot should change state */
                if (current_floor == Building.MAILROOM_LOCATION) {

                    /* If the attachment has left over items then empty it */
                    currentDeliveryAttachment.empty(this.mailPool);

                    deliveryCounter = 0;
                    /* Tell the sorter the robot is ready */
                    mailPool.registerWaiting(this);
                    changeState(RobotState.WAITING);

                } else {
                    /* If the robot is not at the mailroom floor yet, then move towards it! */
                    moveTowards(Building.MAILROOM_LOCATION);
                    break;
                }
            case WAITING:
                /* If the StorageTube is ready and the Robot is waiting in the mailroom then start the delivery */
                if (receivedDispatch && currentDeliveryAttachment.canStartDelivery()) {
                    receivedDispatch = false;
                    deliveryCounter = 0; // reset delivery counter


                    setDestination();
                    changeState(Robot.RobotState.DELIVERING);

                }
                break;

            case DELIVERING:
                /* Ensure that the floor isn't locked due to another robot attempting to deliver food */
                if (current_floor == destination_floor && floorManager.checkFloor(current_floor, this.id)) {
                    /* Delivery complete, report this to the simulator! */
                    DeliveryItem tmp = currentDeliveryAttachment.deliverItem();
                    delivery.deliver(tmp);
                    stats.itemDelivered(tmp);
                    /* If succesful delivery unlock floor */
                    floorManager.releaseFloor(this.destination_floor);

                    deliveryCounter++;
                    /* Check the at robot hasn't delivered more than its capacity */
                    if (deliveryCounter > currentDeliveryAttachment.getCapacity()) {  // Implies a simulation bug
                        throw new ExcessiveDeliveryException();
                    }
                    /* If after the delivery the robot is empty then change status and return to default attachment */
                    if (currentDeliveryAttachment.isEmpty()) {
                        changeState(RobotState.RETURNING);
                        foodAttachment.stopHeating();
                        currentDeliveryAttachment = mailAttachment;
                    } else {
                        /* Else begin delivering */
                        setDestination();
                        changeState(RobotState.DELIVERING);
                    }

                } else {
                    /* The robot is not at the destination yet, move towards it! */
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

        if (currentDeliveryAttachment.equals(foodAttachment)) {
            floorManager.lockFloor(destination_floor, this.id);
        }

    }

    /**
     * Generic function that moves the robot towards the destination
     *
     * @param destination the floor towards which the robot is moving
     */
    private void moveTowards(int destination) {
        if (current_floor < destination) {
            current_floor++;
        } else if (current_floor > destination) {
            current_floor--;
        }
    }


    /**
     * Prints out the change in state
     *
     * @param nextState the state to which the robot is transitioning
     */
    private void changeState(RobotState nextState) {
        if (current_state != nextState) {
            System.out.printf("T: %3d > %7s changed from %s to %s%n", Clock.Time(), currentDeliveryAttachment.getTubeId(this.id), current_state, nextState);
        }
        current_state = nextState;
        if (nextState == RobotState.DELIVERING) {
            System.out.printf("T: %3d > %7s-> [%s]%n", Clock.Time(), currentDeliveryAttachment.getTubeId(this.id), currentDeliveryAttachment.nextToDeliver().toString());
        }
    }


    /**
     * @param item item to be inspected
     * @return A boolean that is true if the item is accepted by the robot.
     * @throws ItemTooHeavyException if item is too heavy to carry.
     */
    public boolean inspectDeliveryItem(DeliveryItem item) throws ItemTooHeavyException {
        /* If item is food, and the tube isn't full and the robot isn't a mail robot.*/
        if (item instanceof FoodItem
                && foodAttachment.foodItemsLoaded() < foodAttachment.getCapacity()
                && mailAttachment.isEmpty()) {
            /* Add to food tube */
            foodAttachment.addToFoodTube((FoodItem) item);
            /* If the current attachment is a mail Attachment attach food attachment */
            if (currentDeliveryAttachment.equals(mailAttachment)) {
                /* Record statistics */
                stats.foodTubeAttachedCount();
                this.currentDeliveryAttachment = foodAttachment;
            }
            /* If full then dispatch */
            if (foodAttachment.foodItemsLoaded() == foodAttachment.getCapacity()) {
                dispatch();
            }
            return true;
            /* If item is a mail item and the robot is not a food robot then attach mail */
        } else if (item instanceof MailItem && foodAttachment.isEmpty()) {
            /* If hand is empty then fill hand */
            if (mailAttachment.nextToDeliver() == null) {
                mailAttachment.addToHand((MailItem) item);
                currentDeliveryAttachment = mailAttachment;
                return true;
                /* If tube is empty fill tube and send off */
            } else if (mailAttachment.getTube() == null) {
                mailAttachment.addToTube((MailItem) item);
                dispatch();
                return true;
            }

        }
        /* If all else fails robot could not accept item and must tell the mail pool the item was not taken. */
        return false;
    }


    /**
     * @return checks if the current attachment is empty or not.
     */
    public boolean isEmpty() {
        return currentDeliveryAttachment.isEmpty();
    }

    /**
     * @return True if been told to dispatch
     */
    public boolean isReceivedDispatch() {
        return receivedDispatch;
    }

    /**
     * This is called when a robot is assigned the mail items and ready to dispatch for the delivery
     */
    public void dispatch() {
        receivedDispatch = true;
    }


    public DeliveryAttachment getCurrentDeliveryAttachment() {
        return currentDeliveryAttachment;
    }





}
