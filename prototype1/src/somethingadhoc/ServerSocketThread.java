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
    private final AtomicBoolean running = new AtomicBoolean(true);
    
    private final String apIPAddress;
    private final int serverPort;
    
    ServerSocket serverSocket;
    Socket clientSocket;
    ServerProcess serverProcess;
            
    public ServerSocketThread(String apIPAddress, int serverPort){
        this.apIPAddress = apIPAddress;
        this.serverPort = serverPort;
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
            while(running.get()){ 
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
            
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerSocketThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerSocketThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            System.out.println("ServerProcess is terminated!!");
        }
    }
    
    // thread safe termination
    public void stopServerSocket() throws IOException{
        running.set(false);
        // 6. when server socket is about to close, then close the connection too
        if(serverProcess instanceof ServerProcess){
            serverProcess.terminateServer();

            if(serverSocket instanceof ServerSocket){
                serverSocket.close();
            }

            // prevent null pointer exception
            if(clientSocket instanceof Socket){
                clientSocket.close();
            }

        }
    }
}
