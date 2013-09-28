package com.windo.common.util.xml;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;



public class Node {
	
	public Vector children;

	
	private Node parent;

	
	private String tagName;

	
	private StringBuffer text;

	
	public Hashtable attributes;

	public int type;
	
	
	public Node() {
		type = -1;
	}
	
	

	public Node(Node _parent, String _name, Hashtable _attributes) {
		tagName = _name;
		attributes = _attributes;

		parent = _parent;
		if (parent != null) {
			parent.addChild(this);
		}
	}
	
	public Node(Node _parent, String _name, Hashtable _attributes, int _type) {
		tagName = _name;
		attributes = _attributes;

		parent = _parent;
		if (parent != null) {
			parent.addChild(this);
		}
		type = _type;
	}
	
	
	public void setName(String name) {
		tagName = name;
	}

	
	public void setParent(Node node) {
		parent = node;
		if (parent != null) {
			parent.addChild(this);
		}
	}

	
	public void addText(String newText) {
		if (text == null)
			text = new StringBuffer();

		text.append(newText);
	}

	
	public void setText(String newText) {
		text = new StringBuffer(newText);
	}

	
	public String getText() {
		String textString = "";
		if (text != null) {
			textString = text.toString();
		}
		return textString;
	}

	
	public String getName() {
		return tagName;
	}

	
	public void addChild(Node childNode) {
		if (children == null){
			children = new Vector();
		}
		children.addElement(childNode);
	}

	
	public boolean removeChild(Node childNode) {
        if (children == null)
            return false;
		boolean removed = children.removeElement(childNode);
		if (removed)
			return true;



		synchronized (children) {
			Enumeration childIterator = children.elements();
			while (childIterator.hasMoreElements()) {
				Node thisChild = (Node) childIterator.nextElement();
				removed = thisChild.removeChild(childNode);
				if (removed)
					return true;
			}
		}
		return false;
	}

	
	public Vector getChildrenByName(String name) {
		Vector namedChildren = new Vector();
		getChildrenByName(name, namedChildren);
		return namedChildren;
	}

	
	public Node getFirstChildrenByName(String name) {
		if (children == null)
			return null;

		try {
			synchronized (children) {
				Enumeration childIterator = children.elements();
				while (childIterator.hasMoreElements()) {
					Node thisChild = (Node) childIterator.nextElement();

					String nodeName = thisChild.getName();
					if (nodeName.equalsIgnoreCase(name)) {
						return thisChild;
					}
					
				}
			}
		} catch (Exception e) {
		
		}
		return null;
	}

	
	protected void getChildrenByName(String name, Vector store) {
		if (children == null)
			return;

		try {
			synchronized (children) {
				Enumeration childIterator = children.elements();
				while (childIterator.hasMoreElements()) {
					Node thisChild = (Node) childIterator.nextElement();

					String nodeName = thisChild.getName();
					if (nodeName.equalsIgnoreCase(name)) {
						store.addElement(thisChild);
					}
					
				}
			}
		} catch (Exception e) {
		}
	}
	

	

	public Node getParent() {
		return parent;
	}

	

	public byte[] getBytes() {
		String data = toString();
		return data.getBytes();
	}

	
	public String toString() {
		String nodeName = getName();

		StringBuffer xmlRepresentation = new StringBuffer("<");
		xmlRepresentation.append(nodeName);

		if (attributes != null) {
			synchronized (attributes) {
				Enumeration attrIter = attributes.keys();
				while (attrIter.hasMoreElements()) {
					String key = (String) attrIter.nextElement();
					String value = (String) attributes.get(key);
					xmlRepresentation.append(' ');
					xmlRepresentation.append(key);
					xmlRepresentation.append("=\'");
					xmlRepresentation.append(value);
					xmlRepresentation.append("\'");
				}
			}
		}

		if ((children == null || children.size() == 0)
				&& (text == null || text.length() == 0)) {
			xmlRepresentation.append("/>");
			return xmlRepresentation.toString();
		}

		xmlRepresentation.append(">");
		if (text != null) {
			xmlRepresentation.append(text.toString());
		}

		if (children != null) {
			synchronized (children) {
				Enumeration iter = children.elements();
				while (iter.hasMoreElements()) {
					Object nextObject = iter.nextElement();
					String nodeRepresentation = nextObject.toString();
					xmlRepresentation.append(nodeRepresentation);
				}
			}
		}

		xmlRepresentation.append("</");
		xmlRepresentation.append(nodeName);
		xmlRepresentation.append(">");

		return xmlRepresentation.toString();
	}

	
	public String getAttribute(String attributeName) {
		if (attributes == null || attributeName == null)
			return null;

		return (String) attributes.get(attributeName);
	}

	
	public long getAttributeLong(String attributeName){
		if (attributes == null || attributeName == null)
			return -1L;

		String str =  (String)attributes.get(attributeName);
		return Long.parseLong(str);
	}
	
	
	public void setAttribute(String attributeName, String value) {
		if (attributeName == null || value == null)
			return;

		if (attributes == null)
			attributes = new Hashtable();

		attributes.put(attributeName, value);
	}

	
	public void removeAttribute(String attributeName) {
		if (attributes == null)
			return;

		attributes.remove(attributeName);
	}

	
	public void replaceNode(Node replacementNode) {
		String replacementNodeName = replacementNode.getName();

		String nameSpace = null;
		if (replacementNode.attributes != null)
			;
		nameSpace = (String) replacementNode.attributes.get("xmlns");

		if (children == null) {
			addChild(replacementNode);
			return;
		}

		synchronized (children) {
			Enumeration childIter = children.elements();
			while (childIter.hasMoreElements()) {
				Node thisNode = (Node) childIter.nextElement();

				String thisNodeName = thisNode.getName();
				if (thisNodeName.equals(replacementNodeName) == false)
					continue;

				if (nameSpace == null) {
					children.removeElement(thisNode);
					addChild(replacementNode);
					return;
				}

				if (thisNode.attributes == null)
					continue;

				String thisNodeNamespace = (String) thisNode.attributes
						.get("xmlns");
				if (nameSpace.equals(thisNodeNamespace)) {
					children.removeElement(thisNode);
					addChild(replacementNode);
					return;
				}
			}
		}

		addChild(replacementNode);
	}

	

	public Vector getChildren() {
		if (children == null)
			return null;

		return children;
	}
	
	public int getChildCount() {
		if (children == null)
			return 0;

		return children.size();
	}
	
	public Node getChild(int location) {
		if (children == null || location < 0 
				|| location >= children.size())
			return null;

		return (Node)children.elementAt(location);
	}
	
	
}
