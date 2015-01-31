package DRSDPMApplicationClientRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class AppController {

	public static void main(String[] args) throws Exception {
		ServiceInstance si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
		Folder rootFolder = si.getRootFolder();
		int choice;
		String newvmname;
		System.out.println("Select an option - ");
		System.out.println("1. To create a new VM.");
		System.out.println("2. To clone from a standard template.");		
		System.out.println("3. To balance the load on datcenter T15_DC.");
		Scanner input = new Scanner(System.in);
		choice = input.nextInt();
		InfoFetcher server = new InfoFetcher ();
		Double CPUusage = null;
		Double FirstCPUusage = null;
		Double SecondCPUusage = null;
		Double ThirdCPUusage = null;
		HashMap<Double,String > vHost = new HashMap<Double,String>();
		HashMap<String, Double> low_cpu = new HashMap<String, Double>();
		HashMap<String, Double> high_cpu = new HashMap<String, Double>();
		switch (choice) {
		/*case 1:
			CPUusage = server.getHostCPUUsage("130.65.133.32");
			System.out.println(CPUusage);
			break;
		
		case 2:
			//server.deployVM();
			break;
			
		case 3:
			server.migrateVM("T15-VM03-Ubu", "130.65.133.31");
			break;
		*/
		case 2: // drs1 deploy from template
			System.out.println("Enter a name for the new VM");
			newvmname = input.next();
			boolean deployed = false;
			deployed = server.deployVM(newvmname);
			if (deployed == true){
				FirstCPUusage = server.getHostCPUUsage("130.65.133.31");
				SecondCPUusage = server.getHostCPUUsage("130.65.133.32");
				
				String newhost = null;
				if (FirstCPUusage >= SecondCPUusage){
					newhost = "130.65.133.32";
				}
				else {
					newhost = "130.65.133.31";
				}
				
				if (server.migrateVM(newvmname, newhost)){
					System.out.println("Please wait, while the vm "+newvmname+" is being poweredon on the host "+newhost);
					VirtualMachine vm = (VirtualMachine) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine",newvmname);
					vm.powerOnVM_Task(null); //send here
				}
				else {
					System.out.println("vm motion failed!");
				}
			}
			else {
				System.out.println("Virtual machine could not be deployed");
			}
			break;
			
		case 1:  //drs1 create new vm
			System.out.println("Enter a name for the new VM");
			newvmname = input.next();
			FirstCPUusage = server.getHostCPUUsage("130.65.133.31");
			SecondCPUusage = server.getHostCPUUsage("130.65.133.32");
			String newhost = null;
			int rpno = 0;
			if (FirstCPUusage >= SecondCPUusage){
				//newhost = "130.65.133.32";
				rpno = 1;
			}
			else {
				//newhost = "130.65.133.31";
				rpno = 0;
			}
			
			server.createVM(newvmname,rpno);
			break;
			
			
		case 3: //drs2 load balance
			si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
			FirstCPUusage = server.getHostCPUUsage("130.65.133.31");
			SecondCPUusage = server.getHostCPUUsage("130.65.133.32");
			ThirdCPUusage = server.getHostCPUUsage("130.65.133.33");
			
			System.out.println("CPU usage before load balance");
			System.out.println("130.65.133.31 : " +FirstCPUusage);
			System.out.println("130.65.133.32 : " +SecondCPUusage);
			System.out.println("130.65.133.33 : " +ThirdCPUusage);
			
			vHost.put(FirstCPUusage, "130.65.133.31");
			vHost.put(SecondCPUusage, "130.65.133.32");
			vHost.put(ThirdCPUusage, "130.65.133.33");
			
			Double least = null;
			Double highest = null;
			
			if (FirstCPUusage < SecondCPUusage && FirstCPUusage < ThirdCPUusage)
				least = FirstCPUusage;
			else if (SecondCPUusage < FirstCPUusage && SecondCPUusage < ThirdCPUusage)
				least = SecondCPUusage;
			else
				least = ThirdCPUusage;
			
			//finding highest cpu usage
			if (FirstCPUusage > SecondCPUusage && FirstCPUusage > ThirdCPUusage)
				highest = FirstCPUusage;
			else if (SecondCPUusage > FirstCPUusage && SecondCPUusage > ThirdCPUusage)
				highest = SecondCPUusage;
			else
				highest = ThirdCPUusage;
			
	
			
			HostSystem high_host = (HostSystem) new InventoryNavigator(rootFolder).searchManagedEntity("HostSystem", vHost.get(highest));
			HostSystem least_host = (HostSystem) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("HostSystem",vHost.get(least));
			System.out.println(least_host);
			System.out.println(high_host);
			int j=1;
			String highVm = null;
			for (VirtualMachine v : high_host.getVms())
			{
				if(!(server.getVMCPUUsage(v.getName())==null))
				{
					high_cpu.put(v.getName(),server.getVMCPUUsage(v.getName()) );
				System.out.println("VM: " + v.getName() + " :" + server.getVMCPUUsage(v.getName()));
				}
				
				j++;
			}
			
			
				for (VirtualMachine v : least_host.getVms()){
					if(!(server.getVMCPUUsage(v.getName())==null)){
					//System.out.println("VM usages");
					low_cpu.put(v.getName(),server.getVMCPUUsage(v.getName()) );
					System.out.println("VM: " + v.getName() + " :" + server.getVMCPUUsage(v.getName()));				
					}
				}
			
			
			
			
			

			Iterator iterator = high_cpu.entrySet().iterator();
			Map.Entry pairs = (Map.Entry) iterator.next();
			
			double vmLow =  highest-((Double) pairs.getValue()+least);
			String vmNameLow = pairs.getKey().toString();
			
			while (iterator.hasNext())  
	        {  
	           pairs = (Map.Entry) iterator.next();  
	          
	            if(vmLow >(highest-((Double) pairs.getValue()+least)))
	            {
	            	  vmLow = highest-((Double) pairs.getValue()+least);
	            	  vmNameLow = pairs.getKey().toString(); 
	            }
	        }
			
			if (server.migrateVM(vmNameLow, vHost.get(least))){
				System.out.println("Migrated!");
			}
			
			FirstCPUusage = server.getHostCPUUsage("130.65.133.31");
			SecondCPUusage = server.getHostCPUUsage("130.65.133.32");
			ThirdCPUusage = server.getHostCPUUsage("130.65.133.33");

			System.out.println("After Load Balancing vHost List: ");
			System.out.println("130.65.133.31: "+FirstCPUusage);
			System.out.println("130.65.133.32: "+SecondCPUusage);
			System.out.println("130.65.133.33: "+ThirdCPUusage);
			
			
			break;
		
		}

	}

}
