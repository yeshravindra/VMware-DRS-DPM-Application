package DRSDPMApp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


import java.util.TimeZone;

import org.apache.axis.types.Time;

import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PerfSampleInfo;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ServiceInstance;
import com.vmware.vim25.mo.VirtualMachine;

public class Logs {
	
	public String t;
	//TimeZone tz = TimeZone.getTimeZone("UTC");
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
    //df.setTimeZone(tz);
    String nowAsISO = df.format(new Date());
    
public double[] getVMdetails(String vmname) throws RemoteException, MalformedURLException{
        
        String vcenter = "https://130.65.133.30/sdk";
        String uname = "administrator";
        String pwd = "12!@qwQW";
        double[] parameter = new double[6];
        
        ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);        
        Folder rootFolder = si.getRootFolder();
        InventoryNavigator inv = new InventoryNavigator(si.getRootFolder());
        
        ManagedEntity VMe = new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine", vmname);
        
        System.out.println(VMe.getName());
        if(VMe == null)    {
            System.out.println("Virtual Machine "+ vmname + " cannot be found.");
            si.getServerConnection().logout();
            return null;
        }
        
        VirtualMachine vm = (VirtualMachine) new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine", vmname);
        PerformanceManager perfMgr = si.getPerformanceManager();
        PerfProviderSummary providerSum = perfMgr.queryPerfProviderSummary(vm);
        //providerSum.setRefreshRate(5);
        
        int RefRate = providerSum.getRefreshRate();      //change here       
       
        //RefRate = 5;
        PerfMetricId[] queryAvailablePerfMetric = perfMgr.queryAvailablePerfMetric(vm, null, null, 20);
        
        
        PerfQuerySpec qSpec = new PerfQuerySpec();
        qSpec.setEntity(vm.getMOR());        
        qSpec.setMaxSample(1);
        qSpec.setMetricId(queryAvailablePerfMetric);
        qSpec.intervalId = RefRate;
        
        PerfEntityMetricBase[] pembs = perfMgr.queryPerf(new PerfQuerySpec[] { qSpec });
        
        
        for (int i = 0; pembs != null && i < pembs.length; i++) 
        {
            PerfEntityMetricBase values = pembs[i];
             PerfEntityMetric pem = (PerfEntityMetric) values;
             PerfMetricSeries[] pms = pem.getValue();
             PerfSampleInfo[] infos = pem.getSampleInfo();
                         
             for (int j = 0; pms != null && j < pms.length; ++j)
             {
                 PerfMetricIntSeries pmis = (PerfMetricIntSeries) pms[j];
                 long[] longs = pmis.getValue();
                 
                
                 
                 if (pmis.getId().getCounterId() == 6)	//cpu
                 {
                     parameter[0] = (double) longs[0];
                 }
                 if (pmis.getId().getCounterId() == 24) //memory
                 {
                     parameter[1] = (double) longs[0];
                 }
                 if (pmis.getId().getCounterId() == 125) //disk io
                 {
                     parameter[2] = (double) longs[0];
                 }
                 if (pmis.getId().getCounterId() == 143) //Network
                 {
                     parameter[3] = (double) longs[0];
                 }
                 if (pmis.getId().getCounterId() == 288) //System resource
                 {
                     parameter[4] = (double) longs[0];
                 }
                 
                 //t = infos[i].timestamp.getTime();
                 t = df.format(infos[i].timestamp.getTime());
                 
                 
             }
             
        }
     
        return parameter;   
    }
    
    public String[][] setControl(PrintStream out) throws IOException{
    	String vcenter = "https://130.65.133.30/sdk";
        String uname = "administrator";
        String pwd = "12!@qwQW";
        boolean closeFile = false;
        String contVM = null;
        String contFile = "control.conf";
        
        
        ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);        
        Folder rootFolder = si.getRootFolder();
        InventoryNavigator inv = new InventoryNavigator(si.getRootFolder());
        
        ManagedEntity[] VMe = new InventoryNavigator(si.getRootFolder()).searchManagedEntities("VirtualMachine");
        String a[][] = new String[VMe.length][2];
        int z = 0;
        //PrintStream out = new PrintStream(new FileOutputStream("control.conf"));
        contVM = getControl();
        
        
    	for (int i=0; i < VMe.length; i++){
    		
    		VirtualMachine vm = (VirtualMachine) VMe[i];
    		//System.out.println(PingTest(vm.getSummary().getGuest().getIpAddress()));
    		// change here ******
    		
    		if ((vm.getSummary().runtime.powerState.toString().equals("poweredOn")) && (PingTest(vm.getSummary().getGuest().getIpAddress()) == true)  && vm.getName().contains("T15-")){
    			
    			

    			a[z][0] = vm.getName();
    			a[z++][1] = vm.getSummary().getGuest().getIpAddress();
    			
    			if (closeFile == false && contVM == null){
    				out.close();
    				out = new PrintStream(new FileOutputStream(contFile));
    				System.out.println("Host logs are collected by "+a[0][0]);
    	    		out.print(a[0][0]);
    	    		closeFile = true;
    	    		//out.close();
    	    		
    			}
    			
    		}
    		
    		if (contVM != null && vm.getName().equalsIgnoreCase(contVM) && (PingTest(vm.getSummary().getGuest().getIpAddress()) == false)){
    			out.close();
    			out = new PrintStream(new FileOutputStream(contFile));
    			//out.close();
    		}
    		
    	}
    	
    	if (a == null){
    		System.out.println("There are no VMs running in the Datacenter");
    	}
    	
    	return a;
    	
    }
    
    public String getControl() throws IOException{
		BufferedReader br = new BufferedReader(new FileReader("control.conf"));
		String line = br.readLine();
    	br.close();
    	//System.out.println(line);
    	return line;
    }
    
    
    
	public boolean PingTest(String vmIP) throws IOException{
		
		String command;
		String PingResult = "";
		command = vmIP;
        command = "ping "+command+" -c 4";
       //command = "ping "+command;
        Runtime r = Runtime.getRuntime();
        Process p = r.exec(command);		/*Ping host ip executes from this line*/
        BufferedReader in = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                	PingResult += inputLine;
                		}		
                	in.close();
                		//if((PingResult.contains("Reply from") ) )
                		if((PingResult.contains("bytes from") ) )
                		{
                			/*Pinging*/
                			
                			return true;
                		}
                		else	{	
                			/*Not Pinging*/
                			return false;
                			
                		}
                		
	}
	
	public double[] getHostdetails(String hostname) throws RemoteException, MalformedURLException
    {
        
        String vcenter = "https://130.65.132.14/sdk";
        String uname = "administrator";
        String pwd = "12!@qwQW";
        double[] parameter = new double[6];
        
        ServiceInstance si = new ServiceInstance(new URL(vcenter), uname, pwd, true);        
        Folder rootFolder = si.getRootFolder();
        InventoryNavigator inv = new InventoryNavigator(si.getRootFolder());
        
        ManagedEntity VMe = new InventoryNavigator(si.getRootFolder()).searchManagedEntity("VirtualMachine", hostname);
        
        System.out.println(VMe.getName());
        if(VMe == null)    
        {
            System.out.println("The host"+ hostname + " cannot be found.");
            si.getServerConnection().logout();
            return null;
        }
        
        VirtualMachine host = (VirtualMachine) VMe;
        PerformanceManager perfMgr = si.getPerformanceManager();
        PerfProviderSummary providerSum = perfMgr.queryPerfProviderSummary(host);
        int RefRate = providerSum.getRefreshRate();            
        PerfMetricId[] queryAvailablePerfMetric = perfMgr.queryAvailablePerfMetric(host, null, null, RefRate);
        
        
        PerfQuerySpec qSpec = new PerfQuerySpec();
        qSpec.setEntity(host.getMOR());        
        qSpec.setMaxSample(1);
        qSpec.setMetricId(queryAvailablePerfMetric);
        qSpec.intervalId = RefRate;
        
        PerfEntityMetricBase[] pembs = perfMgr.queryPerf(new PerfQuerySpec[] { qSpec });
        
        
        for (int i = 0; pembs != null && i < pembs.length; i++) 
        {
            PerfEntityMetricBase values = pembs[i];
             PerfEntityMetric pem = (PerfEntityMetric) values;
             PerfMetricSeries[] pms = pem.getValue();
             PerfSampleInfo[] infos = pem.getSampleInfo();
            
             
             for (int j = 0; pms != null && j < pms.length; ++j)
             {
            	 
                 PerfMetricIntSeries pmis = (PerfMetricIntSeries) pms[j];
                 long[] longs = pmis.getValue();
                 
                 
                 if (pmis.getId().getCounterId() == 6)	
                 {
                     parameter[0] = (double) longs[0];
                 }
                 if (pmis.getId().getCounterId() == 24) //
                 {
                     parameter[1] = (double) longs[0];
                 }
                 if (pmis.getId().getCounterId() == 125) //
                 {
                     parameter[2] = (double) longs[0];
                 }
                 if (pmis.getId().getCounterId() == 143) //
                 {
                     parameter[3] = (double) longs[0];
                 }
                 if (pmis.getId().getCounterId() == 288) //
                 {
                     parameter[4] = (double) longs[0];
                 }
                 
                 t = df.format(infos[i].timestamp.getTime());
                 
                 
             }
             
        }
     
        return parameter;   
    }
}