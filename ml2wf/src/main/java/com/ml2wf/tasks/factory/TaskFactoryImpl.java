package com.ml2wf.tasks.factory;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.w3c.dom.Node;

import com.ml2wf.conventions.enums.TaskTagsSelector;
import com.ml2wf.merge.AbstractMerger;
import com.ml2wf.tasks.base.Task;

public abstract class TaskFactoryImpl implements TaskFactory {

	protected abstract Task createTask(Node node, Class<? extends Task> taskClass);

	protected abstract Map<TaskTagsSelector, Class<? extends Task>> getTaskEnumsClassesMap();

	@Override
	public Set<Task> createTasks(Node node) {
		Set<Task> createdTasks = new HashSet<>();
		for (Entry<TaskTagsSelector, Class<? extends Task>> entry : this.getTaskEnumsClassesMap()
				.entrySet()) {
			if (entry.getKey().isValidTaskTag(node.getNodeName())) {
				for (Node child : AbstractMerger.getNestedNodes(node)) {
					createdTasks.add(this.createTask(child, entry.getValue()));
				}
			}
		}
		return createdTasks;
	}
}
