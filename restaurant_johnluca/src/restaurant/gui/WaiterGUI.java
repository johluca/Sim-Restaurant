package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.WaiterAgent;
import restaurant.interfaces.Customer;

import java.awt.*;

import javax.swing.ImageIcon;

public class WaiterGUI implements Gui {

    private WaiterAgent agent = null;


    private int xPos = 0, yPos = 0;//default waiter position
    private int xDestination = 0, yDestination = 0;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;
    public static final int agentDim = 20;
    
    String customerChoice = "none";
    
  //  Image waiterImg;// = new ImageIcon("/jacklucas/restaurant_johnluca/src/restaurant/Resources/waiter.png").getImage();
    
    boolean canRelease = false;

    public WaiterGUI(WaiterAgent agent) {
        this.agent = agent;
        //waiterImg = Resources.getResource(/agents/src/images/waiter.png);
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        if((xPos == -20 && yPos == -20)) {	//boolean
        	//agent.msgAtDesk();
        }
        if (xPos == xDestination && yPos == yDestination) {
        	if(canRelease)
        		atDestination();
//           agent.msgAtDestination();
        }
        //if(xPos == xDestination && yPos == yDestination & (xDestination <0) & (yDestination < 0)) {

    }

    public void draw(Graphics2D g) {
    	
//    	g.drawImage(waiterImg, xPos, yPos, null);
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, agentDim, agentDim);
        
        if(!customerChoice.equals("none")) {
        	
        g.drawString(customerChoice, xPos, yPos);
        	}

    }
    
    public void displayChoice(String choice) {
    	customerChoice = choice;
    }

    public boolean isPresent() {
        return true;
    }

    public void DoGoToHost() {
    	canRelease = true;
    	xDestination = 20;
    	yDestination = 40;
    }
    
    public void DoGoToCook(int x, int y) {//TODO update for plateposition
    	canRelease = true;
    	xDestination = x - 20;
    	yDestination = y;
    }
    
    public void DoBringToTable(Customer customer, int x, int y) {
        xDestination = x + agentDim;
        yDestination = y - agentDim;
        canRelease = true;
    }
    
    public void DoGoToLoc(int x, int y) {
        xDestination = x + agentDim;
        yDestination = y - agentDim;
        canRelease = true;
    }

    public void DoLeaveCustomer() {
        xDestination = -agentDim;
        yDestination = -agentDim;
    }
    
    public void DoGoAway(int fac) {
    	canRelease = false;
    	xDestination = 200 + (fac * 20) + fac;
    	yDestination = 0;
    	
    			
    }
    
    public void DoGoToCashier() {
    	canRelease = true;
    	xDestination = 400;
    	yDestination = 0; 
    }

    public void DoGoOnBreak() {
    	xDestination = 600;
    	yDestination = 40;
    	
    	canRelease = false;
    }
    
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    public void setStartPosition(int x, int y) {
    	xPos = x;
    	yPos = y;
    }
    
    public void atDestination() {
    	canRelease = false;
    	agent.msgAtDestination();
    	
    }
}
