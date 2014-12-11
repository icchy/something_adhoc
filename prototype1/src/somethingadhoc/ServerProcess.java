
package somethingadhoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.simple.*;
import org.json.simple.parser.*;
import static somethingadhoc.SomethingAdhoc.ap;
import static somethingadhoc.SomethingAdhoc.client;
import static somethingadhoc.SomethingAdhoc.t2;
import static somethingadhoc.SomethingAdhoc.wifiInf;

public class ServerProcess extends Thread{
        // uses for terminate thread
        private final AtomicBoolean running = new AtomicBoolean(true);
        
    	private final Socket clientSocket;
	private BufferedReader input;
	private PrintWriter output;
        private String buffer;
	
	public ServerProcess(Socket s){
                // accepted socket from client
		clientSocket = s;
	}

	@Override
	public void run() {
		System.out.println("run a new server thread!");
		try {
			// 1. initial input / output streams
			input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			output = new PrintWriter(clientSocket.getOutputStream(), true); // true = auto-flush
			
                        buffer = "";
			// 7. socket communications
                        /*
                        Note: There are two ways to stop this socket communication
                            1. client send '__exit__' command
                            2. ServerSocketThread terminate this process by terminateServer()
                        */
                        
                        // no need loop here b/c op done at once?
			//while(running.get()){
				// 8. grab data from client
				buffer = input.readLine(); 
                                
				if( buffer != null ){
					// 9. TODO: logic of relay stuffs will occur here!
                                        //RelayProcess(buffer);
                                        System.out.println("Server receive : "+buffer);
                                        
                                        // TODO: check this seperators to JSON only?
                                        // to prevent fields contains seperator itself > encode each field?
                                        String seperator = "|_|=-=|_|";
                                        // 10.1 make sure received data in the correct format
                                        int count = buffer.length() - buffer.replace(seperator, "").length();
                                        if(count != 2){
                                            System.err.println("Malformed packet! => "+buffer);
                                            return;
                                        }
                                        // ex. 1|_|=-=|_|{"senshin_A":}|senshin_C
                                        // 10.2 seperate field in data payload
                                        
                                        String[] fields = buffer.split(seperator);
                                        // 11. indicate what kind of packet
                                        String type = fields[0];
                                        String payload = fields[1];
                                        
                                        switch(type){
                                            case "1":
                                                // 11.1 route request
                                                /*
                                                current route pattern: 
                                                1. A find C but not in neighbor, then send 
                                                {"senshin_A":["senshin_B","senshin_D"]}|senshin_C
                                                to ask neighbor (B)
                                                2. B has C in neighbor then add to original route 
                                                {"senshin_A":[{"senshin_B":"sehshin_C"},"senshin_D"]}|senshin_C
                                                then B send back to A
                                                
                                                json ref: http://www.tutorialspoint.com/json/json_java_example.htm
                                                */
                                                
                                                // 12. extract data
                                                
                                                // ex. payload => {"senshin_A":{"senshin_B":"sehshin_C"}}|senshin_C
                                                
                                                // ex. routeRaw => {"senshin_A":{"senshin_B":"sehshin_C"}}
                                                String routeRaw = payload.split("|")[0];
                                                
                                                // ex. destName => senshin_C
                                                String destName = payload.split("|")[1];
                                                
                                                // 13. check destName against neighbor list
                                                
                                                // if( destName in neighbor_list)
                                                // then
                                                //      mark the route data that it is in the next
                                                //      output.println(newRoute); // send back?
                                                // else
                                                //     connect & forward rquest to each neighbor
                                                
                                                
                                                // issue: how we can get neighbor list while still in ad-hoc mode?
                                                // possible sol: switch to client mode, scan for neighbors, and switch back
                                                ap.downAP();
                                                client = new AdhocClient(wifiInf, "Linux");
                                                String[] neighbors = client.getNeighbors().split("\n");
                                                boolean found = false;
                                                for(String n : neighbors){
                                                    if(n.contains(destName)){
                                                        found = true;
                                                        break;
                                                    }
                                                }
                                                // TODO: I hope that the connected client still in ap
                                                // while we fallback to client and get back to ap :|
                                                ap = new AdhocAP(wifiInf, "Linux");
                                                // if dest node is in neighbor list
                                                if(found){
                                                    System.out.println("Debug: found destination in neighbor list.");
                                                    // mark the route data that it is in the next
                                                    JSONParser parser = new JSONParser();
                                                    JSONObject routeJson = (JSONObject)JSONValue.parse(payload);
                                                    
                                                    // json stuffs: somehow traversal to this tree and mark it 
                                                    // from: {"senshin_A":["senshin_B","senshin_D"]}|senshin_C
                                                    // to: {"senshin_A":[{"senshin_B":"sehshin_C"},"senshin_D"]}|senshin_C
                                                    // ps. the marking position is based up on where is this node in route info
                                                    String sender = String.valueOf(routeJson.keySet().toArray()[0]);
                                                    String route = String.valueOf(routeJson.get(sender));
                                                    
                                                    // and reply to forwarder who ask for dest node
                                                    String newRoute = "";
                                                    output.println(newRoute);
                                                }else{
                                                    System.out.println("Debug: dest. not found in neighbor list");
                                                    // connect & forward route request to each neighbor
                                                    ap.downAP();
                                                    for(String n : neighbors){
                                                        System.out.println("Debug: forward RRP to : "+n);
                                                        client = new AdhocClient(wifiInf, "Linux");
                                                        // add neighbors into new route
                                                        // json stuffs
                                                        
                                                        String newRoute="";
                                                        t2 = new ModeSenderThread(newRoute, 4);
                                                    }
                                                }
                                                System.out.println("Debug: finish type 1 op.");
                                                break;
                                            case "2":
                                                // 11.2 route reply
                                                // 1. extract route
                                               
                                                JSONParser parser = new JSONParser();
                                                
                                                JSONObject routeJson = (JSONObject)JSONValue.parse(payload);
                                                
                                                // 2. find current node and get next hop AP name from route info
                                                // @TODO: how to search through these json and get the next hop?
                                                String sender = String.valueOf(routeJson.keySet().toArray()[0]);
                                                // tree-like structure route info for sender
                                                String route = String.valueOf(routeJson.get(sender));
                                                // go deep until found this node and get nex hop
                                                // ...
                                                
                                                // 3. switch to client mode
                                                // 4. forward route info
                                                break;
                                            case "3":
                                                // 11.3 data forward
                                                // 1. extract data / route
                                                // 2. switch to client mode
                                                // 3. forward data according to route info
                                                break;
                                            case "4":
                                                // 11.4 data
                                                // 1. just save it
                                                String filename="/tmp/somethingadhoc_"+UUID.randomUUID().toString()+".tmp";
                                                FileWriter out = new FileWriter(new File(filename));
                                                out.append(System.getProperty("line.separator")+payload);
                                                break;
                                            default:
                                                // malform type
                                                System.err.println("Error: Invalid packet type.");
                                        }

                                        /*
                                        if(buffer.contains("__exit__")){
                                            break;
                                        }*/
				}
                                
			//}
                        
			
		} catch (IOException e) {
			System.err.println("Error: "+e.getMessage());
			e.printStackTrace();
		} finally{
			closeClient();
                        System.out.println("ServerProcess is terminated!!");
		}
		
	}
        
        // thread safe termination
        public void terminateServer(){
            running.set(false);
        }

        // TODO: where to maintain routing data, a file?
	private void RelayProcess(String revBuffer){
                // 1. handshake
                //    - name,ip,mac
                // 2. select mode
                //    - exchange routing
                //        - discover
                //        - send back
                //    - transfer file
                // 3. execute
	}
	private void closeClient(){
		try {
			
			input.close();
			output.close();
			clientSocket.close();
			
                        // @TODO: remove from AdhocAP client
			
			
		} catch (IOException e) {
			System.out.println( "Error: "+e.getMessage() );
			e.printStackTrace();
		}
	}
}
