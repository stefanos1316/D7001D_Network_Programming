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
import org.omg.CosNaming.IstringHelper;

import jade.core.AID;
import jade.core.ContainerID;
import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Location;
import jade.util.leap.ArrayList;
import jade.wrapper.*;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.AMSService;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.AMSAgentDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.domain.JADEAgentManagement.KillContainer;
import jade.lang.acl.ACLMessage;


//This class will receive number of agents in a message which he has to create
public class AgentsCreator extends Agent {
	
	//Array where all Agents are stored
	private static AgentController[] Instances;
	private static int numberOfAgents = 0;
	public CondVar startUpLatch = new CondVar();
	public 	AgentContainer cont1;
	public AgentContainer agent;
	ProfileImpl pContainer1;
	public String SenderNameAA = new String();
	public int currentNumberOfAgents = 0;
	
	protected void setup(){
		/** Registration with the DF */
	    DFAgentDescription dfd = new DFAgentDescription();
	    ServiceDescription sd = new ServiceDescription();
	    sd.setType("AgentCreator");
	    sd.setName(getName());
	    sd.setOwnership("AgentSmithMachines");
	    sd.addOntologies("CreateAgent");
	    dfd.setName(getAID());
	    dfd.addServices(sd);
	    try {
	    DFService.register(this,dfd);
	    } catch (FIPAException e) {
	    System.err.println(getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
	    doDelete();
	    }
	    
	    ReceiveMessage rm = new ReceiveMessage();
	    addBehaviour(rm);		
	}

	public class ReceiveMessage extends CyclicBehaviour{
		
		 // Variable to Hold the content of the received Message
	    private String Message_Performative;
	    private String Message_Content;
	    private String SenderName;

	    public void action() {
	        ACLMessage msg = receive();
	        
	        //When recv Msg do....
	        if(msg != null) {
	        	
	            Message_Performative = msg.getPerformative(msg.getPerformative());
	            Message_Content = msg.getContent();
	            SenderName = msg.getSender().getLocalName();
	            SenderNameAA = msg.getSender().getLocalName();
	            
	            ////////////////////////////////////////////////////////////////////////////////////////////////////
	            //if message attack received then start attacking server NEO
	            if ( Message_Content.contains("Attack") ) //String container Attack:HostAddress:Port
	            {
	            	//System.out.println("I received msg to attack");
	            	//String[] spliter = Message_Content.split(":");
	            	for ( int i=0; i<Instances.length; ++i )
	            	{
	            		//Start Attacking the Server
	            		try {
							Instances[i].start();
						} catch (StaleProxyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	}
	            }
	            
	            ////////////////////////////////////////////////////////////////////////////////////////////////////
	            //Receive Die Message
	            if ( Message_Content.equals("Die") )
	            {           	
	            	for (int i=0; i<Instances.length; ++i)
	            	{
						try {
							System.out.println("dsad"+Instances[i].getName());	
							Instances[i].kill();
						} catch (StaleProxyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 		            		
	            	}	            	
	            }
	            
	            ////////////////////////////////////////////////////////////////////////////////////////////////////
	            //if message contains create 
	            if ( Message_Content.contains("Create") )  //Message Content -> "Create:numberOfAgents"
	            {
	            	//Split
	            	String[] spliter = Message_Content.split(":");
	            	//Create number of agents
	            	Instances = new AgentController[Integer.parseInt(spliter[1])];
	            	
	            	//Create Runtime instance
	            	Runtime rt = Runtime.instance();
	            	
	            	//Create Profile Container for the agents
	            	pContainer1 = new ProfileImpl(null, 1099, null);
	              	System.out.println("Launching the agent container ..."+pContainer1);
	              	//Name of Container where my Agents are creates
	              	pContainer1.setParameter(Profile.CONTAINER_NAME,"SmallEvilAgents");
	              	cont1 = rt.createAgentContainer(pContainer1);
	              	System.out.println("Launching the agent container after ..."+pContainer1);
	                numberOfAgents = Integer.parseInt(spliter[1]);
	                Object[] args = new String[spliter.length-2];
	                System.out.println("Values are "+spliter[2]+" "+spliter[3]+" "+spliter[4]);
	                args[0] = spliter[2];
	                args[1] = spliter[3];
	                args[2] = spliter[4];
	                
	               // System.exit(1);
	        
	            	for ( int i=0; i<numberOfAgents; ++i )
	            	{
	            		try {
	            			//StartUpLatch is the var to make all the agents wait until they recv signal
							Instances[i] = cont1.createNewAgent("AS"+i, AgentSmith.class.getName(), args);
							//Instances[i].start();
						} catch (StaleProxyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	}
	            }   
	        }
	    }	
	}
	
	 public static class CondVar {
		    private boolean value = false;

		    synchronized void waitOn() throws InterruptedException {
		      while(!value) {
			wait();
		      }
		    }
		    synchronized void signal() {
		      value = true;
		      notifyAll();
		    }

		  } // End of CondVar class
	 
	 public class SendMessage extends OneShotBehaviour{
		 
		 private String receiver = new String();
		 private String Message_Content = new String();
		 
		 public SendMessage(String content) {
			// TODO Auto-generated constructor stub
			 this.Message_Content = content;
		}
		 
		 ACLMessage msg = new ACLMessage(ACLMessage.REQUEST); 
			//Agent name be default is the AC = Agent Creator
			AID address = new AID();
			@Override
			public void action() {
				// TODO Auto-generated method stub
				for ( int i=0; i<Instances.length; ++i)
				{
				 
				 try {
					address.setName(Instances[i].getName());
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        // address.addAddresses(HostAddress.getText().toString());
		         msg.addReceiver(address);
		         msg.setLanguage("English");
		         send(msg);
		         try {
		        	    Thread.sleep(2000);                 //1000 milliseconds is one second.
		        	} catch(InterruptedException ex) {
		        	    Thread.currentThread().interrupt();
		        	}
		         try {
					Instances[i].start();
				} catch (StaleProxyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
				
			}
			
	 }
	 
}
