package automail;

/**
 * Keeps track of the stats of the robots.
 * Can be used for global robot stats or individual robots.
 * Extend statistic functionality by adding to this class.
 */
public class RobotStatistics {

    private static int timesFoodTubeAttached = 0;

    public static int getTimesFoodTubeAttached() {
        return timesFoodTubeAttached;
    }

    public static void foodTubeAttachedCount() {
        timesFoodTubeAttached++;
    }
}
