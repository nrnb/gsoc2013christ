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
import org.pathvisio.core.preferences.PreferenceManager;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.pathlayout.LayoutManager.PlPreference;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
/**
 * FruchtRein class</p>
 * Class for using the Fruchterman-Reingold layout algorithm in PathVisio.
 * Paper: <i>doi:10.1002/spe.4380211102</i>
 * @author Christ Leemans
 *
 */
public class FruchtRein extends JungAbstract{

	FRLayout<String,String> l; 
	Transformer<String,Point2D> in;
	
	/**
	 * create a new Fruchterman-Reingold layout.
	 * @param swingEngine The PathVisio swing engine
	 * @param selection Boolean whether to use currently selected nodes or complete pathway
	 */
	public FruchtRein(SwingEngine swingEngine, boolean selection){
		super(swingEngine,selection);
		
		createDSMultigraph();
		l = new FRLayout<String,String>( g );
		setDimension(l);
		
		double att = Double.parseDouble(PreferenceManager.getCurrent().get(PlPreference.PL_LAYOUT_FR_ATTRACTION));
		double rep = Double.parseDouble(PreferenceManager.getCurrent().get(PlPreference.PL_LAYOUT_FR_REPULSION));
		l.setAttractionMultiplier(att);
		l.setRepulsionMultiplier(rep);
		
		l.initialize();
		for(int i=0;i<1000;i++){
			l.step();
		}
		
		drawNodes((AbstractLayout<String,String>) l);
		//re-draw the lines
		drawLines();
	}
}
