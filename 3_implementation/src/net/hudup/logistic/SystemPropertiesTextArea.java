package net.hudup.logistic;

import java.util.List;

import javax.swing.JTextArea;

import net.hudup.core.Util;
import net.hudup.core.data.PropList;


/**
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class SystemPropertiesTextArea extends JTextArea {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 * @param rows
	 * @param columns
	 */
	public SystemPropertiesTextArea(int rows, int columns) {
		super(rows, columns);
		// TODO Auto-generated constructor stub
		
		init();
	}


	/**
	 * 
	 */
	public SystemPropertiesTextArea() {
		super();
		init();
	}
	
	
	/**
	 * 
	 */
	private void init() {
		setEditable(false);
		setWrapStyleWord(true);
		setLineWrap(true);
		
		refresh();
	}
	
	
	/**
	 * 
	 */
	public void refresh() {
		PropList sysProps = SystemUtil.getSystemProperties();
		List<String> keys = Util.newList();
		keys.addAll(sysProps.keySet());
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < keys.size(); i++) {
			if (i > 0)
				buffer.append("\n\n");
			String key = keys.get(i);
			buffer.append(key + ": " + sysProps.getAsString(key));
		}
		
		setText(buffer.toString());
	}
	
	
}
