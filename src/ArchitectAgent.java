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

import jade.core.AID;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Location;
import jade.util.leap.ArrayList;
import jade.wrapper.*;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.lang.acl.ACLMessage;


public class ArchitectAgent extends Agent {
	
		private static AgentController[] Instances;
		
		//GUI components column 1
		final JTextField HostAddress = new JTextField("Enter host address");
		final JTextField PortAddress = new JTextField("Enter host port");
		JLabel titleAgent = new JLabel("The Architect");
		final JTextField AgentNumber = new JTextField("Enter agents number");
		final JTextField CorrdinateTime = new JTextField("Corrdinate agents attack");
		JButton CreateAgents = new JButton("Create Agent(s)");
	    JButton AttackButton = new JButton("Attack");
	    JButton StopButton = new JButton("Kill All");
	    JButton ExitButton = new JButton("Exit");
	    JLabel HostLabel = new JLabel("Enter Host Address");
	    JLabel PortLabel = new JLabel("Enter Port Number");
	    JLabel AgentNoLabel = new JLabel("Enter Number of Agent(s)");
	    JLabel TimeIntervalLabel = new JLabel("Enter Time Interval");
	    JTextArea MessageMonitoring = new JTextArea(20,40);

	public void setup() {
	
			final List RemoteHost = new List();
		
	        ///////////////////////////////////////////////////////////////////////////////////////////////////////
	        ///////////////////////////////////////////////////////////////////////////////////////////////////////
	        //GUI code
		    JFrame frame = new JFrame();
		    frame.setTitle("Matrix");
		    JPanel panel = new JPanel();
		    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		    GroupLayout layout = new GroupLayout(panel);
		    
		    panel.setLayout(layout);
		    
		    //Create Second JFrame for the monitoring
		    JFrame monitoringFrame = new JFrame();
		    monitoringFrame.setTitle("Message Monitoring Terminal");
		    JPanel monitoringPanel = new JPanel();
		    monitoringPanel.setPreferredSize(new Dimension(20,40));
		    monitoringPanel.add(MessageMonitoring);
		    monitoringFrame.add(monitoringPanel);
		   
		    
		    // Turn on automatically adding gaps between components
		    layout.setAutoCreateGaps(true);

		    // Turn on automatically creating gaps between components that touch
		    // the edge of the container and the container.
		    layout.setAutoCreateContainerGaps(true);	    
		    titleAgent.setFont(new Font("Serif", Font.PLAIN, 36));

		    GroupLayout.SequentialGroup leftToRight_V = layout.createSequentialGroup();
		    leftToRight_V.addGroup(layout.createParallelGroup()
		    		.addComponent(titleAgent)
		    		.addComponent(HostLabel)
		    		.addComponent(HostAddress)
		    		.addComponent(AgentNoLabel)
		    		.addComponent(AgentNumber)
		    		.addComponent(CreateAgents)
		    		.addComponent(AttackButton));
		    leftToRight_V.addGroup(layout.createParallelGroup()
		    		.addComponent(PortLabel)
		    		.addComponent(PortAddress)
		    		.addComponent(TimeIntervalLabel)
		    		.addComponent(CorrdinateTime)
		    		.addComponent(StopButton)
		    		.addComponent(ExitButton)
		    		);
		    layout.setHorizontalGroup(leftToRight_V);
		    
		    GroupLayout.SequentialGroup leftToRight_G = layout.createSequentialGroup();
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(titleAgent));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(HostLabel) .addComponent(PortLabel));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(HostAddress) .addComponent(PortAddress));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(AgentNoLabel) .addComponent(TimeIntervalLabel));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(AgentNumber) .addComponent(CorrdinateTime));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(CreateAgents) .addComponent(StopButton));
		    leftToRight_G.addGroup(layout.createParallelGroup(Alignment.BASELINE).
		    		addComponent(AttackButton) .addComponent(ExitButton));
		    layout.setVerticalGroup(leftToRight_G);
		    
		    //Declare Send Button action Listener
		    AttackButton.addActionListener( new ActionListener()
		    {
		        public void actionPerformed(ActionEvent e)
		        {
		            //Create agents to attack
		        	//prepareTheArmy(Integer.parseInt(AgentNumber.getText()), Integer.parseInt(CorrdinateTime.getText()));
		        	Thread Attack = new Thread(){
		        		public void run(){
		        			try {
								LaunchAttack(Integer.parseInt(AgentNumber.getText()));
							} catch (NumberFormatException
									| StaleProxyException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
		        		}
		        	};
		        	Attack.start();
		        }
		    });

		    //Button which terminates the agent(s) attack 
		    StopButton.addActionListener( new ActionListener() 
		    {		
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					SendMessage LU = new SendMessage();
			        addBehaviour(LU);
			        MessageMonitoring.append("Killing all agents...please wait...\n");
				}
			});
		    
		    ExitButton.addActionListener( new ActionListener()
		    {			
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					Thread printMsg = new Thread(){
						public void run(){
							MessageMonitoring.append("Exiting from the system...\n");
						}
					};
					printMsg.start();
					
					try {
					    Thread.sleep(4000);
					} catch(InterruptedException ex) {
					    Thread.currentThread().interrupt();
					}
					System.exit(1);
				}
			});
		    
		    CreateAgents.addActionListener( new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					Thread Create = new Thread(){
		        		public void run(){
		        			prepareTheArmy(Integer.parseInt(AgentNumber.getText()), Integer.parseInt(CorrdinateTime.getText())); 
		        			//When calling the the function to prepare army Store the values of host address and port
		        			RemoteHost.add(HostAddress.getText()+":"+PortAddress.getText());
		        			MessageMonitoring.append(AgentNumber.getText()+" Agents created");
		        		}
		        	};
		        	Create.start();
				}
			});
		    
		    //Remove on click data
		    HostAddress.addMouseListener(new MouseAdapter() {
		    	  @Override
		    	  public void mouseClicked(MouseEvent e) {
		    		  HostAddress.setText("");
		    	  }
		    	});
		    
		    //Remove on click data 
		    PortAddress.addMouseListener(new MouseAdapter() {
		    	  @Override
		    	  public void mouseClicked(MouseEvent e) {
		    		  PortAddress.setText("");
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
		    
		    MessageMonitoring.setText("This is the message monitoring terminal, all messages will be displayed here\n");
		    frame.add(panel);
		    frame.pack();
		    frame.setVisible(true);     
		    monitoringFrame.pack();
		    monitoringFrame.setVisible(true);
		   
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
		      	Instances = new AgentController[numberOfAgents];
		      	for ( int i=0; i<numberOfAgents; i++ )
		      		//Add in the array the instances
		      		Instances[i] = cont1.createNewAgent("AS"+i, AgentSmith.class.getName(), new Object[] { startUpLatch });

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
	 
	 //functino which luanch the attck
	 public static void LaunchAttack(int numberOfAgents) throws StaleProxyException
	 {
			for ( int i=0; i<numberOfAgents; i++ )
					Instances[i].start();
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
 
 public class SendMessage extends OneShotBehaviour{

		@Override
		public void action() {
			// TODO Auto-generated method stub
				
			for ( int i=0; i<Integer.parseInt(AgentNumber.getText()); ++i )	
			{
				//For loop with all Agents
				String nameAgent = new String();
				try {
					nameAgent = Instances[i].getName();
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST); 
				AID address = new AID(nameAgent,AID.ISGUID);
				System.out.println(nameAgent);
	            //AID address = new AID(AgentName.getText().toString(),AID.ISGUID);
	            address.addAddresses("localhost");
	            msg.addReceiver(address);
	            msg.setSender(address);
	            msg.setLanguage("English");
	            msg.setContent("die"); //This string symbolize the agents death
	            send(msg);
			}
		}	 
	 }
 
 //Class where we keep hosts information so the Architect knows where to connect
 public class HostsInformation{
	 private String hostAddress = new String();
	 private int postNumber;	 
	 public HostsInformation(String a, int b) {
		// TODO Auto-generated constructor stub
		 this.hostAddress = a;
		 this.postNumber = b;
	}
 }
}

