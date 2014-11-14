package somethingadhoc;

public class AdhocAP extends Adhoc{
    String apName;
    String clientName;
    
    public AdhocAP(String networkInterface, String osType){
        super(networkInterface, osType, true); // true = ap
    }
    
    public int setupAdhoc(){
        // @TODO: senshin_ + mac address + random name?
        apName = "senshin_"+(((int)(Math.random()*1000))+1);
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
