package somethingadhoc;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

public class AdhocAP extends Adhoc{
    String apName;
    String clientName;
    String macAddress;
    
    public AdhocAP(String networkInterface, String osType){
        super(networkInterface, osType, true); // true = ap
        macAddress = getMacAddress();
    }
    
    public int setupAdhoc(){
        // @TODO: senshin_ + mac address + random name?
        apName = "senshin_"+macAddress+"_"+(((int)(Math.random()*1000))+1);
        apName = apName.replaceAll("-", "");
        System.out.println("Starting AdHoc: "+apName);
        return super.setupAdhoc(apName);
    }
    
    public boolean hasClient(){
        if( super.pingTest() ){
            // @TODO: get client name in socket level
            clientName = "Client1";
            return true;
        }
        return false;
    }
    
    public int downAP(){
        // just down the interface, better approach is coming soon
        clientName = null;
        return super.net.downInterface();
    }
    
    public int forceKickClient(){ return 0; }
    

}
