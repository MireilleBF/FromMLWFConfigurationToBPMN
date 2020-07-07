package com.ml2wf.conventions.enums.wf.bpmn;

/**
 * This {@code enum} contains handled attributes' names according to the
 * <a href="http://www.bpmn.org/">BPMN standard</a>.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 */
public enum BPMNAttributes {

	BACKGROUND("ext:shapeBackground"), ID("id"), NAME("name"),
	// positional attributes
	ELEMENT("bpmnElement"), HEIGHT("height"), WIDTH("width"), X("x"), Y("y");

	/**
	 * Tag name.
	 */
	private String name;

	/**
	 * {@code BPMNNodesAttributes}'s constructor.
	 *
	 * @param name name of the tag
	 */
	private BPMNAttributes(String name) {
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
}
