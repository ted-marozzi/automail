package automail;

import simulation.Building;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


/**
 * Class for robot to use to ensure that nothing is delivered to a floor that food is being delivered too
 */
public class FloorManager {
    /* List of LIFO queues representing each floor of the building. LIFO (Last in First out) is used
     * as the robot to first lock a floor is the one who should be able to deliver next */
    private static ArrayList<Queue<String>> lockedFloors = new ArrayList<>(Building.FLOORS);

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
    public static void lockFloor(int floorIndex, String id) {
        lockedFloors.get(floorIndex - 1).add(id);

    }

    /**
     * @param floorIndex the floor to release after food is delivered
     */
    public static void releaseFloor(int floorIndex) {
        lockedFloors.get(floorIndex - 1).poll();
    }

    /**
     * @param floorIndex The floor index to check if its locked
     * @param id         The id to check if allowed to deliver
     * @return Returns a boolean that is true if no one has locked the floor or if the robot checking is the one
     * that has locked the floor first.
     */
    public static boolean checkFloor(int floorIndex, String id) {
        String floorLocked = lockedFloors.get(floorIndex - 1).peek();
        return id.equals(floorLocked) || floorLocked == null;
    }
}
