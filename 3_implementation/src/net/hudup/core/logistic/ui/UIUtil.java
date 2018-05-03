package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import net.hudup.core.Constants;


/**
 * This utility class provides utility methods relevant to user interface (UI) such as getting image from URI, creating button, and creating menu item.
 *  
 * @author Loc Nguyen
 * @version 10.0
 */
public final class UIUtil {

	
	/**
	 * Creating an image from image name.
	 * The directory of such image is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param imageName image name.
	 * @return image from image name.
	 */
	public static Image getImage(String imageName) {
		URL url = getImageUrl(imageName);
		if (url == null)
			return null;
		
		try {
			return Toolkit.getDefaultToolkit().getImage(url);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Retrieving URL of an image from image name.
	 * The directory of such image is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param imageName image name.
	 * @return URL of an image from image name.
	 */
	private static URL getImageUrl(String imageName) {
		if (imageName == null || imageName.isEmpty())
			return null;
		
		URL imageURL = null;
		try {
			String path = Constants.IMAGES_PACKAGE;
			if (!path.endsWith("/"))
				path = path + "/";
			path = path + imageName;
			imageURL = UIUtil.class.getResource(path);
		}
		catch (Exception e) {
			e.printStackTrace();
			imageURL = null;
		}
		
		return imageURL;
	}
	
	
	/**
	 * Creating an icon button.
	 * @param iconName icon file name.
	 * The directory of icon is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param cmd action command for this button.
	 * @param tooltip tool-tip for this button.
	 * @param alt alternative text for this button.
	 * @param listener action listener for this button.
	 * @return {@link JButton} with icon.
	 */
	public static JButton makeIconButton(String iconName, String cmd, String tooltip, String alt, 
			ActionListener listener) {
		
		
		return makeIconButton(getImageUrl(iconName), cmd, tooltip, alt, listener);
	}
	
	
	/**
	 * Creating an icon button.
	 * @param iconURL {@link URL} of icon. URL, abbreviation of Uniform Resource Locator, locates any resource on internet.
	 * @param cmd action command for this button.
	 * @param tooltip tool-tip for this button.
	 * @param alt alternative text for this button.
	 * @param listener action listener for this button.
	 * @return {@link JButton} with icon.
	 */
	private static JButton makeIconButton(URL iconURL, String cmd, String tooltip, String alt, 
			ActionListener listener) {
		JButton button = new JButton();
	    button.setActionCommand(cmd);
	    button.setToolTipText(tooltip);
	    button.addActionListener(listener);
	
	    try {
		    if (iconURL != null) {
		        button.setIcon(new ImageIcon(iconURL, alt));
		    }
		    else {  //no image found
		        button.setText(alt);
		    }
	    }
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
	    
	    return button;
	}

	
	/**
	 * Creating a menu item with icon.
	 * @param iconName icon file name.
	 * The directory of icon is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param text text of menu item.
	 * @param listener action listener for this menu item.
	 * @return {@link JMenuItem} with icon.
	 */
	public static JMenuItem makeMenuItem(String iconName, String text, ActionListener listener) {
		return makeMenuItem(getImageUrl(iconName), text, listener);
	}
	
	
	/**
	 * Creating a menu item with icon.
	 * @param iconURL {@link URL} of icon. URL, abbreviation of Uniform Resource Locator, locates any resource on internet.
	 * @param text text of menu item.
	 * @param listener action listener for this menu item.
	 * @return {@link JMenuItem} with icon.
	 */
	private static JMenuItem makeMenuItem(URL iconURL, String text, ActionListener listener) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(listener);
		
		try {
		    if (iconURL != null) {
		        item.setIcon(new ImageIcon(iconURL, text));
		    }
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
	    return item;
	}

	
	
	/**
	 * Getting the parent window {@link Window} of the specified component.
	 * @param comp specified component.
	 * @return {@link Window} of specified component. The method return {@code null} if the specified component has no parent {@link Window}.
	 */
	public static Window getWindowForComponent(Component comp) {
	    if (comp == null)
	        return null;
	    if (comp instanceof Frame || comp instanceof Dialog)
	        return (Window)comp;
	    else
	    	return getWindowForComponent(comp.getParent());
	}

	
	/**
	 * Getting the parent frame {@link Frame} of the specified component.
	 * @param comp specified component.
	 * @return {@link Frame} of the specified component. The method return {@code null} if the specified component has no parent {@link Frame}.
	 */
	public static Frame getFrameForComponent(Component comp) {
	    if (comp == null)
	        return null;
	    if (comp instanceof Frame)
	        return (Frame)comp;
	    else
	    	return getFrameForComponent(comp.getParent());
	}

	
	/**
	 * Getting the parent dialog {@link Dialog} of the specified component.
	 * @param comp specified component.
	 * @return {@link Dialog} of the specified component. The method return {@code null} if the specified component has no parent {@link Dialog}.
	 */
	public static Dialog getDialogForComponent(Component comp) {
	    if (comp == null)
	        return null;
	    if (comp instanceof Dialog)
	        return (Dialog)comp;
	    else
	    	return getDialogForComponent(comp.getParent());
	}


}
