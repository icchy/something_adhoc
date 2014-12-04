package somethingadhoc;


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
    
    public static String getAllRoute(){
        return null;
    }
    
    public static String getRoute(String nodeName){
        return null;
    }
    
    public static void updateNeighbors(){
        
    }
    
    public static boolean checkRoute(String routeRecord){
        return false;
    }
    public static String getNextRelay(String route){
        return null;
    }
    
}
