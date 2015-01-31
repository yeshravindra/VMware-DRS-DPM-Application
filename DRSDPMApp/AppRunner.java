package DRSDPMApp;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.ServiceInstance;

public class AppRunner {

	public static void main(String[] args) throws IOException, InterruptedException {
		String vcenter = "https://130.65.133.30/sdk";
		String adminvcenter = "https://130.65.132.14/sdk";
        String uname = "administrator";
        String pwd = "12!@qwQW";
        double[] parameter = new double[5];
        ServiceInstance adminsi = new ServiceInstance(new URL(adminvcenter), uname, pwd, true); 
        
        Logs server = new Logs();
        String currentVM = "T15-VM03-Ubu";
       // String currentVM = InetAddress.getLocalHost().getHostName();
		
        
        ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);        
       Folder rootFolder = si.getRootFolder();
        
       
        PThread collectParameter = new PThread(si, currentVM);
       Thread ParaThread = new Thread(collectParameter);
       ParaThread.start();       
        
        
       controlSwitchTh contSwitch = new controlSwitchTh(si);
       Thread contThread = new Thread(contSwitch);
        contThread.start();        
        
        
        HostThread HcollectParameter = new HostThread(adminsi,currentVM );
        Thread HParaThread = new Thread(HcollectParameter);
        HParaThread.start();  
        	
        ParaThread.join();     
        contThread.join();
        
	}

}
