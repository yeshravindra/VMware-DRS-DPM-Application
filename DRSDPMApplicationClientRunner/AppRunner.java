package DRSDPMApplicationClientRunner;

public class AppRunner {

	public static void main(String[] args) throws InterruptedException {
		/*StatusCheck checker = new StatusCheck();
		Thread CheckerThread = new Thread(checker);
		CheckerThread.start();       
		
		CheckerThread.join();*/
		
		DPM checker = new DPM();
		Thread CheckerThread = new Thread(checker);
		CheckerThread.start();       
		
		CheckerThread.join();
	}

}
