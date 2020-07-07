package com.ml2wf.conventions.enums.wf.bpmn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ml2wf.conventions.enums.TaskTagsSelector;
import com.ml2wf.tasks.base.WFTask;
import com.ml2wf.tasks.concretes.BPMNTask;

/**
 * This {@code enum} contains handled tags' names according to the
 * <a href="http://www.bpmn.org/">BPMN standard</a>.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 */
public enum BPMNNames implements TaskTagsSelector {

	// general tags
	PROCESS("bpmn2:process"), INCOMING("bpmn2:incoming"), OUTGOING("bpmn2:outgoing"),
	EXTENSION("bpmn2:extensionElements"), STYLE("ext:style"), DOCUMENTATION("bpmn2:documentation"),
	ANNOTATION("bpmn2:textAnnotation"), TEXT("bpmn2:text"), DIAGRAM("bpmndi:BPMNDiagram"), PLANE("bpmndi:BPMNPlane"),
	// task tags
	TASK("bpmn2:task"), USERTASK("bpmn2:userTask"), SERVICETASK("bpmn2:serviceTask"),
	// positional tags
	SHAPE("bpmndi:BPMNShape"), BOUNDS("dc:Bounds"), LABEL("bpmndi:BPMNLabel"),
	// reserved tags
	SELECTOR("");

	/**
	 * Tag name.
	 */
	private String name;

	private static final Class<? extends WFTask> TASK_CLASS = BPMNTask.class;

	/**
	 * {@code BPMNNodesNames}'s constructor.
	 *
	 * @param name name of the tag
	 */
	private BPMNNames(String name) {
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
		return new ArrayList<>(Arrays.asList(USERTASK.getName(), TASK.getName(), SERVICETASK.getName()));
	}

	public static Class<? extends WFTask> getTaskClass() {
		return TASK_CLASS;
	}
}
