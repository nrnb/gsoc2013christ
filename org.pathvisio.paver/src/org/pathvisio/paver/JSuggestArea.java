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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 * JSuggestArea Class.<p>
 * Provides a text-field that makes suggestions using a provided data-vector.
 * You might have seen this on Google (tm), this is the Java implementation.
 *
 * @author David von Ah
 * 
 * Changed into JSuggestArea
 * @author Christ Leemans
 */
public class JSuggestArea extends JTextArea {

	/** unique ID for serialization */
	private static final long serialVersionUID = 1756202080423312153L;

	/** Dialog used as the drop-down list. */
	private JDialog d;

	/** Location of said drop-down list. */
	private Point location;

	/** List contained in the drop-down dialog. */
	private JList list;

	/**
	 * Vectors containing the original data and the filtered data for the
	 * suggestions.
	 */
	private Vector<String> first,second,third, suggestions;
	
	/**
	 * Strings to add to the end of a string when it's chosen
	 */
	private String firstsuf,secondsuf,thirdsuf;

	/**
	 * Separate matcher-thread, prevents the text-field from hanging while the
	 * suggestions are beeing prepared.
	 */
	private InterruptableMatcher matcher;

	/** Needed for the new narrowing search, so we know when to reset the list */
	private String lastWord = "";
	
	/**
	 * The last chosen variable which exists. Needed if user
	 * continued to type but didn't press the enter key
	 * */
	private String lastChosenExistingVariable;

	/** Listeners, fire event when a selection as occured */
	private LinkedList<ActionListener> listeners;

	/**
	 * Create a new JSuggestArea.
	 *
	 * @param owner
	 *            Frame containing this JSuggestArea
	 */
	public JSuggestArea(Frame owner) {
		super();
		initOwner(owner);
	}
	
	/**
	 * create a new JSuggestArea
	 * @param owner
	 * 			Frame containing this JSuggestArea
	 * @param rows
	 * 			Number of rows
	 * @param columns
	 * 			Number of columns
	 */
	public JSuggestArea(int rows, int columns, Frame owner){
		super(rows,columns);
		initOwner(owner);
	}
	
	private void initOwner(Frame owner){
		first = new Vector<String>();
		second = new Vector<String>();
		third = new Vector<String>();
		suggestions = new Vector<String>();
		listeners = new LinkedList<ActionListener>();
		owner.addComponentListener(new ComponentListener() {
			@Override
			public void componentShown(ComponentEvent e) {
				relocate();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				relocate();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				relocate();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				relocate();
			}
		});
		owner.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
				d.setVisible(false);
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				d.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				d.dispose();
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}
		});
		addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) {
				d.setVisible(false);
				
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (getText().equals("")){
					if (first.size()==0){
						return;
					}
					list.setListData(first);
				}
				
//				d.setVisible(true);
//				showSuggest();
			}
		});
		d = new JDialog(owner);
		d.setUndecorated(true);
		d.setFocusableWindowState(false);
		d.setFocusable(false);
		list = new JList();
		list.addMouseListener(new MouseListener() {
			private int selected;

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (selected == list.getSelectedIndex()) {
					// provide double-click for selecting a suggestion
					complete();
				}
				selected = list.getSelectedIndex();
			}
			

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		list.setListData(first);
		d.add(new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
		d.pack();
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				relocate();
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					d.setVisible(false);
					return;
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					if (d.isVisible()) {
						list.setSelectedIndex(list.getSelectedIndex() + 1);
						list.ensureIndexIsVisible(list.getSelectedIndex() + 1);
						return;
					} else {
						showSuggest();
					}
				} else if (e.getKeyCode() == KeyEvent.VK_UP) {
					list.setSelectedIndex(list.getSelectedIndex() - 1);
					list.ensureIndexIsVisible(list.getSelectedIndex() - 1);
					return;
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER
						&& list.getSelectedIndex() != -1 && suggestions.size() > 0 &&
						list.isShowing()) {
					
					complete();
				}
				showSuggest();
			}
		});
	}

	private void complete(){
		String text = getText();
		
		String lastline = "";
		try {
			lastline = text.substring(getLineStartOffset(getLineCount()-1));
			if (lastline.isEmpty()){
				lastline = text.substring(getLineStartOffset(getLineCount()-2),getLineEndOffset(getLineCount()-2));
			}
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(lastline);
		
		String[] words = lastline.split("\t");
		for (String word : words){
			System.out.println(word);
		}
		String val;
		if (words.length==1 || words.length==0){
			val = (String) list.getSelectedValue() + firstsuf;
		}
		else if (words.length==2){
			val = (String) list.getSelectedValue() + secondsuf;
		}
		else {
			val = (String) list.getSelectedValue() + thirdsuf;
		}
		String[] n = text.split("\n");
		String txt = "";
		
		for (int i = 0; i< n.length-1;i++){
			if (txt.isEmpty()){
				txt = n[i];
			}
			else{
				txt = txt + "\n" + n[i];
			}
		}
		String[] t = lastline.split("\t");
		t[t.length-1] = val;
		if(txt.isEmpty()){
			txt = t[0];
		}
		else{
			txt = txt + "\n" + t[0];
		}
//		txt = "";
		for (int i = 1; i<t.length; i++){
			txt = txt + "\t" + t[i];		
		}
		setText(txt);
		matcher.stop = true;
		lastChosenExistingVariable = list.getSelectedValue().toString();
		fireActionEvent();
		d.setVisible(false);
	}
	

	/**
	 * Sets new data used to suggest similar words for the first word.
	 * words are separated by tabs
	 *
	 * @param first Vector containing available words
	 * @param firstsuf suffix to put behind word when auto-completing
	 * @return success, true unless the data-vector was null
	 */
	public boolean setSuggestFirst(Vector<String> first, String firstsuf) {
		if (first == null){
			return false;
		}
		this.firstsuf = firstsuf;
		Collections.sort(first);
		this.first = first;
		list.setListData(first);
		return true;
	}
	/**
	 * Sets new data used to suggest similar words for the second word.
	 * words are separated by tabs
	 *
	 * @param second Vector containing available words
	 * @param secondsuf suffix to put behind word when auto-completing
	 * @return success, true unless the data-vector was null
	 */
	public boolean setSuggestSecond(Vector<String> second, String secondsuf) {
		if (second == null) {
			return false;
		}
		this.secondsuf = secondsuf;
		Collections.sort(second);
		this.second = second;
		list.setListData(second);
		return true;
	}
	/**
	 * Sets new data used to suggest similar words for the third word.
	 * words are separated by tabs
	 *
	 * @param third Vector containing available words
	 * @param thirdsuf suffix to put behind word when auto-completing
	 * @return success, true unless the data-vector was null
	 */
	public boolean setSuggestThird(Vector<String> third, String thirdsuf){
		if (third == null){
			return false;
		}
		this.thirdsuf = thirdsuf;
		Collections.sort(third);
		this.third = third;
		list.setListData(third);
		return true;
	}
	
	/**
	 * empty all suggestions, so that new ones can be added
	 */
	public void clearSuggestions(){
		first.clear();
		second.clear();
		third.clear();
	}

	/**
	 * Get all words that are available for suggestion.
	 *
	 * @return Vector containing Strings
	 * @throws NumberFormatException if the integer is not between 0 and 3
	 */
	public Vector<String> getSuggestData(int i) throws NumberFormatException {
		if (i==0) return first;
		else if (i==1) return second;
		else if (i==2) return third;
		else throw new NumberFormatException();
	}

	/**
	 * Set preferred size for the drop-down that will appear.
	 *
	 * @param size
	 *            Preferred size of the drop-down list
	 */
	public void setPreferredSuggestSize(Dimension size) {
		d.setPreferredSize(size);
	}

	/**
	 * Set minimum size for the drop-down that will appear.
	 *
	 * @param size
	 *            Minimum size of the drop-down list
	 */
	public void setMinimumSuggestSize(Dimension size) {
		d.setMinimumSize(size);
	}

	/**
	 * Set maximum size for the drop-down that will appear.
	 *
	 * @param size
	 *            Maximum size of the drop-down list
	 */
	public void setMaximumSuggestSize(Dimension size) {
		d.setMaximumSize(size);
	}

	/**
	 * Force the suggestions to be displayed (Useful for buttons
	 * e.g. for using JSuggestionField like a ComboBox)
	 */
	public void showSuggest() {
		if (!getText().toLowerCase().contains(lastWord.toLowerCase())) {
			suggestions.clear();
		}
//		if (suggestions.isEmpty()) {
			suggestions.clear();
			String text = getText();
			String[] lines = text.split("\n");
			String lastLine = lines[lines.length-1];
			String[] words = lastLine.split("\t");
			if (text.length()==0 ||
					words.length==1 &&
					text.charAt(text.length()-1)!='\t'){
				suggestions.addAll(first);
			}
			else if (words.length==1 &&
					text.charAt(text.length()-1)=='\t' ||
					words.length==2 &&
					text.charAt(text.length()-1)!='\t'){
				suggestions.addAll(second);
			}
			else {
				suggestions.addAll(third);
			}
//		}
		if (matcher != null) {
			matcher.stop = true;
		}
		matcher = new InterruptableMatcher();
		matcher.start();
		SwingUtilities.invokeLater(matcher);
		lastWord = getText();
		relocate();
	}

	/**
	 * Force the suggestions to be hidden (Useful for buttons, e.g. to use
	 * JSuggestionField like a ComboBox)
	 */
	public void hideSuggest() {
		d.setVisible(false);
	}

	/**
	 * @return boolean Visibility of the suggestion window
	 */
	public boolean isSuggestVisible() {
		return d.isVisible();
	}

	/**
	 * Place the suggestion window under the JTextField.
	 */
	private void relocate() {
		try {
			location = getLocationOnScreen();
			location.y += getHeight();
			d.setLocation(location);
		} catch (IllegalComponentStateException e) {
			return; // might happen on window creation
		}
	}

	/**
	 * Inner class providing the independent matcher-thread. This thread can be
	 * interrupted, so it won't process older requests while there's already a
	 * new one.
	 */
	private class InterruptableMatcher extends Thread {
		/** flag used to stop the thread */
		private volatile boolean stop;

		/**
		 * Standard run method used in threads
		 * responsible for the actual search
		 */
		@Override
		public void run() {
			try {
				Iterator<String> it = suggestions.iterator();
				//previously everything was seen as a single word, now we want the last word
				String text = getText();
				String[] lines = text.split("\n");
				String lastLine = lines[lines.length-1];
				String[] words = lastLine.split("\t");
				//if the last character is a tab, it's time for the next word
				if (getText().charAt(getText().length()-1)=='\t'){
					if (words.length==1){
						list.removeAll();
						list.setListData(second);
					}
					else {
						list.removeAll();
						list.setListData(third);
					}
				}
				String word;
				if (text.charAt(text.length()-1)=='\t'){
					return;
				}
				else {
					word = words[words.length-1];
				}
				while (it.hasNext()) {
					if (stop) {
						return;
					}
					// rather than using the entire list, let's rather remove
					// the words that don't match, thus narrowing
					// the search and making it faster
					if (!it.next().toLowerCase().contains(word.toLowerCase())) {
						it.remove();
					}
				}
				if (suggestions.size() > 0) {
					list.setListData(suggestions);
					list.setSelectedIndex(0);
					list.ensureIndexIsVisible(0);
					d.setVisible(true);
				} else {
					d.setVisible(false);
				}
			} catch (Exception e) {
				// Despite all precautions, external changes have occurred.
				// Let the new thread handle it...
				return;
			}
		}
	}

	/**
	 * Adds a listener that notifies when a selection has occured
	 * @param listener
	 * 			ActionListener to use
	 */
	public void addSelectionListener(ActionListener listener) {
		if (listener != null) {
			listeners.add(listener);
		}
	}

	/**
	 * Removes the Listener
	 * @param listener
	 * 			ActionListener to remove
	 */
	public void removeSelectionListener(ActionListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Use ActionListener to notify on changes
	 * so we don't have to create an extra event
	 */
	private void fireActionEvent() {
		ActionEvent event = new ActionEvent(this, 0, getText());
		for (ActionListener listener : listeners) {
			listener.actionPerformed(event);
		}
	}
	
	
	/**
	 * Returns the selected value in the drop down list
	 * 
	 * @return selected value from the user or null if the entered value does not exist
	 */
	public String getLastChosenExistingVariable() {
		return lastChosenExistingVariable;
	}
}

