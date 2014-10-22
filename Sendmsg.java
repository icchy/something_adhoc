package something_adhoc;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Sendmsg {
	public Sendmsg (String ip, int p, String msg) {
		Socket sock = null;
		try {
			sock = new Socket(ip, p);
			DataOutputStream outToServer = new DataOutputStream(sock.getOutputStream());
			outToServer.writeBytes(msg);
		} catch (UnknownHostException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		} catch (IOException e) {
			// TODO �����������ꂽ catch �u���b�N
			e.printStackTrace();
		}
	}
}