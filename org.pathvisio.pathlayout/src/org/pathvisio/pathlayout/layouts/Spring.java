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

import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.pathlayout.LayoutManager.PlPreference;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
/**
 * Spring class</p>
 * Class for using a simple force-directed spring-embedder in PathVisio.
 * @author Christ Leemans
 *
 */

public class Spring extends JungAbstract{

	SpringLayout<String,String> l;
	
	/**
	 * create a new Spring Layout.
	 * @param swingEngine The PathVisio swing engine
	 * @param selection Boolean whether to use currently selected nodes or complete pathway
	 */
	
	public Spring(SwingEngine swingEngine, boolean selection){
		super(swingEngine,selection);
		createDSMultigraph();
		l = new SpringLayout<String,String>( g );
		setDimension(l);
		double fm = Double.parseDouble(PreferenceManager.getCurrent().get(PlPreference.PL_LAYOUT_SPRING_FORCE));
		int rep = Integer.parseInt(PreferenceManager.getCurrent().get(PlPreference.PL_LAYOUT_SPRING_REPULSION));
		double s = Double.parseDouble(PreferenceManager.getCurrent().get(PlPreference.PL_LAYOUT_SPRING_STRETCH));
		l.setForceMultiplier(fm);
		l.setRepulsionRange(rep);
		l.setStretch(s);
		
		
		l.initialize();
		for(int i=0;i<1000;i++){
			l.step();
		}
		
		drawNodes((AbstractLayout<String,String>) l);
		//re-draw the lines
		drawLines();
	}
	
	
}
