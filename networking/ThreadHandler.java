package networking;


//The class which runs threads
public class ThreadHandler implements Runnable {
	int type;		//type to check if server or client thread
	String ip;		//IPAddress
	int port;		//port
	
	
	//constructor to define thread
	public ThreadHandler(int type, String ip, int port)
	{
		this.type = type;
		this.ip = ip;
		this.port = port;
	}
	
	public void run()
	{
		if (type==0)		//if server
		{
			udp u = new udp(port);		//setup a server
			try {
				u.startServer();		
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else			//if client
		{		
			UDPClient u = new UDPClient(ip,port);		//start client
		}
	}
}
