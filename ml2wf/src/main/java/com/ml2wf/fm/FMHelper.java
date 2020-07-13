package com.ml2wf.fm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


import org.xml.sax.SAXException;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FMHelper {


	private static final Logger logger = LogManager.getLogger(FMHelper.class);


	private static final String FEATURE = "feature";
	private static final String AND = "and";

	private DocumentBuilder builder;
	private Document document;
	private Element racine;

	private List<String> featureNameList = new ArrayList<>();

	public List<String> getFeatureNameList() {
		return new ArrayList<>(featureNameList);
	}

	public boolean isFeature(String name) {
		return featureNameList.contains(name);
	}

	public FMHelper(String path) throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); // Compliant
		factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); // compliant
		builder = factory.newDocumentBuilder();
		document = builder.parse(new File(path));
		racine = document.getDocumentElement();
		featureNameList = listFeatures();
	}


	//Extract Feature as a Node
	public Node extractFeature(String featureName) {
		return 	extractFeature(racine, featureName);
	}

	private Node extractFeature(Node parent, String featureName) {
		if (parent == null)
			return null;
		if (getFeatureName(parent).equals(featureName)) 
			return parent;
		for (Node child : iterable(parent.getChildNodes())) {
			Node node = extractFeature(child, featureName);
			if (node != null)
				return node;
		}
		return null;
	}



	/*
	 * List Features
	 */
	private List<String> listFeatures() {
		List<String> featureList = new ArrayList<>();
		for (Node child : iterable(racine.getChildNodes())) {
			featureList.addAll(listFeature(child));
		}
		return featureList;
	}

	private List<String> listFeature(Node n) {
		List<String> list = new ArrayList<>();
		switch (n.getNodeName()) {
		case FEATURE:
			list.add(getFeatureName(n));
			break;
		case AND:
			list.add(getFeatureName(n));
			for (Node child : iterable(n.getChildNodes())) {
				list.addAll(listFeature(child));
			}
			break;
		default:
			for (Node child : iterable(n.getChildNodes())) {
				list.addAll(listFeature(child));
			}
			break;
		}
		return list;
	}
	// todo improve it
	private static Iterable<Node> iterable(final NodeList n) {
		return new Iterable<Node>() {
			@Override
			public Iterator<Node> iterator() {

				return new Iterator<Node>() {

					int index = 0;

					@Override
					public boolean hasNext() {
						return index < n.getLength();
					}

					@Override
					public Node next() {
						if (hasNext()) {
							return n.item(index++);
						} else {
							throw new NoSuchElementException();
						}
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}




	private String getFeatureName(Node node) { 
		if ( (node != null) 
				&& (node.getAttributes() != null)
				&& (node.getAttributes().getNamedItem("name") != null) )
		{
			return node.getAttributes().getNamedItem("name").getNodeValue(); 
		}
		else return "";
	}




	public boolean isDirectChildOf(String parent, String childName) {
		Node nodeParent = extractFeature(parent);
		NodeList nodes = nodeParent.getChildNodes();
		for (Node child : iterable(nodes)) { 
			if (childName.contentEquals(getFeatureName(child)) ) 
				return true; }

		return false;
	}


	public boolean isChildOf(String parent, String childName) {
		Node nodeParent = extractFeature(parent);
		return(extractFeature(nodeParent, childName) != null);
	}



	public Document getDocument() {
		return document;
	}


}
