package sensin;

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

		this.init();
	}
	public void init(){
		try {
			ServerSocket ss = new ServerSocket(this.port);
			while(true){
				Socket c= ss.accept();
				DataOutputStream alclient=new DataOutputStream(c.getOutputStream());

				 BufferedReader dalclient =new BufferedReader(new InputStreamReader(c.getInputStream()));

				 System.out.println(dalclient.readLine());
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
