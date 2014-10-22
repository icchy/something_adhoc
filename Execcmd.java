package something_adhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Execcmd {
	 public static void main(String[] args) throws IOException{
//         String command = "netsh wlan show interface";

         String command = "netsh wlan connect name=sensin ssid=sensin";
         String output = Execcmd.executeCommand(command);

		 Server serv = new Server(8000);
		 String msg = serv.init();

         System.out.println("received:" + msg);

         Sendmsg smsg = new Sendmsg("169.254.225.129", 8000, msg);


//         String m_interface = ".*–¼‘O.*: *(.*)";
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

	 public static String executeCommand(String command) {

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
