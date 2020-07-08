package com.ml2wf.tasks.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ml2wf.conventions.enums.TaskTagsSelector;
import com.ml2wf.conventions.enums.fm.FMAttributes;
import com.ml2wf.conventions.enums.fm.FMNames;
import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.concretes.FMTask;
import com.ml2wf.tasks.manager.TasksManager;
import com.ml2wf.util.XMLManager;

/**
 * This class is a factory for the {@code FMTask} class.
 *
 * <p>
 *
 * It is an implementation of the {@link TaskFactory} interface.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see TaskFactoryImpl
 * @see FMTask
 *
 */
public class FMTaskFactory extends TaskFactoryImpl {

	private static final Map<TaskTagsSelector, Class<? extends Task>> TASK_MAP;

	static {
		Map<TaskTagsSelector, Class<? extends Task>> map = new HashMap<>();
		map.put(FMNames.SELECTOR, FMTask.class);
		TASK_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * {@code TaskFactoryImpl}'s empty constructor.
	 */
	public FMTaskFactory() {
		// empty constructor
	}

	@Override
	protected Task createTask(Node node, Class<? extends Task> taskClass) {
		System.out.println(taskClass.getName() + " -> " + XMLManager.getNodeName(node));
		String nodeName = XMLManager.sanitizeName(XMLManager.getNodeName(node));
		Optional<String> optReference = XMLManager.getReferredTask(XMLManager.getAllBPMNDocContent((Element) node));
		System.out.println("reference : " + optReference.orElse("NO REF"));
		Task parentTask = TasksManager.getFMTaskWithName(optReference.orElse("")).orElse(null);
		try {
			return (Task) taskClass.getDeclaredConstructors()[0].newInstance(
					nodeName, parentTask, node, this.isAbstract(node));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			// TODO: log error
			e.printStackTrace(); // TODO: to replace by logger
			return null;
		}
	}

	@Override
	protected Map<TaskTagsSelector, Class<? extends Task>> getTaskEnumsClassesMap() {
		return TASK_MAP;
	}

	/**
	 * Returns whether the given {@code node} is abstract or not.
	 *
	 * <p>
	 *
	 * This helps setting the {@link FMTask#isAbstract} attribute.
	 *
	 * @param node node containing the abstract information
	 * @return whether the given {@code node} is abstract or not
	 *
	 * @since 1.0
	 * @see FMTask
	 */
	private boolean isAbstract(Node node) {
		// TODO: replace FMAttributes.ABSTRACT by entry.key
		Node abstractAttr = node.getAttributes().getNamedItem(FMAttributes.ABSTRACT.getName());
		return (abstractAttr != null) && (abstractAttr.getNodeValue().equals(String.valueOf(true)));
	}
}
