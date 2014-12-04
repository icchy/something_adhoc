package somethingadhoc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static somethingadhoc.SomethingAdhoc.client;


public class SomethingRoute {
    
    private static SomethingRoute route;
    public static String filename;
    
    private SomethingRoute(){}
    
    // singleton
    public SomethingRoute init(){
        if(route == null){
            SomethingRoute.filename = "/tmp/something_route_"+(((int)(Math.random()*1024)));
            route = new SomethingRoute();
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
    
    public static boolean checkRoute(String routeRecord){
        return false;
    }
    public static String getNextRelay(String route){
        return null;
    }
    
}
