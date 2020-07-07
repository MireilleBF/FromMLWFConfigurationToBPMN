package com.ml2wf.generation;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ml2wf.AbstractXMLTest;
import com.ml2wf.constraints.InvalidConstraintException;
import com.ml2wf.conventions.Notation;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNAttributes;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNNames;
import com.ml2wf.merge.base.BaseMergerImpl;
import com.ml2wf.util.XMLManager;

/**
 * This class tests the {@link InstanceFactoryImpl} class.
 *
 * <p>
 *
 * It is an extension of the {@code AbstractXMLTest} base class.
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
 * @see AbstractXMLTest
 * @see InstanceFactoryImpl
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
@DisplayName("Test of InstanceFactoryImpl")
public class TestInstanceFactoryImpl extends AbstractXMLTest {

	/**
	 * {@code ClassLoader}'s instance used to get resources.
	 *
	 * @see ClassLoader
	 */
	private static ClassLoader classLoader = TestInstanceFactoryImpl.class.getClassLoader();
	/**
	 * Default XML filename.
	 */
	private static final String SOURCE_FILE_PATH = "./wf_meta/simple_wf.bpmn";

	@BeforeAll
	public void setUp() throws TransformerException, SAXException, IOException, ParserConfigurationException,
			InvalidConstraintException, URISyntaxException {
		// loading xml test file
		URL url = classLoader.getResource(SOURCE_FILE_PATH);
		// getting source document
		this.sourceDocument = XMLManager.getDocumentFromURL(url);
		// initializing factory
		this.testedClass = new InstanceFactoryImpl(new File(url.toURI()));
		// instantializing generic WF
		((InstanceFactoryImpl) this.testedClass).getWFInstance();
	}

	@AfterEach
	public void clean() {
		this.resultDocument = null;
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
	public void testGlobalStructure(Path path) throws ParserConfigurationException, SAXException, IOException {
		this.resultDocument = XMLManager.preprocess(path.toFile());
		// TODO: to complete/improve considering the wished number of instantiated tasks
		List<Node> sourceNodes = XMLManager.getTasksList(this.sourceDocument, BPMNNames.SELECTOR);
		List<Node> resultNodes = XMLManager.getTasksList(this.resultDocument, BPMNNames.SELECTOR);
		assertTrue(sourceNodes.size() <= resultNodes.size()); // #1
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
	@DisplayName("Verification of the nodes structure")
	public void testNodesStructures(Path path) throws ParserConfigurationException, SAXException, IOException {
		this.resultDocument = XMLManager.preprocess(path.toFile());
		List<Node> resultNodes = XMLManager.getTasksList(this.resultDocument, BPMNNames.SELECTOR);
		for (Node resultNode : resultNodes) {
			NodeList extensionChildren = ((Element) resultNode)
					.getElementsByTagName(BPMNNames.EXTENSION.getName());
			assertTrue(extensionChildren.getLength() > 0);
			NodeList docChildren = ((Element) resultNode).getElementsByTagName(BPMNNames.DOCUMENTATION.getName());
			assertTrue(docChildren.getLength() > 0);
			Node docNode = docChildren.item(0);
			assertTrue(Pattern.matches(Notation.getDocumentationVoc() + "\\d+",
					docNode.getAttributes().getNamedItem(BPMNAttributes.ID.getName()).getNodeValue()));
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
	public void testReferences(Path path) throws ParserConfigurationException, SAXException, IOException {
		// TODO: improve readability
		this.resultDocument = XMLManager.preprocess(path.toFile());
		// retrieving task nodes
		List<Node> sourceNodes = XMLManager.getTasksList(this.sourceDocument, BPMNNames.SELECTOR);
		List<Node> resultNodes = XMLManager.getTasksList(this.resultDocument, BPMNNames.SELECTOR);
		// retrieving meta tasks and instantiated references
		List<String> references = resultNodes.stream().map(Element.class::cast)
				.map(e -> e.getElementsByTagName(BPMNNames.DOCUMENTATION.getName())) // getting doc nodes
				.map(n -> n.item(0).getTextContent()) // getting first doc node's content
				.map(XMLManager::getReferredTask)
				.map(r -> r.orElse(BaseMergerImpl.UNMANAGED))
				.collect(Collectors.toList()); // get referred task
		List<String> metaTasksNames = sourceNodes.stream().map(Node::getAttributes)
				.map(a -> a.getNamedItem(BPMNAttributes.NAME.getName())).map(Node::getNodeValue)
				.map(XMLManager::sanitizeName)
				.collect(Collectors.toList());
		// comparing
		assertTrue(references.containsAll(metaTasksNames)); // #1
	}
}
