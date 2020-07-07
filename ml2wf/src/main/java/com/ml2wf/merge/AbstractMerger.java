package com.ml2wf.merge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ml2wf.constraints.InvalidConstraintException;
import com.ml2wf.constraints.factory.ConstraintFactory;
import com.ml2wf.constraints.factory.ConstraintFactoryImpl;
import com.ml2wf.conventions.Notation;
import com.ml2wf.conventions.enums.fm.FMAttributes;
import com.ml2wf.conventions.enums.fm.FMNames;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNAttributes;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNNames;
import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.concretes.FMTask;
import com.ml2wf.tasks.factory.FMTaskFactory;
import com.ml2wf.tasks.factory.TaskFactory;
import com.ml2wf.tasks.manager.TasksManager;
import com.ml2wf.util.Pair;
import com.ml2wf.util.XMLManager;

/**
 * This class contains useful methods for merging a Workflow element into a
 * FeatureModel xml file.
 *
 * <p>
 *
 * It is an extension of the {@link XMLManager} base class.
 *
 * <p>
 *
 * It aims at the application of <b>metalearning</b> for workflow automation as
 * part of the <b>ml2wf project</b>.
 *
 * <p>
 *
 * It is an implementation of the {@link WFMerger} interface.
 *
 * <p>
 *
 * Please refer to the <a href="https://featureide.github.io/">FeatureIDE
 * framework</a> for further information about a FeatureModel.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see XMLManager
 *
 */
public abstract class AbstractMerger extends XMLManager {

	/**
	 * {@code TaskFactory}'s instance that will generate {@code Task} instances.
	 *
	 * @see TaskFactory
	 */
	private TaskFactory taskFactory;
	/**
	 * {@code ConstraintFactory}'s instance that will generate constraint nodes.
	 *
	 * @see ConstraintFactory
	 */
	private ConstraintFactory constraintFactory;
	/**
	 * Logger instance.
	 *
	 * @since 1.0
	 * @see Logger
	 */
	private static final Logger logger = LogManager.getLogger(AbstractMerger.class);

	/**
	 * {@code AbstractMerger}'s default constructor.
	 *
	 * @param file the XML {@code File}.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public AbstractMerger(File file) throws ParserConfigurationException, SAXException, IOException {
		super(file);
		this.constraintFactory = new ConstraintFactoryImpl(getDocument());
		this.taskFactory = new FMTaskFactory();
	}

	/**
	 * Returns the {@code TaskFactory}'s instance.
	 *
	 * @return the {@code TaskFactory}'s instance
	 *
	 * @see TaskFactory
	 */
	public TaskFactory getTaskFactory() {
		return this.taskFactory;
	}

	/**
	 * Returns the {@code ConstraintFactory}'s instance.
	 *
	 * @return the {@code ConstraintFactory}'s instance
	 *
	 * @see ConstraintFactory
	 */
	public ConstraintFactory getConstraintFactory() {
		return this.constraintFactory;
	}

	/**
	 * Adopts and adds new {@code rules} to the FM document's constraint tag.
	 *
	 * @param rules {@code List} of {@code Node} rules to add to the FM document
	 *
	 * @since 1.0
	 *
	 * @see Node
	 */
	protected void adoptRules(List<Node> rules) {
		Node constraintsNode = createConstraintTag();
		// getting constraints
		List<Node> constraints = new ArrayList<>(
				XMLManager.nodeListAsList(getDocument().getElementsByTagName(FMNames.RULE.getName())));
		for (Node rule : rules) {
			if (constraints.stream().noneMatch(rule::isEqualNode)) {
				// if it is not duplicated constraint
				getDocument().adoptNode(rule);
				constraintsNode.appendChild(rule);
			}
		}
	}

	/**
	 * Creates the <b>constraints</b> {@code Node} if it doesn't exist.
	 *
	 * @return the constraints {@code Node}
	 *
	 * @since 1.0
	 * @see Node
	 */
	protected static Node createConstraintTag() {
		NodeList nodeList = getDocument().getElementsByTagName(FMNames.CONSTRAINTS.getName());
		if (nodeList.getLength() > 0) {
			// aldready exists
			return nodeList.item(0);
		} else {
			Node consTag = getDocument().createElement(FMNames.CONSTRAINTS.getName());
			NodeList fmTagList = getDocument().getElementsByTagName(FMNames.FEATUREMODEL.getName());
			if (fmTagList.getLength() == 0) {
				// if it is an ExtendedFeatureModel
				fmTagList = getDocument().getElementsByTagName(FMNames.EXTENDEDFEATUREMODEL.getName());
			}
			Node rootNode = fmTagList.item(0);
			return rootNode.appendChild(consTag);
		}
	}

	/**
	 * Creates and returns a new feature ({@code FMTask}) with the given
	 * {@code name}.
	 *
	 * @param name name of the feature
	 * @return a new feature ({@code FMTask}) with the given {@code name}
	 *
	 * @since 1.0
	 * @see FMTask
	 */
	protected FMTask createFeatureWithName(String name) {
		Element feature = getDocument().createElement(FMNames.FEATURE.getName());
		feature.setAttribute(FMAttributes.NAME.getName(), name);
		return (FMTask) this.taskFactory.createTasks(feature).stream().findFirst().orElse(null);
	}

	/**
	 * Creates and returns the nested {@code Element} according to the
	 * {@code parent} data.
	 *
	 * <p>
	 *
	 * <b>Note</b> that the created {@code Element} is not added to the
	 * {@code parent}'s children.
	 *
	 * @param parent parent of the created nested node
	 * @param name   name of the nested node
	 * @return the nested {@code Element} according to the {@code parent} data
	 *
	 * @since 1.0
	 */
	protected static Element createNestedNode(Element parent, String name) {
		Element created = parent.getOwnerDocument().createElement(parent.getNodeName());
		created.setAttribute(BPMNAttributes.NAME.getName(), name);
		addDocumentationNode(created, XMLManager.getNodeName(parent));
		return created;
	}

	/**
	 * Creates the <b>tagName</b> {@code Node} and append it to the given
	 * {@code parent} if it doesn't exist.
	 *
	 * @param parent  {@code parent} of {@code tagName Node}
	 * @param tagName name of tag to be created
	 * @return the created tag
	 *
	 * @since 1.0
	 * @see Node
	 */
	protected Node createTag(FMTask parentTask, FMNames tagName) {
		Element parentElement = (Element) parentTask.getNode();
		NodeList nodeList = parentElement.getElementsByTagName(tagName.getName());
		if (nodeList.getLength() > 0) {
			// aldready exists
			return nodeList.item(0);
		}
		Node newTag = getDocument().createElement(tagName.getName());
		return parentElement.appendChild(newTag);
	}

	/**
	 * Returns a {@code List} of nested {@code Element} referred by the given
	 * {@code node}.
	 *
	 * <p>
	 *
	 * <b>Note</b> that nested nodes are created using the given {@code node}'s
	 * data.
	 *
	 * @param node node to extract nested ones
	 * @return a {@code List} of nested {@code Node} referred by the given
	 *         {@code node}
	 *
	 * @since 1.0
	 */
	public static List<Node> getNestedNodes(Node node) {
		List<Node> result = new ArrayList<>();
		// retrieving all nested nodes' names
		String[] nodeName = XMLManager.getNodeName(node).split(Notation.getGeneratedPrefixVoc());
		List<String> names = new ArrayList<>(Arrays.asList(nodeName));
		// sanitizing names
		names = names.stream().filter(n -> !n.isBlank()).map(XMLManager::sanitizeName).collect(Collectors.toList());
		if (names.size() == 1) {
			// if there is no nested node
			return Arrays.asList(node); // return the current node as a list
		}
		// Manage the parentNode
		Element parentNode = (Element) node.cloneNode(true);
		parentNode.setAttribute(BPMNAttributes.NAME.getName(), names.remove(0));
		result.add(parentNode);
		// foreach nested node's name
		for (String name : names) {
			parentNode = createNestedNode(parentNode, name);
			result.add(parentNode);
		}
		return result;
	}

	/**
	 * Returns the given {@code task}'s name.
	 *
	 * <p>
	 *
	 * <b>Note</b> that this method is different from the
	 * {@link XMLManager#getNodeName(Node)} because it only returns the deepest
	 * subtask's name.
	 *
	 * @param task task to get name
	 * @return the given {@code task}'s name
	 *
	 * @since 1.0
	 */
	protected String getTaskName(Node task) {
		String logMsg = String.format("Retrieving name for task : %s...", task);
		logger.debug(logMsg);
		String taskName = XMLManager.getNodeName(task);
		String[] splitted = taskName.split(Notation.getGeneratedPrefixVoc());
		taskName = (splitted.length > 0) ? splitted[splitted.length - 1] : taskName;
		logMsg = String.format("Task's name is : %s", taskName);
		logger.debug(logMsg);
		return taskName;
	}

	/**
	 * Returns a {@code List} containing all {@code nodes}' tasks names.
	 *
	 * @param nodes nodes to get the names
	 * @return a {@code List} containing all {@code nodes}' tasks names
	 *
	 * @since 1.0
	 * @see NodeList
	 */
	protected List<String> getTasksNames(List<Node> tasks) {
		return tasks.stream().map(this::getTaskName)
				.collect(Collectors.toList());
	}

	/**
	 * Returns the workflow's document and name with the given {@code filePath}
	 * using the {@link Pair} class. If the {@code filePath} is not a valid path or
	 * the workflow's name is already in the feature model, return an empty
	 * {@code Pair}.
	 *
	 * @param file {@code File} instance of the workflow
	 * @return a {@code Pair} association between the workflow's name and its
	 *         {@code Document} instance or an empty {@code Pair} if something is
	 *         wrong.
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 *
	 * @since 1.0
	 * @see Pair
	 * @see Document
	 */
	protected Pair<String, Document> getWFDocInfoFromFile(File file)
			throws ParserConfigurationException, SAXException, IOException {
		String wfTaskName;
		String logMsg;
		Document wfDocument;
		if (file.exists()) {
			wfDocument = XMLManager.preprocess(file);
			wfTaskName = getWorkflowName(wfDocument).replace(" ", "_");
			logMsg = String.format("WF's name is %s.", wfTaskName);
			logger.debug(logMsg);
			if (TasksManager.existsinFM(wfTaskName)) {
				logger.warn("This workflow is already in the FeatureModel");
				logger.warn("Skipping...");
				return new Pair<>();
			}
		} else {
			logMsg = String.format("Invalid filepath : %s", file.getAbsolutePath());
			logger.fatal(logMsg);
			return new Pair<>();
		}
		return new Pair<>(wfTaskName, wfDocument);
	}

	/**
	 * Inserts and returns the new task corresponding of the given {@code task}
	 * under the given {@code parentTask}.
	 *
	 * <p>
	 *
	 * The result task matchs the FeatureModel format.
	 *
	 * @param parentTask Parent task
	 * @param task       task to insert
	 * @return the added child
	 *
	 * @since 1.0
	 * @see Task
	 * @see FMTask
	 */
	protected FMTask insertNewTask(FMTask parentTask, Task task) {
		// TODO: recurse for nested tasks
		logger.debug("Inserting task : {}", task.getName());
		// retrieving task name content
		String taskName = task.getName();
		// inserting the new node
		if (task instanceof FMTask) {
			return parentTask.appendChild((FMTask) task);
		}
		FMTask newFeature = this.createFeatureWithName(taskName);
		return parentTask.appendChild(newFeature);
	}

	/**
	 * Returns a {@code List<Node>} containing all annotations of the
	 * {@code wfDocument}.
	 *
	 * @param wfDocument {@code Document} containing annotations
	 * @return a {@code List<Node>} containing all annotations of the
	 *         {@code wfDocument}
	 *
	 * @since 1.0
	 * @see Document
	 */
	protected List<Node> getAnnotations(Document wfDocument) {
		return XMLManager
				.nodeListAsList(wfDocument.getElementsByTagName(BPMNNames.ANNOTATION.getName()));
	}

	/**
	 * Processes {@code document}'s annotations and adds constraints.
	 *
	 * @param wfDocument Workflow {@code Document}'s instance containing
	 *                   annotations.
	 * @throws InvalidConstraintException
	 *
	 * @since 1.0
	 * @see ConstraintFactory
	 */
	protected void processAnnotations(List<Node> annotations) throws InvalidConstraintException {
		logger.info("Processing annotations...");
		List<Pair<FMTask, Node>> orderPairs; // pair that will contain order constraint data
		for (Node annotation : annotations) {
			// TODO: improve performances (check annotation.getChildNodes().item(1)
			// sufficient ?)
			for (Node commentNode : XMLManager.nodeListAsList(annotation.getChildNodes())) {
				String comment = commentNode.getTextContent();
				orderPairs = this.getConstraintFactory().getOrderNodes(comment);
				if (!orderPairs.isEmpty()) {
					// this is an order constraint
					this.processOrderConstraint(orderPairs);
				} else {
					// this is a "classic" constraint
					List<Node> newRules = this.getConstraintFactory().getRuleNodes(comment);
					this.adoptRules(newRules);
				}
			}
		}
		logger.info("Annotations processing ended...");
	}

	/**
	 * Processes the association constraints.
	 *
	 * <p>
	 *
	 * An association constraint is an implication between the {@code wfName} and
	 * the {@code Set<BPMNTask>}.
	 *
	 * @param wfName workflow's name
	 * @param tasks  {@code Set<Task>} containing all workflow's tasks
	 * @throws InvalidConstraintException
	 *
	 * @since 1.0
	 */
	protected void processAssocConstraints(String wfName, Set<Task> tasks) throws InvalidConstraintException {
		String logMsg;
		logger.debug("Retrieving all FM document tasks...");
		List<String> tasksNames = tasks.stream().map(Task::getName)
				.collect(Collectors.toList());
		logger.debug("Getting the constraint association...");
		String associationConstraint = ((ConstraintFactoryImpl) this.getConstraintFactory())
				.getAssociationConstraint(wfName, tasksNames);
		logMsg = String.format("Generated constraint association : %s", associationConstraint);
		logger.info(logMsg);
		// add the new constraint
		logger.info("Adding association constraint...");
		this.adoptRules(this.getConstraintFactory().getRuleNodes(associationConstraint));
	}

	/**
	 * Processes the order constraints.
	 *
	 * *
	 * <p>
	 *
	 * An association constraint is an order implication between two features.
	 *
	 * <p>
	 *
	 * e.g. A before B, C after D
	 *
	 * @param orderPairs {@code List} of {@code Pair} containing the LCA
	 *                   {@code FMTask} as key and the descriptive {@code Node} as
	 *                   value
	 *
	 * @since 1.0
	 *
	 * @see FMTask
	 * @see Pair
	 */
	protected void processOrderConstraint(List<Pair<FMTask, Node>> orderPairs) {
		for (Pair<FMTask, Node> pair : orderPairs) {
			FMTask key;
			if ((key = pair.getKey()) != null) {
				// the order association has been parsed
				Node docNode = this.createTag(key, FMNames.DESCRIPTION);
				if (!XMLManager.mergeNodesTextContent(docNode, pair.getValue().getTextContent())) {
					// if there is a merge failure for description nodes
					logger.error("The merge operation for description nodes failed.");
				}
			} else {
				// the operation failed
				logger.error("Can't merge the order constraint : {}", pair.getValue().getTextContent());
				logger.warn("Maybe one of the involved task is not in the current workflow ?");
			}
		}
	}

}
