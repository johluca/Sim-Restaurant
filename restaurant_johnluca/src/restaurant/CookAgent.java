package restaurant;

import agent.Agent;
import restaurant.CookAgent.Order.state;
import restaurant.CustomerAgent.CustomerEvent;
import restaurant.gui.CookGui;
//import restaurant.HostAgent.Table;
import restaurant.gui.HostGui;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

import java.awt.Dimension;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class CookAgent extends Agent implements Cook {
	//static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	//public List<CustomerAgent> waitingCustomers
	//= new ArrayList<CustomerAgent>();
	//public Collection<Table> tables;
	//private boolean isAtDesk = true;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;

	public CookGui cookGui = null;
	
	private Semaphore active = new Semaphore(0, true);
	



	public static class Order
	{
		Order(String choice, int table, Customer customer, Waiter w, state s) {
			Choice = choice;
			this.table = table;
			cust = customer;
			waiter = w;
			orderState = s;
		}
		Customer cust;
		int table;
		public String Choice;
		Waiter waiter;
		public enum state {none, received, cooking, cooked, givenToWaiter};
		private state orderState;
		
	};
	
	public static class Grill {
		Grill(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public Order o = null;
		public int x;
		public int y;
		
	}
	
	public static class PlateArea {
		PlateArea(int x, int y) {
			this.x = x;
			this.y = y;
		}
		public Order o=null;
		public int x;
		public int y;
	}
	
	List<Grill> grills = new ArrayList<Grill>();
	List<PlateArea> plateAreas = new ArrayList<PlateArea>();
	
	public static class Food
	{
		Food(String type, int cookingTime, int amount, int threshold, int capacity, boolean enroute) {
			this.type = type;
			this.cookingTime = cookingTime;
			this.amount = amount;
			this.threshold = threshold;
			this.capacity = capacity;
			this.enroute = enroute;
			
		}
		
		String type;
		int cookingTime;
		int amount;
		int threshold;
		int capacity;
		boolean enroute;
	}

	private Map<String, Integer> cookingTimes;
	private Map<String, Food> foods = new HashMap<String, Food>();
	
	private List<String> deliveredFoods = new ArrayList<String>();
	private List<Integer> deliveredAmounts = new ArrayList<Integer>();//used to transfer from message to updating foods action
	
	int previousMarket = 0;
	
	List<Order> Orders = Collections.synchronizedList(new ArrayList<Order>());
	Timer CookTimer;
	
	private List<MarketAgent> markets = new ArrayList<MarketAgent>();
//	map<String, int> cookTimes; 
	
	public CookAgent(String name) {
		super();

		this.name = name;
		this.CookTimer = new Timer();
		this.cookingTimes = new HashMap<String, Integer>();
		
		Food tofu = new Food("Tofu", 5*1000, 4, 2, 4, false);
		foods.put("Tofu", tofu);
		Food rice = new Food("Rice", 7*1000, 3, 4, 6, false);
		foods.put("Rice", rice);
		Food sushi = new Food("Sushi", 9*1000, 6, 2, 7, false);
		foods.put("Sushi", sushi);
		Food noodles = new Food("Noodles", 11*1000, 6, 3, 8, false);
		foods.put("Noodles", noodles);
		
		cookingTimes.put("Tofu", 5*1000);
		cookingTimes.put("Rice", 7*1000);//introduce food to map menu
		cookingTimes.put("Sushi", 9*1000);
		cookingTimes.put("Noodles", 11*1000);
		
		grills.add(new Grill(550, 50));
		grills.add(new Grill(550, 75));
		grills.add(new Grill(550, 100));
		grills.add(new Grill(550, 125));
		
		plateAreas.add(new PlateArea(500, 50));
		plateAreas.add(new PlateArea(500, 75));
		plateAreas.add(new PlateArea(500, 100));
		plateAreas.add(new PlateArea(500, 125));
		
		
		
		
//		inventoryMap.put("Tofu", 5);
	}


	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}


	// Messages ################################################
	
	public void msgHereIsAnOrder(String choice, int tableNum, Customer customer, Waiter w) {
		Do("I have received order");
		Orders.add(new Order(choice, tableNum, customer, w, state.received));
//		w.doDisplayChoice("none");//TODO too much sharing
		stateChanged();
		
	}

	public void msgOrderCooked(int orderNum) {
		Order o = Orders.get(orderNum);
		o.orderState = state.cooked;
		stateChanged();
		
	}
	
	public void msgIDontHave(List<String> foodList, List<Integer> amountList) {//for Market implementation NOTE CHANGE TO MSGIDONTHAVE
		//stub for now
		if(foodList.size() == 0) {
			Do("Order from market has everything");
			return;
		}
		else {
			markets.get(++previousMarket%markets.size()).msgLowOnFood(foodList, amountList, this);//hack, need way to pick next market
		}
		stateChanged();
	}
	
	public void msgNullifyPlateArea(int yPos) {
		for(PlateArea p : plateAreas) {
			if (p.y == yPos) {
				p.o = null;
			}
		}
	}
		
//		for(int i = 0; i<foodList.size(); i++) {
//				if(foods.get("Tofu").amount < foods.get("Tofu").threshold) {
//					for(String str: foodList) {
//						if(str.equals("Tofu")) {
//							
//						}
//					}
//					foods.get("Tofu").amount = foods.get("Tofu").capacity;
//				}
//				if(foods.get("Rice").amount < foods.get("Rice").threshold) {
//					foods.get("Rice").amount = foods.get("Rice").capacity;
//				}
//				if(foods.get("Sushi").amount < foods.get("Sushi").threshold) {
//					foods.get("Sushi").amount = foods.get("Sushi").capacity;
//				}
//				if(foods.get("Noodles").amount < foods.get("Noodles").threshold) {
//					foods.get("Noodles").amount = foods.get("Noodles").capacity;
//				}
			
//			if(amountList.get(i) >= foods.get(foodList.get(i)).capacity - foods.get(foodList.get(i)).amount) {//if delivery would completely fulfill order requested
//				foods.get(foodList.get(i)).enroute = true;//update flag that this FOOD is BEINGFULFILLED
//			}
		

	
	public void msgFoodDelivered(List<String> foodList, List<Integer> amountList) {//for Market implementation
		
		
		deliveredFoods = foodList;
//		Do(deliveredFoods.get(0));
		deliveredAmounts = amountList;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		
//		for(Order o : Orders) {
//			if(o.orderState == state.received) {
//				CookOrder(o);
//				o.orderState = state.cooking;
//				return true;
//			}
//		}
//		
//		Do("Entered sched");

		synchronized(Orders) {
			for(int i = 0; i < Orders.size(); i++) {
				if(Orders.get(i).orderState == state.received){
					//				Do("first cook sched");
					CookOrder(Orders.get(i), i);
					//				Orders.get(i).orderState = state.cooking;
					return true;
				}
			}
		}

		synchronized(Orders) {
			for(Order o : Orders) {
				if(o.orderState == state.cooked) {
					OrderCooked(o);
					o.orderState = state.givenToWaiter; 
					return true;
				}
			}
		}
		
		if(!deliveredFoods.isEmpty()) {
			Do("CONDITON MeT");
			updateInventory(deliveredFoods, deliveredAmounts);
			return true;
		}
		

		


		return false;

	}

	// Actions ///////////////////
	private void CookOrder(Order o, final int orderNum) {
	
		Food f = foods.get(o.Choice);
		if(f.amount < f.threshold) { //need to order more from market
			Do("TRYING to orDEr");
			checkInventory();//hack, need to choose
			
		}
		if(f.amount <= 0) {
			o.waiter.msgOutOfFood(o.table, o.Choice);
			Orders.remove(o);
			return;
		}
		for(Grill g : grills) {
			if (g.o==null) {
				goToFridge();
				acquireSemaphore(active);
				goToGrill(g);
				acquireSemaphore(active);
				g.o = o;
				break;
				//animate going to grill TODO
			}
		}
		doGoToIdle();
		
		//DoCooking();//animation
		CookTimer.schedule(new TimerTask() {
			//Object cookie = 1;
			public void run() {
				print("Done cooking ");
				msgOrderCooked(orderNum);
			}
		}, cookingTimes.get(o.Choice));
		f.amount--;
		o.orderState=state.cooking;
	}



	
	public void checkInventory() {//take market as param?
		
		List<String> lowFoodsNames = new ArrayList<String>();
		List<Integer> lowFoodsAmount = new ArrayList<Integer>();
		
		if((foods.get("Tofu").amount < foods.get("Tofu").threshold) && !foods.get("Tofu").enroute) {
			Food temp = foods.get("Tofu");
			lowFoodsNames.add(temp.type);
			lowFoodsAmount.add(temp.capacity - temp.amount);
		}
		if((foods.get("Rice").amount < foods.get("Rice").threshold) && !foods.get("Rice").enroute) {
			Food temp = foods.get("Rice");
			Do("A " + temp.capacity);
			Do("B " + foods.get("Rice").capacity + " " + foods.get("Tofu").capacity);
			lowFoodsNames.add(temp.type);
			lowFoodsAmount.add(temp.capacity - temp.amount);
		}
		if((foods.get("Sushi").amount < foods.get("Sushi").threshold) && !foods.get("Sushi").enroute) {
			Food temp = foods.get("Sushi");
			lowFoodsNames.add(temp.type);
			lowFoodsAmount.add(temp.capacity - temp.amount);
		}
		if((foods.get("Noodles").amount < foods.get("Noodles").threshold) && !foods.get("Noodles").enroute) {
			Food temp = foods.get("Noodles");
			lowFoodsNames.add(temp.type);
			lowFoodsAmount.add(temp.capacity - temp.amount);
		}
		
//		if(lowFoodsNames.size() > 0) {
			markets.get(previousMarket % markets.size()).msgLowOnFood(lowFoodsNames, lowFoodsAmount, this);
//		}
	}
	
	public void updateInventory(List<String> deliveredFoodsList, List<Integer> deliveredAmountsList) {
		Do("updating inventory");//NEVER CALLED
		for(int i = 0; i < deliveredFoodsList.size(); i++) {
			if(deliveredFoodsList.get(i).equals("Tofu")) {
				foods.get("Tofu").amount = deliveredAmountsList.get(i);//increase amount have with amount delivered
				
			}
			if(deliveredFoodsList.get(i).equals("Rice")) {
				Do("Received Rice " + deliveredAmountsList.get(i));
				foods.get("Rice").amount = deliveredAmountsList.get(i);//increase amount have with amount delivered
			
			}
			if(deliveredFoodsList.get(i).equals("Sushi")) {
				foods.get("Sushi").amount = deliveredAmountsList.get(i);//increase amount have with amount delivered
				
			}
			if(deliveredFoodsList.get(i).equals("Noodles")) {
				foods.get("Noodles").amount = deliveredAmountsList.get(i);//increase amount have with amount delivered
				
			}
		}
		deliveredFoods.clear();
		deliveredAmounts.clear();
		//update amount Cook has
	}

	

	
		
	private void OrderCooked(Order o) {
		for (Grill g: grills) {
			if(g.o == o) {
				
				
				goToGrill(g);
				acquireSemaphore(active);
				g.o = null;
				break;
				
				//animate go to this grill TODO
			}
		}
		doGoToIdle();
		for(PlateArea p: plateAreas) {
			if(p.o == null) {
				
				
				goToPlateArea(p);
				Dimension plateDim = new Dimension(p.x, p.y);
				o.waiter.msgOrderIsReady(o.cust, o.Choice, o.table, plateDim);
				acquireSemaphore(active);
				p.o = o;
				break;
				
				
				//animate go to plating area TODO
			
			}
		}
		doGoToIdle();
				o.orderState = state.givenToWaiter;
	}



	//utilities
	
	public void acquireSemaphore(Semaphore a) {
		try {
			a.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	public void addMarket(MarketAgent m) {
		markets.add(m);
	}

	public void setGui(CookGui gui) {
		cookGui = gui;
	}

	public CookGui getGui() {
		return cookGui;
	}
	
	public List<Grill> getGrills() {
		return grills;
	}
	
	public List<PlateArea> getPlateAreas() {
		return plateAreas;
	}
	
	public void goToGrill(Grill g) {
		cookGui.DoGoToGrill(g);
	}
	
	public void goToPlateArea(PlateArea p) {
		cookGui.DoGoToPlateArea(p);
	}
	
	public void doGoToIdle() {
		cookGui.DoGoIdle();
	}
	
	private void goToFridge() {
		cookGui.DoGoToFridge();
		
	}
	
	public void msgAtDestination() {
		active.release();
	}
	

}

	

