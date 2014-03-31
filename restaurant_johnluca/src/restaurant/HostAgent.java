package restaurant;

import agent.Agent;
import restaurant.gui.HostGui;
import restaurant.interfaces.Customer;

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
public class HostAgent extends Agent {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<CustomerAgent> waitingCustomers	= new ArrayList<CustomerAgent>();
	public Collection<Table> tables;
	public List<MyWaiter> myWaiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	
	public int waiterChoice = 0;
	
	private Map<Integer, Dimension> tableMap = new HashMap<Integer, Dimension>();


	private String name;
	
	int waitingPositionNum = 0;


	public HostGui hostGui = null;
	
	int occupiedNum = 0;
	
	public enum waiterState {working, breakRequested, onBreak};
	
	public static class MyWaiter {
		MyWaiter(WaiterAgent waiter, waiterState state) {
			this.waiter = waiter;
			this.state = state;
		}
		
		WaiterAgent waiter;
		boolean noCustomers = true;
		waiterState state;
		List<CustomerAgent> customers = new ArrayList<CustomerAgent>();
	}

	public HostAgent(String name) {
		super();
		
		Dimension t1 = new Dimension(100, 100);
		Dimension t2 = new Dimension(200, 100);
		Dimension t3 = new Dimension(300, 100);
		Dimension t4 = new Dimension(400, 100);
		tableMap.put(0, t1);
		tableMap.put(1, t2);
		tableMap.put(2, t3);
		tableMap.put(3, t4);
		

		this.name = name;
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		for (int ix = 0; ix <= NTABLES; ix++) {
			tables.add(new Table(ix, (ix%4 + 1) * 100, (ix/4 + 1) * 100  ));//how you add to a collections
		}
	}

	public String getMaitreDName() {
		return name;
	}
	
	public Map<Integer, Dimension> getTableMap() {
		return tableMap;
	}
	
	public void addWaiter(WaiterAgent waiter) {
		Do("Welcome new waiter: " + waiter.getName());
		myWaiters.add(new MyWaiter(waiter, waiterState.working));
		waiter.setIdlePosition(myWaiters.size()); //ensures unique idle positions
		stateChanged();
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	
	// MESSAGES//////////////////////#########################################################

	public void msgIWantFood(CustomerAgent cust) {
		waitingCustomers.add(cust);
		stateChanged();
	}

	public void msgLeavingTable(Customer customer) {
		for(MyWaiter mw : myWaiters) {
			for(int i = 0; i<mw.customers.size(); i++) {
				if(mw.customers.get(i) == customer) {
					mw.customers.remove(i);	
				}	
			}
		}
		for (Table table : tables) {
			if (table.getOccupant() == customer) {
				print(customer + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}
	
	public void msgIdLikeToGoOnBreak(WaiterAgent w) {//v2.1 put waiter onbreak
		if(myWaiters.size()>1) { //HACKY, should tell waiter
			findMyWaiter(w).state = waiterState.breakRequested;//problem, waiter can be added after this
		}
		stateChanged();
	}

	public void msgNoCustomers (WaiterAgent w) {
		MyWaiter mw = findMyWaiter(w);
		mw.noCustomers = true;
		stateChanged();
	}
	
	public void msgReturningToWork(WaiterAgent w) {
		MyWaiter mw = findMyWaiter(w);
		mw.state = waiterState.working;
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
//		if(!waitingCustomers.isEmpty()) {
//			waitingCustomers.get(0).msgSetWaitingPosition(waitingPositionNum);
//			waitingPositionNum++;
//		}
		synchronized(tables) {
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty() && !myWaiters.isEmpty()) {
						seatCustomer(waitingCustomers.get(0), table, chooseWaiter());//the action

						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
				}
//				if(table.isOccupied()) {
//					
//					occupiedNum++;
//					Do("mMmMm" + occupiedNum);
//					if(occupiedNum == 3 && !waitingCustomers.isEmpty()) {//all tables occupied
//						Do("MEHHDHD");
//						waitingCustomers.get(0).goToWaitPosition(0);
//						occupiedNum = 0;
//						return true;
//					}
//					
//				}
			}
		}
		
		synchronized(myWaiters) {
			for(MyWaiter mw: myWaiters) {
				if(mw.state==waiterState.breakRequested && mw.noCustomers) {
					sendOnBreak(mw);
					return true;
				}
			}
		}
		
		
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private WaiterAgent chooseWaiter() {
		 //chooses different waiter by increasing by one every time called if more available
		while(!myWaiters.isEmpty()){
			WaiterAgent w = myWaiters.get(waiterChoice%myWaiters.size()).waiter;
			waiterChoice++;
			if(findMyWaiter(w).state == waiterState.working) {
				
				return w;
			}	
		}
		
		return null;

	}

	private void seatCustomer(CustomerAgent customer, Table table, WaiterAgent waiter) {

		MyWaiter mw = findMyWaiter(waiter);
		mw.customers.add(customer);
		mw.noCustomers = false;
		table.setOccupant(customer);
		waitingCustomers.remove(customer);
		waiter.msgSitAtTable(customer, table.tableNumber);
		

	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(CustomerAgent customer, Table table) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		
		print("Seating " + customer + " at " + table);
		hostGui.DoBringToTable(customer, table.getXCoord(), table.getYCoord()); 

	}

	private void sendOnBreak(MyWaiter mw) {
		mw.waiter.msgGoOnBreak();
		mw.state = waiterState.onBreak;
	}
	//utilities

	public MyWaiter findMyWaiter(WaiterAgent w) {
		for(MyWaiter mw: myWaiters) {
			if(mw.waiter == w)
				return mw;
		
		}
		return null;
		
	}
	
	public void setWaiterBreak(String waiterName) {//called from panel 
		for(MyWaiter mw: myWaiters) {
			if(mw.waiter.getName().equals(waiterName) && myWaiters.size() >= 1) {
				for(MyWaiter mw2: myWaiters) {
					if(mw2.state == waiterState.working && mw!=mw2) {
						Do("time for break " + waiterName);//TODO changed UNSURE
						mw.waiter.msgSetBreakBit();
					}
				}
			}
		}
	}
	
	public void pauseAll() {
		for(MyWaiter w: myWaiters) {
			w.waiter.pause();
			w.waiter.pauseAllCustomers();
		}
			
	}
	
	public void restartAll() {
		for(MyWaiter w: myWaiters) {
			w.waiter.restart();
			w.waiter.restartAllCustomers();
		}
	}
	
	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

	private class Table {
		CustomerAgent occupiedBy;
		int tableNumber;
		int xCoord;
		int yCoord;
		

		Table(int tableNumber, int x, int y) {
			this.tableNumber = tableNumber;
			this.xCoord = x;
			this.yCoord = y;
		}

		void setOccupant(CustomerAgent cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		CustomerAgent getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
		
		int getXCoord() {
			return xCoord;
		}
		
		int getYCoord() {
			return yCoord;
		}
		
		void setXcoord(int num) {
			xCoord = num;
		}
		
		void setYCoord(int num) {
			yCoord = num;
		}
		
	
		
	}
}

