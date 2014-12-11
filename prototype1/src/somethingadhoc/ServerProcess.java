
package somethingadhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.*;
import org.json.simple.parser.*;

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
                                                
                                                current route pattern: 
                                                1. A find C but not in neighbor, then send 
                                                {"senshin_A":["d":"senshin_C",]}
                                                to ask neighbor (B)
                                                2. B has C in neighbor then add to original route 
                                                {"senshin_A":{"senshin_B":"sehshin_C"}}|senshin_C
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
                                                
                                                // issue: how we can get neighbor list while still in ad-hoc mode?
                                                // possible sol: switch to client mode, scan for neighbors, and switch back
                                                
                                                // if( destName in neighbor_list)
                                                // then
                                                //      mark the route data that it is in the next
                                                //      output.println(newRoute); // send back?
                                                // else
                                                //     connect & forward rquest to each neighbor
                                                
                                                // this is how we parse route in json format for further checks
                                                // parse json string in routeRaw
                                                JSONParser parser = new JSONParser();
                                                
                                                JSONObject routeJson = (JSONObject)JSONValue.parse(payload);
                                                
                                                // top-level key
                                                String key = String.valueOf(routeJson.keySet().toArray()[0]);
                                                // value of top-level key
                                                String value = String.valueOf(routeJson.get(key));
                                                
                                                
                                                
                                                break;
                                            case "2":
                                                // 11.2 route reply
                                                break;
                                            case "3":
                                                // 11.3 data forward
                                                break;
                                            case "4":
                                                // 11.4 data
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
