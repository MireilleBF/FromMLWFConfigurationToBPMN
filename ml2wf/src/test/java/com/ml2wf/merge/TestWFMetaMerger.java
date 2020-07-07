package com.ml2wf.merge;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ml2wf.AbstractXMLTest;
import com.ml2wf.constraints.InvalidConstraintException;
import com.ml2wf.conventions.Notation;
import com.ml2wf.conventions.enums.fm.FMAttributes;
import com.ml2wf.conventions.enums.fm.FMNames;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNAttributes;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNNames;
import com.ml2wf.merge.concretes.WFMetaMerger;
import com.ml2wf.util.XMLManager;

/**
 * This class tests the {@link WFTasksMerger} class.
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
 * @see WFTasksMerger
 *
 */
@DisplayName("Test of WFTasksMerger")
public class TestWFMetaMerger extends AbstractXMLTest {

	/**
	 * Back up path.
	 */
	private String backUpPath;
	/**
	 * Default XML filename.
	 */
	private static final String FM_FILE_PATH = "./src/test/resources/feature_models/model.xml";

	@BeforeEach
	public void setUp() throws TransformerException, SAXException, IOException, ParserConfigurationException,
			InvalidConstraintException {
		// loading xml test file
		this.testedClass = new WFMetaMerger(new File(FM_FILE_PATH));
	}

	@AfterEach
	public void clean() throws IOException, URISyntaxException {
		this.testedClass = null;
		this.sourceDocument = null;
		this.resultDocument = null;
		// TODO; clean the test directory
		// this.cleanTestDir();
		// this.backUpPath = null;
	}

	/**
	 * Updates the {@link #backUpPath}.
	 *
	 * @return the back up path.
	 */
	private String updateBackUpPath(String fm_source_file_path) {
		SimpleDateFormat dateFormater = null;
		Date backUpDate = new Date();
		dateFormater = new SimpleDateFormat("_dd_MM_yy_hh_mm");
		String[] splittedPath = fm_source_file_path.split("\\.");
		this.backUpPath = splittedPath[0] + Notation.getBackupVoc() + dateFormater.format(backUpDate) + "."
				+ splittedPath[1];
		return this.backUpPath;
	}

	/**
	 * Cleans the src/test/resources directory.
	 *
	 * <p>
	 *
	 * It allows other tests to be performed independently.
	 *
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	private void cleanTestDir() throws IOException, URISyntaxException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		// saving the modified FM file
		URL url = classLoader.getResource(FM_FILE_PATH);
		Path path = Paths.get(url.toURI());
		Files.copy(path, path.resolveSibling(FM_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
		// replacing the FM source file by the backed up one
		url = classLoader.getResource(this.backUpPath);
		path = Paths.get(url.toURI());
		Files.move(path, path.resolveSibling(FM_FILE_PATH), StandardCopyOption.REPLACE_EXISTING);
	}

	/**
	 * Tests that the merged XML file <b>contains all new WF's tasks</b>.
	 *
	 * <p>
	 *
	 * More precisely, this method tests if :
	 *
	 * <ol>
	 * <li>all WF's tasks are present in the result XML FM file.</li>
	 * </ol>
	 *
	 * <p>
	 *
	 * <b>Note</b> that this is a {@link ParameterizedTest}.
	 *
	 * @throws Exception
	 *
	 * @since 1.0
	 *
	 */
	@ParameterizedTest
	@MethodSource("instanceFiles")
	@DisplayName("Test of merging feature")
	public void testMergingStructure(Path path)
			throws Exception {
		((WFMetaMerger) this.testedClass).mergeWithWF(false, true, new File(path.toUri()));
		this.sourceDocument = XMLManager.preprocess(path.toFile());
		this.resultDocument = XMLManager.getDocument();
		// getting WF's source task nodes
		List<Node> sourceNodes = XMLManager.getTasksList(this.sourceDocument, BPMNNames.SELECTOR);
		// List<Node> sourceNestedNodes = ;
		// getting FM tasks
		List<Node> resultNodes = XMLManager.getTasksList(this.resultDocument, FMNames.SELECTOR);
		// getting tasks' names
		List<String> sourceNodesNames = sourceNodes.stream()
				.flatMap(n -> AbstractMerger.getNestedNodes(n).stream()) // flattening
				.map(Node::getAttributes) // getting attributes
				.map(a -> a.getNamedItem(BPMNAttributes.NAME.getName())) // getting Name attribute
				.map(Node::getNodeValue) // getting name value
				.map((v) -> XMLManager.sanitizeName(v))
				.collect(Collectors.toList());
		List<String> resultNodesNames = resultNodes.stream().map(Node::getAttributes)
				.map(a -> a.getNamedItem(FMAttributes.NAME.getName())).map(Node::getNodeValue)
				.map((v) -> XMLManager.sanitizeName(v)).collect(Collectors.toList());
		// testing
		assertTrue(resultNodesNames.containsAll(sourceNodesNames)); // #1
	}

	// TODO: add a test checking that generated nodes are children of the right
	// parents
}
