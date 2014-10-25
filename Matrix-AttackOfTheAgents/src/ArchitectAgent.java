import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.text.StyleConstants;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import org.omg.CORBA.portable.OutputStream;
import org.w3c.dom.Document;
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
	    JButton AttackButton = new JButton("Attack Neo");
	    JButton StopButton = new JButton("Kill All");
	    JButton ExitButton = new JButton("Exit");
	    JLabel HostLabel = new JLabel("Enter Host Address");
	    JLabel PortLabel = new JLabel("Enter Port Number");
	    JLabel AgentNoLabel = new JLabel("Enter Number of Agent(s)");
	    JLabel TimeIntervalLabel = new JLabel("Enter Time Interval");
	    JTextPane MessageMonitoring = new JTextPane();
	    StyledDocument doc = MessageMonitoring.getStyledDocument();
	    StyleContext sc = StyleContext.getDefaultStyleContext();
	    AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,StyleConstants.Foreground, Color.red);
	   // textEditorDoc.setCharacterAttributes(offset, length, aset, true);
	    
	public void setup() {
	
			final ArrayList RemoteHost = new ArrayList();
		
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
		    
		    
		    ///////////////////////////////////////////////////////////////////////////////////////////////////////
	        ///////////////////////////////////////////////////////////////////////////////////////////////////////
		    //Declare and functionalities of the buttons
		    AttackButton.addActionListener( new ActionListener()
		    {
		        public void actionPerformed(ActionEvent e)
		        {
		            //Check for  the parameters before sending
		        	//Catch all errors
					if ( HostAddress.getText().equals(""))
					{
						try {
							doc.insertString(doc.getLength(), "Error : No inpout for Host Address please try againg\n", aset);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						//MessageMonitoring.append("Error : No inpout for Host Address please try againg\n");
						return;
					}
					
					//Catch all errors
					if ( PortAddress.getText().equals(""))
					{
						try {
							doc.insertString(doc.getLength(), "Error : No inpout for Port Address please try againg\n", aset);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						//MessageMonitoring.append("Error : No inpout for Host Address please try againg\n");
						return;
					}
		        	
		        	Thread Attack = new Thread(){
		        		public void run(){
		        				SendMessage PA = new SendMessage("Attack",RemoteHost);
			        			addBehaviour(PA);						
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
					SendMessage LU = new SendMessage("Die",RemoteHost);
			        addBehaviour(LU);
			        try {
						doc.insertString(doc.getLength(), "Killing all agents...please wait...\n", aset);
					} catch (BadLocationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			       // MessageMonitoring.appendTo("Killing all agents...please wait...\n");
				}
			});
		    
		    ExitButton.addActionListener( new ActionListener()
		    {			
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					Thread printMsg = new Thread(){
						public void run(){
							//MessageMonitoring.append("Exiting from the system...\n");
							try {
								doc.insertString(doc.getLength(), "Exiting from the system...\n", aset);
							} catch (BadLocationException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					};
					printMsg.start();
					System.exit(1);
				}
			});
		    
		    CreateAgents.addActionListener( new ActionListener() {				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					//Catch all errors
					if ( HostAddress.getText().equals(""))
					{
						try {
							doc.insertString(doc.getLength(), "Error : No inpout for Host Address please try againg\n", aset);
						} catch (BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						//MessageMonitoring.append("Error : No inpout for Host Address please try againg\n");
						return;
					}
					
					if ( AgentNumber.getText().equals(""))
					{
						try {
							doc.insertString(doc.getLength(), "Error : No inpout for Agents Number please try againg\n", aset);
						} catch (BadLocationException e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
						//MessageMonitoring.append("Error : No inpout for Agents Number please try againg\n");
						return;
					}
					
					
					Thread Create = new Thread(){
		        		public void run(){
		        			//prepareTheArmy(Integer.parseInt(AgentNumber.getText()), Integer.parseInt(CorrdinateTime.getText())); 
		        			//When calling the the function to prepare army Store the values of host address and port
		        			SendMessage PA = new SendMessage("Create",RemoteHost);
		        			addBehaviour(PA);
		        			//Keep all Agent Information Host address where agents are located and agent name
		        			RemoteHost.add(HostAddress.getText());			
		        		}
		        	};
		        	Create.start();
		        	try {
						doc.insertString(doc.getLength(), AgentNumber.getText()+" Agents created\n", null);
					} catch (BadLocationException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
		        	//MessageMonitoring.append(AgentNumber.getText()+" Agents created\n");
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

 //Sends single message to the Agent Creator to start creating the agents
 public class SendMessage extends OneShotBehaviour{
	 
	 	private String command = new String();
	 	private ArrayList list = new ArrayList();
	 
	 	//SendMessage Constructor first param is the command Create, Attack, Die 
	 	// second is the list with the Host of the AgentCreators
	 	public SendMessage(String command,ArrayList list){
	 		this.command = command;
	 		this.list = list;
	 	}

		@Override
		public void action() {
			// TODO Auto-generated method stub
			//Generete Message Default message parameters
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST); 
				//Agent name be default is the AC = Agent Creator
				AID address = new AID();
				address.setName("AC@AgentsCreator");
	            address.addAddresses(HostAddress.getText().toString());
	            msg.addReceiver(address);
	            msg.setLanguage("English");
	            
	            //We create a "Create Message" when an AgentCreator recv the message it generates a number of agents
	            if ( command.contains("Create") )
	            {
	            	msg.setContent("Create:"+AgentNumber.getText()); 
	            	send(msg);
	            	System.out.println("****I Sent Message to::> AC *****"+"\n"+
	                         "The Content of My Message is::>"+ msg.getContent());
	            }
	            
	            //We create a "Attack Msg" when an AgentCreator recv the message it initiates attack to the server
	            if ( command.contains("Attack"))
	            {
	            	//For Send creator we are sending to all agents creators single msg
	            	//list is a ArrayList which contains all the host address this is 
	            	//how we know where to send messages
	            	for ( int i=0; i<list.size(); ++i )
	            	{	
	            		ACLMessage msgA = new ACLMessage(ACLMessage.REQUEST);
	            		AID addressAttack = new AID();
	    				addressAttack.setName("AC@AgentsCreator");
	    	            addressAttack.addAddresses(list.get(i).toString());	
	    	            msgA.addReceiver(addressAttack);
	    	            msgA.setLanguage("English");
	    	            msgA.setContent("Attack:"+HostAddress.getText()+":"+PortAddress.getText()+":"+CorrdinateTime.getText());
	    	            send(msgA);
	    	            System.out.println("****I Sent Message to::> AC *****"+"\n"+
	                            "The Content of My Message is::>"+ msgA.getContent());
	            	}
	            }
	            
	            //We create a "Die Msg" when an AgentCreator recv the message it kill all the instances of agents 
	            if ( command.equals("Die"))
	            {
	            	//Send to each AgentCreator message to kill all the instances of Agents Smith
	            	for ( int i=0; i<list.size(); ++i )
	            	{
	            		AID addressAttack = new AID();
	    				addressAttack.setName("AC@AgentsCreator");
	    	            addressAttack.addAddresses(list.get(i).toString());	  
	    	            msg.addReceiver(addressAttack);
	    	            msg.setContent("Die");
	    	            send(msg);
	    	            System.out.println("****I Sent Message to::> AC *****"+"\n"+
	                            "The Content of My Message is::>"+ msg.getContent());
	            	}
	            }	   
		}		
	 }	
}

