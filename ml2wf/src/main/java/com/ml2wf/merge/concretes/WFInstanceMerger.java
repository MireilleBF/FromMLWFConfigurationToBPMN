package com.ml2wf.merge.concretes;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.ml2wf.constraints.InvalidConstraintException;
import com.ml2wf.constraints.factory.ConstraintFactoryImpl;
import com.ml2wf.conventions.Notation;
import com.ml2wf.conventions.enums.fm.FMNames;
import com.ml2wf.merge.base.BaseMergerImpl;
import com.ml2wf.tasks.concretes.BPMNTask;
import com.ml2wf.tasks.concretes.FMTask;
import com.ml2wf.util.Pair;
import com.ml2wf.util.XMLManager;

/**
 * This class merges a given Workflow's instance into a FeatureModel xml file.
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
public class WFInstanceMerger extends BaseMergerImpl {

	/**
	 * Instances default task tag name.
	 */
	private static final String INSTANCES_TASK = "Instances";
	/**
	 * Logger instance.
	 *
	 * @since 1.0
	 * @see Logger
	 */
	private static final Logger logger = LogManager.getLogger(WFInstanceMerger.class);

	/**
	 * {@code WFInstanceMerger}'s default constructor.
	 *
	 * @param file the XML {@code File}
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public WFInstanceMerger(File file) throws ParserConfigurationException, SAXException, IOException {
		super(file);
	}

	@Override
	public FMTask getSuitableParent(BPMNTask task) {
		return this.getReferredFMTask(task, unmanagedTask);
	}

	@Override
	public FMTask getRootParentNode() {
		return this.getGlobalFMTask(INSTANCES_TASK);
	}

	@Override
	public void processSpecificNeeds(Pair<String, Document> wfInfo) throws InvalidConstraintException {
		Document wfDocument = wfInfo.getValue();
		logger.debug("Specific need : meta reference.");
		String metaReferrence = this.getMetaReferenced(wfDocument);
		if (metaReferrence == null) {
			logger.error("No referenced meta-workflow.");
			logger.info("Make sure to use instance-Workflows using the generate command before merging/building.");
			logger.error("Skipping...");
			return;
		}
		String associationConstraint = ((ConstraintFactoryImpl) super.getConstraintFactory())
				.getAssociationConstraint(wfInfo.getKey(), Arrays.asList(metaReferrence));
		this.adoptRules(this.getConstraintFactory().getRuleNodes(associationConstraint));
		this.addReferences(wfDocument);
	}

	/**
	 * Returns the referenced metaworkflow's name.
	 *
	 * @param wfDocument document containing the reference
	 * @return the referenced metaworkflow's name
	 *
	 * @since 1.0
	 */
	private String getMetaReferenced(Document wfDocument) {
		List<String> docContent = XMLManager.getAllBPMNDocContent(wfDocument.getDocumentElement());
		return getReferredTask(docContent).orElse(UNMANAGED);
	}

	/**
	 * Adds a description {@code Node} to the current {@code createdWFNode}
	 * containing all references (meta-workflow, dataset, author/articleà
	 *
	 * @param wfDocument document containing the references
	 *
	 * @since 1.0
	 */
	private void addReferences(Document wfDocument) {
		logger.debug("Adding references...");
		// getting global annotation content
		Node globalAnnotation = getGlobalAnnotationNode(wfDocument);
		String references = globalAnnotation.getTextContent();
		// removing WF's name
		// and references delimiters
		// TODO: remove WF's name part
		references = references.replace(Notation.getReferencesDelimiterLeft(), "");
		references = references.replace(Notation.getReferencesDelimiterRight(), "");
		// getting/creating the createdWFNode description
		Node descNode = this.createTag(this.createdWFTask, FMNames.DESCRIPTION);
		// merging content with description
		mergeNodesTextContent(descNode, references);
	}
}
