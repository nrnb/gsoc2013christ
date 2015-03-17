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

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bridgedb.DataSource;
import org.bridgedb.IDMapperException;
import org.pathvisio.core.data.GdbManager;
import org.pathvisio.core.model.DataNodeType;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.Pathway;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.core.view.MIMShapes;
/**
 * Constructor class.<p>
 * Class that takes care of putting the nodes and edges on the screen in a structured manner
 * @author Christ Leemans
 *
 */
public class Constructor {
	Map<String,Node> nodes;
	GdbManager db;
	Pathway pwy;
	Map<Node,PathwayElement> pels;
	Set<PathwayElement> lines;
	Point start;
	boolean newpwy;
	
	private static int HEIGHT = 20;
	private static int WEIGHT = 80;
	private static int PLUSX = 120;
	private static int PLUSY = 50;
	private static int CSIZE = 12;
	
	/**
	 * Create a new constructor
	 * 
	 * @param pwy The current pathway
	 * @param db The GdbManager
	 */
	public Constructor(Pathway pwy, GdbManager db){
		this.db = db;
		this.pwy = pwy;
		start = new Point(50,50);
	}
	/**
	 * Create a new constructor
	 * 
	 * @param pwy The current pathway
	 * @param db The GdbManager
	 * @param newpwy boolean representing if it's an empty pathway.
	 * If not, new nodes should be added on the right side of the already existing ones.
	 */
	public Constructor(Pathway pwy, GdbManager db,boolean newpwy){
		this(pwy,db);
		this.newpwy = newpwy;
	}
	/**
	 * Plot connections on a pathway, if there are lots of connections, they are 
	 * plotted in separate columns
	 * @param connections List of connections
	 */
	public void plotConnections(List<Connection> connections){
		nodes = new HashMap<String,Node>();
		pels = new HashMap<Node,PathwayElement>();
		lines = new HashSet<PathwayElement>();
		float j = 1;
		int index = 0;
		float x = (float)connections.size()/(float)CSIZE;
		int cols = (int)Math.ceil(x);
		
		if (newpwy){
			j = j - .5f * cols;
		
			if (j<-2.9f){
				j = -2.9f;
			}
		}
		
		//first complete columns of 12
		for (int k = 1;k<cols;k++){
			List<Connection> col = new ArrayList<Connection>();
			for (int l = 0; l<CSIZE;l++){
				col.add(connections.get(index));
				index++;
			}
			plotConColumn(col,j);
			j = j+2;
		}
		//then the last column, this one might be incomplete
		List<Connection> lastCol = new ArrayList<Connection>();
		int m = cols-1;
		for (int n = m*CSIZE ;n<connections.size();n++){
			lastCol.add(connections.get(index));
			index++;
		}
		plotConColumn(lastCol,j);
		
		for (Entry<Node,PathwayElement> entry : pels.entrySet()){
			pwy.add(entry.getValue());
		}
		for (PathwayElement line : lines){
			pwy.add(line);
		}
	}
	/**
	 * plot a single column of connections
	 * @param connections List of connections
	 * @param j number used to calculate the location of the nodes on the x-axis
	 */
	public void plotConColumn(List<Connection> connections,double j){
		int i = 0;
		double startj = j-0.5;
		double endj = j+0.5;
		for (Connection connection : connections){
			Node start = connection.getStart();
			if (!nodes.containsValue(start)){
				String sp = i + ":" + startj;
				nodes.put(sp, start);
				try {
					PathwayElement pel = createNode(start);
					drawNode(i, startj, pel);
					pels.put(start,pel);
				} catch (IDMapperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Node end = connection.getEnd();
			if (!nodes.containsValue(end)){
				String sp = i + ":" + endj;
				nodes.put(sp, end);
				try {
					PathwayElement pel = createNode(end);
					drawNode(i, endj, pel);
					pels.put(end,pel);
				} catch (IDMapperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			PathwayElement line = drawLine(pels.get(start),pels.get(end),connection.getLineType());
			
			lines.add(line);
			i++;
		}
	}
	/**
	 * Plot nodes on a pathway, if there are lots of nodes, they are 
	 * plotted in separate columns
	 * @param nodes List of nodes
	 */
	public void plotNodes(List<Node> nodes){
		float j = 0;
		int index = 0;
		float x = (float)nodes.size()/(float)CSIZE;
		
		int cols = (int)Math.ceil(x);
		if (newpwy){
			j = j - cols/2;
			if (j<-3){
				j = -3;
			}
		}
		
		//first complete columns of 12
		for (int k = 1;k<cols;k++){
			List<Node> col = new ArrayList<Node>();
			for (int l = 0; l<CSIZE;l++){
				col.add(nodes.get(index));
				index++;
			}
			plotNodeColumn(col,j);
			j++;
		}
		//then the last column
		List<Node> lastCol = new ArrayList<Node>();
		int m = cols-1;
		for (int n = m*CSIZE ;n<nodes.size();n++){
			lastCol.add(nodes.get(index));
			index++;
		}
		plotNodeColumn(lastCol,j);
		
	}
	/**
	 * plot a single column of nodes
	 * @param nodes List of nodes
	 * @param j number used to calculate the location of the nodes on the x-axis
	 */
	public void plotNodeColumn(List<Node> nodes,float j){
		int i = 0;
		for (Node node: nodes){
			try {
				PathwayElement pel = createNode(node);
				pel.setTextLabel(node.getName());
				drawNode(i,j,pel);
				pwy.add(pel);
				i++;
			} catch (IDMapperException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void drawNode(int i, double j, PathwayElement pel){
		int x = (int)(start.x + PLUSX * j);
		
		int y = start.y + PLUSY * i;
		pel.setMCenterX(x);
		pel.setMCenterY(y);
		pel.setMHeight(HEIGHT);
		pel.setMWidth(WEIGHT);
	}
	
	private PathwayElement drawLine(PathwayElement startnode, PathwayElement endnode, LineType ltype){
		PathwayElement line = PathwayElement.createPathwayElement(ObjectType.LINE);
		line.setGraphId(pwy.getUniqueGraphId());
		
		if (startnode.getMCenterX() < endnode.getMCenterX()) {
			line.setMStartY(startnode.getMCenterY());
			line.setMStartX(startnode.getMCenterX() + startnode.getMWidth() / 2);
			line.setMEndY(endnode.getMCenterY());
			line.setMEndX(endnode.getMCenterX() - endnode.getMWidth() / 2);
		} else if (startnode.getMCenterX() > endnode.getMCenterX()) {
			line.setMStartY(startnode.getMCenterY());
			line.setMStartX(startnode.getMCenterX() - startnode.getMWidth() / 2);
			line.setMEndY(endnode.getMCenterY());
			line.setMEndX(endnode.getMCenterX() + endnode.getMWidth() / 2);
		} else if (startnode.getMCenterY() > endnode.getMCenterY()){
			line.setMStartX(startnode.getMCenterX());
			line.setMStartY((startnode.getMCenterY() - startnode.getMHeight() /2));
			line.setMEndX(startnode.getMCenterX());
			line.setMEndY(endnode.getMCenterY() + startnode.getMHeight() /2);
		} else {
			line.setMStartX(startnode.getMCenterX());
			line.setMStartY(startnode.getMCenterY() + startnode.getMHeight() / 2);
			line.setMEndX(endnode.getMCenterX());
			line.setMEndY(endnode.getMCenterY() - endnode.getMHeight() / 2);
		}
		line.getMStart().linkTo(startnode);
		line.getMEnd().linkTo(endnode);
		
		MIMShapes.registerShapes();
		if (isBothWays(ltype)){
			line.setStartLineType(ltype);
		}
		else{
			line.setStartLineType(LineType.LINE);
		}
		
		line.setEndLineType(ltype);
		return line;
	}
	
	/**
	 * set value to start the calculation of locations on the x-axis. In case of a new
	 * pathway it should be the centre of the pathway window. But if nodes/connections
	 * are added to an existing pathway its the starting point of new node creation.
	 * @param x integer value to start the calculation
	 */
	public void setStartX(int x){
		start.setLocation(x,start.y);
	}
	
	private PathwayElement createNode(Node node) throws IDMapperException{
		PathwayElement pel;
		pel = PathwayElement.createPathwayElement(ObjectType.DATANODE);
		pel.setGraphId(pwy.getUniqueGroupId());
		pel.setTextLabel(node.getName());
		if (!node.getId().equals("NULL")){
			pel.setElementID(node.getId());
		}
		if (!node.getSysCode().equals("NULL")){
			DataSource source = DataSource.getBySystemCode(node.getSysCode());
			if (source.isMetabolite()){
				pel.setColor(Color.BLUE);
				pel.setDataNodeType(DataNodeType.METABOLITE);
			}
			else{
				pel.setDataNodeType(DataNodeType.GENEPRODUCT);
			}
			pel.setDataSource(source);
		}
		
		return pel;
	}
	/**
	 * check if the line-type should be displayed on both the start and end side of
	 * the connecting line. If not, the line-type is displayed on the end side of the line.
	 * @param lt a line-type
	 * @return <i>True</i> if both ways or <i>False</i> if not
	 */
	public static boolean isBothWays(LineType lt){
		if (lt.equals(MIMShapes.MIM_BINDING)||
			lt.equals(MIMShapes.MIM_COVALENT_BOND)||
			lt.equals(MIMShapes.MIM_GAP)){
			return true;
		}
		else return false;
	}
}
