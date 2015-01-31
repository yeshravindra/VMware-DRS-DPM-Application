package DRSDPMApplicationClientRunner;

public class StatusCheck implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		InfoFetcher server = new InfoFetcher();
		Double CPUusage = null;
		while(true){
			
			CPUusage = server.getHostCPUUsage("130.65.133.32");
			System.out.println(CPUusage);
			
			
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} //while ends here
		
	}
		
		
		
	
}
