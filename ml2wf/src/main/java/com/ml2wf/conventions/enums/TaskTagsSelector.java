package com.ml2wf.conventions.enums;

import java.util.List;

/**
 * The {@code TaskTagsSelector} interface provides a method for selecting xml
 * task tags.
 *
 * <p>
 *
 * {@code Node} task tags can change considering the type of xml standard
 * (<b>FeatureModel</b> or <b>BPMN</b> for instance).
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see <a href="https://featureide.github.io/">FeatureIDE framework</a>
 * @see <a href="https://www.bpmn.org/">BPMN</a>
 *
 */
public interface TaskTagsSelector {

	/**
	 * Returns a {@code List<String>} containing all task tags considering the type
	 * of xml file.
	 *
	 * @return a {@code List<String>} containing all task tags considering the type
	 *         of xml file
	 *
	 * @since 1.0
	 */
	public List<String> getTaskTags();

	/**
	 * Returns whether the given tag is a valid task tag's name or not according to
	 * the implementation context.
	 *
	 * @param tag tag to check
	 * @return whether the given tag is a FeatureModel task tag's name or not
	 *
	 * @since 1.0
	 */
	public default boolean isValidTaskTag(String tag) {
		return this.getTaskTags().contains(tag);
	}
}
