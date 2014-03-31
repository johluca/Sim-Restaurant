package restaurant.interfaces;

import java.util.List;

import restaurant.CookAgent;
import restaurant.WaiterAgent;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Jack Lucas
 *
 */
public interface Cook {

	public abstract void msgHereIsAnOrder(String choice, int tableNum, Customer customer, Waiter w);
	
	public abstract void msgOrderCooked(int orderNum);
	
	public abstract void msgIDontHave(List<String> foodList, List<Integer> amountList);
	
	public abstract void msgFoodDelivered(List<String> foodList, List<Integer> amountList);
}