
package somethingadhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

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
			//while(running.get()){
				// 8. grab data from client
				buffer = input.readLine(); 
                                
				if( buffer != null ){
					// 9. TODO: logic of relay stuffs will occur here!
                                        //RelayProcess(buffer);
                                        System.out.println("Server receive : "+buffer);
                                        // output.println(rev); // send dummy to client
                                        
                                        // 10.1 make sure received data in the correct format
                                        int count = buffer.length() - buffer.replace(".", "").length();
                                        if(count != 2){
                                            System.err.println("Malformed packet! => "+buffer);
                                            return;
                                        }
                                        // 10.2 seperate field in data payload
                                        String[] fields = buffer.split(",");
                                        // 11. indicate what kind of packet
                                        String type = fields[0];
                                        String payload = fields[1];
                                        
                                        switch(type){
                                            case "1":
                                                // 11.1 route request
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
                                        }

                                        /*
                                        if(buffer.contains("__exit__")){
                                            break;
                                        }*/
				}
                                
			//}
                        
			
		} catch (IOException e) {
			System.out.println("Error: "+e.getMessage());
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
