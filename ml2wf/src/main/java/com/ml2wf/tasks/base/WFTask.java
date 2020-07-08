package com.ml2wf.tasks.base;

/**
 * This class represents an abstract workflow {@code Task}</a>.
 *
 * <p>
 *
 * It is an extension of the {@code Task} class.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see Task
 *
 */
public abstract class WFTask extends Task {

	/**
	 * The name of the referred meta-task.
	 */
	private String reference;

	/**
	 * {@code WFTask}'s full constructor.
	 *
	 * <p>
	 *
	 * It initializes a {@code WFTask} specifying its {@code name}, {@code parent}
	 * and {@code reference}.
	 *
	 * @param name      name of the task
	 * @param parent    parent of the task
	 * @param reference reference of the task
	 */
	public WFTask(String name, Task parent, String reference) {
		super(name, parent);
		this.reference = reference;
	}

	/**
	 * Returns the current {@link #reference}.
	 *
	 * @return the current {@code reference}
	 */
	public String getReference() {
		return this.reference;
	}

	/**
	 * Sets the current {@link #reference}.
	 *
	 * @param reference the new task's reference
	 */
	public void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + ((this.reference == null) ? 0 : this.reference.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof WFTask) && super.equals(obj);
	}

}
