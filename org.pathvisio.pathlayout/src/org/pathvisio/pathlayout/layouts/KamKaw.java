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
import org.apache.commons.collections15.Transformer;
import org.pathvisio.gui.SwingEngine;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
/**
 * Balloon class</p>
 * Class for using the Kamada-Kawai layout algorithm in PathVisio.
 * Paper: <i>doi:10.1016/0020-0190(89)90102-6</i>
 * @author Christ Leemans
 *
 */
public class KamKaw extends JungAbstract{

	KKLayout<String,String> l; 
	Transformer<String,Point2D> in;
	
	/**
	 * create a new Kamada-Kawai Layout
	 * @param swingEngine The PathVisio swing engine
	 * @param selection Boolean whether to use currently selected nodes or complete pathway
	 */
	public KamKaw(SwingEngine swingEngine, boolean selection){
		super(swingEngine,selection);
		
		createDSMultigraph();
		l = new KKLayout<String,String>( g );
		setDimension(l);

		l.setAdjustForGravity(false);
		l.setExchangeVertices(false);
		l.setLengthFactor(0.6);
		l.setDisconnectedDistanceMultiplier(1);
		
		
		l.initialize();
		while(!l.done()){
			l.step();
		}
		
		drawNodes((AbstractLayout<String,String>) l);
		//re-draw the lines
		drawLines();
	}

}
