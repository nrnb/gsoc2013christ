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

import org.pathvisio.gui.SwingEngine;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
/**
 * ISOM class</p>
 * Class for using Meyer's Self-Organizing Map layout algorithm in PathVisio.
 * Paper: <i>doi:10.1007/3-540-37623-2_19</i>
 * @author Christ Leemans
 */
public class ISOM extends JungAbstract{

	ISOMLayout<String,String> l; 
	
	/**
	 * create a new Meyer's Self-Organizing Map Layout.
	 * @param swingEngine The PathVisio swing engine
	 * @param selection Boolean whether to use currently selected nodes or complete pathway
	 */
	public ISOM(SwingEngine swingEngine, boolean selection){
		super(swingEngine,selection);
		
		createDSMultigraph();
		l = new ISOMLayout<String,String>( g );
		setDimension(l);
		l.initialize();
		while(!l.done()){
			l.step();
		}
		
		drawNodes((AbstractLayout<String,String>) l);
		//re-draw the lines
		drawLines();
	}

}
