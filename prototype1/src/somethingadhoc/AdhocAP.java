package somethingadhoc;

public class AdhocAP extends Adhoc{
    public static String apName;
    public static int uID;
    String clientName;
    String macAddress;
    
    public AdhocAP(String networkInterface, String osType){
        super(networkInterface, osType, true); // true = ap
        macAddress = getMacAddress();
    }
    
    /**
     * uId is incremental b/c if SSID was cached in the air
     * client will select the highest number of SSID as latest AP
     * @return status of setupAdhoc()
     */
    public int setupAdhoc(){
        // @TODO: senshin_ + mac address + random no or incremental no?
        // int uID = (((int)(Math.random()*1000))+1);
        uID++;
        apName = "senshin_"+macAddress+"_"+uID;
        apName = apName.replaceAll("-", ""); // remove '-' from MAC addr
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
        //clientName = null;
        //return super.net.downInterface();
        
        return super.downAP();
    }
    
    public int forceKickClient(){ return 0; }
    

}
