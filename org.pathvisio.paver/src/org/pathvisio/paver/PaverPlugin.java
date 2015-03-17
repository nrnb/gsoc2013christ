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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JMenu;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.pathvisio.core.model.LineType;
import org.pathvisio.core.preferences.Preference;
import org.pathvisio.desktop.PvDesktop;
import org.pathvisio.desktop.plugin.Plugin;
import org.pathvisio.pathlayout.LayoutManager.Layout;

/**
 * PaverPlugin class.<p>
 * Main Plug-in Class with Activator.
 * 
 * @author Christ Leemans
 *
 */
public class PaverPlugin implements Plugin, BundleActivator {

	private static String PLUGIN_NAME = "Paver";
	private Layout layout;
	
	protected List<LineType> lineTypes;
	private JFrame frame;
	private PvDesktop desktop;
	private JMenu subMenu;
	private static BundleContext context;

	/**
	 * Enumerator with the actions that are available in the plug-in's subMenu.
	 *
	 * @author Christ Leemans
	 */
	public static enum Action
	{
		BUILD("Build new Pathway"),
		ADD("Add to Pathway"),
		LAYOUT("Layout All"),
		LAYSEL("Layout Selection");
		
		private Action(final String text) {
	        this.text = text;
	    }

	    private final String text;

	    @Override
	    public String toString() {
	        return text;
	    }
	}
	/**
	 * initiate the plug-in, adds new menu items and registers the plug-in.
	 */
	@Override
	public void init(PvDesktop desktop) {
		this.desktop = desktop;
		
		subMenu = new JMenu();
		subMenu.setText(PLUGIN_NAME);
		for (Action a: Action.values()){
			subMenu.add(new selectAction(a));
		}
		
		layout = Layout.PREFUSE;
	
		desktop.registerSubMenu("Plugins", subMenu);
	}

	
	private class selectAction extends AbstractAction {

		private Action action;
		selectAction(Action action)
		{
			putValue(NAME,action.text);
			this.action = action;
		}
		@Override
		public void actionPerformed(ActionEvent arg0) 
		{
			switch(action)
			{
			case BUILD:
				frame = new JFrame("Build pathway");
				InputWindow newWindow = new InputWindow(desktop,frame);
				newWindow.newpwy(true);
			
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().add(newWindow, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(desktop.getFrame());
				frame.setVisible(true);
				break;
			case LAYOUT:
				layout.useSelection(false);
				layout.doLayout(desktop.getSwingEngine());
				break;
			case ADD:
				frame = new JFrame("Add to existing pathway");
				InputWindow addWindow = new InputWindow(desktop,frame);
				addWindow.newpwy(true);
				frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				frame.getContentPane().add(addWindow, BorderLayout.CENTER);
				frame.pack();
				frame.setLocationRelativeTo(desktop.getFrame());
				frame.setVisible(true);
				break;
			case LAYSEL:
				layout.useSelection(true);
				layout.doLayout(desktop.getSwingEngine());
				break;
			}
		}
	}
	/**
	 * enumerator used to set preference's
	 * 
	 * @author Christ Leemans
	 */
	public static enum PbPreference implements Preference
	{
		PB_PLUGIN_TXT_FILE(System.getProperty("user.home") + File.separator + "text_file.txt");

		private final String defaultVal;
		
		PbPreference (String _defaultVal) { defaultVal = _defaultVal; }
		
		@Override
		public String getDefault() { return defaultVal; }
	}

	
	static BundleContext getContext() {
		return context;
	}
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception 
	{
		context.registerService(Plugin.class.getName(), this, null);	
	}

	@Override
	public void stop(BundleContext context) throws Exception 
	{
		done();
	}
	@Override
	public void done() {
		desktop.unregisterSubMenu("Plugins", subMenu);
		
	}
}