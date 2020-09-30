package automail;

import java.util.LinkedList;
import java.util.Comparator;
import java.util.ListIterator;

import exceptions.ItemTooHeavyException;
import simulation.Clock;
import simulation.PriorityMailItem;

/**
 * addToPool is called when there are mail items newly arrived at the building to add to the MailPool or
 * if a robot returns with some undelivered items - these are added back to the MailPool.
 * The data structure and algorithms used in the MailPool is your choice.
 * 
 */
public class MailPool {

	private class Item {
		int priority;
		int destination;
		DeliveryItem deliveryItem;
		// Use stable sort to keep arrival time relative positions
		
		public Item(DeliveryItem deliveryItem) {
			priority = (deliveryItem instanceof PriorityMailItem) ? ((PriorityMailItem) deliveryItem).getPriorityLevel() : 1;
			destination = deliveryItem.getDestFloor();
			this.deliveryItem = deliveryItem;
		}
	}
	
	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.priority < i2.priority) {
				order = 1;
			} else if (i1.priority > i2.priority) {
				order = -1;
			} else if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}
	
	private LinkedList<Item> pool;
	private LinkedList<Robot> robots;

	public MailPool(){
		// Start empty
		pool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
	}

	/**
     * Adds an item to the mail pool
     * @param mailItem the mail item being added.
     */
	public void addToPool(DeliveryItem mailItem) {
		Item item = new Item(mailItem);
		pool.add(item);
		pool.sort(new ItemComparator());
	}
	
	
	
	/**
     * load up any waiting robots with mailItems, if any.
	 * 
     */
	public void loadItemsToRobot() throws ItemTooHeavyException {
		//List available robots
		ListIterator<Robot> i = robots.listIterator();
		while (i.hasNext()) loadItem(i);
	}
	//TODO: Add logic determining if its a food or mail item, use add to food tube method
	//load items to the robot
	private void loadItem(ListIterator<Robot> i) throws ItemTooHeavyException {
		Robot robot = i.next();
		assert(robot.isEmpty());

		ListIterator<Item> j = pool.listIterator();

		if (pool.size() == 0 && !robot.isArmsAttached()) {

			if (Clock.Time() - robot.getHeatingStarted() >= 5) {

				robot.dispatch(); // send the robot off if it has any items to
				// deliver
				i.remove();
			}
		}



		if (pool.size() > 0) {
			try {

				DeliveryItem item = j.next().deliveryItem;
				String itemType = item.getItemType();
				if (itemType.equals("Food") || !robot.isArmsAttached()) {

					if (robot.isArmsAttached()) {
						robot.attachFoodTube();

					}

					if (itemType.equals("Food") && robot.foodItemsLoaded() < 3) {
						robot.addToFoodTube((FoodItem)item);
						j.remove();
					}

					while (j.hasNext() && robot.foodItemsLoaded() < 3) {
						DeliveryItem item2 = j.next().deliveryItem;
						if (item2.getItemType().equals("Food")) {
							robot.addToFoodTube((FoodItem) item2);
							j.remove();
						}
					}

					if (Clock.Time() - robot.getHeatingStarted() >= 5) {
						robot.dispatch(); // send the robot off if it has any items to
						// deliver
						i.remove();
					}

				} else if (robot.isArmsAttached()) {
					assert (robot.isEmpty());
					robot.addToHand((MailItem) item);
					j.remove();

					while (j.hasNext() && robot.istubeEmpty()) {
						DeliveryItem item2 = j.next().deliveryItem;
						if (!(item2.getItemType().equals("Food"))) {
							robot.addToTube((MailItem)j.previous().deliveryItem);
							j.remove();
						}
					}
					robot.dispatch(); // send the robot off if it has any items to deliver
					i.remove();
				}


			} catch (Exception e) {
	            throw e; 
	        } 
		}



	}

	/**
     * @param robot refers to a robot which has arrived back ready for more mailItems to deliver
     */	
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
