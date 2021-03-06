package simulation;

import automail.*;
import exceptions.ExcessiveDeliveryException;
import exceptions.ItemTooHeavyException;
import exceptions.MailAlreadyDeliveredException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * This class simulates the behaviour of AutoMail
 */
public class Simulation {
	private static int NUM_ROBOTS;
	
    /** Constant for the mail generator */
    private static int MAIL_TO_CREATE;
    private static int MAIL_MAX_WEIGHT;
    
    private static boolean OVERDRIVE_ENABLED;
    private static boolean STATISTICS_ENABLED;

	private static boolean DELIVER_FOOD;
    public static boolean getDELIVER_FOOD()	{
    	return DELIVER_FOOD;
	}
    private static ArrayList<DeliveryItem> DELIVERED;
    private static double total_delay = 0;

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
    	/** Load properties for simulation based on either default or a properties file.**/
    	Properties automailProperties = setUpProperties();
    	
    	//An array list to record mails that have been delivered
        DELIVERED = new ArrayList<DeliveryItem>();
                
        
        
        /** This code section below is to save a random seed for generating mails.
         * If a program argument is entered, the first argument will be a random seed.
         * If not a random seed will be from a properties file. 
         * Otherwise, no a random seed. */
        
        /** Used to see whether a seed is initialized or not */
        HashMap<Boolean, Integer> seedMap = new HashMap<>();
        if (args.length == 0 ) { // No arg
        	String seedProp = automailProperties.getProperty("Seed");
        	if (seedProp == null) { // and no property
        		seedMap.put(false, 0); // so randomise
        	} else { // Use property seed
        		seedMap.put(true, Integer.parseInt(seedProp));
        	}
        } else { // Use arg seed - overrides property
        	seedMap.put(true, Integer.parseInt(args[0]));
        }
        Integer seed = seedMap.get(true);
        System.out.println("A Random Seed: " + (seed == null ? "null" : seed.toString()));
        
        
        /**
         * This code section is for running a simulation
         */
        /* Instantiate MailPool and Automail */
     	MailPool mailPool = new MailPool();
        Automail automail = new Automail(mailPool, new ReportDelivery(), NUM_ROBOTS);

        MailGenerator mailGenerator = new MailGenerator(MAIL_TO_CREATE, MAIL_MAX_WEIGHT, mailPool, seedMap);
        
        /** Generate all the mails */
        mailGenerator.generateAllMail();
        // PriorityMailItem priority;  // Not used in this version
        while(DELIVERED.size() != mailGenerator.MAIL_TO_CREATE) {
			// Given the time, add items to the mail pool
            mailGenerator.addToMailPool();
            try {
				// For each waiting robot loads one or two pieces of mail 
                automail.mailPool.loadItemsToRobot();

				for (int i=0; i < NUM_ROBOTS; i++) {
					automail.robots[i].operate();
				}
			} catch (ExcessiveDeliveryException|ItemTooHeavyException e) {
				e.printStackTrace();
				System.out.println("Simulation unable to complete.");
				System.exit(0);
			}
            Clock.Tick();
        }
        printResults(automail.getStats());
    }
    
    static private Properties setUpProperties() throws IOException {
    	Properties automailProperties = new Properties();

		final String FALSE = "false";
    	automailProperties.setProperty("Robots", "Standard");
    	automailProperties.setProperty("MailPool", "strategies.SimpleMailPool");
    	automailProperties.setProperty("Floors", "10");
    	automailProperties.setProperty("Fragile", FALSE);
    	automailProperties.setProperty("Mail_to_Create", "80");
    	automailProperties.setProperty("Mail_Receving_Length", "100");
    	automailProperties.setProperty("Overdrive", FALSE);
    	automailProperties.setProperty("Statistics", FALSE);

    	// Read properties
		FileReader inStream = null;
		try {
			inStream = new FileReader("automail.properties");
			automailProperties.load(inStream);
		} finally {
			 if (inStream != null) {
	                inStream.close();
	            }
		}
		
		// Floors
		Building.FLOORS = Integer.parseInt(automailProperties.getProperty("Floors"));
        System.out.println("#Floors: " + Building.FLOORS);
		// Mail_to_Create
		MAIL_TO_CREATE = Integer.parseInt(automailProperties.getProperty("Mail_to_Create"));
        System.out.println("#Created mails: " + MAIL_TO_CREATE);
        // Mail_to_Create
     	MAIL_MAX_WEIGHT = Integer.parseInt(automailProperties.getProperty("Mail_Max_Weight"));
        System.out.println("Maximum weight: " + MAIL_MAX_WEIGHT);
		// Last_Delivery_Time
		Clock.MAIL_RECEVING_LENGTH = Integer.parseInt(automailProperties.getProperty("Mail_Receving_Length"));
        System.out.println("Mail receving length: " + Clock.MAIL_RECEVING_LENGTH);
        // Overdrive ability
        OVERDRIVE_ENABLED = Boolean.parseBoolean(automailProperties.getProperty("Overdrive"));
        System.out.println("Overdrive enabled: " + OVERDRIVE_ENABLED);
        // Statistics tracking
        STATISTICS_ENABLED = Boolean.parseBoolean(automailProperties.getProperty("Statistics"));
        System.out.println("Statistics enabled: " + STATISTICS_ENABLED);
		// Robots
		NUM_ROBOTS = Integer.parseInt(automailProperties.getProperty("Robots"));
		System.out.print("#Robots: "); System.out.println(NUM_ROBOTS);
		assert(NUM_ROBOTS > 0);

		DELIVER_FOOD = Boolean.parseBoolean(automailProperties.getProperty("DeliverFood"));

		return automailProperties;
    }
    
    static class ReportDelivery implements IMailDelivery {
    	
    	/** Confirm the delivery and calculate the total score */
    	public void deliver(DeliveryItem deliveryItem){
    		if(!DELIVERED.contains(deliveryItem)){
    			DELIVERED.add(deliveryItem);
                System.out.printf("T: %3d > Delivered(%4d) [%s]%n", Clock.Time(), DELIVERED.size(), deliveryItem.toString());
    			// Calculate delivery score
    			total_delay += calculateDeliveryDelay(deliveryItem);
    		}
    		else{
    			try {
    				throw new MailAlreadyDeliveredException();
    			} catch (MailAlreadyDeliveredException e) {
    				e.printStackTrace();
    			}
    		}
    	}

    }
    
    private static double calculateDeliveryDelay(DeliveryItem deliveryItem) {
    	// Penalty for longer delivery times
    	final double penalty = 1.2;
    	double priority_weight = 0;
        // Take (delivery time - arrivalTime)**penalty * (1+sqrt(priority_weight))
    	if(deliveryItem instanceof PriorityMailItem){
    		priority_weight = ((PriorityMailItem) deliveryItem).getPriorityLevel();
    	}
        return Math.pow(Clock.Time() - deliveryItem.getArrivalTime(),penalty)*(1+Math.sqrt(priority_weight));
    }

    public static void printResults(RobotStatistics stats){
        System.out.println("T: "+Clock.Time()+" | Simulation complete!");
        System.out.println("Final Delivery time: "+Clock.Time());
        System.out.printf("Delay: %.2f%n", total_delay);


        /* Only print theses stats if enabled in automail.properties */
        if(STATISTICS_ENABLED)	{

			System.out.println("Mail Items Delivered: " + stats.getNumberMailDelivered());
			System.out.println("Food Items Delivered: " + stats.getNumberFoodDelivered());
			System.out.println("Mail Items Weight: " + stats.getTotalMailWeight());
			System.out.println("Food Items Weight: " + stats.getTotalFoodWeight());
			System.out.println("Food Tube Attached: " + stats.getTimesFoodTubeAttached() + " times");

		}



    }

}
