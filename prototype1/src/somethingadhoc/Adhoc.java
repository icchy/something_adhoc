package somethingadhoc;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class Adhoc {
    
    OSNetwork net;
    
    
    boolean isAP;
    String clientIPAddress;
    String apIPAddress;
    String subnetMask;
    
    public Adhoc(String networkInterface, String osType, boolean isAP){
        this.isAP = isAP;
        apIPAddress = "192.168.1.1";
        clientIPAddress = "192.168.1.2";
        subnetMask = "255.255.255.0";
        
        if(osType.equals("Linux")){
            net = new LinuxNetwork(networkInterface);
        }else{
            // to be implemented
            System.err.println("Unimplemented OS");
            System.exit(-1);
        }
    }
    
    public int setupAdhoc(String adhocName){
        if(!isAP){
            return -2;
        }
        // 1. setup ad and set ip
        int setupStatus = net.setupAP(adhocName, "ad-hoc", apIPAddress, subnetMask);
        if( setupStatus != 0 ){
            System.err.println("Error: Cannot setup Adhoc");
            return -1;
        }
        return 0;
    }
    
    public boolean pingTest(){
        if(isAP){
            return pingTest(clientIPAddress);
        }
        return pingTest(apIPAddress);
    }
    
    public boolean pingTest(String ipAddress){
        // TODO: input validations (ip?)
        return net.pingTest(ipAddress);
    }
    
    /*
    Note: We have to set to temporary IP and ping .2 to check
          if there is no other clients, then change to .2
          finally, ping test to AP which is .1
    */
    public int connectRelay(String ssid){
        if(isAP){
            System.err.println("Adhoc in AP mode cannot connect to another AP");
            return -1;
        }
        
        // 1. connect to AP with temporary ip 
        // how about temp IP collision?
        String temporaryIP = "192.168.1."+(((int)(Math.random()*252))+3);
        int connectStatus = net.connectAP(ssid, temporaryIP, subnetMask);
        if( connectStatus != 0){
            System.err.println("Error: Cannot connect Adhoc: "+ssid);
            return -2;
        }
        
        // 2. verify there is no other client by ping test .2
        if(pingTest(clientIPAddress)){
            return -3; // ping test success, there is another client exists!
        }
        
        // 3. reconnect to AP with client ip
        connectStatus = net.connectAP(ssid, clientIPAddress, subnetMask);
        if( connectStatus != 0){
            System.err.println("Error: Cannot reconnect Adhoc: "+ssid);
            return -4;
        }
        
        // 4. verify connection to AP by ping test .1
        if(!pingTest()){
            System.err.println("Error: Ping test toAP failed.");
            return -5;
        }
        
        return 0;
    }
    
    
    public ArrayList<ScannedAPData>  scanAdhoc(){
        // filter only SSIDs started with 'senshin_'
        return net.scanAvailableAdhoc();
    }
    
    public String getMacAddress(){
        final StringBuilder sb = new StringBuilder();
        try {
            String netInfName = "lo";
            NetworkInterface netInf = null;
            Enumeration<NetworkInterface> intfs = NetworkInterface.getNetworkInterfaces();
            // prevent attempt to get mac addresss from 'lo' inferface ( which doesn't have mac addr, result in error)
            while(netInfName.equals("lo") && intfs.hasMoreElements()){
                 netInf = intfs.nextElement();
                 netInfName = netInf.getName();
            }
            if(netInf == null){
                System.err.println("Error: Network Interface is not found");
                System.exit(1);
            }
            final byte[] mac = netInf.getHardwareAddress();
            if(mac == null){
                System.err.println("Error: This Network Interface cannot get MAC Address");
                System.exit(1);
            }
            for (int i = 0; i < mac.length; i++) {        
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
        } catch (SocketException ex) {
            System.err.println("Error: "+ex.getMessage());
        }
        return sb.toString();
    }
    
        
    public int downAP() {
        // 1. up AP
        net.upInterface();

        // 2. turn-off ESSID
        int exitCode = net.turnoffESSID();
        if(exitCode != 0 ){
            System.err.println("Cannot turn off ESSID");
            return exitCode;
        }
        
        // 3. back to default mode (managed mode)
        exitCode = net.defaultWifiMode();
        if(exitCode != 0){
            System.err.println("Cannot set to default WiFi mode");
            return exitCode;
        }
        
        // 4. reset interface again
        net.downInterface();
        net.upInterface();
        net.downInterface();
        net.upInterface();
        
        return exitCode;
    }
}
