package somethingadhoc;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class SomethingAdhoc {
    public static ServerSocketThread t1;
    public static ModeSenderThread t2;
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
        // open the program at the first time
        // turn into AP mode
        String mode = "1";
        
        while(!command.equals("exit")){
            printBanner(mode); // switch AP/Client occur inside printBanner
            command = in.nextLine();
            
            switch(command){
                case "ap_mode":
                    if(mode.equals("2")){
                        System.out.println("[+] Turning on Ad-hoc AP");
                        mode = "1";
                    }else{
                        System.err.println("Error: This command work only in Sender mode");
                    }
                    break;
                // change from AP mode to sender mode
                case "sender_mode":
                    if(mode.equals("1")){
                        System.out.println("[+] Turn off AP and switch to sender Mode");
                        mode = "2";
                    }else{
                        System.err.println("Error: This command work only in AP mode");
                    }
                    break;
                case "rescan":
                    // 0. precondition
                    if(client==null || mode.equals("2")){
                        System.out.println("[+] Scan !");
                        refreshAndPrintAdhocList();
                    }else{
                        System.err.println("Error: This command work only in Sender mode");
                    }
                    break;
                case "send":
                    if(mode.equals("2")){
                         System.out.println("[+] Send !");
                        // 0. precondition
                        if(client==null || !mode.equals("2")){
                            System.err.println("Please switch to Sender Mode");
                            break;
                        }
                        // String relayName = client.adhocAvailable.get(0).ssid;
                        // 1. select destination node, at this rate.. just essid
                        System.out.print("Enter Target Node (ex.senshin_BBB): ");
                        String targetNode = in.nextLine();

                        // @TODO:   the number at the end of node should not be entered
                        //          because it is incrementally number to prevent cache SSID
                        //          but we have to make connectAP understand it too
                        System.out.println("Target is : "+targetNode);
                        SomethingRoute route = SomethingRoute.init();

                        // 2. update neighbor into routing table & discover routing
                        
                        String routeRecord = route.getRoute(targetNode); // RRP + local neihjbor links

                        // 2.1 get Message to send
                        // @TODO:   this should provide dependency injection 
                        //          to support nother types of data eg. file/streaming
                        System.out.print("Enter Message: ");
                        String message = in.nextLine();

                        // 2.2 convert target Node into ESSID ?
                        // 2.3 get relay for next hop

                        //String relayName = route.getNextRelay(route);
                        // @TODO: implement getting next hop!
                        String relayName = targetNode; // no next hop yet!!
                        System.out.println("Connecting to : "+relayName);
                        // 3. connect to relay <--------------------------- using extracted info from SomethingRoute
                        int status = client.connectRelay(relayName);
                        System.out.println("Debug: connect status = "+status);
                        // 4. client socket connect to AP server socket
                        /*
                        Node: If next hop is not destination yet, 
                            then forward routing table to neighbors (except the forwarder/sender)
                        */

                        if(relayName.equals(targetNode)){
                            // 1. destination is within the neighbor, send it directly
                            t2 = new ModeSenderThread(message, 4);

                        }else if(routeRecord!=null){
                            // 2. send data+RTP to known route (in cached route file)
                            t2 = new ModeSenderThread(message, routeRecord);

                        }else{
                            // 3. send routing request packet neighbors to construct routing
                            t2 = new ModeSenderThread(routeRecord, 1);

                        }
                        t2.start();
                        // 5. client should receive ack. that confirm message reach server socket
                        // 6. fall back to AP mode
                        // 7. force close client socket?
                    }else{
                        System.err.println("Error: This command work only in Sender mode");
                    }
                    break;
                default:
                    System.err.println("Error: Invalid command line, it does not exists");
            }
            
        } 
        
        // 1. down AP (remove essid + set to managed mode)        
        SomethingAdhoc.turnoffAP();
        // @TODO 2. down Client (fallback to manage mode) before exit for client mode
        
        System.exit(0);
        
    }
    public static void printBanner(String mode) throws InterruptedException, IOException{
        //System.out.println("Debug: printBanner() is called");
        System.out.println("-----------------------------------");
        System.out.println("--    Something Ad-Hoc console   --");
        System.out.println("-----------------------------------");
        switch(mode){                
            case "1": // ap mode
                client = null;
                System.out.println("-- Mode: AP Mode              --");
                System.out.println("-- Status: idle               --");
                /*
                Status: 
                    1. idle = just broadcast ad-hoc
                    2. connected w/ request route = a client is connected to find route
                    3. connected w/ reply route = a client is connected to send route back to next hop
                    4. connected w/ send route = a client is connected to send route to this hop as last hop, prepare to send data
                    5. connected w/ send data = a client is connected to send data to this node
                    6. connected w/ forward data = a client is connected to this as relay (forward to next hop)
                */
                System.out.println("Commands: sender_mode, exit");
                if(!apOn){ // should we re-enable AP every times for increate trailing number in ESSID?
                   
                    SomethingAdhoc.modeAP();
                    apOn = true;
                }
                break;
            case "2": // sender mode
                if(apOn){
                    try {
                        SomethingAdhoc.turnoffAP();
                    } catch (IOException ex) {
                        System.err.println("Error: I/O fail when turnOffAP");
                    }
                }
                System.out.println("-- Mode: Sender Mode          --");
                System.out.println("Commands: ap_mode, rescan, send, exit");
                SomethingAdhoc.modeSender();
                //refreshAndPrintAdhocList();
                break;
            default:
                System.out.println("Invalid mode!");
                break;
        }
        System.out.println("-----------------------------------");
        System.out.print("dummy@localhost: ~/ ");
    }
    public static void modeAP(){
        //System.out.println("Debug: modeAP() is called");
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
    public static void modeSender() throws InterruptedException{
        //System.out.println("Debug: modeSender() is called");
        // 1. do client setup stuffs
        client = new AdhocClient(wifiInf, "Linux");
        // 2. get list of adhoc AP
        refreshAndPrintAdhocList();
    }
    
    public static void refreshAndPrintAdhocList() throws InterruptedException{
        //System.out.println("Debug: refreshAndPrintAdhocList() is called");
        // 1. refresh adhoc list (re-scan and add to list)
        client.refreshAdhocList();
        System.out.println("Scanning...");
        // 2. waiting by delay for 1 second
        Thread.sleep(1000);
        // 3. show adhoc list in console
        client.showAdhocList();
    }
    public static void turnoffAP() throws InterruptedException, IOException{
        //System.out.println("Debug: turnoffAP() is called");
        if(ap instanceof AdhocAP){
            // 1. down AP (remove essid + set to managed mode)
            ap.downAP();
            ap = null;
            apOn = false;
            System.out.println("Shutdown AP...");
            Thread.sleep(5000);
        }else{
            System.err.println("Error: cannot turnoff AP");
        }
        if(t1 instanceof ServerSocketThread){
            // 2. down socket server thread
            System.out.println("Closing the Server Socket..");
            t1.stopServerSocket();
        }else{
            System.err.println("Error: cannot down server socket");
        }
    }
}
