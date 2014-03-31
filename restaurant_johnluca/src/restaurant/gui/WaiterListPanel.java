package restaurant.gui;

import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.gui.RestaurantGui;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Subpanel of restaurantPanel.
 * This holds the scroll panes for the customers and, later, for waiters
 */
public class WaiterListPanel extends JPanel implements ActionListener {

    public JScrollPane pane =
            new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    private JPanel view = new JPanel();
    private List<JButton> list = new ArrayList<JButton>();
    private JButton addPersonB = new JButton("Add Waiter");//button clicked to add customer
    private JTextField waiterForm = new JTextField(1);
    private RestaurantPanel restPanel;
    private String type;
    private JCheckBox breakCB;
    
    int waiterNum = 0;

    /**
     * Constructor for ListPanel.  Sets up all the gui
     *
     * @param rp   reference to the restaurant panel
     * @param type indicates if this is for customers or waiters
     */
    public WaiterListPanel(RestaurantPanel rp, String type) {
    	
    	waiterForm.setMaximumSize(new Dimension(200,200));
        restPanel = rp;
        this.type = type;

        setLayout(new BoxLayout((Container) this, BoxLayout.Y_AXIS));
        add(new JLabel("<html><pre> <u>" + type + "</u><br></pre></html>"));

        addPersonB.addActionListener(this);
        add(addPersonB);
        add(waiterForm);
//        add(startHungryLabel);
//        add(startHungryCb);

        view.setLayout(new BoxLayout((Container) view, BoxLayout.Y_AXIS));
        pane.setViewportView(view);
        add(pane);
    }

    /**
     * Method from the ActionListener interface.
     * Handles the event of the add button being pressed
     */
    public void actionPerformed(ActionEvent e) {
    	
        if (e.getSource() == addPersonB && !waiterForm.getText().equalsIgnoreCase("")) {
        	System.out.println("ADD WAITER");
        	// Chapter 2.19 describes showInputDialog()
        	//JTextField customerForm = new JTextField("NAME");
            addPerson(waiterForm.getText());//here is change needed for lab2
            waiterNum++;
        }
        if (waiterNum > 4) {
        	addPersonB.setEnabled(false);
        }
//        if (e.getSource() == breakCB) {
//        	restPanel.putWaiterOnBreak();
//        	breakCB.setEnabled(false);//disable checkbox, needs to be re enabled 
////        	System.out.println("HEre");
//        }
        else {
        	// Isn't the second for loop more beautiful?
            /*for (int i = 0; i < list.size(); i++) {
                JButton temp = list.get(i);*/
        	for (JButton temp:list){
                if (e.getSource() == temp) {
//                	System.out.println("KKKKKKKKK!!!!" + temp.getText());
//                	restPanel.putWaiterOnBreak(temp.getText());
//                	System.out.println("HELLLLLOOOOOO" + temp.getText());
                    restPanel.showInfo(type, temp.getText());
                }
            }
        }
    }

    /**
     * If the add button is pressed, this function creates
     * a spot for it in the scroll pane, and tells the restaurant panel
     * to add a new person.
     *
     * @param name name of new person
     */
    public void addPerson(String name) {
        if (name != null) {
            JButton button = new JButton(name);
            
            breakCB = new JCheckBox();
            breakCB.addActionListener(this);
            button.setBackground(Color.white);

            Dimension paneSize = pane.getSize();
            Dimension buttonSize = new Dimension(paneSize.width - 20,
                    (int) (paneSize.height / 7));
            button.setPreferredSize(buttonSize);
            button.setMinimumSize(buttonSize);
            button.setMaximumSize(buttonSize);
            button.addActionListener(this);
//            button.add(breakCB);
            list.add(button);
            view.add(button);
            restPanel.addWaiter(type, name);//puts waiter on list
            restPanel.showInfo(type, name);//puts hungry button on panel
            validate();
        }
    }
}
    
   
