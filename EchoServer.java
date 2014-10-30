package something_adhoc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

public class EchoServer {
	public static void main(String[] args) throws IOException {
		int BUFSIZE = 32;
		int port = 10000;
		int interval = 100;

		InetAddress addr = InetAddress.getByName("0.0.0.0");

		ServerSocket serverSocket = new ServerSocket(10000, 5, addr);

		int recvMsgSize;
		byte[] msgBuf = new byte[BUFSIZE];

		// wait connecting from client
		while (true) {
			Socket clSock = serverSocket.accept();
			SocketAddress clAddress = clSock.getRemoteSocketAddress();
			System.out.println("Connecting to " + clAddress + "...");

			InputStream in = clSock.getInputStream();
			OutputStream out = clSock.getOutputStream();

			while ((recvMsgSize = in.read(msgBuf)) != -1) {
				out.write(msgBuf, 0, recvMsgSize);
			}
			clSock.close();
		}
	}
}