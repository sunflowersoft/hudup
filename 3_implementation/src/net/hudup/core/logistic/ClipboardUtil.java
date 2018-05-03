package net.hudup.core.logistic;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;


/**
 * This class provides utility methods to process on clip-board such as getting text from clip-board and setting text to clip-board.
 *  
 * @author Loc Nguyen
 * @version 10.0
 */
public class ClipboardUtil implements ClipboardOwner {

	
	/**
	 * The system clip-board.
	 */
	protected Clipboard clipboard = null;
	
	
	/**
	 * The public utility variable for processing on clip-board.
	 */
	public final static ClipboardUtil util = new ClipboardUtil();
	
	
	/**
	 * Default constructor.
	 */
	private ClipboardUtil() {
		try {
			this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}
		catch (Throwable e) {
			e.printStackTrace();
			this.clipboard = null;
		}
	}

	
	/**
	 * Setting (copying) the specified text data into clip-board.
	 * @param data specified text that is copied into clip-board.
	 */
	public void setText(String data) {
		if (clipboard == null || data == null)
			return;
		
		try {
			StringSelection stringSelection = new StringSelection(data);
		    clipboard.setContents(stringSelection, this);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Getting (retrieving) current text data from clip-board.
	 * @return current text data from clip-board.
	 */
	public String getText() {
		
		String result = "";
		if (clipboard == null)
			return result;
		
		try {
			Transferable contents = clipboard.getContents(null);
			
			if (contents == null || 
					!contents.isDataFlavorSupported(DataFlavor.stringFlavor))
				return "";
			
			result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return result;
	}
	
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
		
	}
	
	
}
