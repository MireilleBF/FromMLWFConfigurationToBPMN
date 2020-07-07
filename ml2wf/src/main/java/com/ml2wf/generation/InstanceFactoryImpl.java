package com.ml2wf.generation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ml2wf.constraints.InvalidConstraintException;
import com.ml2wf.conventions.Notation;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNAttributes;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNNames;
import com.ml2wf.util.XMLManager;

/**
 * This class is a factory for the instantiation of abstract workflows.
 *
 * <p>
 *
 * It is an extension of the {@link XMLManager} base class.
 *
 * <p>
 *
 * It is an implementation of the {@link InstanceFactory} interface.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 */
public class InstanceFactoryImpl extends XMLManager implements InstanceFactory {

	/**
	 * Hexadecimal color code of instantiated elements.
	 */
	public static final String INSTANTIATE_COLOR = "#00f900";
	/**
	 * This {@code taskCounter} is incremented for each task. This allows other
	 * methods to give an unique name to a given task.
	 */
	private int taskCounter = 0;
	/**
	 * Logger instance.
	 *
	 * @since 1.0
	 * @see Logger
	 */
	private static final Logger logger = LogManager.getLogger(InstanceFactoryImpl.class);

	/**
	 * {@code InstanceFactory}'s default constructor.
	 *
	 * @param file the XML file.
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 */
	public InstanceFactoryImpl(File file) throws ParserConfigurationException, SAXException, IOException {
		super(file);
	}

	/**
	 * Instantiates the given workflow and saves it using the {@link #save()}
	 * method.
	 *
	 * <p>
	 *
	 * More precisely,
	 *
	 * <ul>
	 * <li>iterates over all XML file's nodes,</li>
	 * <li>replaces each <b>task</b> node by an instantiated version,</li>
	 * <li>saves the modifications using the {@link #save()} method.</li>
	 * </ul>
	 *
	 * <p>
	 *
	 * The result filename will be <b>FileBaseName</b> +
	 * <b>{@link Notation#getInstanceVoc()}</b> + <b>.FileExtension</b>.
	 *
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 * @throws InvalidConstraintException
	 *
	 * @since 1.0
	 * @see Node
	 */
	@Override
	public void getWFInstance(File outputDir)
			throws TransformerException, SAXException, IOException, ParserConfigurationException {
		logger.info("Starting the Workflow instatiation...");
		if (!outputDir.isDirectory()) {
			outputDir = outputDir.getParentFile();
		}
		this.addMetaWFReferences();
		String logMsg;
		for (Node node : XMLManager.getTasksList(getDocument(), BPMNNames.SELECTOR)) {
			logMsg = String.format("Instantiating the node %s...", node);
			logger.debug(logMsg);
			this.instantiateNode(node);
		}
		logger.info("Instantiation finished.");
		String resultFname = XMLManager.insertInFileName(super.getSourceFile().getName(), Notation.getInstanceVoc());
		super.save(new File(Paths.get(outputDir.getPath(), resultFname).toString()));
	}

	/**
	 * Calls the {@link #getWFInstance(String)} method with
	 * {@link #getSourceFile()} as parameter.
	 *
	 * @throws TransformerException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws InvalidConstraintException
	 *
	 * @since 1.0
	 * @see {@link #getWFInstance(String)}
	 */
	public void getWFInstance() throws TransformerException, SAXException, IOException, ParserConfigurationException {
		this.getWFInstance(super.getSourceFile());
	}

	/**
	 * Instantiates the given {@code node}.
	 *
	 * <p>
	 *
	 * More precisely,
	 *
	 * <ul>
	 * <li>adds the documentation node using the {@link #addDocumentationNode(Node)}
	 * method,</li>
	 * <li>adds the extension node using the {@link #addExtensionNode(Node)}
	 * method,</li>
	 * <li>renames the current node</li>
	 * </ul>
	 *
	 * @param node Node to instantiate.
	 *
	 * @since 1.0
	 * @see Node
	 */
	private void instantiateNode(Node node) {
		// documentation part
		// TODO: factorize in method ?
		String content = node.getAttributes().getNamedItem(BPMNAttributes.NAME.getName()).getNodeValue();
		addDocumentationNode(node, content);
		// extension part
		this.addExtensionNode(node);
		// node renaming part
		Node nodeAttrName = node.getAttributes().getNamedItem(BPMNAttributes.NAME.getName());
		// TODO: update instance syntax
		String nodeName = XMLManager.sanitizeName(nodeAttrName.getNodeValue()) + "_" + this.taskCounter++;
		nodeAttrName.setNodeValue(nodeName);
	}

	/**
	 * Adds the metaworkflow references in the document.
	 *
	 * <p>
	 *
	 * More precisely,
	 *
	 * <p>
	 *
	 * <ul>
	 * <li>add the reference in a documentation tag with the
	 * {@link #addMetaWFRefDoc(String)} method,</li>
	 * <li>add the reference in an annotation with the
	 * {@link #addMetaWFRefAnnot(String...)} method,</li>
	 * </ul>
	 *
	 * @since 1.0
	 */
	private void addMetaWFReferences() {
		// TODO: add logs
		logger.debug("Adding the meta reference...");
		String metaReference = XMLManager.getWorkflowName(getDocument()).replace(" ", "_");
		this.addMetaWFRefDoc(metaReference);
		this.addWFRefAnnot(metaReference);
	}

	/**
	 * Adds the metaworkflow reference in a documentation tag under the process one.
	 *
	 * @param reffered referred meta task
	 *
	 * @since 1.0
	 */
	private void addMetaWFRefDoc(String referred) {
		logger.debug("Adding meta referrence in the documentation...");
		NodeList processNodeList = getDocument().getElementsByTagName(BPMNNames.PROCESS.getName());
		if (processNodeList.getLength() == 0) {
			logger.error("Error while getting the reffered metaworkflow's name.");
			logger.warn("Skipping this step...");
			return;
		}
		Node processNode = processNodeList.item(0);
		addDocumentationNode(processNode, referred);
	}

	/**
	 * Adds the workflow's reference in an annotation tag visible to the user.
	 *
	 * @param reference the referred meta-workflow
	 *
	 * @since 1.0
	 */
	private void addWFRefAnnot(String reference) {
		Node globalAnnotation = XMLManager.getGlobalAnnotationNode(getDocument());
		List<String> lines = new ArrayList<>(Notation.getGlobalAnnotationDefaultContent());
		String metaRef = lines.remove(1);
		metaRef = String.format(metaRef, Notation.getReferencesDelimiterLeft(), reference,
				Notation.getReferencesDelimiterRight());
		lines = lines.stream()
				.map(l -> String.format(l, Notation.getReferencesDelimiterLeft(),
						Notation.getReferencesDelimiterRight()))
				.collect(Collectors.toList());
		lines.add(1, metaRef);
		Node newTextNode = getDocument().createElement(BPMNNames.TEXT.getName());
		newTextNode.setTextContent(String.join("\n", lines));
		globalAnnotation.appendChild(newTextNode);
	}

	/**
	 * Adds the extension part to the given {@code node}.
	 *
	 * <p>
	 *
	 * The extension consists in style parameters to highlight instantiated tasks.
	 *
	 * @param node Node to add the extension
	 *
	 * @since 1.0
	 * @see Node
	 */
	private void addExtensionNode(Node node) {
		Node extension = getDocument().createElement(BPMNNames.EXTENSION.getName());
		Element style = getDocument().createElement(BPMNNames.STYLE.getName());
		String logMsg = String.format("	Adding style %s to node %s", INSTANTIATE_COLOR, node);
		logger.debug(logMsg);
		style.setAttribute(BPMNAttributes.BACKGROUND.getName(), INSTANTIATE_COLOR);
		extension.appendChild(style);
		logMsg = String.format("   Inserting node : %s before %s...", node, node.getFirstChild());
		logger.debug(logMsg);
		node.insertBefore(extension, node.getFirstChild());
	}

}
