package com.ml2wf.tasks.manager;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Node;

import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.concretes.BPMNTask;
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
 * @see BPMNTask
 *
 */
public final class TasksManager {

	// TODO: use FMTask or Task for generic type ?

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
	 * {@code Set} containing all {@code BPMNTask}.
	 */
	private static Set<BPMNTask> bpmnTasks = new HashSet<>();

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
	 * @return all stored {@code Task}
	 *
	 * @see Task
	 */
	public static Set<Task> getTasks() {
		return Stream.concat(fmTasks.stream(), bpmnTasks.stream())
				.collect(Collectors.toSet());
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
	public static List<FMTask> getFMTaskWithParent(Task parent) {
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
		return fmTasks.stream().filter(t -> t.getNode().equals(node)).findFirst();
	}

	/**
	 * Returns all stored {@code BPMNTask}.
	 *
	 * @return all stored {@code BPMNTask}
	 *
	 * @see BPMNTask
	 *
	 * @since 1.0
	 */
	public static Set<BPMNTask> getBPMNTasks() {
		return bpmnTasks;
	}

	/**
	 * Returns an {@code Optional} containing the {@code BPMNTask} with the given
	 * {@code name}.
	 *
	 * <p>
	 *
	 * If there is no {@code BPMNTask} with the given {@code name}, then the
	 * returned {@code Optional} is empty.
	 *
	 * @param name name of wanted task
	 * @return an {@code Optional} containing the {@code BPMNTask} with the given
	 *         {@code name}
	 *
	 * @since 1.0
	 * @see BPMNTask
	 */
	public static Optional<BPMNTask> getBPMNTaskWithName(String name) {
		return bpmnTasks.stream().filter(t -> t.getName().equals(name)).findFirst();
	}

	// adder

	/**
	 * Adds the given {@code task} to the right {@code Set} according to its nature
	 * and returns it.
	 *
	 * @param task task to add
	 *
	 * @return the added {@code task}
	 *
	 * @since 1.0
	 * @see Task
	 */
	public static Task addTask(Task task) {
		if (task != null) {
			if (task instanceof FMTask) {
				fmTasks.add((FMTask) task);
			} else {
				// removing if a task with the same name and a blank reference is already
				// contained to keep most precise tasks
				bpmnTasks.removeIf(
						t -> (t.getName() != null) && t.getName().equals(task.getName()) && t.getReference().isBlank());
				bpmnTasks.add((BPMNTask) task);
			}
		}
		return task;
	}

	// remover

	/**
	 * Removes the given {@code task} from the right {@code Set} according to its
	 * nature.
	 *
	 * @param task task to remove
	 * @return whether the remove operation succeed or not
	 *
	 * @since 1.0
	 * @see Task
	 */
	public static boolean removeTask(Task task) {
		if (task == null) {
			return false;
		}
		if (task instanceof FMTask) {
			return fmTasks.remove(task);
		} else {
			return bpmnTasks.remove(task);
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
	 * {@link #clearBPMNTasks()} methods.
	 *
	 * @since 1.0
	 */
	public static void clear() {
		clearFMTasks();
		clearBPMNTasks();
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
	 * Clears the {@link #bpmnTasks}.
	 *
	 * @since 1.0
	 */
	public static void clearBPMNTasks() {
		bpmnTasks.clear();
	}

}
