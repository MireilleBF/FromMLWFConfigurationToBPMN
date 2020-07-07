package com.ml2wf.merge.base;

import java.io.File;

import org.w3c.dom.Document;

import com.ml2wf.tasks.concretes.BPMNTask;
import com.ml2wf.tasks.concretes.FMTask;
import com.ml2wf.util.Pair;

/**
 * This interface provides a method for the <b>merging</b> of an instantiated
 * workflow into a FeatureModel.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see <a href="https://en.wikipedia.org/wiki/Feature_model">FeatureModel</a>
 *
 */
public interface BaseMerger {

	/**
	 * Merges the workflow {@code File} location into the FM document.
	 *
	 * <p>
	 *
	 * Saves the current FeatureModel before any modification if {@code backup} is
	 * {@code true}.
	 *
	 * @param wfFile        the workflow {@code File}.
	 * @param backUp        indicates if a backup is needed
	 * @param completeMerge indicates whether the workflow has to be completely
	 *                      merged or not
	 * @throws Exception
	 *
	 * @since 1.0
	 */
	public void mergeWithWF(boolean backUp, boolean completeMerge, File wfFile) throws Exception;

	/**
	 * Merges the workflow files at {@code filesPath} location into the WF document.
	 *
	 * <p>
	 *
	 * Saves the current FeatureModel before any modification if {@code backup} is
	 * {@code true}.
	 *
	 * @param wfFiles       {@code File} instances of the workflow files.
	 * @param backUp        indicates if a backup is needed
	 * @param completeMerge indicates whether the workflow has to be completely
	 *                      merged or not
	 * @throws Exception
	 *
	 * @since 1.0
	 */
	public void mergeWithWF(boolean backUp, boolean completeMerge, File... wfFiles) throws Exception;

	/**
	 * Returns a suitable parent {@code FMTask} for the given {@code Task}.
	 *
	 * @return a suitable parent {@code FMTask} for the given {@code Task}
	 *
	 * @since 1.0
	 *
	 * @see FMTask
	 * @see BPMNTask
	 */
	public abstract FMTask getSuitableParent(BPMNTask child);

	/**
	 * Returns the root parent {@code FMTask} according to the workflow's type (meta
	 * or instance).
	 *
	 * <p>
	 *
	 * This root parent {@code FMTask} is used during the workflow's task insertion
	 * (complete merge).
	 *
	 * @return the root parent {@code FMTask} according to the workflow's type (meta
	 *         or instance)
	 *
	 * @since 1.0
	 *
	 * @see FMTask
	 */
	public abstract FMTask getRootParentNode();

	/**
	 * Processes specific needs to complete the merge operation.
	 *
	 * <p>
	 *
	 * e.g. add references
	 *
	 * @param wfInfo {@code Pair} containing the {@code Document} and its name
	 * @throws Exception
	 *
	 * @since 1.0
	 *
	 * @see Pair
	 * @see Document
	 */
	public void processSpecificNeeds(Pair<String, Document> wfInfo) throws Exception;
}
