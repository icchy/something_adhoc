package somethingadhoc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ServerSocketThread extends Thread{
    // uses for terminate thread
    //private final AtomicBoolean running = new AtomicBoolean(true);
    private volatile boolean running;
    
    public static ServerSocket serverSocket = null;
    public static Socket clientSocket = null;
    public static ServerProcess serverProcess = null;
        
    private final String apIPAddress;
    private final int serverPort;
    
    
            
    public ServerSocketThread(String apIPAddress, int serverPort){
        this.apIPAddress = apIPAddress;
        this.serverPort = serverPort;
        //running.set(true);
        running = true;
    }
    /*
        Packets should be in form ofc tree structure?
        
        https://stackoverflow.com/questions/8480284/storing-a-tree-structure-in-java
        Use Java's serialization mechanism for persisting your tree, 
        is the simplest solution, and use ObjectInputStream for reading / 
        ObjectOutputStream for writing the serialized data from/to disk.
        
        Example of storing tree like structure in Java
        http://www.sourcecodesworld.com/articles/java/java-data-structures/Reading_and_Writing_Trees.asp
    */
    @Override
    public void run() {
        

        try {
            /*
            The maximum queue length for incoming connection indications 
            (a request to connect) is set to the backlog parameter. 
            If a connection indication arrives when the queue is full, 
            the connection is refused.
            */
            int backlog = 10;
            InetAddress inetAddr = InetAddress.getByName(apIPAddress);
            // 1. open server socket
            
            serverSocket = new ServerSocket(serverPort, backlog, inetAddr);
   
            // 2. wait for new connection
            //while(running.get()){ 
            while(running){
                
                // 3. accpet incoming connection (from client)
                clientSocket = serverSocket.accept();
                // 4. get client info
                String clientIP = clientSocket.getInetAddress().getHostAddress();
                int clientSrcPort = clientSocket.getPort();
                String clientName = clientIP+":"+clientSrcPort;
                System.out.println("Debug: Aceept new connection from "+clientName);
                
                // 5. starts server socket
                serverProcess = new ServerProcess(clientSocket);
                serverProcess.setName(clientName);
                serverProcess.start();
            }
            //System.out.println("Debug: loop serversocket is terminaledsss"); // this is unreachable
            
        } catch (IOException ex) {
            //System.out.println("Whoosp~ something wrong but it's okay");
        } finally{
            System.out.println("ServerProcess is terminated!!");
        }
    }
    
    // thread safe termination
    public void stopServerSocket(){
        //running.set(false); // this is not work :|?, quick-n-dirty fix is below
        //System.out.println("Debug: stopServerSocket()");
        this.running= false;
        if(serverSocket.isBound()){
            try {
                serverSocket.close();
            } catch (IOException ex) {
                //System.out.println("Whoosp~ something wrong but it's okay");
            }
                        // 6. when server socket is about to close, then close the connection too
             if(serverProcess instanceof ServerProcess){
                 serverProcess.terminateServer();
             }else{
                 //System.out.println("Debug: Server Process is not initial?");
             }
             if(serverSocket instanceof ServerSocket){
                 try {
                     serverSocket.close();
                 } catch (IOException ex) {
                     //System.err.println("Error: closing serverSocket");
                 }
             }else{
                 //System.out.println("Debug: Server Socket is not initial?");
             }

             // prevent null pointer exception
             if(clientSocket instanceof Socket){
                 try {
                     clientSocket.close();
                 } catch (IOException ex) {
                     //System.err.println("Error: closing clientSocket");
                 }
             }else{
                 //System.out.println("Debug: Client Socket is not initial?");
             }
        }
        
    }
}
