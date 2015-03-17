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
package org.pathvisio.pathlayout;

import org.pathvisio.core.preferences.Preference;
import org.pathvisio.gui.SwingEngine;
import org.pathvisio.pathlayout.layouts.*;
/**
 * LayoutManager class<p>
 * Class containing 2 enumerators. 1 to store all possible layout algorithms
 * and another to store preferences.
 * @author Christ Leemans
 *
 */
public class LayoutManager {
	/**
	 * enumerator containing the layout algorithms that can be chosen. Contains a method to
	 * create new layout objects as well.
	 * 
	 * @author Christ Leemans
	 */
	public static enum Layout {
		BALLOON("Balloon","A Layout implementation that assigns positions to vertices using associations with nested circles.","JUNG2.0"),
		FREIN("Fruchtman-Reingold","The Fruchterman-Rheingold algorithm.","JUNG2.0"),
		ISOM("ISOM","Meyer's \"Self-Organizing Map\" layout.","JUNG2.0"),
		KAMKAW("Kamada-Kawai","The Kamada-Kawai algorithm for node layout","JUNG2.0"),
		SPRING("Spring","A simple force-directed spring-embedder","JUNG2.0"),
		PREFUSE("Force-Directed Layout","Force-Directed layout algorithm","Prefuse");
		
		private final String name;
		private final String desc;
		private final String src;
		private boolean selection;
		
		private Layout(String name, String desc, String src){
			this.name = name;
			this.desc = desc;
			this.src = src;
			selection = false;
		}
		/**
		 * @return the name of the layout algorithm
		 */
		@Override
		public String toString() {
			return name;
		}
		/**
		 * @return a description of the layout algorithm
		 */
		public String getDescription(){
			return desc;
		}
		/**
		 * @return the source of the code used for the algorithm
		 */
		public String getSource(){
			return src;
		}
		/**
		 * select whether the complete pathway is used as input or only the nodes in
		 * the current selection
		 * @param selection true if only the currently selected nodes are to be used
		 */
		public void useSelection(boolean selection){
			this.selection = selection;
		}
		/**
		 * Perform the layout algorithm
		 * @param se the Swing Engine
		 * @return The layout algorithm as an AbstractLayout object.
		 */
		public LayoutAbstract doLayout(SwingEngine se){
			switch (this){
			case BALLOON:
				return new Balloon(se,selection);
			case FREIN:
				return new FruchtRein(se,selection);
			case ISOM:
				return new ISOM(se,selection);
			case KAMKAW:
				return new KamKaw(se,selection);
			case SPRING:
				return new Spring(se,selection);
			case PREFUSE:
				return new Prefuse(se,selection);
			}
			return null;
		}
	
	}
	/**
	 * enumerator with the preferences used in layout algorithms
	 * 
	 * @author Christ Leemans
	 */
	public static enum PlPreference implements Preference
	{
		PL_LAYOUT_FR_ATTRACTION("0.5"),
		PL_LAYOUT_FR_REPULSION("1"),
		PL_LAYOUT_SPRING_FORCE("0.33"),
		PL_LAYOUT_SPRING_REPULSION("100"),
		PL_LAYOUT_SPRING_STRETCH("0.7");
		
	
		private final String defaultVal;
		
		PlPreference (String _defaultVal) { defaultVal = _defaultVal; }
		
		@Override
		public String getDefault() { return defaultVal; }
	}
}
