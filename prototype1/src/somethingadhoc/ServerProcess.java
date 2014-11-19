
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
			while(running.get()){
				// 8. grab data from client
				buffer = input.readLine(); 
                                
				if( buffer != null ){
					// 9. TODO: logic of relay stuffs will occur here!
                                        //RelayProcess(buffer);
                                        String rev = "good, Server get : "+buffer;
					output.println(rev);
                                        System.out.println(rev);
                                        if(buffer.contains("__exit__")){
                                            break;
                                        }
				}
                                
			}
                        
			
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
