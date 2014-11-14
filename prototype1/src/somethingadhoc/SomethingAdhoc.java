package somethingadhoc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SomethingAdhoc {
    public static Thread t1,t2;
    public static Scanner in;
    public static AdhocClient client;
    public static AdhocAP ap;
    
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
            
        in = new Scanner(System.in);
        String command = "";
        String mode = "";
        while(!command.equals("exit")){
            System.out.println("-----------------------------------");
            System.out.println("--    Something Ad-Hoc console   --");
            System.out.println("-----------------------------------");
            switch(mode){
                case "":
                    System.out.println("Commands: mode, exit");
                    break;
                case "1":
                    System.out.println("-- Mode: "+mode+"                  --");
                    System.out.println("Commands: mode, exit");
                    break;
                case "2":
                    System.out.println("-- Mode: "+mode+"                  --");
                    System.out.println("Commands: mode, scan, send, exit");
                    break;
            }
            
            System.out.println("-----------------------------------");
            System.out.print("dummy@localhost: ~/ (enter mode) ");
            command = in.nextLine();
            switch(command){
                case "mode":
                    System.out.println("-----------------------------------");
                    System.out.println("-- Type: 1       for  AP Mode     -");
                    System.out.println("-- Type: 2       for  Sender Mode -");
                    System.out.println("-----------------------------------");
                    System.out.print("dummy@localhost: ~/mode/ ");
                    mode = in.nextLine();
                    switch(command){
                        case "1":
                            mode = "Soft Access Point";
                            if(t2 != null && t2.isAlive()){
                                t2.stop();
                            }
                            SomethingAdhoc.modeAP();
                            break;
                        case "2":
                            mode = "Sender";
                            if(t1 != null && t1.isAlive()){
                                t1.stop();
                            }
                            SomethingAdhoc.SenderMode();
                            break;
                    } // end switch in mode sub menu
                    break;
                case "scan":
                        SomethingAdhoc.SenderMode();
                        System.out.println("Scanning...");
                        client.showAdhocList();
                        client.refreshAdhocList();
                        Thread.sleep(1);
                        break;
                case "send":
                        // TODO: later
                        SomethingAdhoc.SenderMode();
                        System.out.println("Send!s");
                        break;
            } // end switch in main menu
            
        }
        
        if(t2 != null && t2.isAlive()){
            //t2.stop();
            t2.interrupt();
        }
        if(t1 != null && t1.isAlive()){
            //t1.stop();
            t1.interrupt();
        }
    }
    public static void modeAP(){
        // 1. do AP stuffs
        ap = new AdhocAP("wlan0", "Linux");
        int setupAdhocStatus = ap.setupAdhoc(); // random ssid
        // 2. do socket stuffs
        if(setupAdhocStatus == 0){
            
            // 3. thread 1: server socket stuffs
            t1 = new Thread(new Runnable() {

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
                        System.err.println("Fetal Error: "+ex.getMessage());
                    } catch (IOException ex) {
                        System.err.println("Fetal Error: "+ex.getMessage());
                    } 
                }
            });
            
            System.exit(0);
        }
    }
    public static void SenderMode(){
        client = new AdhocClient("wlan0", "Linux");
        client.refreshAdhocList();
        // 4. thread 2: user input sutffs for interrupt thread1 and switch to client mode
        t2 = new Thread(new Runnable() {
            
            public void run() {
                // 1. input target name
                // 2. input data
                // 3. interrupt thread 1
                // 4. start new thread for client
                // 5. after client jobs finished, start thread 1 again
                
            }
        });
        t2.start();
        // 5. add a loop condition for 3.-4.
    }
}
