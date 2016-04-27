package networking;

import java.io.*; 
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import com.google.gson.Gson; 
  

//Server Side 
class udp { 
	int port;
  
    ArrayList<Long> last_ping = new ArrayList<Long>(4);			//Array of last time ping
    ArrayList<Integer> ids = new ArrayList<Integer>(4);			// Mapping from ips to ids 
      
  public udp(int port){
    this.port=port;
  }
  
  //Start server
  public void startServer() throws Exception 
    {
	 
	//default values set for last ping and ids  
    for(int i=0;i<4;i++)
    {
      last_ping.add(new Long(-1));
      ids.add(-1);
    }
       
    
      //Server Socket	
      DatagramSocket serverSocket = new DatagramSocket(port); 
  
      byte[] receiveData = new byte[1024]; 
      byte[] sendData  = new byte[1024]; 
      Boolean b = false;
      int[] m = new int[4];		//to check if it was initial data
  
      
      //Continuous operation
      while(true)
        {
    	  
        try
           {
        for(int y=0;y<last_ping.size();y++)
        {
        //check if time difference is large	
        if(System.currentTimeMillis()-last_ping.get(y)>=5000 && last_ping.get(y)>0 && !(m[y]==-2))
          {
            Board.paddle[ids.get(y)].index = -1;		//initiating the computer bot
            //System.out.println("success");
            //setting the default values
            last_ping.set(y,new Long(-1));	
            ids.set(y, -1);
            m[y]=0;
            //break;
          }
        }
  
          receiveData = new byte[1024]; 

          DatagramPacket receivePacket = 
             new DatagramPacket(receiveData, receiveData.length);

          serverSocket.setSoTimeout(5000);		//setting timeout to check disconnection
          serverSocket.receive(receivePacket);

          String sentence = new String(receivePacket.getData()); 
          InetAddress IPAddress = receivePacket.getAddress();
          int x = Main.ips.indexOf(IPAddress.toString().replace("/", ""));		//finding index
          last_ping.set(x, System.currentTimeMillis());					//setting last pinged time
          String sent="";
          int port = receivePacket.getPort();
          
          for (int i=0;i<sentence.length();i++){
            if(sentence.charAt(i)==0){
              break;
            }
            sent+=sentence.charAt(i);
          }
          
          //converting string to Paddle object
          Paddle a=new Gson().fromJson(sent,Paddle.class);
          m[x] = a.index;
          //System.out.println(a);
          if(!(a.index==-2))
          {
        	//System.out.println();
            Board.paddle[a.index] = a;	//setting index
            ids.set(x,a.index);		//setting ids
          }
          
          //System.out.println ("From client " + IPAddress + ":" + port);
          
          //System.out.println ("Message: from client"+a.p+sent + sentence.replaceAll("[^\\x00-\\x7F]", ""));

          
          String data = new Gson().toJson(Board.paddle[Board.p_idx]);

          sendData = data.getBytes();
  
          DatagramPacket sendPacket = 
             new DatagramPacket(sendData, sendData.length, IPAddress, 
                               port); 
  
          serverSocket.send(sendPacket); 

        } 

      catch (SocketException ex) {
        System.out.println("UDP Port 9876 is occupied.");
        System.exit(1);
      }
        catch(SocketTimeoutException ste)
        {
        	//disconnection
        	//System.out.println("server is running");
          continue;
        }

    } 
}}