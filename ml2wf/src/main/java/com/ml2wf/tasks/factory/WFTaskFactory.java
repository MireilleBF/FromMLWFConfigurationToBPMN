package com.ml2wf.tasks.factory;

import org.w3c.dom.Node;

import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.base.WFTask;

/**
 * This class is a factory for the {@code WFTask} class.
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
 * @see WFTask
 *
 */
public class WFTaskFactory extends TaskFactoryImpl {

	private static final String ENUM_PACKAGE_NAME = "com.ml2wf.enums.wf";

	/**
	 * {@code TaskFactoryImpl}'s empty constructor.
	 */
	public WFTaskFactory() {
		// empty constructor
	}

	@Override
	protected String getEnumPackageName() {
		return ENUM_PACKAGE_NAME;
	}

	@Override
	protected Task createTask(Node node, Class<? extends Task> taskClass) {
		return null;
	}

}
