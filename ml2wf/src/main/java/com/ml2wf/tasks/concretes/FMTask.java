package com.ml2wf.tasks.concretes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.w3c.dom.Node;

import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.manager.TasksManager;

/**
 * This class represents {@code Task} using the
 * <a href="https://featureide.github.io/">FeatureIDE
 * framework</a>.
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
public class FMTask extends Task {

	/**
	 * The task's {@code Node} instance.
	 */
	private Node node;
	/**
	 * Abstract status.
	 */
	private boolean isAbstract;

	/**
	 * {@code FMTask}'s full constructor.
	 *
	 * <p>
	 *
	 * It initializes a {@code FMTask} specifying its {@code name}, {@code parent},
	 * {@code node} and {@code isAbstract} status.
	 *
	 * @param name       name of the task
	 * @param parent     parent of the task
	 * @param node       node of the task
	 * @param isAbstract whether the task is abstract or not
	 */
	public FMTask(String name, Task parent, Node node, boolean isAbstract) {
		super(name, parent);
		this.node = node;
		this.isAbstract = isAbstract;
	}

	/**
	 * {@code FMTask}'s partial constructor.
	 *
	 * <p>
	 *
	 * It initializes a {@code FMTask} specifying its {@code name}, {@code node} and
	 * {@code isAbstract} status.
	 *
	 * @param name       name of the task
	 * @param node       node of the task
	 * @param isAbstract whether the task is abstract or not
	 */
	public FMTask(String name, Node node, boolean isAbstract) {
		this(name, null, node, isAbstract);
	}

	/**
	 * Returns whether the current task {@link #isAbstract} or not.
	 *
	 * @return whether the current task is abstract or not
	 */
	public boolean isAbstract() {
		return this.isAbstract;
	}

	/**
	 * Returns the current task's {@link #node}.
	 *
	 * @return the current task's {@code node}
	 */
	public Node getNode() {
		return this.node;
	}

	/**
	 * Sets the current task's {@link #node}.
	 *
	 * @param the new task's {@code node}
	 */
	public void setNode(Node node) {
		this.node = node;
	}

	/**
	 * Appends the given {@code child} to the current task.
	 *
	 * <p>
	 *
	 * More precisely, this method :
	 *
	 * <p>
	 *
	 * <ul>
	 * <li>appends the given {@code child}'s node to the current {@link #node},</li>
	 * <li>sets the given {@code child}'s parent as {@code this}.</li>
	 * </ul>
	 *
	 * <p>
	 *
	 * <b>Note</b> that a {@code FMTask} can only have a unique {@code parent} but
	 * multiple children.
	 *
	 * @param child task to append as child
	 * @return the appended child
	 *
	 * @since 1.0
	 * @see Node
	 */
	public FMTask appendChild(FMTask child) {
		child.setNode(this.node.appendChild(child.getNode()));
		child.setParent(this);
		return child;
	}

	/**
	 * Removes the given {@code oldChild} from the current {@code node}'s children
	 * list.
	 *
	 * <p>
	 *
	 * More precisely, this method :
	 *
	 * <p>
	 *
	 * <ul>
	 * <li>removes the given {@code oldChild}'s node to the current
	 * {@link #node}'s children list,</li>
	 * <li>sets the given {@code oldChild}'s parent as {@code null}.</li>
	 * </ul>
	 *
	 * <p>
	 *
	 * <b>Note</b> that the given {@code oldChild} will be removed from the current
	 * {@code node}'s children list only if its {@code parent} equals {@code this}.
	 *
	 * @param oldChild the child to remove
	 * @return an {@code Optional} containing the removed {@code oldChild} if it was
	 *         a current {@code node}'s child, an empty one else.
	 *
	 * @since 1.0
	 * @see Node
	 */
	public Optional<FMTask> removeChild(FMTask oldChild) {
		Node oldNode = oldChild.getNode();
		Optional<FMTask> optTask = TasksManager.getFMTaskWithNode(oldNode);
		if (optTask.isPresent()) {
			this.node.removeChild(oldNode);
			oldChild.setParent(null);
			return optTask;
		}
		return Optional.empty();
	}

	/**
	 * Returns an {@code Optional} containing the given {@code parent}'s child with
	 * the given {@code name} or an empty one if no suitable child was found.
	 *
	 * <p>
	 *
	 * <b>Note</b> that this method is {@code static}.
	 *
	 * @param parent    parent containing children
	 * @param childName name of child to retrieve
	 * @return an {@code Optional} containing the given {@code parent}'s child with
	 *         the given {@code name} or an empty one if no suitable child was found
	 * 
	 * @since 1.0
	 */
	private static Optional<FMTask> getChildWithName(Task parent, String childName) {
		List<FMTask> childrenTasks = TasksManager.getFMTaskWithParent(parent);
		Optional<FMTask> optChild;
		for (FMTask child : childrenTasks) {
			if (child.getName().equals(childName)) {
				return Optional.of(child);
			} else {
				optChild = getChildWithName(child, childName);
				if (optChild.isPresent()) {
					return optChild;
				}
			}
		}
		return Optional.empty();
	}

	/**
	 * Returns an {@code Optional} containing the child with
	 * the given {@code name} or an empty one if no suitable child was found.
	 *
	 * <p>
	 *
	 * <b>Note</b> that this method uses the {@link #getChildWithName(Task, String)}
	 * method passing {@code this} as parent argument.
	 *
	 * @param childName name of child to retrieve
	 * @return an {@code Optional} containing the child with
	 *         the given {@code name} or an empty one if no suitable child was found
	 *
	 * @since 1.0
	 */
	public Optional<FMTask> getChildWithName(String childName) {
		return getChildWithName(this, childName);
	}

	/**
	 * Returns the Lowest Common Ancestor (LCA) of the given {@code tasks}.
	 *
	 * @param tasks tasks to retrieve the LCA
	 * @return Returns the LCA of the given {@code tasks}
	 *
	 * @since 1.0
	 */
	public static FMTask getLCA(List<FMTask> tasks) {
		FMTask parent;
		List<FMTask> commonParents = new ArrayList<>();
		List<FMTask> nodeParents = new ArrayList<>();
		for (FMTask task : tasks) {
			// for each node
			parent = task;
			while ((parent = (FMTask) parent.getParent()) != null) {
				// get all parents
				nodeParents.add(parent);
			}
			if (commonParents.isEmpty()) {
				commonParents.addAll(nodeParents);
			}
			// retaining common parents
			commonParents.retainAll(nodeParents);
		}
		return commonParents.get(0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = (prime * result) + (this.isAbstract ? 1231 : 1237);
		result = (prime * result) + ((this.node == null) ? 0 : this.node.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof FMTask) && super.equals(obj);
	}

	@Override
	public String toString() {
		return super.toString() + "\n\t[FMTask [node=" + this.node + ", isAbstract="
				+ this.isAbstract + "]]";
	}
}
