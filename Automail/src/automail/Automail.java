package automail;

import simulation.Building;
import simulation.IMailDelivery;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class Automail {
	      
    public Robot[] robots;
    public MailPool mailPool;



    
    public Automail(MailPool mailPool, IMailDelivery delivery, int numRobots) {  	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;



    	/** Initialize robots */
    	robots = new Robot[numRobots];
    	for (int i = 0; i < numRobots; i++) robots[i] = new Robot(delivery, mailPool, i);
    }

    
}
