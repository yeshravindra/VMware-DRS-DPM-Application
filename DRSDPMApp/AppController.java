/**
 * 
 */
package DRSDPMApp;

import java.io.FileOutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.util.Scanner;

import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.FileFault;
import com.vmware.vim25.InsufficientResourcesFault;
import com.vmware.vim25.InvalidDatastore;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.OutOfBounds;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VmConfigFault;

/**
 * @author admin
 *
 */


public class AppController {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		int choice;
		System.out.println("Enter choice");
		Scanner input = new Scanner(System.in);
		choice = input.nextInt();
		Double value;
		double[] parameter = null;
		AppManager server = new AppManager();
		Logs log = new Logs();
		
		
		switch (choice) {
		
		case 1:
			server.createVM();
			break;
			
		case 2:
			server.getPerformance();
			break;
		
		case 3:
			value = server.getCPU();
			System.out.println(value);
			break;
			
		case 4:
			server.getAllCounters();
			break;
			
		case 5:
			
			parameter = log.getVMdetails("T15-VM01-Ubu01");
			System.out.println(parameter[0]);
			break;
		
		case 6:
			String a[][]=null;
			PrintStream out = new PrintStream(new FileOutputStream("control.conf"));
			log.setControl(out);
			System.out.println("************");
			log.setControl(out);
			break;
			
		case 7:
			System.out.println(log.getControl());
			break;
		
		case 8:
			System.out.println(log.PingTest("74.125.239.102"));
			
		case 9:	
			server.getComputername();
		
		}
		
	
	
	}
	
	
	

}
