package somethingadhoc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SomethingAdhoc {

    public static void main(String[] args) throws UnknownHostException, IOException {
        
        // 1. do AP stuffs
        AdhocAP ap = new AdhocAP("wlan0", "Linux");
        int setupAdhocStatus = ap.setupAdhoc(); // random ssid
        // 2. do socket stuffs
        if(setupAdhocStatus == 0){
            
            // 3. thread 1: server socket stuffs
            Thread t1 = new Thread(new Runnable() {

                public void run() {
                    try {
                        int port = 13337;
                        InetAddress serverAddr = InetAddress.getByName(ap.apIPAddress);
                        ServerSocket serverSocket = new ServerSocket(port, 100, serverAddr);
                        Socket clientSocket;
                        while(true){ // infinity loop for new connection
                            clientSocket = serverSocket.accept();
                            // debug
                            String clientIP = clientSocket.getInetAddress().getHostAddress();
                            int srcPort = clientSocket.getPort();
                            String clientName = clientIP+":"+srcPort;
                            System.out.println("Debug: Aceept new connection from "+clientName);
                            //
                            ServerProcess server = new ServerProcess(clientSocket);
                            server.setName(clientName);
                            server.start();
                        }
                    } catch (UnknownHostException ex) {
                        //
                    } catch (IOException ex) {
                        //
                    } 
                }
            });
            
            t1.start();
            
            // 4. thread 2: user input sutffs for interrupt thread1 and switch to client mode
            Thread t2 = new Thread(new Runnable() {

                public void run() {
                    while(true){
                        Scanner in = new Scanner(System.in);
                        System.out.print("Switch Mode: ");
                        String input = in.nextLine();
                        if(input.equals("client")){
                            // 1. input target name
                            // 2. input data
                            // 3. interrupt thread 1
                            // 4. start new thread for client
                            // 5. after client jobs finished, start thread 1 again
                            System.out.println("hoge hoge");
                        }
                    }
                }
            });
            t2.start();
            // 5. add a loop condition for 3.-4.
        }
        
        
        
    }

    
}
