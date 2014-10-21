import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.text.Caret;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.omg.CORBA.portable.OutputStream;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Location;
import jade.wrapper.*;
import jade.core.Agent;


public class theArchitect  {

	public static void main(String[] args) {
	//GUI components column 1
	final JTextField AgentName = new JTextField("Enter host address");
	final JTextField HostAddress = new JTextField("Enter host port");
	JLabel titleAgent = new JLabel("The Architect");
	final JTextField AgentNumber = new JTextField("Enter agents number");
	final JTextField CorrdinateTime = new JTextField("Corrdinate agents attack");
    JButton AttackButton = new JButton("Attack");
    JButton StopButton = new JButton("Stop");
	 
	        ///////////////////////////////////////////////////////////////////////////////////////////////////////
	        ///////////////////////////////////////////////////////////////////////////////////////////////////////
	        //GUI code
		    JFrame frame = new JFrame();
		    JPanel panel = new JPanel();
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    GroupLayout layout = new GroupLayout(panel);
		    panel.setLayout(layout);
		    
		    // Turn on automatically adding gaps between components
		    layout.setAutoCreateGaps(true);

		    // Turn on automatically creating gaps between components that touch
		    // the edge of the container and the container.
		    layout.setAutoCreateContainerGaps(true);	    
		    titleAgent.setFont(new Font("Serif", Font.PLAIN, 36));

		    GroupLayout.SequentialGroup leftToRight_V = layout.createSequentialGroup();
		    leftToRight_V.addGroup(layout.createParallelGroup()
		    		.addComponent(AgentName)
		    		.addComponent(titleAgent)
		    		.addComponent(AgentNumber)
		    		.addComponent(AttackButton));
		    leftToRight_V.addGroup(layout.createParallelGroup()
		    		.addComponent(HostAddress)
		    		.addComponent(CorrdinateTime)
		    		.addComponent(StopButton)
		    		);
		    layout.setHorizontalGroup(leftToRight_V);
		    
		
		    GroupLayout.SequentialGroup leftToRight_G = layout.createSequentialGroup();
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(titleAgent));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(AgentName) .addComponent(HostAddress));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(AgentNumber) .addComponent(CorrdinateTime));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(AttackButton) .addComponent(StopButton));
		    layout.setVerticalGroup(leftToRight_G);
		    
		    //Declare Send Button action Listener
		    AttackButton.addActionListener( new ActionListener()
		    {
		        public void actionPerformed(ActionEvent e)
		        {
		            //Create agents to attck
		        	prepareTheArmy(Integer.parseInt(AgentNumber.getText()), Integer.parseInt(CorrdinateTime.getText()));
		        }
		    });
		    
		    //Cancel Button Action Listener
		    StopButton.addActionListener( new ActionListener()
		    {
		        public void actionPerformed(ActionEvent e)
		        {
		            //stop all agents???
		        }
		    });
		    
		    //Remove on click data
		    AgentName.addMouseListener(new MouseAdapter() {
		    	  @Override
		    	  public void mouseClicked(MouseEvent e) {
		    		  AgentName.setText("");
		    	  }
		    	});
		    
		    //Remove on click data 
		    HostAddress.addMouseListener(new MouseAdapter() {
		    	  @Override
		    	  public void mouseClicked(MouseEvent e) {
		    		  HostAddress.setText("");
		    	  }
		    	});
		    
		    AgentNumber.addMouseListener(new MouseAdapter() {
		    	  @Override
		    	  public void mouseClicked(MouseEvent e) {
		    		  AgentNumber.setText("");
		    	  }
		    	});
		    
		    CorrdinateTime.addMouseListener(new MouseAdapter() {
		    	  @Override
		    	  public void mouseClicked(MouseEvent e) {
		    		  CorrdinateTime.setText("");
		    	  }
		    	});
		    
		    frame.add(panel);
		    frame.pack();
		    frame.setVisible(true);            
		  } 

	 
	 //function which will start the attack
	 static void prepareTheArmy(int numberOfAgents, int interval)
	 {
		 
		 try {
		      // Get hold of JADE runtime
		      Runtime rt = Runtime.instance();

		      // Exit the JVM when there are no more containers around
		      //rt.setCloseVM(true);


		      // set now the default Profile to start a container "Agent_Platform_Container"
		      	ProfileImpl pContainer1 = new ProfileImpl(null, 1099, null);
		      	System.out.println("Launching the agent container ..."+pContainer1);
			    pContainer1.setParameter(pContainer1.CONTAINER_NAME,"Agent_Platform_Container");
		      	AgentContainer cont1 =  rt.createAgentContainer(pContainer1);
		      	System.out.println("Launching the agent container after ..."+pContainer1);

		      	CoordinateAttack startUpLatch = new CoordinateAttack();
		        //Create some delay here to give time for the container to be create before creating the agents
		      	for ( int i=0; i<numberOfAgents; i++ )
		      	{
		      		AgentController Instance =cont1.createNewAgent("AS"+i, AgentSmith.class.getName(), new Object[] { startUpLatch }); 
		      		Instance.start();
		      	}

		        try {
			startUpLatch.waitOn();
		      }
		      catch(InterruptedException ie) {
			ie.printStackTrace();
		      }

		    }
		    catch(Exception e) {
		      e.printStackTrace();
		    }
	 }



 public static class CoordinateAttack { 
	 private boolean value = false;

		public void run() {
			try {
				waitOn();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		synchronized void waitOn() throws InterruptedException {
			while (!value) {
				wait();
			}
		}

		synchronized void signal() {
			value = true;
			notifyAll();
		}	 
}

}

