package org.camunda.cycle.innovator_cycle_connector;

import java.util.Date;

import org.camunda.bpm.cycle.connector.ConnectorNode;
import org.camunda.bpm.cycle.connector.ConnectorNodeType;

public class ModelClient extends ConnectorNode {
	
	  private static final long serialVersionUID = 1L;

	  protected byte[] content;

	  
	  /**
	   * Client is Root or direct child of Root
	   * @param label
	   * @param connectorId
	   * @param nodeType
	   */
	  public ModelClient(String label, long connectorId) {
	    super(label, label, connectorId, ConnectorNodeType.BPMN_FILE);
	  }
	  
	  /**
	   * Client is Root or direct child of Root
	   * @param label
	   * @param connectorId
	   * @param nodeType
	   */
	  public ModelClient(String label, long connectorId, ConnectorNodeType nodeType) {
		    super(label, label, connectorId, nodeType);
		  }

	  /**
	   * Client should be child of specified parent
	   * Not working
	   * @param parent
	   * @param label
	   * @param connectorId
	   * @param nodeType
	   */
	  public ModelClient(String parent, String label, long connectorId, ConnectorNodeType nodeType) {
		
		  //parent hier als ersten Wert zu setzen f�hrt zu rekursion in der getChildren Methode vom Connector
		    super(label, label, connectorId, nodeType);

		  }
	  
	  
	  
	  public byte[] getContent() {
		  
		  return getDummyContent().getBytes();
		  //return "testabcd".getBytes();
	   // return content;
	  }

	  public void setContent(byte[] content) {
	    this.setLastModified(new Date());
	    this.content = content;
	  }

	  private String getDummyContent(){
		  
		  String retStr = "<?xml version=\"1.0\" encoding=\"windows-1252\" standalone=\"no\"?>\r\n<bpmn:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:bpmn=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:tns=\"http://sample.bpmn.mid.de/\" xmlns:ino=\"http://www.mid.de/spec/Innovator/11.5.3\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL http://www.omg.org/spec/BPMN/20100501/BPMN20.xsd http://www.omg.org/spec/BPMN/20100524/DI http://www.omg.org/spec/BPMN/20100501/BPMNDI.xsd http://www.omg.org/spec/DD/20100524/DC http://www.omg.org/spec/BPMN/20100501/DC.xsd http://www.omg.org/spec/DD/20100524/DI http://www.omg.org/spec/BPMN/20100501/DI.xsd\" targetNamespace=\"http://sample.bpmn.mid.de/\" exporter=\"Innovator\" exporterVersion=\"11.5.3\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\">\r\n  <bpmn:extension definition=\"ino:tInnovator\" />\r\n  <bpmn:process isExecutable=\"false\" name=\"Subprocess\" id=\"_0748a009-16bf-49e0-e669-82a16c150cee\">\r\n    <bpmn:extensionElements>\r\n      <ino:profilename value=\"ROOT PROFILE\" />\r\n      <ino:stereotypename value=\"process\" />\r\n      <ino:node id=\"BPDiaProcessViewNode_2\" x=\"0\" y=\"0\" width=\"810\" height=\"300\" isMaster=\"True\" />\r\n      <ino:node id=\"BPDiaProcessViewNode_3\" x=\"30\" y=\"50\" width=\"600\" height=\"300\" isMaster=\"False\" />\r\n    </bpmn:extensionElements>\r\n    <bpmn:subProcess triggeredByEvent=\"false\" isForCompensation=\"false\" startQuantity=\"1\" name=\"Subprocess\" id=\"_27cbf8a9-9642-80b7-8975-1186cd39253b\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"subprocess\" />\r\n      </bpmn:extensionElements>\r\n      <bpmn:incoming>_ebe562f4-9668-c32b-2195-6e9267169b55</bpmn:incoming>\r\n      <bpmn:outgoing>_efeeb438-f580-429f-4b13-1fe700dea130</bpmn:outgoing>\r\n    </bpmn:subProcess>\r\n    <bpmn:endEvent id=\"_12eee49e-0c3f-d15c-fff0-c7e68f808811\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"event\" />\r\n      </bpmn:extensionElements>\r\n      <bpmn:incoming>_efeeb438-f580-429f-4b13-1fe700dea130</bpmn:incoming>\r\n    </bpmn:endEvent>\r\n    <bpmn:exclusiveGateway gatewayDirection=\"Unspecified\" id=\"_b993bd54-d602-4ca4-46d4-0fd35e641f3b\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"gateway\" />\r\n      </bpmn:extensionElements>\r\n      <bpmn:incoming>_465983d3-d8f2-8fd5-a83f-28328c302f3c</bpmn:incoming>\r\n      <bpmn:outgoing>_fb3e3c98-655f-88c6-c6e9-b8cbc7551a02</bpmn:outgoing>\r\n    </bpmn:exclusiveGateway>\r\n    <bpmn:startEvent id=\"_c91605dc-683c-7962-e351-0ededa141177\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"event\" />\r\n      </bpmn:extensionElements>\r\n      <bpmn:outgoing>_465983d3-d8f2-8fd5-a83f-28328c302f3c</bpmn:outgoing>\r\n    </bpmn:startEvent>\r\n    <bpmn:sequenceFlow sourceRef=\"_c91605dc-683c-7962-e351-0ededa141177\" targetRef=\"_b993bd54-d602-4ca4-46d4-0fd35e641f3b\" isImmediate=\"true\" id=\"_465983d3-d8f2-8fd5-a83f-28328c302f3c\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"sequenceFlow\" />\r\n      </bpmn:extensionElements>\r\n    </bpmn:sequenceFlow>\r\n    <bpmn:sequenceFlow sourceRef=\"_cb892101-311f-594a-f40e-802ac255996c\" targetRef=\"_27cbf8a9-9642-80b7-8975-1186cd39253b\" isImmediate=\"true\" id=\"_ebe562f4-9668-c32b-2195-6e9267169b55\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"sequenceFlow\" />\r\n      </bpmn:extensionElements>\r\n    </bpmn:sequenceFlow>\r\n    <bpmn:sequenceFlow sourceRef=\"_27cbf8a9-9642-80b7-8975-1186cd39253b\" targetRef=\"_12eee49e-0c3f-d15c-fff0-c7e68f808811\" isImmediate=\"true\" id=\"_efeeb438-f580-429f-4b13-1fe700dea130\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"sequenceFlow\" />\r\n      </bpmn:extensionElements>\r\n    </bpmn:sequenceFlow>\r\n    <bpmn:sequenceFlow sourceRef=\"_b993bd54-d602-4ca4-46d4-0fd35e641f3b\" targetRef=\"_cb892101-311f-594a-f40e-802ac255996c\" isImmediate=\"true\" id=\"_fb3e3c98-655f-88c6-c6e9-b8cbc7551a02\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"sequenceFlow\" />\r\n      </bpmn:extensionElements>\r\n    </bpmn:sequenceFlow>\r\n    <bpmn:task isForCompensation=\"false\" startQuantity=\"1\" name=\"Task\" id=\"_cb892101-311f-594a-f40e-802ac255996c\">\r\n      <bpmn:extensionElements>\r\n        <ino:profilename value=\"ROOT PROFILE\" />\r\n        <ino:stereotypename value=\"task\" />\r\n      </bpmn:extensionElements>\r\n      <bpmn:incoming>_fb3e3c98-655f-88c6-c6e9-b8cbc7551a02</bpmn:incoming>\r\n      <bpmn:outgoing>_ebe562f4-9668-c32b-2195-6e9267169b55</bpmn:outgoing>\r\n    </bpmn:task>\r\n  </bpmn:process>\r\n  <bpmndi:BPMNDiagram name=\"Subprocess\" resolution=\"96\" id=\"_7977ff21-9466-23b9-6390-94f8caa92593\">\r\n    <bpmndi:BPMNPlane bpmnElement=\"_0748a009-16bf-49e0-e669-82a16c150cee\" id=\"BPDiaProcessViewNode_2\">\r\n      <bpmndi:BPMNShape bpmnElement=\"_b993bd54-d602-4ca4-46d4-0fd35e641f3b\" id=\"BPDiaGatewayNode_1\">\r\n        <dc:Bounds height=\"40.0\" width=\"40.0\" x=\"230.0\" y=\"90.0\" />\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNShape bpmnElement=\"_cb892101-311f-594a-f40e-802ac255996c\" id=\"BPDiaTaskNode_3\">\r\n        <dc:Bounds height=\"60.0\" width=\"80.0\" x=\"360.0\" y=\"80.0\" />\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNShape bpmnElement=\"_27cbf8a9-9642-80b7-8975-1186cd39253b\" id=\"BPDiaSubProcessNode_2\" isExpanded=\"true\">\r\n        <dc:Bounds height=\"100.0\" width=\"140.0\" x=\"490.0\" y=\"60.0\" />\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNShape bpmnElement=\"_c91605dc-683c-7962-e351-0ededa141177\" id=\"BPDiaEventNode_3\">\r\n        <dc:Bounds height=\"40.0\" width=\"40.0\" x=\"120.0\" y=\"90.0\" />\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNShape bpmnElement=\"_12eee49e-0c3f-d15c-fff0-c7e68f808811\" id=\"BPDiaEventNode_4\">\r\n        <dc:Bounds height=\"40.0\" width=\"40.0\" x=\"690.0\" y=\"90.0\" />\r\n      </bpmndi:BPMNShape>\r\n      <bpmndi:BPMNEdge bpmnElement=\"_465983d3-d8f2-8fd5-a83f-28328c302f3c\" id=\"BPDiaSequenceFlow_4\">\r\n        <di:waypoint x=\"160.0\" y=\"110.0\" />\r\n        <di:waypoint x=\"230.0\" y=\"110.0\" />\r\n      </bpmndi:BPMNEdge>\r\n      <bpmndi:BPMNEdge bpmnElement=\"_fb3e3c98-655f-88c6-c6e9-b8cbc7551a02\" id=\"BPDiaSequenceFlow_5\">\r\n        <di:waypoint x=\"270.0\" y=\"110.0\" />\r\n        <di:waypoint x=\"360.0\" y=\"110.0\" />\r\n      </bpmndi:BPMNEdge>\r\n      <bpmndi:BPMNEdge bpmnElement=\"_ebe562f4-9668-c32b-2195-6e9267169b55\" id=\"BPDiaSequenceFlow_6\">\r\n        <di:waypoint x=\"440.0\" y=\"110.0\" />\r\n        <di:waypoint x=\"490.0\" y=\"110.0\" />\r\n      </bpmndi:BPMNEdge>\r\n      <bpmndi:BPMNEdge bpmnElement=\"_efeeb438-f580-429f-4b13-1fe700dea130\" id=\"BPDiaSequenceFlow_7\">\r\n        <di:waypoint x=\"630.0\" y=\"110.0\" />\r\n        <di:waypoint x=\"690.0\" y=\"110.0\" />\r\n      </bpmndi:BPMNEdge>\r\n    </bpmndi:BPMNPlane>\r\n  </bpmndi:BPMNDiagram>\r\n</bpmn:definitions>";
				  
				  return retStr;
	  }
	  
	
}