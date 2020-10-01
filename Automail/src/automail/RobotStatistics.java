package automail;

public class RobotStatistics {

    private static int timesFoodTubeAttached = 0;

    public static int getTimesFoodTubeAttached() {
        return timesFoodTubeAttached;
    }

    public static void foodTubeAttachedCount()   {

        timesFoodTubeAttached++;
        System.out.println("### Times food tube attacked: " + timesFoodTubeAttached);
    }


}
