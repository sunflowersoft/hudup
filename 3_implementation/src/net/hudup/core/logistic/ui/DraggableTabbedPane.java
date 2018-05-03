package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JTabbedPane;


/**
 * This class represents a graphic user interface (GUI) component as a {@link JTabbedPane} whose tabs can be dragged.
 * <br>
 * Thank sir jodonnell http://stackoverflow.com/questions/60269/how-to-implement-draggable-tab-using-java-swing 
 * for excellent solution http://stackoverflow.com/users/4223/jodonnell
 * <br><br>
 * Modified by Loc Nguyen 2011
 * 
 * @author jodonnell
 * @version 1.0
 *
 */
public class DraggableTabbedPane extends JTabbedPane {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Indicating whether a tab is being dragged.
	 */
	protected boolean dragged = false;
	
	/**
	 * A tab is being dragged has an icon specified this image.
	 */
	protected Image draggedTabImage = null;
	
	/**
	 * Index of the tab that is being dragged.
	 */
	protected int draggedTabIndex = -1;
	
	/**
	 * Location of mouse.
	 */
	protected Point mouseLocation = null;
	
	
	/**
	 * The {@link DraggableTabbedPane} is called modified if there is at least a tab which was dragged and dropped at new location
	 * This variable indicate whether {@link DraggableTabbedPane} is modified.
	 */
	protected boolean modified = false;
	
	
	/**
	 * 
	 */
	public DraggableTabbedPane() {
		super();
		
		addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				onDrag(e);
			}
			
		});
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				onRelease(e);
			}

		});
	}
	
	
	/**
	 * This is event-driven method responding the action that user is dragging the mouse.
	 * This method shows the image icon of the tab if such tab is being dragged.
	 * This method also prepares the new location of such tab which is being dragged.
	 * @param e mouse event.
	 */
	protected void onDrag(MouseEvent e) {
		
		if (dragged) {
			mouseLocation = e.getPoint();
			repaint();
			return;
		}
		
		int tabIndex = getUI().tabForCoordinate(this, e.getX(), e.getY());
		if (tabIndex < 0)
			return;
		
		Rectangle bounds = getUI().getTabBounds(this, tabIndex);
		Image paneImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics paneGraphics = paneImage.getGraphics();
		paneGraphics.setClip(bounds);
		setDoubleBuffered(false);
		paintComponent(paneGraphics);
		
		draggedTabImage = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		Graphics tabGraphics = draggedTabImage.getGraphics();
		tabGraphics.drawImage(paneImage, 
				0, 0, bounds.width, bounds.height, 
				bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 
				this);
		
		dragged = true;
		draggedTabIndex = tabIndex;
		repaint();
	}
	
	
	/**
	 * This is event-driven method responding the action that user released the mouse after she/he dragged mouse.
	 * This method updates the new location of the tab if such tab was dragged.
	 * @param e mouse event.
	 */
	protected void onRelease(MouseEvent e) {
		if (!dragged)
			return;
		
		int tabIndex = getUI().tabForCoordinate(this, e.getX(), e.getY());
		if (tabIndex >= 0 && tabIndex != draggedTabIndex) {
			Component draggedTab = getComponentAt(draggedTabIndex);
			String draggedTabTitle = getTitleAt(draggedTabIndex);
			Icon draggedTabIcon = getIconAt(draggedTabIndex);
			String draggedTabToolTip = getToolTipTextAt(draggedTabIndex);
			
			removeTabAt(draggedTabIndex);
			insertTab(draggedTabTitle, draggedTabIcon, draggedTab, draggedTabToolTip, tabIndex);
			modified = true;
		}
		
		dragged = false;
		draggedTabImage = null;
		draggedTabIndex = -1;
		mouseLocation = null;
		
		repaint();
	}


	@Override
	public void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		
		if (dragged && draggedTabImage != null && mouseLocation != null)
			g.drawImage(draggedTabImage, mouseLocation.x, mouseLocation.y, this);
	}
	
	
	/**
	 * The {@link DraggableTabbedPane} is called modified if there is at least a tab which was dragged and dropped at new location
	 * This method tests whether {@link DraggableTabbedPane} is modified.
	 * @return whether whether {@link DraggableTabbedPane} is modified.
	 */
	public boolean isModified() {
		return modified;
	}
	
	
	/**
	 * The {@link DraggableTabbedPane} is called modified if there is at least a tab which was dragged and dropped at new location
	 * This method sets that {@link DraggableTabbedPane} is modified or kept intact according to the flag.
	 * @param modified flag indicating whether or not {@link DraggableTabbedPane} is modified
	 */
	public void setModified(boolean modified) {
		this.modified = modified;
	}
	
	
}
