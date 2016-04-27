package networking;

import java.io.*; 
import java.net.*;
import java.util.Arrays;

import com.google.gson.Gson; 
  

// class for the client side
class UDPClient { 
			int port;
			String ip;
			int latest_id; //to store index which it has been receiving
			Boolean b = false; //to check disconnection(false if connected)
			int fails=0; //number of times failed in connecting
			
			
			//main constructor
			public UDPClient(String ip, int port)
			{
				this.ip = ip;
				this.port = port;
				initialize();
			}

    public void initialize(){ 
    {
    	// continuous operation
    	while(true)
    	{
    		
    	if(b) break;		//if not connected then exit
     try {
        String serverHostname = new String (ip);
        
  
      BufferedReader inFromUser = 
        new BufferedReader(new InputStreamReader(System.in)); 
  
      DatagramSocket clientSocket = new DatagramSocket(); // Connection socket
      
  
      InetAddress IPAddress = InetAddress.getByName(serverHostname); //IPAddress of the server
      
      //System.out.println ("Attemping to connect to " + IPAddress + 
                         // ") via UDP port 9876");
  
      //byte[] sendData = new byte[1024]; 
      byte[] receiveData = new byte[1024];   //byte stream
  
      //System.out.print("Enter Message: ");
      
      String s = new Gson().toJson(Board.paddle[Board.p_idx]);		//Converting object to string
      //latest_id = Board.p_idx;
      byte[] sendData = s.getBytes();			//getting byte stream
      
      DatagramPacket sendPacket = 
         new DatagramPacket(sendData, sendData.length, IPAddress, port); //Data Packet to be sent
  
      clientSocket.send(sendPacket); //sending packet
  
      DatagramPacket receivePacket = 
         new DatagramPacket(receiveData, receiveData.length); //Data Packet to be received
  
      //System.out.println ("Waiting for return packet");
      clientSocket.setSoTimeout(10000);		//TimeOut for disconnection

      try {
    	  clientSocket.setSoTimeout(10000);
           clientSocket.receive(receivePacket); //receiving the data
           
           String sent = "";
           String sentence = new String(receivePacket.getData());	//Converting byte stream to string
           for (int i=0;i<sentence.length();i++){
         	  if(sentence.charAt(i)==0){
         		  break;
         	  }
         	  sent+=sentence.charAt(i);
           }
           
           Paddle a=new Gson().fromJson(sent,Paddle.class);		//Converting String to Object
           
           
           //Setting index of the players
           if(!(a.index==-2))		
           {
         	  Board.paddle[a.index] = a;
         	  latest_id = a.index;		//to remember
           }
  
           InetAddress returnIPAddress = receivePacket.getAddress();	//checking IPaddress
     
           int portfrom = receivePacket.getPort();

           //System.out.println ("From server at: " + returnIPAddress + 
                               //":" + portfrom);
           //System.out.println(modifiedSentence.replaceAll("[\n\r]", ""));
           //System.out.println("Message from server: " + sentence); 

          }
      //Catching timeout exception
      catch (SocketTimeoutException ste)
          {
    	  
    	  //if time exceeded
    	  
       	   //Board.paddle[latest_id].index=-1;		//assigning Computer Player
       	   fails++;
       	   //if connection timed out for more than two times
       	   if(fails>=3){
       		b = true;
       		Board.paddle[latest_id].index=-1;
       	   }
       	   
       	   //System.out.println("working fine");
       	   //break;
          
           //System.out.println ("Timeout Occurred: Packet assumed lost");
      }
      //if(b) break;
  
      clientSocket.close(); 
     }
   catch (UnknownHostException ex) { 
     System.err.println(ex);
    }
   catch (IOException ex) {
     System.err.println(ex);
    }
     try {
		Thread.sleep(6);
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
  } 
}} 
