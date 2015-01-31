package DRSDPMApp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.Calendar;
import java.util.Scanner;

import org.apache.axis.types.Time;
import org.sblim.cimclient.internal.uri.DateTimeValue;

import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class PThread implements Runnable {
	 
ServiceInstance si=null;
String currentVM = null;
boolean collectParam = false;	
	
	
	PThread (ServiceInstance si, String currentVM) throws RemoteException, IOException{
		Logs server = new Logs();
		this.si= si;
		this.currentVM = currentVM;
		if (server.getControl() != null){
			collectParam = server.getControl().equalsIgnoreCase(currentVM);
		}
		
	}
	
	public void run() {
		Folder rootFolder=si.getRootFolder();
		Logs server = new Logs();
		ManagedEntity vms = null;
		
		double[] parameter = null;
		
		String outputFile = "vmout.log";
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream(outputFile, true));
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		try {
			vms = new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", currentVM);
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
								
							
								VirtualMachine vMachine=(VirtualMachine) vms;
								
								if ( vMachine.getSummary().runtime.powerState.toString().equals("poweredOn") && vMachine.getName().contains("T15-")){
								//System.out.println(vMachine.getName());
									
											try {
												parameter = server.getVMdetails(vMachine.getName());
												
												//server.t;
												Date d = new Date((long) parameter[5]);
												//Time t = new Time (parameter[5]);
												 
										            
										           	out.append(vMachine.getName()+ "	");
										            out.append(server.t+ "	");
										            out.append(parameter[0]+ "	");
										            out.append(parameter[1]+ "	");
										            out.append(parameter[2]+ "	");
										            out.append(parameter[3]+ "	");
										            out.append(parameter[4]+ "	");
										            out.println("");
										            
										           
												
												
											} catch (RemoteException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (MalformedURLException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
								}
								
							
							
		
			
			try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			
		}
	}
}

