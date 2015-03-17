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
package org.pathvisio.paver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.bridgedb.DataSource;
import org.bridgedb.DataSourcePatterns;
import org.pathvisio.core.model.LineType;
import org.pathvisio.paver.construct.Connection;
import org.pathvisio.paver.construct.Node;

/**
* TxtReader Class.<p>
* Text reader class for node or connection input by file. 
* The manual input function also uses the static methods of this class. 
* @author christ leemans
*
*/
public class TxtReader
{
	private File file;
	private List<LineType> lineTypes;
	private static String NAME_SEP = ":";
	private static String ITEM_SEP = "\t";
	
	/**
	 * create a new text reader to read file input
	 * @param file
	 */
	public TxtReader(File file)
	{
		this.file = file;
		lineTypes = new ArrayList<LineType>();
	}
	
	/**
	 * add possible linetypes to check
	 * @param lineTypes The possible linetypes
	 */
	public void addLineTypes(List<LineType> lineTypes){
		this.lineTypes = lineTypes;
	}
	
	/**
	 * get the connections from the text file
	 * @return List of connections
	 */
	public List<Connection> getConnections() throws IllegalArgumentException{
		List<Connection> cons = new ArrayList<Connection>();
		String line; 
		try 
		{
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
					
			// continue reading lines until EOF is reached
			while((line = br.readLine()) != null)
			{
			   	Connection con = TxtReader.readConnection(line, lineTypes);
			   	cons.add(con);
			   	   	
			}   
			fr.close();
		}
		catch(IllegalArgumentException e){
			throw e;
		}
		catch(Exception e) 
		{
			System.out.println("Exception: " + e);
		}		
		return cons;
	}
	/**
	 * get the nodes from the text file
	 * @return List of nodes
	 */
	public List<Node> getNodes() throws IllegalArgumentException{
		List<Node> nodes = new ArrayList<Node>();
		String line; 
							
		try 
		{
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
							
			// continue reading lines until EOF is reached
			while((line = br.readLine()) != null)
			{
				Node node = readSingleNode(line);
				
				nodes.add(node);
					   	   	
			}   
			fr.close();
		}
		catch(IllegalArgumentException e){
			throw e;
		}
		catch(Exception e) 
		{
			System.out.println("Exception: " + e);
		}
		
		return nodes;
	}
	/**
	 * Static method to read a string object representing a connection between 2 data nodes
	 * @param line the line to process
	 * @param lineTypes the possible line types
	 * @return the connection
	 */
	public static Connection readConnection(String line, List<LineType> lineTypes) throws IllegalArgumentException{
		// the array to return
		String[] currentRow;

		currentRow=line.split(ITEM_SEP);
		if (currentRow.length!=3){
			throw new IllegalArgumentException();
		}
		String[] start = currentRow[0].split(NAME_SEP);
		String startname = start[0] + ":" + start[1];
		Node startRef = new Node(startname,start[1], start[0]);
	   	String ltstring = currentRow[1];
	   	LineType ltype = LineType.ARROW;
	   	for (LineType lt: lineTypes){
	   		if (lt.getName().equals(ltstring)){
	   			ltype = lt;
	   		}
	   	}
	   	String[] stop = currentRow[2].split(NAME_SEP);
	   	String stopname = stop[0] + ":" + stop[1];
	   	Node stopRef = new Node(stopname, stop[1], stop[0]);
	   	Connection con = new Connection(startRef, stopRef, ltype);
		
		return con;
	}
	
	/**
	 * static method to read a string object representing a data node
	 * @param line the line to process
	 * @return the node
	 */
	public static Node readSingleNode(String line) throws IllegalArgumentException{
		String[] att = line.split("\t");
		if (att.length==3){
			//it could be a system code
			String sysCode = att[2];
			DataSource s = DataSource.getByFullName(att[2]);
			try {
				//but first try if it's a full name
				//NullPointer if it's indeed empty
				s.getSystemCode().isEmpty();
				sysCode = s.getSystemCode();
			}
			catch (NullPointerException e){
			}
			return new Node(att[0],att[1],sysCode);
		}
		else if (att.length==2){
			String name = att[0];
			String sysCode;
			String id = att[1];
			if (!DataSourcePatterns.getDataSourceMatches(att[1]).isEmpty()){
				int l = DataSourcePatterns.getDataSourceMatches(id).toArray().length;
				DataSource[] sources = new DataSource[l];
				DataSourcePatterns.getDataSourceMatches(id).toArray(sources);
				sysCode = sources[0].getSystemCode();
				
			}
			else {
				sysCode = "NULL";
			}
			return new Node(name,id,sysCode);
		}
		else if (att.length==1){
			String name = att[0];
			String id;
			String sysCode;
			//if the name is a identifier, use this identifier
			if (!DataSourcePatterns.getDataSourceMatches(name).isEmpty()){
				id = name;
				int l = DataSourcePatterns.getDataSourceMatches(id).toArray().length;
				DataSource[] sources = new DataSource[l];
				DataSourcePatterns.getDataSourceMatches(id).toArray(sources);
				sysCode=sources[0].getSystemCode();
			}
			//else put in "NULL"
			else {
				id = "NULL";
				sysCode = "NULL";
			}
			return new Node(name,id,sysCode);
		}
		else {
			throw new IllegalArgumentException();
		}
	}
}

