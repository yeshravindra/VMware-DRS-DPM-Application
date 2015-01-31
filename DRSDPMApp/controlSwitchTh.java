package DRSDPMApp;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import com.vmware.vim25.mo.ServiceInstance;

public class controlSwitchTh implements Runnable{
	
	ServiceInstance si=null;
	
	controlSwitchTh(ServiceInstance si){
		si=this.si;
	}
	
	
	public void run() {
		
		Logs contLog = new Logs();
		PrintStream out = null;
		try {
			out = new PrintStream(new FileOutputStream("control.conf"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while(true){
			try {
				contLog.setControl(out);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}
	
}
