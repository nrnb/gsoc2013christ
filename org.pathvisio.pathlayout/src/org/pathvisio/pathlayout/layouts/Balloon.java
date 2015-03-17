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

import java.awt.geom.Point2D;
import java.util.List;

import org.apache.commons.collections15.Transformer;
import org.pathvisio.core.model.ObjectType;
import org.pathvisio.core.model.PathwayElement;
import org.pathvisio.gui.SwingEngine;

import edu.uci.ics.jung.algorithms.layout.BalloonLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;

// TODO: try to avoid adding all nodes on a circle
/**
 * Balloon class</p>
 * Class for using the balloon layout algorithm in PathVisio.
 * @author Christ Leemans
 *
 */
public class Balloon extends JungAbstract{

	BalloonLayout<String,String> l; 
	Transformer<String,Point2D> in;
	/**
	 * create a new Balloon layout.
	 * @param swingEngine The PathVisio swing engine
	 * @param selection Boolean whether to use currently selected nodes or complete pathway
	 */
	public Balloon(SwingEngine swingEngine, boolean selection){
		super(swingEngine,selection);
		
		
		createDSMultigraph();
		Forest<String,String> forest = new DelegateForest<String,String>();
		List<PathwayElement> elements = pwy.getDataObjects();
		
		
		for (PathwayElement element : elements){
			if (element.getObjectType().equals(ObjectType.LINE)){
				if (!forest.containsVertex(element.getStartGraphRef())){
					forest.addVertex(element.getStartGraphRef());
				}
				if (!forest.containsEdge(element.getEndGraphRef())){
					forest.addVertex(element.getEndGraphRef());
				}
				forest.addEdge(element.getGraphId(), element.getStartGraphRef(),element.getEndGraphRef());
			}
		}
		BalloonLayout.DEFAULT_DISTX=200;
		BalloonLayout.DEFAULT_DISTY=200;
		l = new BalloonLayout<String,String>(forest);
		setDimension((Layout<String,String>) l);
		
		
		for (PathwayElement element : elements){
			if (element.getObjectType().equals(ObjectType.DATANODE)){
				Point2D p = new Point2D.Double(element.getMCenterX(),element.getMCenterY());
				l.setLocation(element.getGraphId(), p);
			}
		}
		l.initialize();
	 	
		for (String v : l.getGraph().getVertices()){
			Point2D center = l.transform(v);
			
			double x = center.getX();
			double y = center.getY();
			PathwayElement e = pwy.getElementById(v);
			x = x + e.getMWidth()/2*5;
			y = y + e.getMHeight()/2*5;
			e.setMCenterX(x);
			e.setMCenterY(y);
		}
		//re-draw the lines
		drawLines();
	}

}

	
