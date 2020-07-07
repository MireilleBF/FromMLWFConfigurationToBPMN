package com.ml2wf.tasks.base;

import org.w3c.dom.Node;

/**
 * This class represents a {@code Task}.
 *
 * <p>
 *
 * More precisely, it only contains uselful {@code Node}'s data and is more easy
 * to use than a classic {@code Node}.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see Node
 *
 */
public abstract class Task {

	/**
	 * Task's name.
	 */
	private String name;
	/**
	 * Task's parent.
	 */
	private Task parent;

	/**
	 * {@code Task}'s full constructor.
	 *
	 * @param name   the task's name
	 * @param parent the task's parent
	 *
	 * @see Node
	 */
	public Task(String name, Task parent) {
		this.name = name;
		this.parent = parent;
	}

	/**
	 * {@code Task}'s default constructor.
	 *
	 * @param name   the task's name
	 * @param parent the task's parent
	 *
	 * @see Node
	 */
	public Task(String name) {
		this.name = name;
		this.parent = null;
	}

	/**
	 * Returns the current task's {@code name}.
	 *
	 * @return the current task's {@code name}
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Returns the current task's {@code parent}.
	 *
	 * @return the current task's {@code parent}
	 */
	public Task getParent() {
		return this.parent;
	}

	/**
	 * Sets the current task's {@code parent}.
	 *
	 * @param parent the new task's {@code parent}
	 */
	public void setParent(Task parent) {
		this.parent = parent;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((this.name == null) ? 0 : this.name.hashCode());
		result = (prime * result) + ((this.parent == null) ? 0 : this.parent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		Task other = (Task) obj;
		if (this.name == null) {
			return this.name == other.name;
		} else {
			return this.name.equals(other.name);
		}
	}

	@Override
	public String toString() {
		return "\nTask [name=" + this.name + ", parent=" + this.parent + "]";
	}
}
