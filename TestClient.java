package sensin;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class TestClient {
	public static void main(String args[]){
		int port = 10000;
		//Server localServer = new Server(port);
		//String server_ip = "169.254.184.4";
		String server_ip = "169.254.184.4";
		try {
			Socket s = new Socket(server_ip, port);
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(s.getInputStream()));
		    DataOutputStream outToServer=new DataOutputStream(s.getOutputStream());

		    while(true){
		    	//String input = JOptionPane.showInputDialog("Type me");
		    	String input = new Scanner(System.in).nextLine();
		    	outToServer.writeBytes(input+"\n");
		        String res=inFromServer.readLine();
		        System.out.println("Res: "+res);
		    }


		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Nooo~ "+e.getMessage());
			e.printStackTrace();
		}
	}
}
