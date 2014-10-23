
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;



import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.*;
import jade.domain.FIPAAgentManagement.*;
import jade.lang.acl.*;
import jade.content.*;
import jade.content.lang.*;
import jade.content.lang.sl.*;
import jade.content.onto.*;
import jade.content.onto.basic.*;
import jade.util.leap.*;
import jade.gui.*;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.DFService;
import jade.domain.FIPAException;

public class AgentSmith extends Agent {
	long t0 = System.currentTimeMillis();
	private String IPAddress = "internal-MatrixLB-1452087530.eu-west-1.elb.amazonaws.com";
	private int port = 2222;
	private Socket clientSocket = null;
	private int interval = 1000;
    public Behaviour loop;
    
    
@Override
public void setup() {

	  /** Registration with the DF */
    DFAgentDescription dfd = new DFAgentDescription();
    ServiceDescription sd = new ServiceDescription();
    sd.setType("AgentSmith");
    sd.setName(getName());
    sd.setOwnership("AgentSmithMachines");
    sd.addOntologies("AttackerAgent");
    dfd.setName(getAID());
    dfd.addServices(sd);
    try {
    DFService.register(this,dfd);
    } catch (FIPAException e) {
    System.err.println(getLocalName()+" registration with DF unsucceeded. Reason: "+e.getMessage());
    doDelete();
    }
	
    /*Ticker function implementation*/
	loop = new TickerBehaviour( this, 10000 )
	{
                    protected void onTick() {
                    	 try {
               		      clientSocket = new Socket(IPAddress, port);
               		    } catch (UnknownHostException e) {
               		      System.err.println("Don't know about host " + IPAddress);
               		    } catch (IOException e) {
               		      //System.err.println("Couldn't get I/O for the connection to the host"
               		         // + port);
               		    }
                    }
	};
	 addBehaviour( loop );
	 ReceiveMessage rm = new ReceiveMessage();
     addBehaviour(rm);
}

//Receives message Mostly to kill him self
public class ReceiveMessage extends CyclicBehaviour {

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
            
            if ( Message_Content.equals("die") )
            	doDelete();
            
            //If attack message received configure host and port address
            if ( Message_Content.contains("Attack"))
            {
            	String[] split = Message_Content.split(":");
            	setIpAndPort(split[1], Integer.parseInt(split[2]),Integer.parseInt(split[3]));
            	
            	//Place for the looper group
            }    
            //Inform Server that we are done!!! // after fibonacci is done agent smith is will die
        }
    }
}

//Settters to attack Server
public void setIpAndPort(String ip,int port, int interval)
{
	this.IPAddress = ip;
	this.port = port;
	this.interval = interval;
}

}

