package com.ml2wf.constraints.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ml2wf.constraints.InvalidConstraintException;
import com.ml2wf.constraints.config.ConfigImpl;
import com.ml2wf.constraints.config.DefaultConfig;
import com.ml2wf.constraints.parser.ConstraintParser;
import com.ml2wf.constraints.parser.Parser;
import com.ml2wf.constraints.tree.BinaryTree;
import com.ml2wf.conventions.Notation;
import com.ml2wf.conventions.enums.fm.FMNames;
import com.ml2wf.tasks.base.Task;
import com.ml2wf.tasks.concretes.FMTask;
import com.ml2wf.tasks.manager.TasksManager;
import com.ml2wf.util.Pair;

/**
 * This class is a factory for {@code Node} from constraints.
 *
 * <p>
 *
 * It is able to generate a {@code List<Node>} from a text containing
 * constraints according to the given {@code Config} instance using the
 * {@code ConstraintParser} class.
 *
 * <p>
 *
 * It is an implementation of the {@link ConstraintFactory} interface.
 *
 * @author Nicolas Lacroix
 *
 * @version 1.0
 *
 * @see ConstraintFactory
 * @see Node
 * @see ConfigImpl
 * @see ConstraintParser
 *
 */
public class ConstraintFactoryImpl implements ConstraintFactory {

	/**
	 * {@code Config}'s instance that will be used for constraints parsing.
	 *
	 * @see ConfigImpl
	 */
	private ConfigImpl config;
	/**
	 * {@code Parser}'s instance that will parse given constraints.
	 *
	 * @see Parser
	 */
	private Parser parser;
	/**
	 * {@code Document} instance that will be used for {@code Node} creation.
	 *
	 * @see Document
	 */
	private Document document;
	/**
	 * Logger instance.
	 *
	 * @since 1.0
	 * @see Logger
	 */
	private static final Logger logger = LogManager.getLogger(ConstraintFactoryImpl.class);

	/**
	 * {@code ConstraintFactoryImpl}'s complete constructor.
	 *
	 * <p>
	 *
	 * It initializes :
	 *
	 * <p>
	 *
	 * <ul>
	 * <li>the {@code Config} instance,</li>
	 * <li>{@code ConstraintParser} instance that will be used for parsing
	 * constraints</li>
	 * </ul>
	 *
	 * @param document document used to create nodes
	 * @throws ParserConfigurationException
	 *
	 * @see Document
	 * @see ConfigImpl
	 * @see ConstraintParser
	 */
	public ConstraintFactoryImpl(Document document) throws ParserConfigurationException {
		// Document instantiation
		this.document = document;
		// Parser instantiation
		this.config = ConfigImpl.getInstance();
		this.parser = new ConstraintParser(this.config);
	}

	/**
	 * {@code ConstraintFactoryImpl}'s default constructor.
	 *
	 * <p>
	 *
	 * It initializes :
	 *
	 * <p>
	 *
	 * <ul>
	 * <li>the {@code Document} instance that will be used for {@code Node}
	 * creation,</li>
	 * <li>the {@code Config} instance,</li>
	 * <li>{@code ConstraintParser} instance that will be used for parsing
	 * constraints</li>
	 * </ul>
	 *
	 * @throws ParserConfigurationException
	 *
	 * @see Document
	 * @see ConfigImpl
	 * @see ConstraintParser
	 */
	public ConstraintFactoryImpl() throws ParserConfigurationException {
		// Document instantiation
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		this.document = docBuilder.newDocument();
		// Parser instantiation
		this.config = ConfigImpl.getInstance();
		this.parser = new ConstraintParser(this.config);
	}

	/**
	 * Generates a {@code List} of rule {@code Node} containing all constraints
	 * nodes from the given {@code constraintText}.
	 *
	 * <p>
	 *
	 * Pseudo-code :
	 *
	 * <p>
	 *
	 * <pre>
	 * <code>
	 * function getRuleNodes(constraintText: String)
	 *   rules <- empty list
	 *   trees <- parser.parseContent(constraintText)
	 *   foreach tree in trees do
	 *     rule <- document.createElement(RULE)
	 *     generateNode(tree, rule)
	 *     rules.Add(rule)
	 *   end
	 *   return rules
	 * end
	 * </code>
	 * </pre>
	 *
	 * <p>
	 *
	 * <b>Note</b> that your {@code Document} instance must adopt returned nodes
	 * before adding them.
	 *
	 * @param constraintText text containing constraints
	 * @return a generated {@code Node} containing all constraints nodes
	 * @throws InvalidConstraintException
	 *
	 * @since 1.0
	 * @see Node
	 */
	@Override
	public List<Node> getRuleNodes(String constraintText) throws InvalidConstraintException {
		List<Node> rules = new ArrayList<>();
		List<BinaryTree<String>> trees = this.parser.parseContent(constraintText);
		for (BinaryTree<String> tree : trees) {
			// TODO: check performances
			Set<String> taskNames = TasksManager.getTasks().stream().map(Task::getName).collect(Collectors.toSet());
			List<String> treeNodes = tree.getAllNodes().stream()
					.filter(n -> (n != null) && !this.config.isAnOperator(n))
					.collect(Collectors.toList());
			if (!taskNames.containsAll(treeNodes)) {
				logger.warn("Can't add the constraint : {}{}{} ",
						Notation.getConstraintDelimiterLeft(), tree, Notation.getConstraintDelimiterRight());
				logger.warn("Make sure that all constrained tasks are in the FeatureModel");
				continue;
			}
			Node rule = this.document.createElement(FMNames.RULE.getName());
			this.generateRuleNode(tree, rule);
			rules.add(rule);
		}
		return rules;
	}

	@Override
	public List<Pair<FMTask, Node>> getOrderNodes(String constraintText) {
		// TODO : to test
		List<Pair<FMTask, Node>> pairs = new ArrayList<>();
		Node description;
		if (this.parser.isOrderConstraint(constraintText)) {
			List<BinaryTree<String>> trees = this.parser.parseContent(constraintText);
			for (BinaryTree<String> tree : trees) {
				description = this.document.createElement(FMNames.DESCRIPTION.getName());
				// get involved nodes
				List<String> taskNames = tree.getAllNodes().stream().filter(n -> !this.config.isAnOperator(n))
						.collect(Collectors.toList());
				List<FMTask> tasks = taskNames.stream().map(TasksManager::getFMTaskWithName).map(o -> o.orElse(null))
						.collect(Collectors.toList());
				// get and add LCA
				FMTask lca = (tasks.contains(null)) ? null : FMTask.getLCA(tasks);
				// set description node
				description.setTextContent(tree.toString());
				// add new pair to list
				pairs.add(new Pair<>(lca, description));
			}
		}
		return pairs;
	}

	/**
	 * Returns an <b>implication</b> association of the {@code globalTask} with the
	 * {@code tasksNames}.
	 *
	 * <p>
	 *
	 * <b>Note</b> that this method returns this association using the
	 * {@code DefaultConfig}'s symbols.
	 *
	 * @param globalTask global task
	 * @param tasksNames tasks implied by the {@code global task}
	 * @return an implication association of the {@code globalTask} with the
	 *         {@code tasksNames}
	 */
	public String getAssociationConstraint(String globalTask, List<String> tasksNames) {
		// TODO: add in interface
		return Notation.getConstraintDelimiterLeft() + globalTask + DefaultConfig.IMP.getSymbol()
				+ String.join(DefaultConfig.CONJ.getSymbol(), tasksNames) + Notation.getConstraintDelimiterRight();

	}

	/**
	 * Generates a {@code Node} according to {@code tree}'s root value and call
	 * recursively for each {@code tree}'s child.
	 *
	 * <p>
	 *
	 * Pseudo-code :
	 *
	 * <p>
	 *
	 * <pre>
	 * <code>
	 * function generateNode(tree: BinaryTree, base: Node)
	 *   rootValue <- tree.root
	 *   if rootValue == null then
	 *     return
	 *   end
	 *   base <- base.appendChild(createNode(rootValue))
	 *   foreach child in (tree.leftChild, tree.rightChild) do
	 *     if child != null then
	 *       generateNode(child, base)
	 *     end
	 *   end
	 * end
	 * </code>
	 * </pre>
	 *
	 * <p>
	 *
	 * @param tree tree to convert to {@code Node}
	 * @param base base {@code Node} which will contains newly generated
	 *             {@code Node}
	 * @throws InvalidConstraintException
	 *
	 * @since 1.0
	 * @see BinaryTree
	 * @see Node
	 */
	private void generateRuleNode(BinaryTree<String> tree, Node base) throws InvalidConstraintException {
		String rootValue = tree.getRoot();
		if (rootValue == null) {
			// stop condition
			return;
		}
		base = base.appendChild(this.createNode(rootValue));
		for (BinaryTree<String> child : Arrays.asList(tree.getLeftChild(), tree.getRightChild())) {
			if (child != null) {
				this.generateRuleNode(child, base);
			}
		}
	}

	/**
	 * Creates a {@code Node} from a {@code String} according to {@code config} and
	 * {@code FeatureModelNames} data.
	 *
	 * @param element element to convert to {@code Node}
	 * @return a {@code Node} corresponding to the given {@code element}
	 *
	 * @since 1.0
	 * @see Node
	 * @see ConfigImpl
	 * @see FMNames
	 */
	public Node createNode(String element) {
		Node node;
		if (this.config.isAnOperator(element)) {
			node = this.document.createElement(this.config.getVocmapping().get(element));
		} else {
			node = this.document.createElement(FMNames.VAR.getName());
			node.appendChild(this.document.createTextNode(element));
		}
		return node;
	}
}
