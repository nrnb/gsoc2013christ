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

/**
 * Node Class.<p>
 * Class for new nodes read by the plug-in, from which to create new PathwayElement objects
 * 
 * @author Christ Leemans
 *
 */

public class Node implements Comparable<Node>{
	private final String sysCode;
	private final String id;
	private final String name;
	private final String rep;
	/**
	 * create a new data node
	 * @param name the name to be used as label
	 * @param id the identifier used to identify the node in a data source
	 * @param sysCode the system code of the data source in which the 
	 * biological element represented by the node can be found
	 */
	public Node(String name, String id, String sysCode){
		this.name = name;
		this.id = id;
		this.sysCode = sysCode;
		rep = name + id + sysCode;
	}
	/**
	 * @return the data identifier(e.g. Ensemble identifier)
	 */
	public String getId(){
		return id;
	}
	/**
	 * @return the system code of the data source.
	 */
	public String getSysCode(){
		return sysCode;
	}
	/**
	 * @return name of the data node, used to create the label
	 */
	public String getName(){
		return name;
	}
	@Override
	public int hashCode() 
	{
		return rep.hashCode();
	}
	@Override
	public boolean equals(Object o){
		if (o instanceof Node){
			Node n = (Node) o;
			return rep.equals(n.rep);
		}
		else{
		return false;
		}
	}
	@Override
	public int compareTo(Node n) {
		return rep.compareTo(n.rep);
	}
}
