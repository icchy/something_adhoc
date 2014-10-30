package something_adhoc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
	public int port;
	public String ip;
	public Server(int p){
		this.port = p;
		this.ip = "127.0.0.1";
		try {
			InetAddress addr = InetAddress.getByName(this.ip);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//this.init();
	}
	public String init(){
		try {
			BufferedReader dalclient = null;
			ServerSocket ss = new ServerSocket(this.port);
			//String output = null;
			StringBuilder output = new StringBuilder();
			Socket c= ss.accept();
			while(true){
				
				DataOutputStream alclient = new DataOutputStream(c.getOutputStream());

				dalclient = new BufferedReader(new InputStreamReader(c.getInputStream()));
				String res = dalclient.readLine();
				
				System.out.println("debug: "+res);

				if(res==null){
					ss.close();
					break;
				}
				//output += res;
				output.append(res);
			}
			System.out.println("i am outside yeah");
			return output.toString();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "failed";
		}
	}
}
