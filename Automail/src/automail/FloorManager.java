package automail;

import simulation.Building;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Class for robot to use to ensure that nothing is delivered to a floor that food is being delivered too
 */
public class FloorManager {
    /* List of FIFO queues representing each floor of the building. FIFO (First in First out) is used
     * as the robot to first lock a floor is the one who should be able to deliver next */
    private ArrayList<Queue<String>> lockedFloors = new ArrayList<>(Building.FLOORS);

    /**
     * Initialised the floor manager queues
     */
    public FloorManager() {
        for (int i = 0; i < Building.FLOORS; i++) {
            lockedFloors.add(new LinkedList<>());
        }
    }

    /**
     * @param floorIndex the floor to lock
     * @param id         the robot id thats locking the floor
     */
    public void lockFloor(int floorIndex, String id) {
        System.out.println("Robot " + id + " is locking floor " + floorIndex);
        lockedFloors.get(floorIndex - 1).add(id);
    }

    /**
     * @param floorIndex the floor to release after food is delivered
     */
    public void releaseFloor(int floorIndex) {

        if(lockedFloors.get(floorIndex - 1).poll() != null)
            System.out.println("Floor " + floorIndex + " is now unlocked");
    }

    /**
     * @param floorIndex The floor index to check if its locked
     * @param id         The id to check if allowed to deliver
     * @return Returns a boolean that is true if no one has locked the floor or if the robot checking is the one
     * that has locked the floor first.
     */
    public boolean checkFloor(int floorIndex, String id) {
        String floorLocked = lockedFloors.get(floorIndex - 1).peek();
        return id.equals(floorLocked) || floorLocked == null;
    }
}
