package restaurant.gui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import restaurant.HostAgent;

public class AnimationPanel extends JPanel implements ActionListener {

    private final int WINDOWX = 1100;//previously 450
    private final int WINDOWY = 350;
    public static final int xTable = 200;
    public static final int yTable = 250;
    public static final int tableColor = 50;
    private Image bufferImage;
    private Dimension bufferSize;
    private int animationTime = 10;

    private List<Gui> guis = new ArrayList<Gui>();

    public AnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        
        bufferSize = this.getSize();
    	Timer timer = new Timer(animationTime, this );
    	timer.start();
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D t1 = (Graphics2D)g;
        Graphics2D t2 = (Graphics2D)g;
        Graphics2D t3 = (Graphics2D)g;
        Graphics2D t4 = (Graphics2D)g;
        
        Graphics2D waiterIdleArea = (Graphics2D)g;
        
        Graphics2D cashierArea = (Graphics2D)g;
        

        //Clear the screen by painting a rectangle the size of the frame
        t1.setColor(getBackground());
        t1.fillRect(0, 0, WINDOWX, WINDOWY );
        t2.setColor(getBackground());
        t2.fillRect(0, 0, WINDOWX, WINDOWY );
        t3.setColor(getBackground());
        t3.fillRect(0, 0, WINDOWX, WINDOWY );
        
        
        
        //Here is the table
        t1.setColor(Color.ORANGE);
        t1.fillRect(100, 100, tableColor, tableColor);//200 and 250 need to be table params (locations)
        t2.setColor(Color.ORANGE);
        t2.fillRect(200, 100, tableColor, tableColor);//200 and 250 need to be table params (locations)
        t3.setColor(Color.ORANGE);
        t3.fillRect(300, 100, tableColor, tableColor);//200 and 250 need to be table params (locations)
        t4.setColor(Color.ORANGE);
        t4.fillRect(400, 100, tableColor, tableColor);//200 and 250 need to be table params (locations)

        //Cashier area
        cashierArea.setColor(Color.BLACK);
        cashierArea.fillRect(400, 0, 50, 20);
        cashierArea.setColor(Color.WHITE);
        cashierArea.drawString("Cashier", 400, 15);
        
        //Cook and Waiter areas

        waiterIdleArea.setColor(Color.BLUE);
        waiterIdleArea.fillRect(200, 0, 140, 22);
        
        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(t1);
                gui.draw(t2);
                gui.draw(t3);
                gui.draw(t4);//draws 4 tables
                gui.draw(waiterIdleArea);
                
            }
        }
    }

    public void addGui(CustomerGui gui) {
        guis.add(gui);
    }

    public void addGui(HostGui gui) {
        guis.add(gui);
    }
    
    public void addGui(WaiterGUI gui) {
    	guis.add(gui);
    }
    
    public void addGui(CookGui gui) {
    	guis.add(gui);
    	
    }
}
