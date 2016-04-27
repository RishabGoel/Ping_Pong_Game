package networking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.google.gson.Gson;

/*
 * This class looks for a all the peer ips in the network. If they have their server on
 * then a client server connection is made. Else, a server is made .
 * We are using Gson library to convert java objects to json strings for easy communication
 * and for converting it back to java object  
 */
public class Main {
	static int REQUEST_COUNT_FAILURE=4;		// trials before a machine is said not to have made a server
	public static ArrayList<String> connected_ips=new ArrayList<String>(); 
	public static ArrayList<String> ips=new ArrayList<String>();		//ArrayList of peer ips 
	//public static int[] ids = new int[3];
	
	public static void startNetworking() throws IOException{
//		ips.add(0, "10.192.34.21");
//		ips.add(1, "10.192.59.115");
		
//		Input the ips of the peers
		BufferedReader inFromUser;
		for(int i=0;i<2;i++){
			inFromUser = 
			        new BufferedReader(new InputStreamReader(System.in));
			ips.add(inFromUser.readLine());
		}
		
		startup();
	}
	/*
	 * setup sets up the connection between clients and server and vice versa
	 */
	public static void startup() throws IOException{
		int max=-1;
		/*for(int w=0;w<4;w++)
		{
			Board.paddle[w] = new Paddle();
		}*/
		ArrayList<Thread> th = new ArrayList<Thread>() ;
		System.out.println("got a connection");
		Boolean[] ids = new Boolean[4];
		for(int i=0;i<4;i++)
		{
			ids[i] = false;
		}
		for (int i=0;i<ips.size();i++){
			System.out.println("got a connection");
			System.out.println("size : "+ips.size());
			for(int j=0;j<1;j++){
				try{
					System.out.println("got a connection3");
					System.out.println(j);
					DatagramSocket clientSocket = new DatagramSocket();
					Paddle p = new Paddle();
					p.index = -2;
					String s = new Gson().toJson(p);
				      byte[] sendData = s.getBytes();
					clientSocket.setSoTimeout(12000);
					DatagramPacket sendPacket = 
						new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ips.get(i)), 2600);   
					clientSocket.send(sendPacket);
					DatagramPacket receivePacket = new DatagramPacket(new byte[1024], new byte[1024].length);
					clientSocket.receive(receivePacket);
					//received position of players

					String sentence = new String(receivePacket.getData());
					String received = "";
					for (int k=0;k<sentence.length();k++){
			        	  if(sentence.charAt(k)==0){
			        		  break;
			        	  }
			        	  received+=sentence.charAt(k);
			          }
			          Paddle a=new Gson().fromJson(received,Paddle.class);
			          //if(a.index>max) {max = a.index;}
			          ids[a.index] = true;

//					System.out.println("got a connection5");
					Thread t1=new Thread(new ThreadHandler(1,ips.get(i),2600));
					th.add(t1);

					break;
				}
				catch(Exception e){
					continue;
				}
				
			}

		}
//		Setting the Index of the player
		for(int k=0;k<4;k++)
		{
			if(!ids[k])
			{
				max = k;
				break;
			}
		}
		/*
		 * Update the local state of the machine to be transmitted
		 */
		Board.paddle[max].index=max;
		Board.p_idx=max;
		System.out.println("got a connection1");
		Thread t_s=new Thread(new ThreadHandler(0,"",2600));
		t_s.start();
		for(int x=0;x<th.size();x++)
		{
			th.get(x).start();
		}
//		System.out.println("got a connection2");
	}
}