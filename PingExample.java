import org.tempuri.* ;

public class PingExample
{
   
    public static void ping( String host )
    {
       System.out.println( "Ping Host: " + host ) ;
       Service service = new Service();
       ServiceSoap port = service.getServiceSoap(); 
       String result = port.pingHost( host ) ;
       System.out.println( "Ping Result: " + result ) ;
    }
}
