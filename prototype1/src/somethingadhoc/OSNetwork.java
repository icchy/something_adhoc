package somethingadhoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public abstract class OSNetwork {
    
    String networkInfName; // wlan0
    // 1. this is linux?
    // 2. have required programs (ifconfig, iw, iwconfig)
    // 3. have wifi interface
    // 4. select interface w/ params
    // 5. scan available wifi inf
    // 6. connect to SSIDs
    // 7. disconnect to SSIDs
    
    public abstract int checkRequirement();

    public abstract boolean hasPrograms(String[] programsList);

    public abstract boolean hasWiFiInterface(String networkInfName);

    public abstract int setupAP(String ssid, String mode, String ipAddress, String subnetMask);
    
    public abstract String scanAvailableAP();
    public abstract ArrayList<ScannedAPData> scanAvailableAdhoc();
    public abstract int connectAP(String ssid, String ipAddress, String subnetMask);
    public abstract int downInterface();
    public abstract int upInterface();
    public abstract int defaultWifiMode();
    public abstract int turnoffESSID();
    public abstract int setupIP(String ipAddress, String subnetMask);
    public abstract boolean isPrivileged();
    public abstract boolean pingTest(String targetIP);
    
    public static String getOS() {
        return System.getProperty("os.name");
    }
    
    public static String[] execCmd(String command) {
        StringBuilder output = new StringBuilder();
        Process p;
        int exitCode = -1;
        try {
            // alternative:
            // final String[] cmd = { "sudo", "/path/to/program", "arg1", "arg2" };
            // Runtime.getRuntime().exec(cmd);
            p = Runtime.getRuntime().exec(command);
            exitCode = p.waitFor();
            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Fetal Error while execCmd(): "+command);
            //e.printStackTrace();
        }
        return new String[]{output.toString(), String.valueOf(exitCode)};
    }
    
    public static int execCmds(String[] commands){
        for (String command : commands) {
            int exitCode = Integer.parseInt(execCmd(command)[1]);
            if( exitCode != 0 ){
                System.err.println("Command: "+command+", error code: "+exitCode);
                return exitCode;
            }
        }
        return 0;
    }

}
