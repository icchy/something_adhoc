
package somethingadhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerProcess extends Thread{
    	private final Socket clientSocket;
	private BufferedReader input;
	private PrintWriter output;

	
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
			
                        String buffer = "";
			// 7. communication!
                        // infinity loop for socket communication
			while(buffer == null || !buffer.startsWith("__exit__")){
				// 8. grab data from client
				buffer = input.readLine(); 
				if(buffer!=null){
					
					// 9. TODO: logic of relay stuffs will occur here!
                                        //RelayProcess(buffer);
					output.println("good, Server get : "+buffer);
				}
			}
			
		} catch (IOException e) {
			System.out.println("Error: "+e.getMessage());
			e.printStackTrace();
		} finally{
			closeClient();
		}
		
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
			
			//String clientIP = clientSocket.getInetAddress().getHostAddress();
			//int srcPort = clientSocket.getPort();
			
                        // @TODO: remove from AdhocAP client
			
			
		} catch (IOException e) {
			System.out.println("Error: "+e.getMessage());
			e.printStackTrace();
		}
	}
}
