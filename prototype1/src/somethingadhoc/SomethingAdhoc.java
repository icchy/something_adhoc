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
        String mode = "";
        
        // open the program at the first time
        // turn into AP mode
        mode = "1";
        SomethingAdhoc.modeAP();
        apOn = true;
        command = "";
        
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
                    if(mode.equals("1")){
                        SomethingAdhoc.turnoffAP();
                        mode = "";
                    }else{
                        System.err.println("Error: This command work only in AP mode");
                    }
                    break;
                case "scan":
                    if(mode.equals("2")){
                        System.out.println("[+] Scan !");
                        // 0. precondition
                        if(client==null || !mode.equals("2")){
                            System.err.println("Please switch to Sender Mode");
                            break;
                        }
                        refreshAndPrintAdhocList();
                    }else{
                        System.err.println("Error: This command work only in Client mode");
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
                        System.out.print("Enter Target Node: ");
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
                            t2 = new ModeSenderThread(message);

                        }else if(routeRecord!=null){
                            // 2. send data+RTP to known route (in cached route file)
                            t2 = new ModeSenderThread(message, routeRecord);

                        }else{
                            // 3. send routing request packet neighbors to construct routing
                            t2 = new ModeSenderThread(routeRecord);

                        }
                        t2.start();
                        // 5. client should receive ack. that confirm message reach server socket
                        // 6. fall back to AP mode
                        // 7. force close client socket?
                    }else{
                        System.err.println("Error: This command work only in CLient mode");
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
    public static void printBanner(String mode) throws InterruptedException{
        System.out.println("-----------------------------------");
        System.out.println("--    Something Ad-Hoc console   --");
        System.out.println("-----------------------------------");
        switch(mode){                
            case "1":
                /*
                    @TODO
                    this is how ad-hoc ap handle packet from connected client
                    it should be fullfill inside ServerProcess.java maybe ?
                
                    1. system have to periodic maintain neighbor list
                    for reconstruct routing if needed (reactive)
                
                    2. system have to check types of connected client which are..
                
                    2.1 type 1: Route Request Packet (RRP)
                                it will came from a source or an intermediate node
                                that about to reconstruct route from src. to dst.
                                2.1.1 if this node is dst. then send back route table (RTP).
                                back to source node (original sender)
                                2.1.2 Count number of hops (TTL style?) 
                                to prevent loop/unreachable node
                                maximum should be around ~12 hops? 
                                2.1.3 otherwise, pass RRP to neighbors
                                @TODO if there are many different paths to reach dest. node
                                    | should a node send only the shortest path or send all info back
                                    | becuase some paths may failed and others probably can replace it

                    2.2 type 2: Routing Table Packet (RTP)
                                it will came from a dest. or an intermediate node
                                that realized path from source to dest. node
                                2.2.1 make sure that this node is in the route info
                                2.2.2 if this node is src. then record route info.
                                    and prepare to send data packet
                                2.2.3 otherwise, send back to src/intermediate in next hop
                                2.2.3.1 if next hop specified in RTP does not exist, send back 'type 4'? or just discard it
                                @TODO is it possible to let all intermediate nodes
                                    | learn path between two node from RTP because
                                    | later on, if someone else send RRP to find a node 
                                    | in that path, this intermediate node will be able to
                                    | send route info. directly to reduce the cost of discovery (time)
                                    | however, what about if there is a failed node within path? how do we know?
                                    | another possible way is to learn the time that RTP is arrived too
                                    | ex. if someone request a node that this node learnt from other's RTP
                                    |     within 30 minutes, then reuse the path, otherwise reconstruct it
                                    | anyway, we should not rely upon this learned info b/c it is designed to be reactive routing
                                
                
                    2.3 type 3: Data Packet + RTP
                                it will came from a source or an intermediate node
                                2.3.1 if this node is final node in route info, catch it
                                2.3.2 otherwise, forward Data Packet + RTP to next hop
                                2.3.3 if next hop is not exist in neighbor list, send back 'type 4' to src.
                                @TODO what if link that report link fail is down LoL? maybe set a timeout
                
                    2.4 type 4: Report Link Fail Packet (RLFP)
                                it will came from an intermediate node
                                that cannot find next hop, so learn failed node
                                and delete routing table info which contains it
                    
                    
                    sample of routing info. format (RTP)
                
                    ['started-time-of-discovery','ended-time-of-discovery',['nodeA-macAddr','nodeB-macAddr','nodeC-macAddr']]
                    * time = timestamp
                    * node name is just alias, unique identifier is mac address (even if it's spoofable we don't have another choice?)
                    
                */
                client = null;
                System.out.println("-- Mode: AP Mode              --");
                System.out.println("-- Status: idle              --");
                /*
                Status: 
                    1. idle = just broadcast ad-hoc
                    2. connected w/ request route = a client is connected to find route
                    3. connected w/ reply route = a client is connected to send route back to next hop
                    4. connected w/ send route = a client is connected to send route to this hop as last hop, prepare to send data
                    5. connected w/ send data = a client is connected to send data to this node
                    6. connected w/ forward data = a client is connected to this as relay (forward to next hop)
                */
                System.out.println("Commands: client, exit");
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
    public static void modeSender() throws InterruptedException{
        // 1. do client setup stuffs
        client = new AdhocClient(wifiInf, "Linux");
        // 2. get list of adhoc AP
        refreshAndPrintAdhocList();
    }
    
    public static void refreshAndPrintAdhocList() throws InterruptedException{
        // 1. refresh adhoc list (re-scan and add to list)
        client.refreshAdhocList();
        System.out.println("Scanning...");
        // 2. waiting by delay for 1 second
        Thread.sleep(1000);
        // 3. show adhoc list in console
        client.showAdhocList();
    }
    public static void turnoffAP() throws InterruptedException, IOException{
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
    }
}
