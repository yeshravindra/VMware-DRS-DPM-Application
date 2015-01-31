package DRSDPMApplicationClientRunner;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import com.vmware.vim25.Description;
import com.vmware.vim25.HostVMotionCompatibility;
import com.vmware.vim25.InvalidProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PerfSampleInfo;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.VirtualDeviceConfigSpec;
import com.vmware.vim25.VirtualDeviceConfigSpecFileOperation;
import com.vmware.vim25.VirtualDeviceConfigSpecOperation;
import com.vmware.vim25.VirtualDisk;
import com.vmware.vim25.VirtualDiskFlatVer2BackingInfo;
import com.vmware.vim25.VirtualEthernetCard;
import com.vmware.vim25.VirtualEthernetCardNetworkBackingInfo;
import com.vmware.vim25.VirtualLsiLogicController;
import com.vmware.vim25.VirtualMachineCloneSpec;
import com.vmware.vim25.VirtualMachineConfigSpec;
import com.vmware.vim25.VirtualMachineFileInfo;
import com.vmware.vim25.VirtualMachineMovePriority;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.VirtualMachineRelocateSpec;
import com.vmware.vim25.VirtualPCNet32;
import com.vmware.vim25.VirtualSCSISharing;
import com.vmware.vim25.mo.ComputeResource;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;

public class InfoFetcher {
	public static Double getHostCPUUsage(String HostName) {

		try {
			ServiceInstance si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
			HostSystem host = (HostSystem) new InventoryNavigator(
					si.getRootFolder()).searchManagedEntity("HostSystem",
					HostName);

			PerformanceManager perfMgr = si.getPerformanceManager();
			PerfProviderSummary summary = perfMgr
					.queryPerfProviderSummary(host);
			int perfInterval = summary.getRefreshRate();
			PerfMetricId[] queryAvailablePerfMetric = perfMgr
					.queryAvailablePerfMetric(host, null, null, perfInterval);

			PerfQuerySpec qSpec = new PerfQuerySpec();
			qSpec.setEntity(host.getMOR());

			qSpec.setMaxSample(1);
			qSpec.setMetricId(queryAvailablePerfMetric);

			qSpec.intervalId = perfInterval;
			PerfEntityMetricBase[] pembs = perfMgr
					.queryPerf(new PerfQuerySpec[] { qSpec });

			for (int i = 0; pembs != null && i < pembs.length; i++) {

				PerfEntityMetricBase val = pembs[i];
				PerfEntityMetric pem = (PerfEntityMetric) val;
				PerfMetricSeries[] vals = pem.getValue();
				PerfSampleInfo[] infos = pem.getSampleInfo();

				for (int j = 0; vals != null && j < vals.length; ++j) {
					PerfMetricIntSeries val1 = (PerfMetricIntSeries) vals[j];

					long[] longs = val1.getValue();

					if (val1.getId().getCounterId() == 6)
						return new Double(longs[0]);

				}
			}

			si.getServerConnection().logout();

		} catch (InvalidProperty e) {
			e.printStackTrace();
		} catch (RuntimeFault e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static Double getVMCPUUsage(String vmname) throws RemoteException, MalformedURLException{
		
			ServiceInstance si = new ServiceInstance(new URL(
					"https://130.65.133.30/sdk"), "administrator", "12!@qwQW",
					true);

			VirtualMachine host = (VirtualMachine) new InventoryNavigator(
					si.getRootFolder()).searchManagedEntity("VirtualMachine",
							vmname);
			PerformanceManager perfMgr = si.getPerformanceManager();
			PerfProviderSummary summary = perfMgr
					.queryPerfProviderSummary(host);
			int perfInterval = summary.getRefreshRate();
			PerfMetricId[] queryAvailablePerfMetric = perfMgr
					.queryAvailablePerfMetric(host, null, null, perfInterval);

			PerfQuerySpec qSpec = new PerfQuerySpec();
			qSpec.setEntity(host.getMOR());

			qSpec.setMaxSample(1);
			qSpec.setMetricId(queryAvailablePerfMetric);

			qSpec.intervalId = perfInterval;
			PerfEntityMetricBase[] pembs = perfMgr
					.queryPerf(new PerfQuerySpec[] { qSpec });

			for (int i = 0; pembs != null && i < pembs.length; i++) {

				PerfEntityMetricBase val = pembs[i];
				PerfEntityMetric pem = (PerfEntityMetric) val;
				PerfMetricSeries[] vals = pem.getValue();
				PerfSampleInfo[] infos = pem.getSampleInfo();

				for (int j = 0; vals != null && j < vals.length; ++j) {
					PerfMetricIntSeries val1 = (PerfMetricIntSeries) vals[j];

					long[] longs = val1.getValue();

					if (val1.getId().getCounterId() == 6)
						return new Double(longs[0]);

				}
			}

			si.getServerConnection().logout();
		

		
		return null;
		
	}
	
	public static boolean deployVM(String newvmname) throws RemoteException, MalformedURLException{
		
		ServiceInstance si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
		Folder rootFolder = si.getRootFolder();
		
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", "emptyVMtemp");	
		if(vm==null)
		{
			System.out.println("No such template found");
			si.getServerConnection().logout();
			return false;
		}
		
		
		VirtualMachineCloneSpec cloneSpec =	new VirtualMachineCloneSpec();
		VirtualMachineRelocateSpec location = new VirtualMachineRelocateSpec();
		
		cloneSpec.setLocation(location);
		
		cloneSpec.setPowerOn(false);
		cloneSpec.setTemplate(false);
		
		Task task = vm.cloneVM_Task((Folder) vm.getParent(),newvmname, cloneSpec);
				
		System.out.println("A new vm is being deployed. Please wait ...");
				String status = task.waitForMe();
				if(status==Task.SUCCESS)
				{
				System.out.println("VM created successfully.");
				return true;
				}
				else
				{
				System.out.println("Failure -: VM cannot be created");
				return false;
				}
	}
	
	
	
	public static boolean migrateVM(String vmname, String hostname) throws RemoteException, MalformedURLException{
		ServiceInstance si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
		VirtualMachine vm = (VirtualMachine) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine",vmname);
		HostSystem host = (HostSystem) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("HostSystem",hostname);
		
		ComputeResource cr = (ComputeResource) host.getParent();
		String[] checks = new String[] {"cpu", "software"};
		HostVMotionCompatibility[] vmcs =si.queryVMotionCompatibility(vm, new HostSystem[]{host},checks );
		String[] comps = vmcs[0].getCompatibility();
		if(checks.length != comps.length)
		{
		System.out.println("CPU/software NOT compatible. Exit.");
		si.getServerConnection().logout();
		return false;
		}
		Task task = vm.migrateVM_Task(cr.getResourcePool(), host,VirtualMachineMovePriority.highPriority,VirtualMachinePowerState.poweredOn);
		System.out.println("Migration in progress...");
		if(task.waitForMe()==Task.SUCCESS)
		{
		System.out.println("Success");
		return true;
		}
		else
		{
		System.out.println("VMotion failed!");
		
		TaskInfo info = task.getTaskInfo();
		System.out.println(info.getError().getFault());
		}
		si.getServerConnection().logout();
		return false;
		
		
		
	}
	
	public static boolean createVM(String vmname, int rpno) throws Exception {
		ServiceInstance si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
		Folder rootFolder = si.getRootFolder();
	    Datacenter dc = (Datacenter) new InventoryNavigator(rootFolder).searchManagedEntity("Datacenter", "T15_DC");
	    
	    ResourcePool rp;
	    rp = (ResourcePool) new InventoryNavigator(dc).searchManagedEntities("ResourcePool")[rpno];
	    Folder vmFolder = dc.getVmFolder();
	   // HostSystem host = (HostSystem) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("HostSystem",hostname);
	    //System.out.println(host.getName());
	    String datastoreName = "DataStoreT15";
	    long diskSizeKB = 1000000;
	    VirtualMachineConfigSpec vmSpec = new VirtualMachineConfigSpec();
	    String diskMode = "persistent";
	       
	    vmSpec.setName(vmname);
	    vmSpec.setAnnotation("New T15 VirtualMachine");
	    vmSpec.setMemoryMB((long) 500);
	    vmSpec.setNumCPUs(1);
	    vmSpec.setGuestId("sles10Guest");
	    vmSpec.setPowerOpInfo(null);
	    // create virtual devices
	    int cKey = 1000;
	    VirtualDeviceConfigSpec scsiSpec = createScsiSpec(cKey);
	    VirtualDeviceConfigSpec diskSpec = createDiskSpec(datastoreName, cKey, diskSizeKB, diskMode);
	    VirtualDeviceConfigSpec nicSpec = createNicSpec("VM Network", "Network Adapter 1");
	    
	    vmSpec.setDeviceChange(new VirtualDeviceConfigSpec[]{scsiSpec, diskSpec, nicSpec});
	    
	    // create vm file info for the vmx file
	    VirtualMachineFileInfo vmfi = new VirtualMachineFileInfo();
	    vmfi.setVmPathName("["+ datastoreName +"]");
	    vmSpec.setFiles(vmfi);
	    
	    // call the createVM_Task method on the vm folder
	    Task task = vmFolder.createVM_Task(vmSpec, rp, null);
	    String result = task.waitForMe();
	    if (result == Task.SUCCESS){
	    	VirtualMachine vm = (VirtualMachine) new InventoryNavigator(rootFolder).searchManagedEntity("VirtualMachine", vmname);
	    	vm.powerOnVM_Task(null);
	    	System.out.println("A new vm "+vmname+" has been created.");
	    }
		
		
		
		return false;
	}
	
	
	static VirtualDeviceConfigSpec createScsiSpec(int cKey)
	{
		VirtualDeviceConfigSpec scsiSpec = new VirtualDeviceConfigSpec();
		scsiSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
		VirtualLsiLogicController scsiCtrl = new VirtualLsiLogicController();
		scsiCtrl.setKey(cKey);
		scsiCtrl.setBusNumber(0);
		scsiCtrl.setSharedBus(VirtualSCSISharing.noSharing);
		scsiSpec.setDevice(scsiCtrl);
		return scsiSpec;
	}
  
	static VirtualDeviceConfigSpec createDiskSpec(String dsName, int cKey, long diskSizeKB, String diskMode)
	{
		VirtualDeviceConfigSpec diskSpec = new VirtualDeviceConfigSpec();
		diskSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
		diskSpec.setFileOperation(VirtualDeviceConfigSpecFileOperation.create);
		
		VirtualDisk vd = new VirtualDisk();
		vd.setCapacityInKB(diskSizeKB);
		diskSpec.setDevice(vd);
		vd.setKey(0);
		vd.setUnitNumber(0);
		vd.setControllerKey(cKey);
		
		VirtualDiskFlatVer2BackingInfo diskfileBacking = new VirtualDiskFlatVer2BackingInfo();
		String fileName = "["+ dsName +"]";
		diskfileBacking.setFileName(fileName);
		diskfileBacking.setDiskMode(diskMode);
		diskfileBacking.setThinProvisioned(true);
		vd.setBacking(diskfileBacking);
		return diskSpec;
	}
  
	static VirtualDeviceConfigSpec createNicSpec(String netName, String nicName) throws Exception
	{
		VirtualDeviceConfigSpec nicSpec = new VirtualDeviceConfigSpec();
		nicSpec.setOperation(VirtualDeviceConfigSpecOperation.add);
		
		VirtualEthernetCard nic =  new VirtualPCNet32();
		VirtualEthernetCardNetworkBackingInfo nicBacking = new VirtualEthernetCardNetworkBackingInfo();
		nicBacking.setDeviceName(netName);
		
		Description info = new Description();
		info.setLabel(nicName);
		info.setSummary(netName);
		nic.setDeviceInfo(info);
		
		// type: "generated", "manual", "assigned" by VC
		nic.setAddressType("generated");
	    nic.setBacking(nicBacking);
	    nic.setKey(0);
	   
	    nicSpec.setDevice(nic);
	    return nicSpec;
	}



	public static Boolean isLessThan30(String name) throws RemoteException, MalformedURLException {
		ServiceInstance si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
		HostSystem host = (HostSystem) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("HostSystem", name);
		//System.out.println(host.getSummary().);
		return (getHostCPUUsage(name) / 4786  <= 0.3);
		//return false;
	}

	public static void powerOffVhost(String hostname) throws RemoteException, MalformedURLException {
		ServiceInstance si = new ServiceInstance(new URL("https://130.65.133.30/sdk"), "administrator", "12!@qwQW",true);
		HostSystem host = (HostSystem) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("HostSystem", hostname);
		
		Task powerofftask = host.enterMaintenanceMode(0, true);
		String poff = powerofftask.waitForMe();
		if (poff == Task.SUCCESS){
			//host.shutdownHost_Task(true);
			System.out.println(hostname+" has been taken out of service.");
		}
		
		
	}
	
}
