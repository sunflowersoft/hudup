package net.hudup.core.logistic.ui;

import static net.hudup.core.Constants.ROOT_PACKAGE;
import static net.hudup.core.Constants.VERSION;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JTextArea;

import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;


/**
 * The graphic user interface (GUI) component as a text area shows a tool-tip.
 * Note, tool-tip is a hinted text splashing an additional explanation for some GUI component. 
 * However, in the current implementation, this tool-tip class shows the &quot;README&quot; file that introduces Hudup framework in short.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class TooltipTextArea extends JTextArea {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * File name of the &quot;README&quot; file that introduces Hudup framework in short.
	 */
	public final static String README = "readme.txt";
	
	
	/**
	 * Default constructor.
	 */
	public TooltipTextArea() {
		super();

		setEditable(false);
		setWrapStyleWord(true);
		setLineWrap(true);
		
		try {
			InputStream is = getClass().getResourceAsStream(ROOT_PACKAGE + README);
			StringBuffer textBuffer = new StringBuffer();
			
			if (is != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				
				char[] charBuffer = new char[4096];
				int read = 0;
				do {
					textBuffer.append(charBuffer, 0, read);
					read = reader.read(charBuffer);
				}
				while (read >= 0);
				
				
				reader.close();
				is.close();
			}
			else {
				UriAdapter adapter = new UriAdapter(README);
				textBuffer = adapter.readText(xURI.create(README));
				adapter.close();
			}
			
			String text = textBuffer.toString();
			text = text.replaceAll("\\$\\{version\\}", VERSION);
			
			setText(text);
			setCaretPosition(0);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	

}
