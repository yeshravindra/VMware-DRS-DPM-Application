package DRSDPMApp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class HostThread implements Runnable {
	 
ServiceInstance si=null;
String currentVM = null;
boolean collectParam = false;

HostThread (ServiceInstance adminsi, String currentVM) throws RemoteException, IOException{
	Logs server = new Logs();
	this.si= adminsi;
	this.currentVM = currentVM;
	/*this.currentHost = currentHost;
	//if (server.getControl() != null){
		//collectParam = server.getControl().equalsIgnoreCase(currentHost);
	}*/
	
}

public void run() 
{
	Folder rootFolder=this.si.getRootFolder();
	Logs server = new Logs();
	ManagedEntity[] host = null;
	
	double[] parameter = null;
	
	String outputFile = "hostout.log";
	PrintStream out = null;
	
	try {
		out = new PrintStream(new FileOutputStream(outputFile, true));
	} catch (FileNotFoundException e2) {
		// TODO Auto-generated catch block
		e2.printStackTrace();
	}
	
	try {
		host = new InventoryNavigator(rootFolder).searchManagedEntities("VirtualMachine");
	} catch (InvalidProperty e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (RuntimeFault e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (RemoteException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}
	
	
	while(true){
		try {
			if (server.getControl() != null){
				 
		
		try {
			if (server.getControl().equalsIgnoreCase(currentVM)){
				
				
					
						for (int i=0;i < host.length ; i++)
						{
							//System.out.println(host[i].getName());
							if (host[i].getName().contains("T15-vHost")){
								
							
							VirtualMachine vHost=(VirtualMachine) host[i];
							
							if ( vHost.getSummary().runtime.powerState.toString().equals("poweredOn"))
							{
							
										try 
										{
											parameter = server.getHostdetails(vHost.getName());
											
									           	out.append(vHost.getName()+ "	");
									            out.append(server.t+ "	");
									            out.append(parameter[0]+ "	");
									            out.append(parameter[1]+ "	");
									            out.append(parameter[2]+ "	");
									            out.append(parameter[3]+ "	");
									            out.append(parameter[4]+ "	");
									            out.println("");
										
									 	} 
										
										
										catch (RemoteException e) 
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										} 
										
										catch (MalformedURLException e) 
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
							}
							}
						}
						
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
						try
						{
							Thread.sleep(20000);
						} 
						catch (InterruptedException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						
						
					}

		
		
	

}
}