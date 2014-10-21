package testjna;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdHocCMD {
	public static void main(String[] args) throws IOException{

		String adhocName = AdHocCMD.findAdhoc();
		if(!adhocName.equals(null)){
			System.out.println("Found ad-hoc at \""+adhocName+"\" :)");
		}else{
			System.out.println("Not found :(");
		}

		/*
		 * Sample Output:
		 *  Found ad-hoc at "sensin2" :)
		 */

	}
	// @TODO: fix this mess up!! (more than 1 adhoc available)
	public static String findAdhoc(){
		String command = "netsh wlan show network";
		String output = AdHocCMD.executeCommand(command);
		String NetType = "(.*)アドホック"; // type == adhoc
		Pattern p = Pattern.compile(NetType, Pattern.DOTALL);
		Matcher m = p.matcher(output);
		String ssid;
		if (m.find()) {
			ssid = m.group(0);
			ssid = ssid.substring(ssid.lastIndexOf("SSID"));
			ssid = ssid.substring(ssid.indexOf("SSID"),ssid.indexOf("\n"));
			ssid = ssid.substring(ssid.lastIndexOf(" ")+1);
			return ssid;
	    }
		return null;
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
