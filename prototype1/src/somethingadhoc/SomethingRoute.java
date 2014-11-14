package somethingadhoc;


public class SomethingRoute {
    
    private static SomethingRoute route;
    String filename = "";
    
    private SomethingRoute(){}
    
    // singleton
    public SomethingRoute init(){
        if(route == null){
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
    
}
