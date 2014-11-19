package somethingadhoc;
import java.util.*;

public class AdhocClient extends Adhoc{
 
    ArrayList<ScannedAPData> adhocAvailable; 
    Date lastScan;
    ScannedAPData connectedAdhoc;
    
    public AdhocClient(String networkInterface, String osType){
        super(networkInterface, osType, false); // false = client
    }
    
    public void refreshAdhocList(){
        disconnectAP();
        super.net.upInterface();
        adhocAvailable = net.scanAvailableAdhoc();
        lastScan = new Date();
    }
    
    // print to console
    public void showAdhocList(){
        int totalAdhoc = adhocAvailable.size();
        System.out.println("Found ad-hoc: "+totalAdhoc);
        for (int i = 1; i <= totalAdhoc; i++) {
            String adhocName = adhocAvailable.get(i-1).ssid;
            System.out.println("\n"+i+": "+adhocName);
        }
    }
    
    /*
    Refer: http://superuser.com/a/616425
    */
    @Override
    public int connectRelay(String ssid){
        // TODO: ensure SSID name in the scan list?
        int connectionStatus = super.connectRelay(ssid);
        if(connectionStatus != 0 ){
            System.err.println("Failed to connect relay.");
            return -1;
        }
        for (ScannedAPData adhocAP : adhocAvailable) {
            if(adhocAP.ssid.equals(ssid)){
                connectedAdhoc = adhocAP;
                return 0;
            }
        }
        System.err.println("Error: Connected to Relay outside scan list?");
        return -2;
    }
    
    public int disconnectAP(){
        // just down the interface, better approach is coming soon
        connectedAdhoc = null;
        return super.net.downInterface();
    }
    
    public String getAPSignal(){
        if(connectedAdhoc==null){
            return "no data";
        }
        return connectedAdhoc.signal;
    }
    
    public boolean isConnectedToAP(){
        if(connectedAdhoc == null){
            return true;
        }
        return false;
    }
    
    
    
}
