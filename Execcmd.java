package something_adhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class Execcmd {
	 public static void main(String[] args) throws IOException{
//         String command = "netsh wlan show interface";

         
         setAdhoc("senshin_1", 0);
		 Server serv = new Server(10000);
		 String msg = serv.init();
		 long startTime = Calendar.getInstance().getTimeInMillis();
         System.out.println("received:" + msg);
         
         setAdhoc("senshin_2", 1);

         Sendmsg smsg = new Sendmsg("192.168.1.1", 10000, msg);
         long endTime = Calendar.getInstance().getTimeInMillis();
         long totalTime = endTime-startTime;
         System.out.println("Forward time: "+totalTime);
//         String m_interface = ".*���O.*: *(.*)";
//         Pattern p = Pattern.compile(m_interface);
//         Matcher m = p.matcher(output);
//         if (m.find()) {
//        	 System.out.println(m.group(1));
//         }
//         String iface_name = m.group(1);

//         System.out.println(output);
//         System.out.println("---------- END OUTPUT ----------------");
//         System.out.println("hoge");
	 }
	 
	 public static void getSSIDList() {
		 String res = execCmd("iwlist scan");
		 System.out.println(res);
	 }

	 public static int setAdhoc(String ssid, int mode) {
		 String[] cmd = {
			 "ip link set wlan0 down 2>&1",
			 "iwconfig wlan0 mode ad-hoc",
			 "iwconfig wlan0 essid " + ssid,
			 "ifconfig wlan0 192.168.1."+(mode==0?"1":"10")+" netmask 255.255.255.0",
			 "ip link set wlan0 up"
		 };
		 
		 for (int i = 1; i <= cmd.length; i++) {
			 execCmd(cmd[i-1]);
			 int re = retCode();
//			 if (re != 0) {
//				 return i;
//			 }
		 }

		 return 0;
	 }

	 public static int retCode() {
		 String res = execCmd("/bin/bash -c 'echo $?'");
//		 System.out.println("b: "+res);
		 return (res=="0") ? 0 : -1;
	 }
	 

	 public static String execCmd(String command) {
         StringBuffer output = new StringBuffer();
         Process p;
         try {
                 p = Runtime.getRuntime().exec(command);
                 p.waitFor();
                 BufferedReader reader =
                     new BufferedReader(new InputStreamReader(p.getInputStream()));
                 String line = "";
                 while ((line = reader.readLine())!= null) {
                         output.append(line + "\n");
                 }
         } catch (Exception e) {
                 e.printStackTrace();
         }
         return output.toString();
	 }
}
