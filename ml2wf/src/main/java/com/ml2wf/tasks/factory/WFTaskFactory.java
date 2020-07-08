package com.ml2wf.tasks.factory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.ml2wf.conventions.enums.TaskTagsSelector;
import com.ml2wf.conventions.enums.wf.bpmn.BPMNNames;
import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.base.WFTask;
import com.ml2wf.tasks.concretes.BPMNTask;
import com.ml2wf.tasks.manager.TasksManager;
import com.ml2wf.util.XMLManager;

/**
 * This class is a factory for the {@code WFTask} class.
 *
 * <p>
 *
 * It is an extension of the {@link TaskFactoryImpl} base class.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see TaskFactoryImpl
 * @see WFTask
 *
 */
public class WFTaskFactory extends TaskFactoryImpl {

	private static final Map<TaskTagsSelector, Class<? extends Task>> TASK_MAP;

	static {
		Map<TaskTagsSelector, Class<? extends Task>> map = new HashMap<>();
		map.put(BPMNNames.SELECTOR, BPMNTask.class);
		TASK_MAP = Collections.unmodifiableMap(map);
	}

	/**
	 * {@code TaskFactoryImpl}'s empty constructor.
	 */
	public WFTaskFactory() {
		// empty constructor
	}

	@Override
	protected Task createTask(Node node, Class<? extends Task> taskClass) {
		System.out.println(taskClass.getName() + " -> " + XMLManager.getNodeName(node));
		String nodeName = XMLManager.sanitizeName(XMLManager.getNodeName(node));
		Optional<String> optRef = XMLManager.getReferredTask(XMLManager.getAllBPMNDocContent((Element) node));
		String parentName = XMLManager.sanitizeName(XMLManager.getNodeName(node.getParentNode()));
		Task parentTask = TasksManager.getWFTaskWithName(parentName).orElse(null);
		try {
			return (Task) taskClass.getDeclaredConstructors()[0].newInstance(nodeName, parentTask, optRef.orElse(""));
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {
			// TODO: log error
			System.out.println(e.getMessage()); // TODO: to replace by logger
			return null;
		}
	}

	@Override
	protected Map<TaskTagsSelector, Class<? extends Task>> getTaskEnumsClassesMap() {
		return TASK_MAP;
	}

}
