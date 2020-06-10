package com.ml2wf.conventions.enums.bpmn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ml2wf.conventions.enums.TaskTagsSelector;

/**
 * This {@code enum} contains handled tags' names according to the
 * <a href="https://www.bpmn.org/">BPMN standard</a>.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 */
public enum BPMNNodesNames implements TaskTagsSelector {

	// general tags
	PROCESS("bpmn2:process"), INCOMING("bpmn2:incoming"), OUTGOING("bpmn2:outgoing"),
	EXTENSION("bpmn2:extensionElements"), STYLE("ext:style"), DOCUMENTATION("bpmn2:documentation"),
	ANNOTATION("bpmn2:textAnnotation"), TEXT("bpmn2:text"),
	// task tags
	TASK("bpmn2:task"), USERTASK("bpmn2:userTask"),
	// reserved tags
	SELECTOR("");

	/**
	 * Tag name.
	 */
	private String name;

	/**
	 * {@code BPMNNodesNames}'s constructor.
	 *
	 * @param name name of the tag
	 */
	private BPMNNodesNames(String name) {
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
		return new ArrayList<>(Arrays.asList(USERTASK.getName(), TASK.getName()));
	}
}
