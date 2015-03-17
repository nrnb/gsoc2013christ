/*******************************************************************************
 * Paver,
 * 
 * a PathVisio plug-in to automate pathway creation
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 ******************************************************************************/
package org.pathvisio.paver.construct;

import org.pathvisio.core.model.LineType;
/**
 * Connection Class.<p>
 * A connection between two nodes
 * @author Christ Leemans
 *
 */
public class Connection {
	private Node start;
	private Node end;
	private LineType lineType;
	
	/**
	 * Create a new connection	
	 * @param start the node at the start of the connecting line
	 * @param end the node at the end of the connecting line
	 * @param lineType the type of line connecting the 2 nodes
	 */
	public Connection(Node start, Node end, LineType lineType){
		this.start = start;
		this.end = end;
		this.lineType = lineType;
	}
	/**
	 * Get the node at the start of the connection
	 * @return the start node
	 */
	public Node getStart(){
		return start;
	}
	/**
	 * Get the node at the end of the connection
	 * @return the end node
	 */
	public Node getEnd(){
		return end;
	}
	/**
	 * Get the type of line connecting the two nodes
	 * @return the line-type
	 */
	public LineType getLineType(){
		return lineType;
	}
	/**
	 * Get the two nodes in the connection
	 * @return Array with two nodes, [0] is start,[1] is end
	 */
	public Node[] getNodes(){
		Node[] nodes = {start,end};
		return nodes;
	}
	

}
