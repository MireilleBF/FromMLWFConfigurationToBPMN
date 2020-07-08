package com.ml2wf.tasks.concretes;

import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.base.WFTask;

/**
 * This class represents a {@code Task} using the
 * <a href="http://www.bpmn.org/">BPMN standard</a>.
 *
 * <p>
 *
 * It is an extension of the {@code WFTask} class.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see WFTask
 *
 */
public class BPMNTask extends WFTask {

	/**
	 * {@code BPMNTask}'s full constructor.
	 *
	 * <p>
	 *
	 * It initializes a {@code BPMNTask} specifying its {@code name}, {@code parent}
	 * and {@code reference}.
	 *
	 * @param name      name of the task
	 * @param parent    parent of the task
	 * @param reference reference of the task
	 */
	public BPMNTask(String name, Task parent, String reference) {
		super(name, parent, reference);

	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof BPMNTask) && super.equals(obj);
	}

	@Override
	public String toString() {
		return super.toString() + "[\n\tBPMNTask]";
	}
}
