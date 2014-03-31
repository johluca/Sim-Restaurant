package restaurant;

import agent.Agent;
import restaurant.CookAgent.Order.state;
//import restaurant.Customer.CustomerEvent;
//import restaurant.HostAgent.Table;
import restaurant.gui.HostGui;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class CashierAgent extends Agent implements Cashier {
	//static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	//public List<Customer> waitingCustomers
	//= new ArrayList<Customer>();
	//public Collection<Table> tables;
	//private boolean isAtDesk = true;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented

	private String name;

	public HostGui hostGui = null;
	
	private double money = 1000;
	
//	boolean marketPaymentRequested = false;//handles paying market

	
	public static class MarketPayment {
		MarketPayment(Market m, double a) {
			setMarket(m);
			setAmount(a);
		}
		
		public Market getMarket() {
			return market;
		}
		public void setMarket(Market market) {
			this.market = market;
		}

		public double getAmount() {
			return amount;
		}

		public void setAmount(double amount) {
			this.amount = amount;
		}

		private Market market;
		private double amount;
	}
	 private List<MarketPayment> marketPayments = Collections.synchronizedList(new ArrayList<MarketPayment>());


	public enum customerState {doneEating, waitingForCheck, paying, leaving};
	public static class MyCustomer {
		MyCustomer(Customer customer, Waiter waiter, String choice, customerState state, double payment) {
			this.setCustomer(customer);
			this.waiter = waiter;
			this.setChoice(choice);
			this.setState(state);
			this.setPayment(payment);
		}
		
		public Customer getCustomer() {
			return customer;
		}
		public void setCustomer(Customer customer) {
			this.customer = customer;
		}
		public Waiter getWaiter() {
			return waiter;
		}

		public String getChoice() {
			return choice;
		}

		public void setChoice(String choice) {
			this.choice = choice;
		}

		public double getPayment() {
			return payment;
		}

		public void setPayment(double payment) {
			this.payment = payment;
		}

		public customerState getState() {
			return state;
		}

		public void setState(customerState state) {
			this.state = state;
		}

		private Customer customer;
		Waiter waiter;
		private String choice;
		private customerState state;
		private double payment;
		
		
	}
	
	
//	List<Order> Orders = new ArrayList<Order>();
	Timer CookTimer;
	
	private List<MyCustomer> myCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	private Map<String, Double> priceMenu = new HashMap<String, Double>();

	
	public CashierAgent(String name) {
		super();
		
		getPriceMenu().put("Tofu", 15.99);//populates pricemenu map
		getPriceMenu().put("Rice", 10.99);
		getPriceMenu().put("Sushi", 5.99);
		getPriceMenu().put("Noodles", 8.99);

		this.name = name;
		this.CookTimer = new Timer();
	}


	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}


	// Messages ################################################
	//##########################################################
	public void msgComputeBill(Customer c, String choice, Waiter w) { //from waiter
		myCustomers.add(new MyCustomer(c, w, choice, customerState.doneEating, 0));
		stateChanged();
		
	}
	public void msgHereIsMyPayment(Customer c, double cash) { //from customer
		MyCustomer mc = findCustomer(c);
		mc.setPayment(cash);
		mc.setState(customerState.paying);
		stateChanged();
	}
	public void msgRequestPayment(Market m, double amount) {//request from market for payment upon delivery
//		marketPaymentAmount = amount;
		marketPayments.add(new MarketPayment(m, amount));
		stateChanged();
		//TODO
	}



	/**
	 * Scheduler.  Determine what action is called for, and do it. ############################################################
	 */
	public boolean pickAndExecuteAnAction() {
		
		synchronized(myCustomers) {
			for(MyCustomer mc : myCustomers) {
				if(mc.getState()== customerState.doneEating) {
					calculateBill(mc);
					mc.setState(customerState.waitingForCheck);
					return true;
				}
			}
		}
		
		synchronized(myCustomers) {
			for(MyCustomer mc: myCustomers) {
				if(mc.getState() == customerState.paying) {
					calculateChange(mc);
					return true;
				}
			}
		}
		
		if(!marketPayments.isEmpty()) {
			//action pay market
			payMarket();
			return true;
		}


		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions ///////////////////
	private void calculateBill(MyCustomer mc) {
		Do("calculateBill");
		double price = getPriceMenu().get(mc.getChoice());
		mc.waiter.msgHereIsCheck(mc.getCustomer(), price);
	}
	
	private void calculateChange(MyCustomer mc) {
		double price = getPriceMenu().get(mc.getChoice());
		double change = mc.getPayment() - price;
		money += mc.getPayment();
		//NOW check if covers bill for non-norm scenarios!
		mc.getCustomer().msgHereIsChange(change);
		mc.setState(customerState.leaving);
	}
	private void payMarket() {
		MarketPayment temp = marketPayments.get(0);
		
		if(temp.getAmount() <= getMoney()) {//if cashier has money to pay market
			Do("Paying Market for delivery: $" + temp.getAmount());
			temp.getMarket().msgHereIsPayment(temp.getAmount());
			setMoney(getMoney() - temp.getAmount());
			marketPayments.remove(0);
		}
		else {//if cashier doesn't have enough money, gives all he has
			Do("Cannot afford to pay Market for delivery, instead giving all I have: $" + getMoney());
			temp.getMarket().msgHereIsPayment(getMoney());
			setMoney(0);
			marketPayments.remove(0);
		}
	}


	//utilities
	public MyCustomer findCustomer(Customer c) {
		for (MyCustomer mc : myCustomers) {
			if(mc.getCustomer() == c) {
				return mc;
			}
		
		}
		 return null;
	}

	public void setGui(HostGui gui) {
		hostGui = gui;
	}
	
	public List<MyCustomer> getMyCustomers() {
		return myCustomers;
	}

	public HostGui getGui() {
		return hostGui;
	}


	public double getMoney() {
		return money;
	}


	public void setMoney(double money) {
		this.money = money;
	}
	
	public List<MarketPayment> getMarketPayments() {
		return marketPayments;
	}


	public Map<String, Double> getPriceMenu() {
		return priceMenu;
	}


	public void setPriceMenu(Map<String, Double> priceMenu) {
		this.priceMenu = priceMenu;
	}


	



}

	

