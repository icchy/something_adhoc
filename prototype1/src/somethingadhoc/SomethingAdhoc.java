package somethingadhoc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class SomethingAdhoc {
    public static Thread t1,t2;
    public static Scanner in;
    public static AdhocClient client;
    public static AdhocAP ap;
    public static String wifiInf;
    public static boolean apOn;
    
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
        
        in = new Scanner(System.in);
        
        System.out.println("Enter WiFi interface: ");
        wifiInf = in.nextLine();
        
        String command = "";
        String mode = "";
        
        while(!command.equals("exit")){
            printBanner(mode);
            command = in.nextLine();
            
            switch(command){
                
                case "mode":
                    System.out.println("-----------------------------------");
                    System.out.println("-- Type: 1       for  AP Mode     -");
                    System.out.println("-- Type: 2       for  Sender Mode -");
                    System.out.println("-----------------------------------");
                    System.out.print("dummy@localhost: ~/mode/ ");
                    mode = in.nextLine();
                    break;
                case "turnoff":
                    ap.downAP();
                    apOn = false;
                    mode = "";
                    System.out.println("Turning-of AP...");
                    Thread.sleep(10000);
                    break;
                case "scan":
                    System.out.println("[+] Scan !");
                    // 0. precondition
                    if(client==null || !mode.equals("2")){
                        System.err.println("Please switch to Sender Mode");
                        break;
                    }
                    // 1. refresh adhoc list (re-scan and add to list)
                    client.refreshAdhocList();
                    System.out.println("Scanning...");
                    // 2. waiting by delay for 1 second
                    Thread.sleep(1000);
                    // 3. show adhoc list in console
                    client.showAdhocList();
                    break;
                case "send":
                    System.out.println("[+] Send !");
                    // 0. precondition
                    if(client==null || !mode.equals("2")){
                        System.err.println("Please switch to Sender Mode");
                        break;
                    }
                    // String relayName = client.adhocAvailable.get(0).ssid;
                    // 1. select destination node, at this rate.. just essid
                    String relayName = JOptionPane.showInputDialog(null, "Enter ESSID:");
                    System.out.println("Connecting to : "+relayName);
                    // 2. get/discover routing
                    // 3. connect to relays
                    int status = client.connectRelay(relayName);
                    System.out.println("Debug: connect status = "+status);
                    // 4. client socket connect to AP IP
                    
//                    t2 = new Thread(new Runnable() {
//
//                        public void run() {
                            // 1. input target name
                            // 2. input data
                            // 3. interrupt thread 1
                            // 4. start new thread for client
                            // 5. after client jobs finished, start thread 1 again
//                        }
//                    });
//                    t2.start();
     
                    break;
            }
            
        } 
        System.exit(0);
        
    }
    public static void printBanner(String mode){
        System.out.println("-----------------------------------");
        System.out.println("--    Something Ad-Hoc console   --");
        System.out.println("-----------------------------------");
        switch(mode){                
            case "1":
                System.out.println("-- Mode: AP Mode              --");
                System.out.println("Commands: mode, turnoff, exit");
                if(!apOn){
                    SomethingAdhoc.modeAP();
                    apOn = true;
                }
                break;
            case "2":
                System.out.println("-- Mode: Sender Mode          --");
                System.out.println("Commands: mode, scan, send, exit");
                SomethingAdhoc.SenderMode();
                break;
            default:
                System.out.println("Welcome! Commands: mode, exit");
                break;
        }
        System.out.println("-----------------------------------");
        System.out.print("dummy@localhost: ~/");
    }
    public static void modeAP(){
        
        // 1. do AP stuffs
        ap = new AdhocAP(wifiInf, "Linux");
        int setupAdhocStatus = ap.setupAdhoc(); // random ssid
        // 2. do socket stuffs
        if(setupAdhocStatus == 0){
            
            // 3. thread 1: server socket stuffs
            /*
            Note: we needs thread here because
            server socket must running while user can interact with interface
            at the same time
            */
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
            
            
        }
    }
    public static void SenderMode(){
        client = new AdhocClient(wifiInf, "Linux");
        client.refreshAdhocList();
    }
}
