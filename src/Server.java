import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

/*
 * A chat server that delivers public and private messages.
 */
public class Server {

  // The server socket.
  private static ServerSocket serverSocket = null;
  // The clientThread socket.
  private static Socket clientThreadSocket = null;
  // The number of clients currently conneted
  private static int number = 0;

  // This chat server can accept up to maxclientThreadsCount clientThreads' connections.
  private static final int maxclientThreadsCount = 100000;
  private static final clientThread[] threads = new clientThread[maxclientThreadsCount];

  public static void main(String args[]) {

    // The default port number.
    int portNumber = 2222;
    if (args.length < 1) {
      System.out
          .println("Usage: java MultiThreadChatServer <portNumber>\n"
              + "Now using port number=" + portNumber);
    } else {
      portNumber = Integer.valueOf(args[0]).intValue();
    }

    /*
     * Open a server socket on the portNumber (default 2222). Note that we can
     * not choose a port less than 1023 if we are not privileged users (root).
     */
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    /*
     * Create a clientThread socket for each connection and pass it to a new clientThread
     * thread.
     */
    while (true) {
      try {
        clientThreadSocket = serverSocket.accept();
        int i = 0;
        for (i = 1; i <= maxclientThreadsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientThreadSocket, threads)).start();
            number++;
            System.out.println(i+" Clients Connected");
            break;
          }
        }
        if (i == maxclientThreadsCount) {
          PrintStream os = new PrintStream(clientThreadSocket.getOutputStream());
          System.out.println("Too busy man");
          os.println("Server too busy. Try later.");
          os.close();
          clientThreadSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}

class clientThread extends Thread {

	  private DataInputStream is = null;
	  private PrintStream os = null;
	  private Socket clientThreadSocket = null;
	  private final clientThread[] threads;
	  private int maxclientThreadsCount;

	  public clientThread(Socket clientThreadSocket, clientThread[] threads) {
	    this.clientThreadSocket = clientThreadSocket;
	    this.threads = threads;
	    maxclientThreadsCount = threads.length;
	  }

	  public void run() {
	    int maxclientThreadsCount = this.maxclientThreadsCount;
	    clientThread[] threads = this.threads;

	    try {
	      /*
	       * Create input and output streams for this clientThread.
	       */
	      is = new DataInputStream(clientThreadSocket.getInputStream());
	      os = new PrintStream(clientThreadSocket.getOutputStream());
	     /*while (true) {
	        String line = is.readLine();
	        if (line.startsWith("/quit")) {
	          break;
	        }
	      }*/
	      /*
	       * Clean up. Set the current thread variable to null so that a new clientThread
	       * could be accepted by the server.
	       */
	      
	      int k = fibonacci(100);
	      System.out.println("Fibonacci Done!!!");
	      for (int i = 0; i < maxclientThreadsCount; i++) {
	        if (threads[i] == this) {
	          threads[i] = null;
	        }
	      }

	      /*
	       * Close the output stream, close the input stream, close the socket.
	       */
	      is.close();
	      os.close();
	      clientThreadSocket.close();
	    } catch (IOException e) {
	    }
	  }
	  
	  public int fibonacci(int n)  {
		    if(n == 0)
		        return 0;
		    else if(n == 1)
		      return 1;
		   else
		      return fibonacci(n - 1) + fibonacci(n - 2);
		}
	}

