package org.camunda.cycle.innovator_cycle_connector;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.swing.JOptionPane;

import org.camunda.bpm.cycle.aspect.LoginAspect;
import org.camunda.bpm.cycle.configuration.CycleConfiguration;
import org.camunda.bpm.cycle.connector.Connector;
import org.camunda.bpm.cycle.connector.ConnectorLoginMode;
import org.camunda.bpm.cycle.connector.ConnectorNode;
import org.camunda.bpm.cycle.connector.ConnectorNodeType;
import org.camunda.bpm.cycle.connector.ContentInformation;
import org.camunda.bpm.cycle.connector.Secured;
import org.camunda.bpm.cycle.entity.ConnectorConfiguration;
//import org.camunda.bpm.cycle.entity.ConnectorConfiguration;
import org.camunda.bpm.cycle.entity.ConnectorCredentials;
import org.camunda.bpm.cycle.entity.User;
import org.camunda.bpm.cycle.connector.crypt.EncryptionService;

//import com.trilead.ssh2.log.Logger;

import de.mid.innovator.net.InoNetException;
import de.mid.innovator.srv.LicenseServer;
import de.mid.innovator.srv.Model;
import de.mid.innovator.srv.RepositoryServer;
import de.mid.innovator.srv.SrvContext;
import de.mid.innovator.srv.SrvErrorException;
import de.mid.innovator.srv.excellence.ExcellenceModel;
import de.mid.innovator.srv2api.icw2bp.BPCollaboration;
import de.mid.innovator.srv2api.icw2bp.BPProcess;
import de.mid.innovator.srv2api.icw2bp.BPProcessHelper;
import de.mid.innovator.srv2api.icw2bpdia.BPDia;
import de.mid.innovator.srv2api.icw2elem.ELContAsgnAble;
import de.mid.innovator.srv2api.icw2elem.ELContainerAble;
import de.mid.innovator.srv2api.icw2elem.K_ADMODEL;
import de.mid.innovator.srv2api.icw2elem.K_CONTASGN;
import de.mid.innovator.srv2api.icw2elem.K_CONTROL_OPTION;
import de.mid.innovator.srv2api.icw2meta.ADClientAble;
import de.mid.innovator.srv2api.icw2meta.ADModel;
import de.mid.innovator.srv2api.icw2meta.MMStereotype;
import de.mid.innovator.srv2api.icw2model.MEPackage;
import de.mid.innovator.srv2api.icw2model.MESystemModel;

public class innovatorConnector extends Connector {

	// connector-configurations.xml Keys
	public final static String CONFIG_KEY_InnoLiServ_HOST = "innolsrvHost";
	public final static String CONFIG_KEY_InnoLiServ_PORT = "innolsrvPort";

	public final static String CONFIG_KEY_InnoRemoteServ_HOST = "innoRemoteHost";
	public final static String CONFIG_KEY_InnoRemoteServ_PORT = "innoRemotePort";

	public final static String CONFIG_KEY_PROXY_USERNAME = "proxyUsername";
	public final static String CONFIG_KEY_PROXY_PASSWORD = "proxyPassword";

	protected String lHost = "cloud.mid.de";
	protected int lPort = 23100;

	public String getUserName() {
		/*
		 * String retVal = getConfiguration().getGlobalUser(); if(retVal == null
		 * || retVal.length() == 0){
		 * 
		 * }
		 */
		String retVal;

		
		if (getConfiguration().getLoginMode().equals(ConnectorLoginMode.GLOBAL)) {
			retVal = getConfiguration().getGlobalUser();
		} else {
			retVal = getConfiguration().getProperties().get(CONFIG_KEY_PROXY_USERNAME);
		}
		//System.out.println("usrname:: " + retVal);

		return retVal;
	}

	public String getPassword() {
		org.camunda.bpm.cycle.entity.User user;

		// String encrypted = getConfiguration().getGlobalPassword();

		// String decrypted =
		// encryptionService.decryptConnectorPassword(encrypted);
		// System.out.println("psswrd:: " + decrypted);
		String retVal;
		if (getConfiguration().getLoginMode().equals(ConnectorLoginMode.GLOBAL)) {
			retVal = getConfiguration().getGlobalPassword();
		} else {
			retVal = getConfiguration().getProperties().get(CONFIG_KEY_PROXY_PASSWORD);

		}
		//System.out.println("pssword:: " + retVal);
		return retVal;
	}

	/*
	 * public static void main(String[] args) throws Exception {
	 * 
	 * gatherLicServRepos(lHost,lPort);
	 * 
	 * }
	 */

	/**
	 * 
	 * @param Host
	 *            The Hostname of the Innovator licenseserver
	 * @param Port
	 *            The Portnumber of the Innovator licenseserver
	 * @return
	 * @throws InoNetException
	 */

	protected Map<RepositoryClient, RepositoryServer> gatherLicServRepos(String Host, int Port) throws InoNetException {
		LicenseServer licServ = new LicenseServer(Host, Port);
		List<RepositoryServer> repList = licServ.getServerList();

		Map<RepositoryClient, RepositoryServer> cycleClients = new HashMap<RepositoryClient, RepositoryServer>();
		for (RepositoryServer rs : repList) {

			// Register repositories with browserRoot
			RepositoryClient repClientInstance = new RepositoryClient("Repo: " + rs.getName(), "Repo: " + rs.getName(),
					getId(), ConnectorNodeType.FOLDER, this, "");
			cycleClients.put(repClientInstance, rs);

			// add repository children to rootNodes
			List<RepositoryClient> repoMdlList = new ArrayList<RepositoryClient>();

			for (Model innoMdl : getModelsOfRepository(rs)) {

				RepositoryClient modelClientInstance = new RepositoryClient(rs.getName() + innoMdl.getModelName(),
						innoMdl.getModelName(), getId(), ConnectorNodeType.FOLDER, this);
				repoMdlList.add(modelClientInstance);

				ModelClientMap.put(modelClientInstance.getId(), innoMdl);
			}
			RepModelMap.put(repClientInstance.getId(), repoMdlList);
		}
		return cycleClients;
	}

	protected List<RepositoryClient> RepoClients = new ArrayList<RepositoryClient>();
	protected Map<String, List<RepositoryClient>> RepModelMap = new HashMap<String, List<RepositoryClient>>();
	protected Map<String, Model> ModelClientMap = new HashMap<String, Model>();
	protected Map<String, ELContainerAble> PackageMap = new HashMap<String, ELContainerAble>();
	protected Map<String, ExcellenceModel> ExcellenceMDLs = new HashMap<String, ExcellenceModel>();
	// protected Map<String,List<ACActivity>> ModelActivityMap = new
	// HashMap<String,List<ACActivity>>();

	/**
	 * Gathers all models contained within the given repository.
	 * 
	 * @param repSrv
	 *            the given repository
	 * @return returns the models within the repository
	 */
	protected List<Model> getModelsOfRepository(RepositoryServer repSrv) {
		return new ArrayList<Model>(repSrv.getModels());
		// repoModels.addAll(repSrv.getModels());
		// return repoModels;
	}

	protected RepositoryClient rootNode;

	protected Map<String, RepositoryClient> nodes = new HashMap<String, RepositoryClient>();

	public void init(ConnectorConfiguration config) {

		super.init(config);

		// org.camunda.bpm.cycle.entity.User usr = new
		// org.camunda.bpm.cycle.entity.User();
		// for(ConnectorCredentials cc : usr.getConnectorCredentials()) {

		/*
		 * for(Entry<String,String> entries: config.getProperties().entrySet()){
		 * 
		 * System.out.println(entries.getKey() + "..." + entries.getValue()); }
		 */

		System.out.println("innoConnector: init");
		// Root connection setup
		rootNode = new RepositoryClient("/", "/", getId(), ConnectorNodeType.FOLDER, this, "");
		this.lHost = getConfiguration().getProperties().get(CONFIG_KEY_InnoLiServ_HOST);
		this.lPort = Integer.parseInt(getConfiguration().getProperties().get(CONFIG_KEY_InnoLiServ_PORT));

		RemoteBpmnXmlConversion.setRemoteConfiguration(
				getConfiguration().getProperties().get(CONFIG_KEY_InnoRemoteServ_HOST),
				Integer.parseInt(getConfiguration().getProperties().get(CONFIG_KEY_InnoRemoteServ_PORT)));
		// folder = new
		// RepositoryClient(config.getProperties().get("folderName"), getId(),
		// ConnectorNodeType.FOLDER);

		try {
			gatherLicServRepos(lHost, lPort).keySet().forEach((e) -> RepoClients.add(e));
		} catch (InoNetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ConnectorNode createNode(String parentId, String label, ConnectorNodeType type, String message) {
		System.out.print("Innoconnector: createNode");
		try {
			ExcellenceModel excelMdl = ExcellenceMDLs.get(parentId);
			excelMdl.getRepositoryServer().connect();
			if (getConfiguration().getLoginMode().equals(ConnectorLoginMode.LOGIN_NOT_REQUIRED)) {
				ExcellenceMDLs.get(parentId).loginModelGuest("");
			} else {
				try {
					ExcellenceMDLs.get(parentId).loginUser(getUserName(), getPassword(), "Business Analyst");
					//ExcellenceMDLs.get(parentId).loginModelAdmin("", "");

				} catch (Exception e) {
					//ExcellenceMDLs.get(parentId).loginUser(getUserName(), getPassword(), "Business Analyst");
					throw(e);
				}
			}

			ELContainerAble parentCont = PackageMap.get(parentId);
			SrvContext srvCtx = ExcellenceMDLs.get(parentId).getADModel().getSrvCon();

			String repoName = ExcellenceMDLs.get(parentId).getRepositoryServer().getFullName();
			String mdlName = ExcellenceMDLs.get(parentId).getADModel().getDisplayName();
			MMStereotype ProcessStereo = ExcellenceMDLs.get(parentId).getADModel().getStereotype("ROOT PROFILE",
					"process", BPProcess.class);
			BPProcess createdProcess = BPProcessHelper.create(srvCtx, ProcessStereo, parentCont);

			String extensionEnding = ".bpmn";
			String processName = label.substring(0, label.length() - extensionEnding.length());
			createdProcess.setUniqueName(processName);
			//processName = createdProcess.getName();// falls der vorherige schon
													// existiert wurde er
													// umbenannt
			processName = createdProcess.getQualifiedNamespaceName() + "::" + createdProcess.getDisplayName();
			//repoName + ",,," + mdlName + "___" + item.getQualifiedNamespaceName()+"::"+item.getDisplayName(), item.getQualifiedNamespaceName()+"::"+item.getDisplayName(),
			//getId(), ConnectorNodeType.BPMN_FILE, this, repoName);
			RepositoryClient ProcessNode = new RepositoryClient(repoName + ",,," + mdlName + "___" + processName,
					processName, getId(), ConnectorNodeType.BPMN_FILE, this, repoName);
			// Store owning repository to restore node with a new Websession.
			// ProcessNode.setMessage(parent.getId());
			// activityNodes.add(ProcessNode);
			nodes.put(ProcessNode.getId(), ProcessNode);
			return ProcessNode;
			/*
			 * RepositoryClient newNode = new RepositoryClient(parentId, label,
			 * getId(),type, this); nodes.put(label, newNode); return newNode;
			 */
		} catch (InoNetException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SrvErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				ExcellenceMDLs.get(parentId).asExcellenceModel().logout();
				ExcellenceMDLs.get(parentId).getRepositoryServer().disconnect();
			} catch (InoNetException | SrvErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;

	}

	public void deleteNode(ConnectorNode node, String message) {
		nodes.remove(node.getLabel());
	}

	public List<ConnectorNode> getChildren(ConnectorNode parent) {

		if (RepModelMap.containsKey(parent.getId())) {

			// avoid recursive hierarchies: getId doesn't use an incrementing
			// number but the name of the Node, thus if 2 nodes share the same
			// name there may by a infinite loop
			ArrayList<ConnectorNode> filtered = new ArrayList<ConnectorNode>();
			for (RepositoryClient icl : RepModelMap.get(parent.getId())) {
				if (!parent.getId().equals(icl.getId())) {
					filtered.add(icl);
				}
			}
			return new ArrayList<ConnectorNode>(RepModelMap.get(parent.getId()));

		} else if (ModelClientMap.containsKey(parent.getId())) {

			List<ConnectorNode> activityNodes = new ArrayList<ConnectorNode>();
			try {
				ModelClientMap.get(parent.getId()).getRepositoryServer().connect();
				if (getConfiguration().getLoginMode().equals(ConnectorLoginMode.LOGIN_NOT_REQUIRED)) {
					ModelClientMap.get(parent.getId()).asExcellenceModel().loginModelGuest("");
				} else {
					try {
						ModelClientMap.get(parent.getId()).asExcellenceModel().loginUser(getUserName(), getPassword(), "Business Analyst");
						//ExcellenceMDLs.get(parent.getId()).loginModelAdmin("", "");

					} catch (Exception e) {
						//ExcellenceMDLs.get(parentId).loginUser(getUserName(), getPassword(), "Business Analyst");
						throw(e);
					}
				}
				List<MESystemModel> allContent = ModelClientMap.get(parent.getId()).asExcellenceModel().getADModel()
						.getOwnedSystemModel();

				String repoName = ModelClientMap.get(parent.getId()).getRepositoryServer().getFullName();
				String mdlName = ModelClientMap.get(parent.getId()).asExcellenceModel().getADModel().getDisplayName();
				for (ELContAsgnAble item : allContent) {
					RepositoryClient ProcessNode = new RepositoryClient(
							repoName + ",,," + mdlName + "___" + item.getDisplayName(), item.getDisplayName(), getId(),
							ConnectorNodeType.FOLDER, this, repoName);
					// Store owning repository to restore node with a new
					// Websession.
					ProcessNode.setMessage(parent.getId());
					ExcellenceMDLs.put(ProcessNode.getId(), ModelClientMap.get(parent.getId()).asExcellenceModel());
					activityNodes.add(ProcessNode);
					nodes.put(ProcessNode.getId(), ProcessNode);
					PackageMap.put(ProcessNode.getId(), (ELContainerAble) item);
				}
			} catch (SrvErrorException e) {
				e.printStackTrace();
			} catch (InoNetException e) {
				e.printStackTrace();
			} finally {
				try {
					ModelClientMap.get(parent.getId()).asExcellenceModel().logout();
					ModelClientMap.get(parent.getId()).getRepositoryServer().disconnect();
				} catch (InoNetException | SrvErrorException e) {
					e.printStackTrace();
				}
			}

			/*
			 * List<ConnectorNode> activityNodes = new
			 * ArrayList<ConnectorNode>(); //BPProcess List<BPProcess>
			 * mdlActivities = new ArrayList<BPProcess>(); try {
			 * ModelClientMap.get(parent.getId()).getRepositoryServer().connect(
			 * );
			 * if(getConfiguration().getLoginMode().equals(ConnectorLoginMode.
			 * LOGIN_NOT_REQUIRED)){
			 * ModelClientMap.get(parent.getId()).asExcellenceModel().
			 * loginModelGuest(""); }else{ try{
			 * //ModelClientMap.get(parent.getId()).asExcellenceModel().
			 * loginModelAdmin(getUserName(), getPassword());
			 * ModelClientMap.get(parent.getId()).asExcellenceModel().
			 * loginModelAdmin("",""); }catch(Exception e){
			 * ModelClientMap.get(parent.getId()).asExcellenceModel().loginUser(
			 * getUserName(),getPassword(), "Business Analyst");
			 * 
			 * } }
			 * 
			 * mdlActivities =
			 * ModelClientMap.get(parent.getId()).asExcellenceModel().getADModel
			 * ().getOwnedElementTransitiveDownExcludeProfiles(BPProcess.class,
			 * K_CONTROL_OPTION.ExactReturnType);
			 * 
			 * for(BPProcess aca : mdlActivities){ try { String repoName =
			 * ModelClientMap.get(parent.getId()).getRepositoryServer().
			 * getFullName(); String mdlName =
			 * ModelClientMap.get(parent.getId()).asExcellenceModel().getADModel
			 * ().getDisplayName();
			 * 
			 * RepositoryClient ProcessNode = new
			 * RepositoryClient(repoName+",,,"+mdlName+"___"+aca.getDisplayName(
			 * ),aca.getDisplayName(),getId(), ConnectorNodeType.BPMN_FILE,
			 * this, repoName);
			 * 
			 * 
			 * 
			 * //Store owning repository to restore node with a new Websession.
			 * ProcessNode.setMessage(parent.getId());
			 * activityNodes.add(ProcessNode); nodes.put(ProcessNode.getId(),
			 * ProcessNode); } catch (InoNetException | SrvErrorException e) {
			 * // TODO Auto-generated catch block e.printStackTrace(); } }
			 * 
			 * 
			 * ModelClientMap.get(parent.getId()).asExcellenceModel().logout();
			 * ModelClientMap.get(parent.getId()).getRepositoryServer().
			 * disconnect(); } catch (SrvErrorException e) { // TODO
			 * Auto-generated catch block e.printStackTrace(); } catch
			 * (InoNetException e) { // TODO Auto-generated catch block
			 * e.printStackTrace(); }
			 */

			return activityNodes;

		} else if (PackageMap.containsKey(parent.getId())) {

			List<ConnectorNode> activityNodes = new ArrayList<ConnectorNode>();

			try {
				ExcellenceModel excelMdl = ExcellenceMDLs.get(parent.getId());
				excelMdl.getRepositoryServer().connect();
				if (getConfiguration().getLoginMode().equals(ConnectorLoginMode.LOGIN_NOT_REQUIRED)) {
					ExcellenceMDLs.get(parent.getId()).loginModelGuest("");
				} else {
					try {
						ExcellenceMDLs.get(parent.getId()).loginUser(getUserName(), getPassword(), "Business Analyst");
						//ExcellenceMDLs.get(parentId).loginModelAdmin("", "");

					} catch (Exception e) {
						//ExcellenceMDLs.get(parentId).loginUser(getUserName(), getPassword(), "Business Analyst");
						throw(e);
					}
				}

				List<ELContAsgnAble> allContent = PackageMap.get(parent.getId()).getOwnedElement(ELContAsgnAble.class,
						K_CONTASGN.Default);

				String repoName = ExcellenceMDLs.get(parent.getId()).getRepositoryServer().getFullName();
				String mdlName = ExcellenceMDLs.get(parent.getId()).getADModel().getDisplayName();
				for (ELContAsgnAble item : allContent) {
					try {
						if (item instanceof MEPackage) {
							RepositoryClient ProcessNode = new RepositoryClient(
									repoName + ",,," + mdlName + "___" + item.getDisplayName(), item.getDisplayName(),
									getId(), ConnectorNodeType.FOLDER, this, repoName);
							ProcessNode.setMessage(parent.getId());
							ExcellenceMDLs.put(ProcessNode.getId(), ExcellenceMDLs.get(parent.getId()));
							activityNodes.add(ProcessNode);
							nodes.put(ProcessNode.getId(), ProcessNode);
							PackageMap.put(ProcessNode.getId(), (ELContainerAble) item);
							
						} else if (item instanceof BPProcess || item instanceof BPCollaboration) {
							RepositoryClient ProcessNode = new RepositoryClient(
									/*repoName + ",,," + mdlName + "___" + item.getDisplayName(), item.getDisplayName(),
									getId(), ConnectorNodeType.BPMN_FILE, this, repoName);*/
									repoName + ",,," + mdlName + "___" + item.getQualifiedNamespaceName()+"::"+item.getDisplayName(), item.getQualifiedNamespaceName()+"::"+item.getDisplayName(),
									getId(), ConnectorNodeType.BPMN_FILE, this, repoName);
							ProcessNode.setMessage(parent.getId());
							activityNodes.add(ProcessNode);
							nodes.put(ProcessNode.getId(), ProcessNode);
						}
					} catch (InoNetException | SrvErrorException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			} catch (SrvErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InoNetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				try {
					ExcellenceMDLs.get(parent.getId()).logout();
				} catch (InoNetException | SrvErrorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					ExcellenceMDLs.get(parent.getId()).getRepositoryServer().disconnect();
				} catch (InoNetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			return activityNodes;

		} else if (parent.getId().equals(rootNode.getId())) {
			return new ArrayList<ConnectorNode>(RepoClients);
			// return Collections.<ConnectorNode>singletonList(folder);
		} else {
			return Collections.emptyList();
		}
	}

	public InputStream getContent(ConnectorNode node) {
		/*
		 * RepositoryClient RepositoryClient = (RepositoryClient)node;
		 * ByteArrayInputStream inputStream = null; byte[] content =
		 * RepositoryClient.getContent(); if(content == null) { content = new
		 * byte[0]; } inputStream = new ByteArrayInputStream(content);
		 * 
		 * return inputStream;
		 */

		String nodeId = node.getId().replace((char) 65533, '�').replace("�", "").replace("�", "").replace("�", "")
				.replace("�", "");
		// node.setId(nodeId);

		/*
		 * char dbg = node.getId().charAt(node.getId().length()-9);
		 * System.out.println("? zahl:"); System.out.println((int)dbg);
		 */

		// logger.log(0, "innoConnector: getContent");
		System.out.println("innoConnector: getContent: NodeId:" + node.getId());
		RepositoryClient repoClient = nodes.get(nodeId);
		System.out.println("getContent Node:" + node.getId());
		ByteArrayInputStream inputStream = null;
		if (repoClient == null) {
			inputStream = new ByteArrayInputStream(new byte[0]);
			System.out.println("innoConnector: getContent:Node not found in nodemap");
		} else {
			byte[] content = repoClient.getContent();
			if (content == null) {
				content = new byte[0];
			}
			inputStream = new ByteArrayInputStream(content);
		}
		return inputStream;
	}

	public ContentInformation getContentInformation(ConnectorNode node) {
		System.out.println("innoConnector: getContentInformation");
		String nodeId = node.getId().replace((char) 65533, '�').replace("�", "").replace("�", "").replace("�", "")
				.replace("�", "");

		// node.setId(nodeId);

		// extract Info: repo,,,mdl___diag

		if (!nodes.containsKey(nodeId)) {
			if (node.getId().indexOf("___") > 0) {
				int repoendIdx = node.getId().indexOf(",,,") + 3;
				int mdlendIdx = node.getId().indexOf("___") + 3;
				String repoName = node.getId().substring(0, repoendIdx - 3);
				String mdlName = node.getId().substring(repoendIdx, mdlendIdx - 3);
				String diagName = node.getId().substring(mdlendIdx, node.getId().length());
				

				nodes.put(nodeId, new RepositoryClient(repoName + ",,," + mdlName + "___" + diagName, diagName, getId(),
						ConnectorNodeType.BPMN_FILE, this, repoName));

			}

		}

		System.out.println("innoConnector: getContentInformation node: " + node.getId());
		RepositoryClient repoClient = nodes.get(nodeId);
		if (repoClient == null) {
			System.out.println("innoConnector: getContentInformation : null");

			return ContentInformation.notFound();
		} else {
			System.out.println("innoConnector: getContentInformation : not null");
			return new ContentInformation(true, repoClient.getLastModified());
		}
	}

	public ConnectorNode getNode(String id) {
		System.out.println("innoConnector: getNode");
		return nodes.get(id);
	}

	public ConnectorNode getRoot() {
		System.out.println("innoConnector: getRoot");
		return rootNode;
	}

	public boolean isSupportsCommitMessage() {
		return true;
	}

	public boolean needsLogin() {
		return false;
	}

	public ContentInformation updateContent(ConnectorNode node, InputStream newContent, String message)
			throws Exception {
		System.out.println("innoConnector: updateContent");
		RepositoryClient repoClient = nodes.get(node.getId());
		if (repoClient == null) {
			throw new RuntimeException("Node with id " + node.getId() + " not found.");
		} else {
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int bytesRead = 0;
			while ((bytesRead = newContent.read(buffer, 0, buffer.length)) > 0) {
				byteArrayOutputStream.write(buffer, 0, bytesRead);
			}
			repoClient.setContent(byteArrayOutputStream.toByteArray());
			return getContentInformation(repoClient);
		}
	}

}
