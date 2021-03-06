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
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.JADEAgentManagement.KillAgent;
import jade.lang.acl.ACLMessage;



//This class will receive number of agents in a message which he has to create
public class AgentsCreator extends Agent {
	//Array where all Agents are stored
	private static AgentController[] Instances;
	private static int numberOfAgents = 0;
	public CondVar startUpLatch = new CondVar();
	
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
	        
	        if(msg != null) {
	        	System.out.println("I received messaeg");
	            Message_Performative = msg.getPerformative(msg.getPerformative());
	            Message_Content = msg.getContent();
	            SenderName = msg.getSender().getLocalName();
	            
	            ////////////////////////////////////////////////////////////////////////////////////////////////////
	            //Receive a number
	            if ( Message_Content.equals("die") )
	            {
	            	for (int i=0; i<Instances.length; ++i)
	            	{
	            		try {
							Instances[i].kill();
						} catch (StaleProxyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	}
	            }
	            
	            ////////////////////////////////////////////////////////////////////////////////////////////////////
	            //if message contains create and number create number of agents
	            if ( Message_Content.contains("Create") )  //create:numberOfAgents
	            {
	            	String[] spliter = Message_Content.split(":");
	            	//Create number of agents
	            	Instances = new AgentController[Integer.parseInt(spliter[1])];
	            	Runtime rt = Runtime.instance();
	            	ProfileImpl pContainer1 = new ProfileImpl(null, 1099, null);
	              	System.out.println("Launching the agent container ..."+pContainer1);
	              	pContainer1.setParameter(Profile.CONTAINER_NAME,"SmallEvilAgents");
	              	AgentContainer cont1 = rt.createAgentContainer(pContainer1);
	              	System.out.println("Launching the agent container after ..."+pContainer1);

	              
	                numberOfAgents = Integer.parseInt(spliter[1]);
	        
	            	for ( int i=0; i<Integer.parseInt(spliter[1]); ++i )
	            	{
	            		try {
							Instances[i] = cont1.createNewAgent("AS"+i, AgentSmith.class.getName(), new Object[] { startUpLatch });
							//Instances[i].start();
						} catch (StaleProxyException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	            	}
	            	
	            	 /* try {
	          			startUpLatch.waitOn();
	          			
	          		      }
	          		      catch(InterruptedException ie) {
	          			ie.printStackTrace();
	          		      }   
	          		    catch(Exception e) {
	          		      e.printStackTrace();
	          		    }  */         	
	            }
	            ////////////////////////////////////////////////////////////////////////////////////////////////////
	            //if message attack received then start attacking server NEO
	            if ( Message_Content.contains("Attack") ) //String container Attack:HostAddress:Port
	            {
	            	System.out.println("I received msg to attack");
	            	//String[] spliter = Message_Content.split(":");
	            	for ( int i=0; i<Instances.length; ++i )
	            	{
	            		//Before starting the attack 
	            		//startUpLatch.signal();
	            		//Start Attacking the Server
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
	 
	 public class SendMessageAgentParam extends OneShotBehaviour{
		 private String host = new String();
		 private int port = 0;
		 
		 public SendMessageAgentParam(String host,int port){
		 		this.host = host;
		 		this.port = port;
		 	}

			@Override
			public void action() {
				
				
			}
		 
	 }
}
