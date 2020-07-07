package com.ml2wf.merge.concretes;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ml2wf.merge.base.BaseMergerImpl;
import com.ml2wf.tasks.concretes.BPMNTask;
import com.ml2wf.tasks.concretes.FMTask;
import com.ml2wf.util.Pair;

/**
 * This class merges a given MetaWorkflow into a FeatureModel xml file.
 *
 * <p>
 *
 * It is an extension of the {@link BaseMergerImpl} base class.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see BaseMergerImpl
 *
 */
public class WFMetaMerger extends BaseMergerImpl {

	/**
	 * Meta default task tag name.
	 */
	private static final String META_TASK = "Meta";
	/**
	 * Steps default task tag name.
	 */
	public static final String STEP_TASK = "Steps";

	/**
	 * {@code WFMetaMerger}'s default constructor.
	 *
	 * @param file the XML {@code File}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public WFMetaMerger(File file) throws ParserConfigurationException, SAXException, IOException {
		super(file);
	}

	@Override
	public FMTask getSuitableParent(BPMNTask task) {
		return this.getReferredFMTask(task, this.getGlobalFMTask(STEP_TASK));
	}

	@Override
	public FMTask getRootParentNode() {
		return this.getGlobalFMTask(META_TASK);
	}

	@Override
	public void processSpecificNeeds(Pair<String, Document> wfInfo) throws Exception {
		// TODO
	}

}
