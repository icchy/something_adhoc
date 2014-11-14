package com.example.something_adhoc;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void createToast(String str){
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_SHORT;

		Toast toast = Toast.makeText(context, str, duration);
		toast.show();
	}
	
	public void createSocket(View view){
		createToast("called");
		final int UDPPORT = 5555;

		createToast("initialized");
		DatagramSocket ds;
		createToast("Socket Created");
		byte[] packet = {(byte)0x80, (byte)0x0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x0, (byte)0xf, (byte)0x66, (byte)0xe3, (byte)0xe4, (byte)0x3, (byte)0x0, (byte)0xf, (byte)0x66, (byte)0xe3, (byte)0xe4, (byte)0x3, (byte)0x0, (byte)0x0, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff, (byte)0x64, (byte)0x0, (byte)0x11, (byte)0x0, (byte)0x0, (byte)0xf, (byte)0x73, (byte)0x6f, (byte)0x6d, (byte)0x65, (byte)0x74, (byte)0x68, (byte)0x69, (byte)0x6e, (byte)0x67, (byte)0x63, (byte)0x6c, (byte)0x65, (byte)0x76, (byte)0x65, (byte)0x72, (byte)0x1, (byte)0x8, (byte)0x82, (byte)0x84, (byte)0x8b, (byte)0x96, (byte)0x24, (byte)0x30, (byte)0x48, (byte)0x6c, (byte)0x3, (byte)0x1, (byte)0x1, (byte)0x5, (byte)0x4, (byte)0x0, (byte)0x1, (byte)0x0, (byte)0x0, (byte)0x2a, (byte)0x1, (byte)0x5, (byte)0x2f, (byte)0x1, (byte)0x5, (byte)0x32, (byte)0x4, (byte)0xc, (byte)0x12, (byte)0x18, (byte)0x60, (byte)0xdd, (byte)0x5, (byte)0x0, (byte)0x10, (byte)0x18, (byte)0x1, (byte)0x1, (byte)0xdd, (byte)0x16, (byte)0x0, (byte)0x50, (byte)0xf2, (byte)0x1, (byte)0x1, (byte)0x0, (byte)0x0, (byte)0x50, (byte)0xf2, (byte)0x2, (byte)0x1, (byte)0x0, (byte)0x0, (byte)0x50, (byte)0xf2, (byte)0x2, (byte)0x1, (byte)0x0, (byte)0x0, (byte)0x50, (byte)0xf2, (byte)0x2};

		try {
			ds = new DatagramSocket(UDPPORT);
			ds.setBroadcast(true);

			DatagramPacket dp;

			dp = new DatagramPacket(packet,
					packet.length,
					getBroadcastAddress(this),
					UDPPORT);
			createToast("begin sending the packet");
			for (int i = 0; i < 100; i++) {
				ds.send(dp);
				ds.close();
			}
			createToast("sended packet");
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getMyAddress( Context context ){
		String strIp = "0.0.0.0";
		try {
			WifiManager wifiManager = 
             (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			int ip = wifiInfo.getIpAddress();

			strIp = 	((ip >> 0) & 0xFF)+"."+
						((ip >> 8) & 0xFF)+"."+
						((ip >> 16) & 0xFF)+"."+
						((ip >> 24) & 0xFF); 

		} catch (Exception e) {
		}
		return strIp;
	}
	
	public InetAddress getBroadcastAddress( Context context ) throws IOException {
			WifiManager wifiManager = 
	        (WifiManager)context.getSystemService(Context.WIFI_SERVICE);

			DhcpInfo dhcp = wifiManager.getDhcpInfo();

			int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;

			byte[] quads = new byte[4];

			for (int k = 0; k < 4; k++)
					quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);

			return InetAddress.getByAddress(quads);
		}

}
