package somethingadhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ModeSenderThread extends Thread{
    int portNumber;
    String destinationIP;
    
    // The client socket
    private static Socket clientSocket;
    // The output stream
    private static PrintStream os;
    // The input stream
    private static BufferedReader is;

    private static BufferedReader inputLine;
    private static boolean closed;
    
    
    String message;
    
    /*
    
    Constructor should support 3 mode as being passed by Main()
    1. destination is within the neighbor, send it directly
    2. send data+RTP to known route (in cached route file)
    3. send routing request packet neighbors to construct routing
    
    */
    public ModeSenderThread(String message){
        this(message,"");
    }
    public ModeSenderThread(String message, String route){
        this.message = message;
        portNumber = 13337;
        destinationIP = "192.168.1.1";
    }
    @Override
    public void run(){
        /*
         * Open a socket on a given host and port. Open input and output
         * streams.
         */
        try {
                clientSocket = new Socket(destinationIP, portNumber);
                //inputLine = new BufferedReader(new InputStreamReader(System.in));
                os = new PrintStream(clientSocket.getOutputStream());
                is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + destinationIP);
        } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to the host "+ destinationIP);
        }
        /*
        
        @TODO:
        1. Insteads of sending raw string, it should pack strings with Packet Type info
        as specified in the Routing specification
        
        2. We may need one more layer to manage this task and pass it back to here after processed
        
        
        */
        if (clientSocket != null && os != null && is != null) {
                try {

                /* Create a thread to read from the server. */
                new Thread(new ClientProcess()).start();
                //while (!closed ) {
                        os.println(message.trim()); // send one time!
                //}
                /*
                 * Close the output stream, close the input stream, close the
                 * socket.
                 */
                os.close();
                is.close();
                clientSocket.close();
                } catch (IOException e) {
                        System.err.println("IOException:  " + e);
                }
        }


    }
}
