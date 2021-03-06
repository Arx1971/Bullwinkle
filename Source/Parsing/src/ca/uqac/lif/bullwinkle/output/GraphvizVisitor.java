/*
  Copyright 2014-2016 Sylvain Hallé
  Laboratoire d'informatique formelle
  Université du Québec à Chicoutimi, Canada

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package ca.uqac.lif.bullwinkle.output;

import java.util.ArrayDeque;
import java.util.Deque;

import ca.uqac.lif.bullwinkle.CaptureBlockParseNode;
import ca.uqac.lif.bullwinkle.ParseNode;

/**
 * Visitor that builds a DOT file from a parse tree. The file can be used
 * by <a href="http://graphviz.org/">Graphviz</a> to display a parse tree
 * graphically.
 * 
 * @author Sylvain Hallé
 */
public class GraphvizVisitor implements OutputFormatVisitor
{
	/**
	 * A stack keeping the parent node IDs
	 */
	private Deque<Integer> m_parents;

	/**
	 * A counter keeping the last integer used for node IDs
	 */
	private int m_nodeCount = 0;

	/**
	 * A string builder where the file contents are progressively created
	 */
	private StringBuilder m_output;

	/**
	 * Creates a new GraphvizVisitor with default settings 
	 */
	public GraphvizVisitor()
	{
		super();
		m_parents = new ArrayDeque<Integer>();
		m_output = new StringBuilder();
	}

	@Override
	public void visit(final ParseNode node)
	{
		int cur_node = m_nodeCount++;
		if (!m_parents.isEmpty())
		{
			int parent = m_parents.peek();
			m_output.append(parent).append(" -> ").append(cur_node).append(";\n");
		}
		String shape = "oval";
		if (node instanceof CaptureBlockParseNode)
		{
			// Special treatment for regex capture blocks
			shape = "rectangle";
		}
		String color = "white";
		String fillcolor = "blue";
		String label = escape(node.getValue());
		if (label == null)
		{
			label = escape(node.getToken());
			shape = "rect";
			fillcolor = "white";
			color = "black";
		}
		m_output.append("  ").append(cur_node).append(" [fontcolor=\"").append(color).append("\",style=\"filled\",fillcolor=\"").append(fillcolor).append("\",shape=\"").append(shape).append("\",label=\"").append(label).append("\"];\n");
		m_parents.push(cur_node);
	}

	@Override
	public void pop()
	{
		m_parents.pop();
	}

	@Override
	public String toOutputString()
	{
		StringBuilder out = new StringBuilder();
		out.append("# File auto-generated by Bullwinkle\n\n");
		out.append("digraph G {\n").append(m_output).append("}");
		return out.toString();
	}

	/**
	 * Escapes a few characters in a string to make it compatible with
	 * DOT files
	 * @param input The input string
	 * @return The escaped string
	 */
	private static String escape(/*@Null*/ String input)
	{
		if (input == null)
		{
			return null;
		}
		return input.replaceAll("\\\"", "&quot;");
	}

}
