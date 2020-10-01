package automail;

import simulation.Building;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class FloorManager {

    private ArrayList<Queue<String>> lockedFloors = new ArrayList<>(Building.FLOORS);

    private static FloorManager instance = null;

    public static FloorManager getInstance() {
        if(instance == null)
        {
            instance = new FloorManager();
        }
        return instance;
    }

    private FloorManager()  {
        for (int i = 0; i < Building.FLOORS; i++) {
            lockedFloors.add( new LinkedList<>());
        }
    }

    public void lockFloor(int floorIndex, String id) {
        lockedFloors.get(floorIndex-1).add(id);
        System.out.println("Robot " + id + " is locking floor " + floorIndex);
    }

    public void releaseFloor(int floorIndex) {
        lockedFloors.get(floorIndex-1).poll();
        System.out.println("Floor " + floorIndex + " released");
    }

    public boolean checkFloor(int floorIndex, String id) {
        String floorLocked = lockedFloors.get(floorIndex-1).peek();
        if (id.equals(floorLocked) || floorLocked == null) {
            return true;
        }
        return false;
    }

    public ArrayList<Queue<String>> getLockedFloors() {
        return lockedFloors;
    }
}
