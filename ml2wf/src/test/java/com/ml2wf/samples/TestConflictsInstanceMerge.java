package com.ml2wf.samples;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.ml2wf.TestHelper;
import com.ml2wf.util.FMHelper;

/**
 * @author blay
 *
 */
public class TestConflictsInstanceMerge {

	/**
	 * {@code Logger}'s instance.
	 */
	private static final Logger logger = LogManager.getLogger(TestConflictsInstanceMerge.class);

	private static final String CONFLICT_SAMPLE_PATH = "./src/test/resources/ForValidationTests/onConflicts/";
	private static final String FM_IN_PATH = CONFLICT_SAMPLE_PATH + "feature_models/";
	private static final String DEFAULT_IN_FM = FM_IN_PATH + "FMA.xml";
	private static final String FM_OUT_PATH = "./target/generated/FM_CI/";
	private static final String WF_IN_PATH = CONFLICT_SAMPLE_PATH + "wf_instance/";

	@BeforeEach
	public void setUp() throws ParserConfigurationException, SAXException, IOException, URISyntaxException {
		logger.info("Hello Test Conflicts in merge of instance WF!");
	}

	@AfterEach
	public void clean() {
	}

	@Test
	@DisplayName("No Conflict : just to ensure equivalence between refersTo and #")
	public void testWFInstance1UsingCommandLine() throws ParserConfigurationException, SAXException, IOException {
		String wfPATH = WF_IN_PATH + "WF1_instance.bpmn2";
		File fin = new File(wfPATH);
		assertTrue(fin.exists());

		String sourceFM = DEFAULT_IN_FM;
		// I make a copy for test
		String copiedFM = FM_OUT_PATH + "FMA_WF1_i.xml";
		TestHelper.copyFM(sourceFM, copiedFM);

		FMHelper fmBefore = new FMHelper(copiedFM);
		// Command
		String[] command = new String[] { "merge", "--instance", "-i ", wfPATH, "-o ", copiedFM, "-v", "7" };
		com.ml2wf.App.main(command);

		assertTrue(new File(copiedFM).exists());
		FMHelper fmAfter = new FMHelper(copiedFM);

		// General Properties to check
		List<String> afterList = TestHelper.nothingLost(fmBefore, fmAfter, wfPATH);
		// This test involves managing naming differences using '_' in FM and BPMN
		String logMsg = String.format("added features : %s ", afterList);
		logger.debug(logMsg);
		System.out.println(logMsg);

		// Specific properties
		assertEquals(1, afterList.size());
		assertTrue(fmAfter.isChildOf("F31", "F31_0"));

		// Check idempotence
		TestHelper.checkIdempotence(copiedFM, command);

		// ---- The same behavior is expected but the task refers_to it's name
		// #F31#F31_0
		wfPATH = WF_IN_PATH + "WF1_instance2.bpmn2";
		sourceFM = DEFAULT_IN_FM;
		// I make a copy for test
		String copiedFMBis = FM_OUT_PATH + "FMA_WF1_i_bis.xml";
		TestHelper.copyFM(sourceFM, copiedFMBis);

		// Command
		command = new String[] { "merge", "--instance", "-i ", wfPATH, "-o ", copiedFMBis, "-v", "7" };
		com.ml2wf.App.main(command);
		assertTrue(new File(copiedFMBis).exists());
		fmAfter = new FMHelper(copiedFMBis);

		// General Properties to check
		afterList = TestHelper.nothingLost(fmBefore, fmAfter, wfPATH);
		// This test involves managing naming differences using '_' in FM and BPMN
		logMsg = String.format("added features : %s ", afterList);
		logger.debug(logMsg);
		System.out.println(logMsg);

		// Specific properties
		assertEquals(1, afterList.size());
		assertTrue(fmAfter.isChildOf("F31", "F31_0"));

	}

	// Todo : test the presence of a warning
	@Test
	@DisplayName("Warning : refer to a task known as abstract")
	public void testWFInstance2UsingCommandLine() throws ParserConfigurationException, SAXException, IOException {
		String wfPATH = WF_IN_PATH + "WF2_instance.bpmn2";
		File fin = new File(wfPATH);
		assertTrue(fin.exists());

		String sourceFM = DEFAULT_IN_FM;
		// I make a copy for test
		String copiedFM = FM_OUT_PATH + "FMA_WF2_i.xml";
		TestHelper.copyFM(sourceFM, copiedFM);

		FMHelper fmBefore = new FMHelper(copiedFM);
		// Command
		String[] command = new String[] { "merge", "--instance", "-i ", wfPATH, "-o ", copiedFM, "-v", "7" };
		com.ml2wf.App.main(command);

		assertTrue(new File(copiedFM).exists());
		FMHelper fmAfter = new FMHelper(copiedFM);

		// General Properties to check
		List<String> afterList = TestHelper.nothingLost(fmBefore, fmAfter, wfPATH);
		// This test involves managing naming differences using '_' in FM and BPMN
		String logMsg = String.format("added features : %s ", afterList);
		logger.debug(logMsg);
		System.out.println(logMsg);

		// Specific properties
		// No features are added
		assertEquals(0, afterList.size());
		assertFalse(fmAfter.isAbstract("F31"));
	}

	// Todo : test the presence of a warning
	@Test
	@DisplayName("Unmanaged : A task not refered to is stored in the FM")
	public void testWFInstance3UsingCommandLine() throws ParserConfigurationException, SAXException, IOException {
		String wfPATH = WF_IN_PATH + "WF3_instance.bpmn2";
		File fin = new File(wfPATH);
		assertTrue(fin.exists());

		String sourceFM = DEFAULT_IN_FM;
		// I make a copy for test
		String copiedFM = FM_OUT_PATH + "FMA_WF3_i.xml";
		TestHelper.copyFM(sourceFM, copiedFM);

		FMHelper fmBefore = new FMHelper(copiedFM);
		// Command
		String[] command = new String[] { "merge", "--instance", "-i ", wfPATH, "-o ", copiedFM, "-v", "7" };
		com.ml2wf.App.main(command);

		assertTrue(new File(copiedFM).exists());
		FMHelper fmAfter = new FMHelper(copiedFM);

		// General Properties to check
		List<String> afterList = TestHelper.nothingLost(fmBefore, fmAfter, wfPATH);
		// This test involves managing naming differences using '_' in FM and BPMN
		String logMsg = String.format("added features : %s ", afterList);
		logger.debug(logMsg);
		System.out.println(logMsg);

		// Specific properties
		// No features are added
		assertEquals(2, afterList.size());
		// FIX
		// assertFalse(fmAfter.isAbstract("F32"));
		assertTrue(fmAfter.isChildOf("Unmanaged", "F32"));

	}

	// One feature is abstract and concret
	// e.g. a Meta declare Normalize and an instance uses also Normalize
	// ==> suggest user to specialize Normalize with a concrete task ?
	// e.g. Normalize is said concrete in FM
	// a Meta uses Normalize
	// ==> A warning is raised and Normalize becomes "abstract"?
}