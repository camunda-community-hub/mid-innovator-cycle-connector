package org.camunda.cycle.innovator_cycle_connector;

import java.io.BufferedReader;//for debugging with main method
import java.io.IOException;
import java.io.InputStreamReader;//for debugging with main method
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RemoteBpmnXmlConversion {
	//User set parameters
	private static String hostName = "PC9.mid.de";
	private static int receiverListenPort = 23111;
	private final static String endoftransm = "::endoftransmission::";
	private final static String fragmented = "::fragmentoftransm::";
    private final static String keepgoing = "::sendnextfragment::";
    public final static String importCommand = "::IMPORT::";
    
    public static void setRemoteConfiguration(String Host,int Port){
    	
    	hostName = Host;
    	receiverListenPort = Port;
    	
    }
	
	public static String tryGetXMLDiag(String requestStr) throws IOException{
		//::HOST:PC9::PORT:12300::REPO:Demo_Camunda_BPMN_XML_Import::MDL:Modell einer Autovermietung::DIAG:Fahrzeugreservierungsanfrage::
		  DatagramSocket clientSocket = new DatagramSocket();
	     InetAddress IPAddress = InetAddress.getByName(hostName);
	      byte[] sendData = new byte[1024];
	      byte[] receiveData = new byte[65536];
	      
	      sendData = requestStr.getBytes();
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, receiverListenPort);
	      clientSocket.send(sendPacket);
	      DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
	      clientSocket.receive(receivePacket);
	      
	      
	      String responseMessage = new String(receivePacket.getData());
	      
	      
	      
	      //addition for fragmentation of big packages
	      int endIdx = -999;
	      endIdx = responseMessage.indexOf(fragmented);
	      String fragmentparts = "";
	      if(endIdx != -1){
	      while(endIdx != -1){
	    	  fragmentparts += responseMessage.substring(0,endIdx);
	    	  
	    	  sendPacket = new DatagramPacket(keepgoing.getBytes(), keepgoing.getBytes().length, IPAddress, receiverListenPort);
		      clientSocket.send(sendPacket);
		      receiveData = new byte[65536];
		      receivePacket = new DatagramPacket(receiveData, receiveData.length);
		      clientSocket.receive(receivePacket);
		      responseMessage = new String(receivePacket.getData());
		      
		      endIdx = responseMessage.indexOf(fragmented);
	      }
	      endIdx = responseMessage.indexOf(endoftransm);
	      fragmentparts += responseMessage.substring(0,endIdx+endoftransm.length());
	      responseMessage = fragmentparts;
	      }
	      //^^addition for fragmentation of big packages
	      
	      
	      
	      endIdx = responseMessage.indexOf(endoftransm);
	      if(endIdx == -1){
	    	  System.out.println("invalid response");
	      }else{
	    	  responseMessage = responseMessage.substring(0,endIdx);
	      }
	      
	      clientSocket.close();
	      return responseMessage;
	}
	
	/*public static void main(String[] args) throws IOException {
		System.out.println("starting socketmessage");
		// TODO Auto-generated method stub
		//::HOST:PC9::PORT:12300::REPO:Demo_Camunda_BPMN_XML_Import::MDL:Modell einer Autovermietung::DIAG:Fahrzeugreservierungsanfrage::
		 BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
		     
		System.out.println(tryGetXMLDiag(inFromUser.readLine()));
		
	}*/
}
