package org.camunda.cycle.innovator_cycle_connector;

import java.io.IOException;
import java.util.Date;

import org.camunda.bpm.cycle.connector.ConnectorNode;
import org.camunda.bpm.cycle.connector.ConnectorNodeType;

public class RepositoryClient extends ConnectorNode {
	
	  private static final long serialVersionUID = 1L;

	  //public ExcellenceModel owningExcellenceModel; //die nodes die die anderen methoden aufrufen lassen sich nicht casten => in eine map
	  
	  protected byte[] content = new byte[0];
	  protected innovatorConnector innoConnect;
	  
	  /**
	   * Client is Root or direct child of Root
	   * @param label
	   * @param connectorId
	   * @param nodeType
	   */
	  /*public RepositoryClient(String label, long connectorId, innovatorConnector innoConnect) {
	    super(label, label, connectorId, ConnectorNodeType.FOLDER);
	    this.innoConnect = innoConnect;
	    //content = getDummyContent().getBytes();
	  }*/
	  
	  /**
	   * Client is Root or direct child of Root
	   * @param label
	   * @param connectorId
	   * @param nodeType
	   */
	  public RepositoryClient(String id,String label, long connectorId, ConnectorNodeType nodeType, innovatorConnector innoConnect, String repoName) {
		    super(id, label, connectorId, nodeType);
		   // content = getDummyContent().getBytes();
		    this.innoConnect = innoConnect;
		    //this.repoName = repoName;
		  }

	  /**
	   * Only used for innomodel-client
	   * Not working
	   * @param parent
	   * @param label
	   * @param connectorId
	   * @param nodeType
	   */
	  public RepositoryClient(String id, String label, long connectorId, ConnectorNodeType nodeType, innovatorConnector innoConnect) {
		
		  //parent hier als ersten Wert zu setzen f�hrt zu rekursion in der getChildren Methode vom Connector
		    super(id, label, connectorId, nodeType);
		   // content = getDummyContent().getBytes();
		    this.innoConnect = innoConnect;
		    
		  }
	  public String getLabel(){
		  String lblStr = "";
		  try{
		  
		  int idx = this.getId().lastIndexOf("::")+2;
		  if(idx != 1){
		  lblStr = this.getId().substring(idx,this.getId().length());
		  }else{
			  int mdlendIdx = this.getId().indexOf("___");
			  lblStr = this.getId().substring(mdlendIdx, this.getId().length());
			  
			 mdlendIdx = this.getId().indexOf("___");
			  if(mdlendIdx != -1){//zweites mal f�r elemente die anders aufgebaut sind ToDo: vereinheitlichen!
			  lblStr = this.getId().substring(mdlendIdx+3, this.getId().length());
			  }
			  
			  
		  }
		  }catch(Exception e){
			  
			  lblStr = super.getLabel();
		  }
		  return lblStr;
	  }
	  
	  //protected String repoName;
	  public byte[] getContent() {
		  System.out.println("RepositoryClient:getContent:NODENAME:::: " + this.getLabel());
		  //extract Info:  repo,,,mdl___diag
		  int repoendIdx = this.getId().indexOf(",,,") + 3;
		  int mdlendIdx = this.getId().indexOf("___") + 3;
		  
		  if(mdlendIdx > -1){
			  String repoName = this.getId().substring(0, repoendIdx-3);
			  String mdlName = this.getId().substring(repoendIdx,mdlendIdx-3);
			  String diagName = this.getId().substring(mdlendIdx, this.getId().length());
			  System.out.println(diagName);
			  try {	
				//return RemoteBpmnXmlConversion.tryGetXMLDiag("::HOST:PC9::PORT:12300::REPO:Demo_Camunda_BPMN_XML_Import::MDL:Modell einer Autovermietung::DIAG:"+diagName+"::").getBytes();
				  return RemoteBpmnXmlConversion.tryGetXMLDiag(":::ACC:"+innoConnect.getUserName()+":::PW:"+innoConnect.getPassword()+":::HOST:"+innoConnect.lHost+":::PORT:"+innoConnect.lPort+":::REPO:"+repoName+":::MDL:"+mdlName+":::DIAG:"+diagName+":::").getBytes();
			  } catch (IOException e) {
				e.printStackTrace();
			}
		  }
		//return getDummyContent().getBytes(); 
		  return new byte[1];
	  }

	  public void setContent(byte[] content) {
		  System.out.println("RepoClient-setContent: call camundainnobpmn importer here");
		  System.out.println(new String(content));
	    this.setLastModified(new Date());
	    //this.content = content;
	    //innocamundaxml importer call
	    
	    System.out.println("RepositoryClient:getContent:NODENAME:::: " + this.getLabel());
	  //extract Info:  repo,,,mdl___diag
		  int repoendIdx = this.getId().indexOf(",,,") + 3;
		  int mdlendIdx = this.getId().indexOf("___") + 3;
		  
		  if(mdlendIdx > -1){
			  String repoName = this.getId().substring(0, repoendIdx-3);
			  String mdlName = this.getId().substring(repoendIdx,mdlendIdx-3);
			  String diagName = this.getId().substring(mdlendIdx, this.getId().length());
			  System.out.println(diagName);
			  try {	
				//return RemoteBpmnXmlConversion.tryGetXMLDiag("::HOST:PC9::PORT:12300::REPO:Demo_Camunda_BPMN_XML_Import::MDL:Modell einer Autovermietung::DIAG:"+diagName+"::").getBytes();
				  String resultMsg = RemoteBpmnXmlConversion.tryGetXMLDiag(":::ACC:"+innoConnect.getUserName()+":::PW:"+innoConnect.getPassword()+":::HOST:"+innoConnect.lHost+":::PORT:"+innoConnect.lPort+":::REPO:"+repoName+":::MDL:"+mdlName+":::DIAG:"+diagName+":::"+
						  			 RemoteBpmnXmlConversion.importCommand+new String(content));
			  } catch (IOException e) {
				e.printStackTrace();
			}
		  }
	    
	  }

	  private String getDummyContent(){
		  String retStr = "";
		try {
			retStr = RemoteBpmnXmlConversion.tryGetXMLDiag("::HOST:PC9::PORT:12300::REPO:Demo_Camunda_BPMN_XML_Import::MDL:Modell einer Autovermietung::DIAG:Fahrzeugreservierungsanfrage::");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  //String retStr = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<bpmn:definitions xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" id=\"Definitions_1\" targetNamespace=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" exporter=\"Camunda Modeler\" exporterVersion=\"1.2.2\">\r\n  <bpmn:process id=\"Process_1\" isExecutable=\"true\">\r\n    <bpmn:startEvent id=\"StartEvent_1\" />\r\n    <bpmn:task id=\"Task_1d6kbdl\">\r\n      <bpmn:outgoing>SequenceFlow_006dbt1</bpmn:outgoing>\r\n    </bpmn:task>\r\n    <bpmn:sequenceFlow id=\"SequenceFlow_0t65v97\" sourceRef=\"StartEvent_1\" targetRef=\"Task_1d6kbdl\" />\r\n    <bpmn:exclusiveGateway id=\"ExclusiveGateway_0r8bjc2\">\r\n      <bpmn:incoming>SequenceFlow_0os3a0t</bpmn:incoming>\r\n    </bpmn:exclusiveGateway>\r\n    <bpmn:task id=\"Task_0gz2e7o\">\r\n      <bpmn:incoming>SequenceFlow_006dbt1</bpmn:incoming>\r\n      <bpmn:outgoing>SequenceFlow_0os3a0t</bpmn:outgoing>\r\n    </bpmn:task>\r\n    <bpmn:sequenceFlow id=\"SequenceFlow_0os3a0t\" sourceRef=\"Task_0gz2e7o\" targetRef=\"ExclusiveGateway_0r8bjc2\" />\r\n    <bpmn:sequenceFlow id=\"SequenceFlow_006dbt1\" sourceRef=\"Task_1d6kbdl\" targetRef=\"Task_0gz2e7o\" />\r\n  </bpmn:process>\r\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\r\n    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"Process_1\">\r\n      <bpmndi:BPMNShape id=\"_BPMNShape_StartEvent_2\" bpmnElement=\"StartEvent_1\">\r\n        <dc:Bounds x=\"173\" y=\"102\" width=\"36\" height=\"36\" />\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNShape id=\"Task_1d6kbdl_di\" bpmnElement=\"Task_1d6kbdl\">\r\n        <dc:Bounds x=\"324\" y=\"80\" width=\"100\" height=\"80\" />\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNEdge id=\"SequenceFlow_0t65v97_di\" bpmnElement=\"SequenceFlow_0t65v97\">\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"209\" y=\"120\" />\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"324\" y=\"120\" />\r\n        <bpmndi:BPMNLabel>\r\n          <dc:Bounds x=\"221.5\" y=\"95\" width=\"90\" height=\"20\" />\r\n        </bpmndi:BPMNLabel>\r\n      </bpmndi:BPMNEdge>\r\n      <bpmndi:BPMNShape id=\"ExclusiveGateway_0r8bjc2_di\" bpmnElement=\"ExclusiveGateway_0r8bjc2\" isMarkerVisible=\"true\">\r\n        <dc:Bounds x=\"767\" y=\"95\" width=\"50\" height=\"50\" />\r\n        <bpmndi:BPMNLabel>\r\n          <dc:Bounds x=\"747\" y=\"145\" width=\"90\" height=\"20\" />\r\n        </bpmndi:BPMNLabel>\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNShape id=\"Task_0gz2e7o_di\" bpmnElement=\"Task_0gz2e7o\">\r\n        <dc:Bounds x=\"566\" y=\"94\" width=\"100\" height=\"80\" />\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNEdge id=\"SequenceFlow_0os3a0t_di\" bpmnElement=\"SequenceFlow_0os3a0t\">\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"666\" y=\"134\" />\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"717\" y=\"134\" />\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"717\" y=\"120\" />\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"767\" y=\"120\" />\r\n        <bpmndi:BPMNLabel>\r\n          <dc:Bounds x=\"687\" y=\"117\" width=\"90\" height=\"20\" />\r\n        </bpmndi:BPMNLabel>\r\n      </bpmndi:BPMNEdge>\r\n      <bpmndi:BPMNEdge id=\"SequenceFlow_006dbt1_di\" bpmnElement=\"SequenceFlow_006dbt1\">\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"424\" y=\"120\" />\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"495\" y=\"120\" />\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"495\" y=\"134\" />\r\n        <di:waypoint xsi:type=\"dc:Point\" x=\"566\" y=\"134\" />\r\n        <bpmndi:BPMNLabel>\r\n          <dc:Bounds x=\"465\" y=\"117\" width=\"90\" height=\"20\" />\r\n        </bpmndi:BPMNLabel>\r\n      </bpmndi:BPMNEdge>\r\n    </bpmndi:BPMNPlane>\r\n  </bpmndi:BPMNDiagram>\r\n</bpmn:definitions>\r\n";
				  return retStr;
	  }
	  
	
	  
	  
}
