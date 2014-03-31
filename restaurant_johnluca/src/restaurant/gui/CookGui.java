package restaurant.gui;

import restaurant.CookAgent.Grill;
import restaurant.CookAgent.Order;
import restaurant.CookAgent.PlateArea;
import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.CookAgent;

import java.awt.*;
import java.util.concurrent.Semaphore;

public class CookGui implements Gui{

	private CookAgent agent = null;
	private boolean isPresent = true;
	private boolean isHungry = false;

	//private HostAgent host;
	RestaurantGui gui;

	private int xPos, yPos;
	private int xDestination, yDestination;
	private enum Command {noCommand, GoToSeat, LeaveRestaurant};
	private Command command=Command.noCommand;
	
	String choiceDisplay = "";
	
	private Semaphore active = new Semaphore(0);

	public static final int xTable = 200;
	public static final int yTable = 250;
	public static final int agentDim = 20;
	
	boolean canRelease = false;

	public CookGui(CookAgent c){ //HostAgent m) {
		agent = c;
		xPos = 530;
		yPos = 100;
		xDestination = 530;
		yDestination = 100;
		//maitreD = m;
//		this.gui = gui;
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

		if (xPos == xDestination && yPos == yDestination) {
			if(canRelease) {
				atDestination();
			}
			
			}
			command=Command.noCommand;
		}
	

	public void draw(Graphics2D g) {//abstract definition, needed for Graphics
		
		g.setColor(Color.BLUE);
		g.fillRect(520, 145, 30, 20);
		for(Grill gr : agent.getGrills()) {
			g.setColor(Color.BLACK);
			g.fillRect(gr.x, gr.y, 20, 20);
			if(gr.o!=null) {
				g.setColor(Color.RED);
				g.drawString(gr.o.Choice, gr.x, gr.y);
			}
		}
		
		for(PlateArea p : agent.getPlateAreas()) {
			g.setColor(Color.DARK_GRAY);
			g.fillRect(p.x, p.y, 20, 20);
			if(p.o!=null) {
				g.setColor(Color.RED);
				g.drawString(p.o.Choice, p.x, p.y);
			}
		}
		g.setColor(Color.CYAN);
		g.fillRect(xPos, yPos, agentDim, agentDim);
		
		if(!choiceDisplay.equals("")) {
			g.drawString(choiceDisplay, xPos, yPos);
		}
		
		

	}
	
	public void displayChoice(String choice) {
		choiceDisplay = choice;
	}
	
	public void drawLetter(Graphics2D g, String letter) {
		g.setColor(Color.black);
		g.drawString(letter, xPos+10, yPos+10);
		
	}

	public boolean isPresent() {
		return isPresent;
	}


	public void setPresent(boolean p) {
		isPresent = p;
	}

	public void DoGoToGrill(Grill g) {
		canRelease = true;
		xDestination = g.x - 20;
		yDestination = g.y;
		
	}
	
	public void DoGoToPlateArea(PlateArea p) {
		canRelease = true;
		xDestination = p.x + 20;
		yDestination = p.y;
	}
	
	public void DoGoIdle() {
		canRelease = false;
		xDestination = 530;
		yDestination = 100;
	}
	
	public void DoGoToFridge() {
		canRelease = true;
		xDestination =530;
		yDestination = 145;
	}
	
	public void displayText(Grill g, String text) {
		//TODO
	}
	
    public void atDestination() {
    	canRelease = false;
    	agent.msgAtDestination();
    	
    }
	
}

