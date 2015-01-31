package DRSDPMApplicationClientRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Set;

import com.vmware.vim25.HostSystemPowerState;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class DPM implements Runnable{
	
	private static HashMap<String, Boolean> hostPoweredOff = new HashMap<String, Boolean>();
	private static HashMap<HostSystem, Boolean> host_list = new HashMap<HostSystem, Boolean>();
	private static HashMap<HostSystem, Integer> vmNumber = new HashMap<HostSystem, Integer>();
	
	
	
	
	public void run() {
		
		ServiceInstance si = null;
		try {
			si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ManagedEntity[] hosts = null;
		try {
			hosts = (ManagedEntity[]) new InventoryNavigator(si.getRootFolder()).searchManagedEntities("HostSystem");
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
		
			
			for (ManagedEntity h : hosts) {
				
				HostSystem h1 = (HostSystem) h;
				hostPoweredOff.put(h1.getName(), false);
			}
		
		
		
		
		InfoFetcher cpuUsage = new InfoFetcher();
		while (true) {
			//System.out.println("Hey");
			try {
				
				

				Set<String> offset = hostPoweredOff.keySet();
				int count = 0;
				for (String name : offset) {

					if (!hostPoweredOff.get(name)) {

						count++;
					}

				}

					if (count == 1) {

					System.out.println("Only One host Left.Turning off DPM");

					return;
				}
				host_list.clear();
				//System.out.println("Hello");
				for (ManagedEntity h : hosts) {
					HostSystem h1 = (HostSystem) h;

					try {
						host_list.put(h1, InfoFetcher.isLessThan30(h1.getName()));
						vmNumber.put(h1, h1.getVms().length);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				Set<HostSystem> host_set = host_list.keySet();

				HostSystem toremove = null;
				for (HostSystem s1 : host_set) {
					
					if (s1.getRuntime().getPowerState().compareTo(HostSystemPowerState.poweredOff) != 0) {
							System.out.println("DPM activated.");
						if (!hostPoweredOff.get(s1.getName())&& host_list.get(s1)) {
							
							System.out.println("The Host " + s1.getName() + " is using less than 30%");
							migrateVMS(s1);
							toremove = s1;
							break;
							

						}
						
					}

				}
				
				if (toremove != null)
					hostPoweredOff.put(toremove.getName(), true);
					
				
				
				
			} catch (InvalidProperty e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RuntimeFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				System.out.println("Thread sleeping for some time");
				Thread.sleep(1 * 60 * 1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
	}

	private void migrateVMS(HostSystem from) throws InvalidProperty,
			RuntimeFault, RemoteException, MalformedURLException,
			InterruptedException {
		// TODO Auto-generated method stub

		Set<HostSystem> vms = vmNumber.keySet();
		HostSystem to = null;
		System.out.println("Choosing the to host to the one with lesser VMS");
		for (HostSystem h : vms) {

			if (!hostPoweredOff.get(h.getName())
					&& !h.getName().equalsIgnoreCase(from.getName())) {

				if (to == null)
					to = h;
				if (vmNumber.get(to) > vmNumber.get(h))
					to = h;
			}

		}

		for (VirtualMachine v : from.getVms()) {

			InfoFetcher server = new InfoFetcher();

			System.out.println("Migrating VM:" + v.getName() + "  from "
					+ from.getName() + " to " + to.getName());
			if (server.getHostCPUUsage(to.getName()) <= 1800){
			server.migrateVM(v.getName(), to.getName());
			}
			System.out.println("Successfully migrated VM:" + v.getName()
					+ "  from " + from.getName() + " to " + to.getName());

		}
		System.out.println("Powering off VHOST:" + from.getName());
		InfoFetcher.powerOffVhost(from.getName());

	}
	
	

	
}
