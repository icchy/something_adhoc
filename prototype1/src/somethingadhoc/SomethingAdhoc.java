package somethingadhoc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JOptionPane;

public class SomethingAdhoc {
    public static ServerSocketThread t1;
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
                    if(ap instanceof AdhocAP){
                        // 1. down AP (remove essid + set to managed mode)
                        ap.downAP();
                        ap = null;
                        apOn = false;
                        System.out.println("Shutdown AP...");
                        Thread.sleep(5000);
                    }
                    if(t1 instanceof ServerSocketThread){
                        // 2. down socket server thread
                        System.out.println("Closing the Server Socket..");
                        t1.stopServerSocket();
                    }
                    mode = "";
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
                client = null;
                System.out.println("-- Mode: AP Mode              --");
                System.out.println("Commands: mode, turnoff, exit");
                if(!apOn){
                    SomethingAdhoc.modeAP();
                    apOn = true;
                }
                break;
            case "2":
                ap = null;
                System.out.println("-- Mode: Sender Mode          --");
                System.out.println("Commands: mode, scan, send, exit");
                SomethingAdhoc.modeSender();
                break;
            default:
                System.out.println("Welcome! Commands: mode, exit");
                break;
        }
        System.out.println("-----------------------------------");
        System.out.print("dummy@localhost: ~/");
    }
    public static void modeAP(){
        
        // 1. do AP setup stuffs
        ap = new AdhocAP(wifiInf, "Linux");
        int setupAdhocStatus = ap.setupAdhoc(); // random ssid
        // 2. do socket server stuffs
        if(setupAdhocStatus == 0){
            
            // 3. thread 1: server socket stuffs
            /*
            Note: we needs thread here because
            server socket must running while user can interact with interface
            at the same time
            */
            System.out.println("Starting Server Socket...");
            t1 = new ServerSocketThread(ap.apIPAddress,13337);
            t1.start();
        }else{
            System.err.println("Error: Failed to setup Ad-Hoc & Server Socket");
        }
    }
    public static void modeSender(){
        // 1. do client setup stuffs
        client = new AdhocClient(wifiInf, "Linux");
        // 2. get list of adhoc AP
        client.refreshAdhocList();
    }
}
