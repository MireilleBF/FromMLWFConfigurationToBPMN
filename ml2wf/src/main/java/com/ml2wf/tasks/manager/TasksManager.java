package com.ml2wf.tasks.manager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Node;

import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.base.WFTask;
import com.ml2wf.tasks.concretes.FMTask;
import com.ml2wf.util.XMLManager;

/**
 * This class manages all created tasks.
 *
 * <p>
 *
 * <b>Note</b> that it uses {@code Set} to store created tasks and thus to avoid
 * tasks duplication.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see Task
 * @see FMTask
 * @see WFTask
 *
 */
public final class TasksManager {

	/**
	 * {@code Set} containing all {@code FMTask}.
	 *
	 * <p>
	 *
	 * <b>Note</b> that this is a {@code LinkedHashSet} that allows to keep the
	 * insertion order which is needed for workflows (which are sequantial).
	 */
	private static Set<FMTask> fmTasks = new LinkedHashSet<>();
	/**
	 * {@code Set} containing all {@code WFTask}.
	 */
	private static Set<WFTask> wfTasks = new HashSet<>();

	/**
	 * {@code TasksManager}'s default constructor.
	 *
	 * <p>
	 *
	 * <b>Note</b> that this constructor is private due to the static nature of this
	 * class's methods.
	 */
	private TasksManager() {

	}

	// getters

	/**
	 * Returns all stored {@code Task}.
	 *
	 * @param <T> Any {@code class} extending the {@code Task class}
	 *
	 * @return all stored {@code Task}
	 *
	 * @see Task
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Task<?>> Set<T> getTasks() {
		return (Set<T>) Stream.concat(fmTasks.stream(), wfTasks.stream()).collect(Collectors.toSet());
	}

	/**
	 * Returns all stored {@code FMTask}.
	 *
	 * @return all stored {@code FMTask}
	 *
	 * @see FMTask
	 */
	public static Set<FMTask> getFMTasks() {
		return fmTasks;
	}

	/**
	 * Returns an {@code Optional} containing the {@code FMTask} with the given
	 * {@code name}.
	 *
	 * <p>
	 *
	 * If there is no {@code FMTask} with the given {@code name}, then the
	 * returned {@code Optional} is empty.
	 *
	 * @param name name of wanted task
	 * @return an {@code Optional} containing the {@code FMTask} with the given
	 *         {@code name}
	 *
	 * @since 1.0
	 * @see FMTask
	 */
	public static Optional<FMTask> getFMTaskWithName(String name) {
		if (name == null) {
			return Optional.empty();
		}
		return fmTasks.stream().filter(t -> t.getName().equals(name)).findFirst();
	}

	/**
	 * Returns an {@code Optional} containing the {@code FMTask} with the given
	 * {@code parent}.
	 *
	 * <p>
	 *
	 * If there is no {@code FMTask} with the given {@code parent}, then the
	 * returned {@code Optional} is empty.
	 *
	 * @param parent parent of wanted task
	 * @return an {@code Optional} containing the {@code FMTask} with the given
	 *         {@code parent}
	 *
	 * @since 1.0
	 * @see FMTask
	 */
	public static List<FMTask> getFMTaskWithParent(Task<?> parent) {
		if (parent == null) {
			return new ArrayList<>();
		}
		return fmTasks.stream().filter(t -> (t.getParent() != null) && t.getParent().equals(parent))
				.collect(Collectors.toList());
	}

	/**
	 * Returns an {@code Optional} containing the {@code FMTask} with the given
	 * {@code node}.
	 *
	 * <p>
	 *
	 * If there is no {@code FMTask} with the given {@code node}, then the returned
	 * {@code Optional} is empty.
	 *
	 * @param node node of wanted task
	 * @return an {@code Optional} containing the {@code FMTask} with the given
	 *         {@code node}
	 *
	 * @since 1.0
	 * @see FMTask
	 */
	public static Optional<FMTask> getFMTaskWithNode(Node node) {
		if (node == null) {
			return Optional.empty();
		}
		return fmTasks.stream().filter(t -> t.getNode().equals(node)).findFirst();
	}

	/**
	 * Returns all stored {@code WFTask}.
	 *
	 * @return all stored {@code WFTask}
	 *
	 * @see WFTask
	 *
	 * @since 1.0
	 */
	public static Set<WFTask> getWFTasks() {
		return wfTasks;
	}

	/**
	 * Returns an {@code Optional} containing the {@code WFTask} with the given
	 * {@code name}.
	 *
	 * <p>
	 *
	 * If there is no {@code WFTask} with the given {@code name}, then the
	 * returned {@code Optional} is empty.
	 *
	 * @param name name of wanted task
	 * @return an {@code Optional} containing the {@code WFTask} with the given
	 *         {@code name}
	 *
	 * @since 1.0
	 * @see WFTask
	 */
	public static Optional<WFTask> getWFTaskWithName(String name) {
		if (name == null) {
			return Optional.empty();
		}
		return wfTasks.stream().filter(t -> t.getName().equals(name)).findFirst();
	}

	/**
	 * Returns an {@code Optional} containing the {@code WFTask} with the given
	 * {@code node}.
	 *
	 * <p>
	 *
	 * If there is no {@code WFTask} with the given {@code node}, then the returned
	 * {@code Optional} is empty.
	 *
	 * @param node node of wanted task
	 * @return an {@code Optional} containing the {@code WFTask} with the given
	 *         {@code node}
	 *
	 * @since 1.0
	 * @see WFTask
	 */
	public static Optional<WFTask> getWFTaskWithNode(Node node) {
		if (node == null) {
			return Optional.empty();
		}
		return wfTasks.stream().filter(t -> t.getNode().equals(node)).findFirst();
	}

	// adder

	/**
	 * Adds the given {@code task} to the right {@code Set} according to its nature
	 * and returns it.
	 *
	 * @param <T>  Any {@code class} extending the {@code Task class}
	 * @param task task to add
	 *
	 * @return the added {@code task}
	 *
	 * @since 1.0
	 * @see Task
	 */
	public static <T extends Task<?>> T addTask(T task) {
		if (task != null) {
			if (task instanceof FMTask) {
				fmTasks.add((FMTask) task);
			} else {
				// removing if a task with the same name and a blank reference is already
				// contained to keep most precise tasks
				wfTasks.removeIf(
						t -> (t.getName() != null) && t.getName().equals(task.getName()) && t.getReference().isBlank());
				wfTasks.add((WFTask) task);
			}
		}
		return task;
	}

	// remover

	/**
	 * Removes the given {@code task} from the right {@code Set} according to its
	 * nature.
	 *
	 * @param <T>  Any {@code class} extending the {@code Task class}
	 * @param task task to remove
	 * @return whether the remove operation succeed or not
	 *
	 * @since 1.0
	 * @see Task
	 */
	public static <T extends Task<?>> boolean removeTask(T task) {
		if (task == null) {
			return false;
		}
		if (task instanceof FMTask) {
			return fmTasks.remove(task);
		} else {
			return wfTasks.remove(task);
		}
	}

	//

	/**
	 * Returns whether a task with the given {@code name} exists or not.
	 *
	 * @param name name of the task to verify its existence
	 * @return whether a task with the given {@code name} exists or not
	 *
	 * @since 1.0
	 * @see FMTask
	 */
	public static boolean existsinFM(String name) {
		return fmTasks.stream().map(Task::getName).anyMatch(n -> n.equals(name));
	}

	/**
	 * Updates the given {@code tasks}' parents.
	 *
	 * <p>
	 *
	 * This can be useful after a batch treatment where the order treatment is
	 * random and thus where parents can be treated after children.
	 *
	 * @param tasks tasks to update
	 *
	 * @since 1.0
	 * @see FMTask
	 */
	public static void updateFMParents(Set<FMTask> tasks) {
		tasks.stream().filter(t -> t.getParent() == null).forEach(t -> {
			Optional<FMTask> opt = TasksManager.getFMTaskWithName(XMLManager.getNodeName(t.getNode().getParentNode()));
			if (opt.isPresent()) {
				t.setParent(opt.get());
			}
		});
	}

	// clearers

	/**
	 * Clears all {@code Set}s by calling the {@link #clearFMTasks()} and
	 * {@link #clearWFTasks()} methods.
	 *
	 * @since 1.0
	 */
	public static void clear() {
		clearFMTasks();
		clearWFTasks();
	}

	/**
	 * Clears the {@link #fmTasks}.
	 *
	 * @since 1.0
	 */
	public static void clearFMTasks() {
		fmTasks.clear();
	}

	/**
	 * Clears the {@link #wfTasks}.
	 *
	 * @since 1.0
	 */
	public static void clearWFTasks() {
		wfTasks.clear();
	}

}
