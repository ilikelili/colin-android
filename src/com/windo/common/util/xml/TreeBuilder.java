package com.windo.common.util.xml;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Hashtable;



public class TreeBuilder
{
	

	
	

	private Node rootNode;

	

	public TreeBuilder()
	{
		
	}

	public Node getRootNode()
	{
		return rootNode;
	}

	

	

	public Node createTree(byte[] data) throws Exception
	{
		
		
		
		
		
		
		

		rootNode = parseTree(new ByteArrayInputStream(data));

		return rootNode;

	}

	public Node createTree(String str) throws Exception
	{
		
		
		
		
		
		
		

		rootNode = parseTree(new ByteArrayInputStream(str.getBytes("utf-8")));
		return rootNode;

	}

	public Node createTree(InputStream is)
	{
		
		
		
		
		
		
		
		
		rootNode = parseTree(is);
		return rootNode;

	}

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static Node parseTree(String data)
	{

		try
		{
			byte[] bytes = data.getBytes("utf-8");
			return parseTree(new ByteArrayInputStream(bytes));
		} catch (UnsupportedEncodingException e)
		{
			
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		return null;

	}

	public static Node parseTree(byte[] data)
	{
		return parseTree(new ByteArrayInputStream(data));
	}

	public static Node parseTree(InputStream in)
	{
		XMLParser parser;

		try
		{
			InputStreamReader inputStreamReader = new InputStreamReader(in,
					"utf-8");
			parser = new XMLParser(inputStreamReader);
		} catch (IOException exception)
		{
			throw new RuntimeException("Could not create xml parser."
					+ exception);
		}

		Node root = new Node();
		Node currentNode = root;
		String newName;
		int newType;

		try
		{
			while ((parser.next()) != XMLParser.END_DOCUMENT)
			{
				newName = parser.getName();
				newType = parser.getType();

				if (newType == XMLParser.START_TAG)
				{
					Hashtable attributes = null;
					int attributeCount = parser.getAttributeCount();

					if (attributeCount > 0)
					{
						attributes = new Hashtable();

						for (int i = 0; i < attributeCount; i++)
						{
							attributes.put(parser.getAttributeName(i), parser
									.getAttributeValue(i));
						}
					}

					Node newNode = new Node(currentNode, newName, attributes,
							newType);
					currentNode = newNode;
				}

				else if (newType == XMLParser.END_TAG)
				{
					currentNode = currentNode.getParent();
				}

				else if (newType == XMLParser.TEXT)
				{
					String text = parser.getText();
					currentNode.setText(text);
				}
			}
		} catch (Exception exception)
		{
			throw new RuntimeException("parse error:" + exception);
		}
		if (root.getChildCount() == 1)
		{
			return root.getChild(0);
		} else
		{
			return root;
		}
	}

	
	
	
	
	
	
	
	
	
	
	

}
