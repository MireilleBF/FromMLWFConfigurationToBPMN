package com.ml2wf.tasks.factory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.reflections.Reflections;
import org.w3c.dom.Node;

import com.ml2wf.conventions.enums.TaskTagsSelector;
import com.ml2wf.merge.AbstractMerger;
import com.ml2wf.tasks.base.Task;

public abstract class TaskFactoryImpl implements TaskFactory {

	private static final String DECLARED_FIELD = "TASK_CLASS";

	protected static final Class<TaskTagsSelector> SELECTOR_CLASS = TaskTagsSelector.class;

	private final Reflections reflections = new Reflections(this.getEnumPackageName());

	protected abstract String getEnumPackageName();

	protected abstract Task createTask(Node node, Class<? extends Task> taskClass);

	@SuppressWarnings("unchecked")
	private Optional<Class<? extends Task>> getTaskClass(Class<? extends TaskTagsSelector> enumerator)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field field = enumerator.getField(DECLARED_FIELD);
		if (field.trySetAccessible()) {
			return Optional.of((Class<? extends Task>) field.get(null));
		}
		return Optional.empty();
	}

	private Set<Class<? extends TaskTagsSelector>> getEnums() {
		return this.reflections.getSubTypesOf(SELECTOR_CLASS);
	}

	@Override
	public Set<Task> createTasks(Node node) {
		Set<Task> createdTasks = new HashSet<>();
		try {
			for (Class<? extends TaskTagsSelector> enumTask : this.getEnums()) {
				// get all task enums
				Method m = enumTask.getDeclaredMethod("isValidTaskTag"); // get the validation method
				if ((boolean) m.invoke(enumTask.getEnumConstants()[0], node.getNodeName())) {
					// if the current enum corresponds to the node specs
					for (Node child : AbstractMerger.getNestedNodes(node)) {
						// foreach node's nested child
						Optional<Class<? extends Task>> opt = this.getTaskClass(enumTask);
						if (opt.isPresent()) {
							// if the corresponding task has been found
							createdTasks.add(this.createTask(child, opt.get()));
						} else {
							// TODO: log error
							return new HashSet<>(); // TODO: to change
						}

					}
				}
			}
		} catch (Exception e) {
			// TODO: log error
			e.printStackTrace();
			return new HashSet<>();
		}
		return createdTasks;
	}
}
