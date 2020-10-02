package automail;

import simulation.IMailDelivery;


/**
 * Represents the automail system, an aggregation of robots and a mail pool.
 */
public class Automail {

    /* The robots in automail */
    public Robot[] robots;
    /* The mailPool in automail */
    public MailPool mailPool;


    /**
     * @param mailPool  Pool of mail
     * @param delivery  The final report
     * @param numRobots The number of robots to generate
     */
    public Automail(MailPool mailPool, IMailDelivery delivery, int numRobots) {
        /* Initialize the MailPool */
        this.mailPool = mailPool;

        /* Initialize robots */
        robots = new Robot[numRobots];
        for (int i = 0; i < numRobots; i++) robots[i] = new Robot(delivery, mailPool, i);
    }


}
