package restaurant.test.mock;


import java.util.Map;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Jack Lucas
 *
 */
public class MockCashier extends Mock implements Cashier {

        /**
         * Reference to the Cashier under test that can be set by the unit test.
         */
	
	EventLog log = new EventLog();
	
        public Cashier cashier;

        public MockCashier(String name) {
                super(name);

        }

		@Override
		public void msgComputeBill(Customer c, String choice, Waiter w) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void msgHereIsMyPayment(Customer c, double cash) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void msgRequestPayment(Market m, double amount) {
			// TODO Auto-generated method stub
			
		}

		
       

}