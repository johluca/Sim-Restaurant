package restaurant.gui;

import java.awt.Color;
import java.awt.Image;

import javax.imageio.ImageIO;

import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
/**
 * Main GUI class.
 * Contains the main frame and subsequent panels
 */
public class RestaurantGui extends JFrame implements ActionListener {
    /* The GUI has two frames, the control frame (in variable gui) 
     * and the animation frame, (in variable animationFrame within gui)
     */
//	JFrame animationFrame = new JFrame("Restaurant Animation");
	AnimationPanel animationPanel = new AnimationPanel();
	
    /* restPanel holds 2 panels
     * 1) the staff listing, menu, and lists of current customers all constructed
     *    in RestaurantPanel()
     * 2) the infoPanel about the clicked Customer (created just below)
     */    
    private RestaurantPanel restPanel = new RestaurantPanel(this);
    
    /* infoPanel holds information about the clicked customer, if there is one*/
    private JPanel infoPanel;
    private JLabel infoLabel; //part of infoPanel
    private JPanel graphicPanel; //added for Lab1
    private JLabel graphicLabel;
    private JLabel graphicImage;
    private JCheckBox stateCB;//part of infoLabel
    private JCheckBox breakCB;
    
    //private Image image;

    private Object currentPerson;/* Holds the agent that the info is about.
    								Seems like a hack */

    /**
     * Constructor for RestaurantGui class.
     * Sets up all the gui components.
     */
    public RestaurantGui() {
        int WINDOWX = 1100;
        int WINDOWY = 1200;

//        animationFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        animationFrame.setBounds(100+WINDOWX, 50 , WINDOWX+100, WINDOWY+100);
//        animationFrame.setVisible(true);
//    	animationFrame.add(animationPanel); 
    	
    	setBounds(50, 50, WINDOWX, WINDOWY);

        setLayout(new BoxLayout((Container) getContentPane(), 
        		BoxLayout.Y_AXIS));

        ImageIcon image = new ImageIcon("/Users/jacklucas/Documents/earthlogo.jpg");
        
        
        Dimension restDim = new Dimension(WINDOWX, (int) (WINDOWY * .3));
        restPanel.setPreferredSize(restDim);
        restPanel.setMinimumSize(restDim);
        restPanel.setMaximumSize(restDim);
        add(restPanel);
        
        // Now, setup the info panel
        Dimension infoDim = new Dimension(WINDOWX, (int) (WINDOWY * .125));
        infoPanel = new JPanel();
        infoPanel.setPreferredSize(infoDim);
        infoPanel.setMinimumSize(infoDim);
        infoPanel.setMaximumSize(infoDim);
        infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));
        
        Dimension graphicDim = new Dimension(WINDOWX, (int) (WINDOWY * .30));
        graphicPanel = new JPanel();
        graphicPanel.setPreferredSize(graphicDim);
        graphicPanel.setMinimumSize(graphicDim);
        graphicPanel.setMaximumSize(graphicDim);
        graphicPanel.setBorder(BorderFactory.createTitledBorder("Graphic"));
        

        stateCB = new JCheckBox();
        stateCB.setVisible(false);
        stateCB.addActionListener(this);
        
        breakCB = new JCheckBox();
        breakCB.setVisible(false);
        breakCB.addActionListener(this);

        infoPanel.setLayout(new BorderLayout(4, 8));//changed
        
        graphicPanel.setLayout(new BorderLayout(1,2));
        
        graphicLabel = new JLabel(image);
    
        
        infoLabel = new JLabel(); 
        infoLabel.setText("<html><pre><i>Click Add to make customers</i></pre></html>");
       
       // graphicLabel.setText("This is my graphic addded for Lab 1");//added for lab 1
        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(stateCB, BorderLayout.EAST);
        infoPanel.add(breakCB, BorderLayout.CENTER);
        graphicPanel.add(graphicLabel);
        graphicPanel.add(graphicLabel, BorderLayout.NORTH);
        graphicPanel.setBackground(Color.red);
        add(infoPanel);
        add(animationPanel);//animation panel added here FIXIT
//        
        //add(graphicPanel);
    }
    /**
     * updateInfoPanel() takes the given customer (or, for v3, Host) object and
     * changes the information panel to hold that person's info.
     *
     * @param person customer (or waiter) object
     */
    public void updateInfoPanel(Object person) {

        currentPerson = person;
        
        
        JTextField customerForm = new JTextField();
        
        System.out.println("BALL");
        
        if (person instanceof CustomerAgent) {
        	stateCB.setVisible(true);
        	breakCB.setVisible(false);
        	
            CustomerAgent customer = (CustomerAgent) person;
            stateCB.setText("Hungry?");
          //Should checkmark be there? 
            stateCB.setSelected(customer.getGui().isHungry());
          //Is customer hungry? Hack. Should ask customerGui
            stateCB.setEnabled(!customer.getGui().isHungry());
          // Hack. Should ask customerGui
            infoLabel.setText(
               "<html><pre>     Name: " + customer.getName() + " </pre></html>");
        }
        
        if (person instanceof WaiterAgent) {
        	stateCB.setVisible(false);
        	breakCB.setVisible(true);
        	
        	WaiterAgent waiter = (WaiterAgent) person;
        	breakCB.setText("Request Break?");
        	breakCB.setSelected(waiter.getWantToGoOnBreak());
        	breakCB.setEnabled(!waiter.getWantToGoOnBreak());
//        	 breakCB.setText("Hungry?");
        	infoLabel.setText( "<html><pre>     Name: " + waiter.getName() + " </pre></html>");
        
        	
        }
       // infoPanel.add(customerForm, BorderLayout.CENTER);
        infoPanel.validate();
    }
    /**
     * Action listener method that reacts to the checkbox being clicked;
     * If it's the customer's checkbox, it will make him hungry
     * For v3, it will propose a break for the waiter.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == stateCB) {
            if (currentPerson instanceof CustomerAgent) {
                CustomerAgent c = (CustomerAgent) currentPerson;
                c.getGui().setHungry(restPanel.customerWaitPosition);
                restPanel.incrementCustomerWaitPosition();
                if (restPanel.customerWaitPosition > 8) {
                	restPanel.setCustomerWaitPosition(1);
                }
                stateCB.setEnabled(false);
            }
        }
        
        if (e.getSource() == breakCB) {
        	if(currentPerson instanceof WaiterAgent) {
        		WaiterAgent w = (WaiterAgent) currentPerson;
        		w.msgSetBreakBit();
        		breakCB.setEnabled(false);
        	}
        }
    }
    
    public void showCB() {
    	stateCB.setVisible(true);
    }
    /**
     * Message sent from a customer gui to enable that customer's
     * "I'm hungry" checkbox.
     *
     * @param c reference to the customer
     */
    public void setCustomerEnabled(CustomerAgent c) {
        if (currentPerson instanceof CustomerAgent) {
            CustomerAgent cust = (CustomerAgent) currentPerson;
            if (c.equals(cust)) {
                stateCB.setEnabled(true);
                stateCB.setSelected(false);
            }
        }
    }
    /**
     * Main routine to get gui started
     */
    public static void main(String[] args) {
        RestaurantGui gui = new RestaurantGui();
        gui.setTitle("Jack's Food on a Stick");
        gui.setVisible(true);
        gui.setResizable(false);
        gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
