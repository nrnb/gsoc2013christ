/*******************************************************************************
 * pathlayout,
 * 
 * a library for PathVisio plug-ins with layout algorithms
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
 ******************************************************************************/
package org.pathvisio.pathlayout.layouts;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.gui.SwingEngine;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;

/**
 * JungAbstract Class<p>
 * Class used by implementations of layout algorithms of the JUNG2.0 package.
 * @author Christ Leemans
 *
 */
public abstract class JungAbstract extends LayoutAbstract{
	
	Graph<String,String> g;
	
	JungAbstract(SwingEngine se,boolean selection) {
		super(se,selection);
	}
	
	protected void createDSMultigraph(){
		g = new DirectedSparseMultigraph<String, String>();
		for (PathwayElement pe:pwyNodes){
			g.addVertex(pe.getGraphId());
		}
		for (PathwayElement pe:pwyLines){
			if (pe.getMStart().isLinked()&&pe.getMEnd().isLinked()){
				g.addEdge(pe.getGraphId(),pe.getStartGraphRef(), pe.getEndGraphRef());
			}
		}
	}
	
	protected void drawNodes(AbstractLayout<String,String> l){
		Map<String,Point2D> points = new HashMap<String,Point2D>();
		for (String v : l.getGraph().getVertices()){
			points.put(v,l.transform(v));
		}
		setLocations(points);
	}
	
	
	
	protected void setDimension(Layout<String,String> l){
		int i = 0;
		for (PathwayElement node: pwy.getDataObjects()){
			if (node.getObjectType().equals(ObjectType.DATANODE)){
				i++;
			}
		}
		Dimension d = new Dimension(i*20+40,i*15+30);
		l.setSize(d);
	}
}
