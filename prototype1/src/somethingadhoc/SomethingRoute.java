package somethingadhoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static somethingadhoc.SomethingAdhoc.client;


public class SomethingRoute {
    
    private static SomethingRoute route;
    public static String filename;
    
    public SomethingRoute(){}
    
    // singleton
    public static SomethingRoute init(){
        if(route == null){
            try {
                String path = "/tmp/something_route_"+(((int)(Math.random()*1024)));
                File f = new File(path);
                // f.mkdirs();
                f.createNewFile();
                SomethingRoute.filename = path;
                route = new SomethingRoute();
            } catch (IOException ex) {
                System.err.println("Error: Cannot Create File");
            }
        }
        return route;
    }
    
    /**
        get all routes out of route file
        return null if file did not exists
        @return String
    */
    public static String getAllRoute(){
        /*
            should we remove (or filter out) routes that
            we received after X minutes for just reconstruct it? (timeout)
        */
        StringBuilder sb = new StringBuilder("");
        try {
            // read route file and get all lines
            Scanner in = new Scanner(new File(SomethingRoute.filename));
            // @TODO: check format of lines in file? discard it if format is invalid
            while(in.hasNextLine()){
                sb.append(in.nextLine());
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Error: Route File not found");
            return null;
        }
        return sb.toString();
    }
    
    /**
     * get a route out of route file, return null if not exists
     * For example, 
     * ['started-time-of-discovery','ended-time-of-discovery',
     *                      ['nodeA-macAddr','nodeB-macAddr','nodeC-macAddr']]
     * 
     * @param nodeName
     * @return String
     */
    public static String getRoute(String nodeName){
        // read route file and find a line contains nodeNames
        String allRoutes = SomethingRoute.getAllRoute();
        String pattern = ".*"+nodeName+".*";
        Pattern p = Pattern.compile(pattern, Pattern.MULTILINE);
	Matcher m = p.matcher(allRoutes);
        
        while (m.find()) {
                return m.group(0); // first match is okay?
	}
        
        System.err.println("Error: route cannot be found, so reconstruct it");
        return null;
    }
    
    /**
     * add route info. from RTP into route file
     * @return 
     */
    public static int addRoute(String routeRecord){
        try {
            FileWriter out = new FileWriter(new File(SomethingRoute.filename));
            out.append(System.getProperty("line.separator")+routeRecord);
            return 0;
        } catch (FileNotFoundException ex) {
            System.err.println("Error: Route File Not Found");
            return 1;
        } catch (IOException ex) {
            System.err.println("Error: IO Exception.. Unable to write due to perms denied? ");
            return 2;
        }
    }
    
    /*
        scan nearby AdHoc
    */
    public static String getUpdatedNeighbors(){
        // just use the get ad-hoc list from AdhocClient ?
        if(SomethingAdhoc.client instanceof AdhocClient && SomethingAdhoc.client != null){
            SomethingAdhoc.client.refreshAdhocList();
            return SomethingAdhoc.client.getNeighbors();
        }
        return null;
    }
    
    /**
     * to check a specific route record is in the route file or not
     * @param routeRecord
     * @return boolean indicate status of routeRecord (exist/not exist)
     */
    public static boolean checkRoute(String routeRecord){
        if(getRoute(routeRecord) != null){
            return true;
        }
        return false;
    }
    
    /**
     * @TODO: update this format with tree according to Ryo
     * routeRecord looks like ['started-time-of-discovery','ended-time-of-discovery',
     *                      'nodeA-macAddr','nodeB-macAddr','nodeC-macAddr']
     * 
     * toDest is a boolean value indicate that 
     * - (true) get next hop in the right side
     * - (false) get next hop in the left side
     * 
     * @param routeRecord, rightHop
     * @return relayname of next hop
     */
    public static String getNextRelay(String routeRecord, boolean rightHop){
        // @TODO: should we use serialized object in Java insteads of string - -?
        ArrayList<String> nextRelays = new ArrayList(Arrays.asList(routeRecord.split("','")));
        // 1. discard first and second member in array which are start/end time
        nextRelays.remove(0);
        nextRelays.remove(0);
        // 2. loop through each relays to get next hop
        String thisNodeName = "??";
        for (int i = 0; i < nextRelays.size(); i++) {
            // 3. if found this node in routing path
            if(nextRelays.get(i).equals(thisNodeName)){
                if(rightHop){
                    if(i==0){
                        System.err.println("Error! there is no next hop to this node!");
                        return null; 
                    }
                    // 3.1 get next hop on the right
                    return nextRelays.get(i-1);
                }else{
                    if(i==nextRelays.size()-1){
                        System.err.println("Error! there is no next hop to this node!");
                        return null;
                    }
                    // 3.2 get next hop on the left
                    return nextRelays.get(i+1);
                }
            }
        }
        System.err.println("Error! this node is not in the route record");
        return null;
    }
    
    
}
