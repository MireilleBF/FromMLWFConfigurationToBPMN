package com.ml2wf.conventions.enums.fm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ml2wf.conventions.enums.TaskTagsSelector;

/**
 * This {@code enum} contains handled tags' names according to the
 * <a href="https://featureide.github.io/">FeatureIDE
 * framework</a>.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 */
public enum FeatureNames implements TaskTagsSelector {

	// general tags
	FEATUREMODEL("featureModel"), EXTENDEDFEATUREMODEL("extendedFeatureModel"), PROPERTIES("properties"),
	STRUCT("struct"), GRAPHICS("graphics"),
	DESCRIPTION("description"),
	// task tags
	FEATURE("feature"), ALT("alt"), AND("and"),
	// constraint tags
	CONSTRAINTS("constraints"), RULE("rule"), IMPLIES("imp"), NOT("not"), EQUIVALENT("equ"), CONJ("conj"), DISJ("disj"),
	VAR("var"),
	// reserved tags
	SELECTOR("");

	/**
	 * Tag name.
	 */
	private String name;

	/**
	 * {@code FeatureModelNames}'s constructor.
	 *
	 * @param name name of the tag
	 */
	private FeatureNames(String name) {
		this.name = name;
	}

	/**
	 * Returns the current tag's {@code name}.
	 *
	 * @return the current tag's {@code name}
	 */
	public String getName() {
		return this.name;
	}

	@Override
	public List<String> getTaskTags() {
		return new ArrayList<>(Arrays.asList(FEATURE.getName(), AND.getName(), ALT.getName()));
	}

}
