package com.ml2wf.generation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ml2wf.constraints.InvalidConstraintException;
import com.ml2wf.conventions.Notation;
import com.ml2wf.conventions.enums.bpmn.BPMNNodesAttributes;
import com.ml2wf.conventions.enums.bpmn.BPMNNodesNames;
import com.ml2wf.util.XMLManager;

/**
 * This class tests the {@link InstanceFactoryImpl} class.
 *
 * <p>
 *
 * Tests are executed with the <a href="https://junit.org/junit5/">JUnit
 * framework</a>.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see InstanceFactoryImpl
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Test of InstanceFactoryImpl")
public class TestInstanceFactoryImpl {

	/**
	 * Instance of the class to be tested.
	 *
	 * @see InstanceFactoryImpl
	 */
	private InstanceFactoryImpl factory;
	/**
	 * XML source document.
	 *
	 * @see Document
	 */
	private Document sourceDocument;
	/**
	 * XML result document.
	 *
	 * @see Document
	 */
	private Document resultDocument;
	/**
	 * {@code ClassLoader}'s instance used to get resources.
	 *
	 * @see ClassLoader
	 */
	private static ClassLoader classLoader = TestInstanceFactoryImpl.class.getClassLoader();
	/**
	 * The BPMN extension used to filter files.
	 */
	private static final String BPMN_EXTENSION = ".bpmn";
	/**
	 * Default XML filename.
	 */
	private static final String SOURCE_FILE_PATH = "./wf_meta/simple_wf.bpmn";
	/**
	 * Result filename according to {@code FILE_NAME}.
	 */
	private static final String INSTANCES_DIRECTORY = "./wf_instances/";

	@BeforeAll
	public void setUp() throws TransformerException, SAXException, IOException, ParserConfigurationException,
			InvalidConstraintException {
		// loading xml test file
		URL url = classLoader.getResource(SOURCE_FILE_PATH);
		String fDirectory = url.getPath().replace("%20", " "); // TODO: improve sanitization
		// getting source document
		this.sourceDocument = XMLManager.getDocumentFromURL(url);
		// initializing factory
		this.factory = new InstanceFactoryImpl(fDirectory);
		// instantializing generic WF
		this.factory.getWFInstance();
	}

	@AfterEach
	public void clean() {
		this.resultDocument = null;
	}

	/**
	 * Returns all {@code File}'s instances in the {@code INSTANCES_DIRECTORY}
	 * directory.
	 *
	 * @return all {@code File}'s instances in the {@code INSTANCES_DIRECTORY}
	 *         directory
	 * @throws IOException
	 * @throws URISyntaxException
	 *
	 * @since 1.0
	 */
	static Stream<File> instanceFiles() throws IOException, URISyntaxException {
		URI uri = classLoader.getResource(INSTANCES_DIRECTORY).toURI();
		Path myPath = Paths.get(uri);
		return Files.walk(myPath, 1).filter(p -> p.toString().endsWith(BPMN_EXTENSION)).map(Path::toFile);
	}

	/**
	 * Tests that the instantiated XML file <b>contains all source file's tasks</b>.
	 *
	 * <p>
	 *
	 * More precisely, this method tests if :
	 *
	 * <ol>
	 * <li>all generic <b>tasks</b> have been instantiated <b>at least one
	 * time</b>,</li>
	 * <li>all generic <b>usertasks</b> have been instantiated <b>at least one
	 * time</b></li>
	 * </ol>
	 *
	 * <p>
	 *
	 * <b>Note</b> that this is a {@link ParameterizedTest}.
	 *
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 *
	 * @since 1.0
	 *
	 */
	@ParameterizedTest
	@MethodSource("instanceFiles")
	@DisplayName("Verification of the global structure")
	public void testGlobalStructure(File file) throws ParserConfigurationException, SAXException, IOException {
		this.resultDocument = XMLManager.preprocess(file);
		// TODO: to complete/improve considering the wished number of instantiated tasks
		NodeList sourceNodes = this.sourceDocument.getElementsByTagName(BPMNNodesNames.TASK.getName());
		NodeList resultNodes = this.resultDocument.getElementsByTagName(BPMNNodesNames.TASK.getName());
		assertTrue(sourceNodes.getLength() <= resultNodes.getLength()); // #1
		sourceNodes = this.sourceDocument.getElementsByTagName(BPMNNodesNames.USERTASK.getName());
		resultNodes = this.resultDocument.getElementsByTagName(BPMNNodesNames.USERTASK.getName());
		assertTrue(sourceNodes.getLength() <= resultNodes.getLength()); // #2
	}

	/**
	 * Tests that instantiated nodes have the <b>right structure</b>.
	 *
	 * <p>
	 *
	 * More precisely, this method tests if :
	 *
	 * <ol>
	 * <li>each node has an <b>extension child node</b>,</li>
	 * <li>each node has a <b>documentation child node</b>,</li>
	 * <li>each documentation node's value <b>matchs the defined regex</b></li>
	 * </ol>
	 *
	 * <p>
	 *
	 * <b>Note</b> that this is a {@link ParameterizedTest}.
	 *
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 *
	 * @since 1.0
	 */
	@ParameterizedTest
	@MethodSource("instanceFiles")
	@DisplayName("Verification of the nodes' structures")
	public void testNodesStructures(File file) throws ParserConfigurationException, SAXException, IOException {
		this.resultDocument = XMLManager.preprocess(file);
		// TODO: tests for usertask
		NodeList resultNodes = this.resultDocument.getElementsByTagName(BPMNNodesNames.TASK.getName());
		for (int i = 0; i < resultNodes.getLength(); i++) {
			Element node = (Element) resultNodes.item(i);
			NodeList extensionChildren = node.getElementsByTagName(BPMNNodesNames.EXTENSION.getName());
			assertTrue(extensionChildren.getLength() > 0);
			NodeList docChildren = node.getElementsByTagName(BPMNNodesNames.DOCUMENTATION.getName());
			assertTrue(docChildren.getLength() > 0);
			Node docNode = docChildren.item(0);
			assertTrue(Pattern.matches(Notation.getDocumentationVoc() + "\\d+",
					docNode.getAttributes().getNamedItem(BPMNNodesAttributes.ID.getName()).getNodeValue()));
		}
	}

	/**
	 * Tests that instantiated tasks are <b>refering the right generic tasks</b>.
	 *
	 * <p>
	 *
	 * More precisely, this method tests if :
	 *
	 * <ol>
	 * <li>reference nodes are referencing <b>all generic tasks</b>.</li>
	 * </ol>
	 *
	 * <p>
	 *
	 * <b>Note</b> that this is a {@link ParameterizedTest}.
	 *
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 *
	 * @since 1.0
	 */
	@ParameterizedTest
	@MethodSource("instanceFiles")
	@DisplayName("Verification of references")
	public void testReferences(File file) throws ParserConfigurationException, SAXException, IOException {
		// TODO: improve readability
		this.resultDocument = XMLManager.preprocess(file);
		// retrieving task nodes
		List<Node> sourceNodes = XMLManager.getTasksList(this.sourceDocument, BPMNNodesNames.SELECTOR);
		List<Node> resultNodes = XMLManager.getTasksList(this.resultDocument, BPMNNodesNames.SELECTOR);
		// retrieving meta tasks and instantiated references
		List<String> references = resultNodes.stream().map(Element.class::cast)
				.map(e -> e.getElementsByTagName(BPMNNodesNames.DOCUMENTATION.getName())) // getting doc nodes
				.map(n -> n.item(0).getTextContent()) // getting first doc node's content
				.map(XMLManager::getReferredTask).collect(Collectors.toList()); // get referred task
		List<String> metaTasksNames = sourceNodes.stream().map(Node::getAttributes)
				.map(a -> a.getNamedItem(BPMNNodesAttributes.NAME.getName())).map(Node::getNodeValue)
				.map(XMLManager::getReferredTask).collect(Collectors.toList());
		// comparing
		assertTrue(references.containsAll(metaTasksNames)); // #1
	}
}
