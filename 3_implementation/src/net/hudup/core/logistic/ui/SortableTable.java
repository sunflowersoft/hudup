package net.hudup.core.logistic.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * JCommon : a free general purpose class library for the Java(tm) platform.<br>
 * <br>
 * <br>
 * (C) Copyright 2000-2005, by Object Refinery Limited and Contributors.<br>
 * <br>
 * Project Info:  http://www.jfree.org/jcommon/index.html<br>
 * <br>
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by 
 * the Free Software Foundation; either version 2.1 of the License, or 
 * (at your option) any later version.<br>
 * <br>
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public 
 * License for more details.<br>
 * <br>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, 
 * USA.<br>
 * <br>
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc. 
 * in the United States and other countries.]<br>
 * <br>
 * ------------------<br>
 * SortableTable.java<br>
 * ------------------<br>
 * (C) Copyright 2000-2004, by Object Refinery Limited.<br>
 * <br>
 * Available at <a href="http://www.java2s.com/Code/Java/Swing-JFC/AsimpleextensionofJTablethatsupportstheuseofaSortableTableModel.htm">http://www.java2s.com/Code/Java/Swing-JFC/AsimpleextensionofJTablethatsupportstheuseofaSortableTableModel.htm</a><br>
 * <br>
 * This class written by David Gilbert and Nobuo Tamemasa allows users to create and show a table which can be sorted its rows according its columns.<br>
 * <br>
 * Modified by Loc Nguyen 2009.<br>
 * <br>
 * @author Original Author:  David Gilbert (for Object Refinery Limited), Nobuo Tamemasa.<br>
 * @version 2.1
 */
public class SortableTable extends JTable {
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	/** A listener for sorting. */
    private SortableTableHeaderListener headerListener;

    /**
     * Standard constructor - builds a table for the specified model.
     *
     * @param model  the data.
     */
    public SortableTable(final SortableTableModel model) {
        super(model);
        init();
    }
    
    /**
     * Default constructor. Added by Loc Nguyen 2010.02.03
     */
    public SortableTable() {
        super(new SortableTableModel());
        init();
    }
    
    /**
     * Initialize the sortable table. Added by Loc Nguyen 2010.02.03
     */
    protected void init() {
    	SortableTableModel model = (SortableTableModel)getModel();

    	final SortButtonRenderer renderer = new SortButtonRenderer();
        final TableColumnModel cm = getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); i++) {
            cm.getColumn(i).setHeaderRenderer(renderer);
        }

        final JTableHeader header = getTableHeader();
        if (this.headerListener != null) {
        	header.removeMouseListener(this.headerListener);
        	header.removeMouseMotionListener(this.headerListener);
        }
        this.headerListener = new SortableTableHeaderListener((SortableTableModel)model, renderer);
        header.addMouseListener(this.headerListener);
        header.addMouseMotionListener(this.headerListener);

    	((SortableTableModel)model).sortByColumn(0, true);
    }
    /**
     * Changes the model for the table.  Takes care of updating the header listener at the
     * same time.
     *
     * @param model  the table model.
     *
     */
    public void setSortableModel(final SortableTableModel model) {
        super.setModel(model);
        this.headerListener.setTableModel(model);
        final SortButtonRenderer renderer = new SortButtonRenderer();
        final TableColumnModel cm = getColumnModel();
        for (int i = 0; i < cm.getColumnCount(); i++) {
            cm.getColumn(i).setHeaderRenderer(renderer);
        }
        model.sortByColumn(0, true);
    }

}


/**
 * The listener for mouse operators on the header of sortable table.
 * @author David Gilbert (for Object Refinery Limited), Nobuo Tamemasa
 * @version 2.1
 *
 */
class SortableTableHeaderListener implements MouseListener, MouseMotionListener {

    /** A reference to the table model. */
    private SortableTableModel model;

    /** The header renderer. */
    private SortButtonRenderer renderer;

    /** The index of the column that is sorted - used to determine the state of the renderer. */
    private int sortColumnIndex;

    /**
     * Standard constructor.
     *
     * @param model  the model.
     * @param renderer  the renderer.
     */
    public SortableTableHeaderListener(final SortableTableModel model, 
                                       final SortButtonRenderer renderer) {
        this.model = model;
        this.renderer = renderer;
    }

    /**
     * Sets the table model for the listener.
     *
     * @param model  the model.
     */
    public void setTableModel(final SortableTableModel model) {
        this.model = model;
    }

    /**
     * Handle a mouse press event - if the user is NOT resizing a column and NOT dragging a column
     * then give visual feedback that the column header has been pressed.
     *
     * @param e  the mouse event.
     */
    public void mousePressed(final MouseEvent e) {

        final JTableHeader header = (JTableHeader) e.getComponent();

        if (header.getResizingColumn() == null) {  // resizing takes precedence over sorting
            if (header.getDraggedDistance() < 1) {   // dragging also takes precedence over sorting
                final int columnIndex = header.columnAtPoint(e.getPoint());
                final int modelColumnIndex 
                    = header.getTable().convertColumnIndexToModel(columnIndex);
                if (this.model.isSortable(modelColumnIndex)) {
                    this.sortColumnIndex = header.getTable().convertColumnIndexToModel(columnIndex);
                    this.renderer.setPressedColumn(this.sortColumnIndex);
                    header.repaint();
                    if (header.getTable().isEditing()) {
                        header.getTable().getCellEditor().stopCellEditing();
                    }
                }
                else {
                    this.sortColumnIndex = -1;
                }
            }
        }

    }

    /**
     * If the user is dragging or resizing, then we clear the sort column.
     *
     * @param e  the mouse event.
     */
    public void mouseDragged(final MouseEvent e) {

        final JTableHeader header = (JTableHeader) e.getComponent();

        if ((header.getDraggedDistance() > 0) || (header.getResizingColumn() != null)) {
            this.renderer.setPressedColumn(-1);
            this.sortColumnIndex = -1;
        }
    }

    /**
     * This event is ignored (not required).
     *
     * @param e  the mouse event.
     */
    public void mouseEntered(final MouseEvent e) {
        // not required
    }

    /**
     * This event is ignored (not required).
     *
     * @param e  the mouse event.
     */
    public void mouseClicked(final MouseEvent e) {
        // not required
    }

    /**
     * This event is ignored (not required).
     *
     * @param e  the mouse event.
     */
    public void mouseMoved(final MouseEvent e) {
        // not required
    }

    /**
     * This event is ignored (not required).
     *
     * @param e  the mouse event.
     */
    public void mouseExited(final MouseEvent e) {
        // not required
    }

    /**
     * When the user releases the mouse button, we attempt to sort the table.
     *
     * @param e  the mouse event.
     */
    public void mouseReleased(final MouseEvent e) {

        final JTableHeader header = (JTableHeader) e.getComponent();

        if (header.getResizingColumn() == null) {  // resizing takes precedence over sorting
            if (this.sortColumnIndex != -1) {
                final SortableTableModel model = (SortableTableModel) header.getTable().getModel();
                final boolean ascending = !model.isAscending();
                model.setAscending(ascending);
                model.sortByColumn(this.sortColumnIndex, ascending);

                this.renderer.setPressedColumn(-1);       // clear
                header.repaint();
            }
        }
    }

}



/**
 * Cell renderer guides how to draw (render) cells of sortable table.
 * 
 * @author David Gilbert (for Object Refinery Limited), Nobuo Tamemasa
 * @version 2.1
 *
 */
class SortButtonRenderer implements TableCellRenderer {

    /**
     * Useful constant indicating NO sorting.
     */
    public static final int NONE = 0;

    /**
     * Useful constant indicating ASCENDING (that is, arrow pointing down) sorting in the table.
     */
    public static final int DOWN = 1;

    /**
     * Useful constant indicating DESCENDING (that is, arrow pointing up) sorting in the table.
     */
    public static final int UP = 2;

    /**
     * The current pressed column (-1 for no column).
     */
    private int pressedColumn = -1;

    /**
     * The three buttons that are used to render the table header cells.
     */
    private JButton normalButton;

    /**
     * The three buttons that are used to render the table header cells.
     */
    private JButton ascendingButton;

    /**
     * The three buttons that are used to render the table header cells.
     */
    private JButton descendingButton;

    /**
     * Used to allow the class to work out whether to use the buttuns
     * or labels. Labels are required when using the aqua look and feel cos the
     * buttons won't fit.
     */
    private boolean useLabels;

    /**
     * The normal label (only used with MacOSX).
     */
    private JLabel normalLabel;

    /**
     * The ascending label (only used with MacOSX).
     */
    private JLabel ascendingLabel;

    /**
     * The descending label (only used with MacOSX).
     */
    private JLabel descendingLabel;

    /**
     * Creates a new button renderer.
     */
    public SortButtonRenderer() {

        this.pressedColumn = -1;
        this.useLabels = UIManager.getLookAndFeel().getID().equals("Aqua");

        final Border border = UIManager.getBorder("TableHeader.cellBorder");

        if (this.useLabels) {
            this.normalLabel = new JLabel();
            this.normalLabel.setHorizontalAlignment(SwingConstants.LEADING);

            this.ascendingLabel = new JLabel();
            this.ascendingLabel.setHorizontalAlignment(SwingConstants.LEADING);
            this.ascendingLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            this.ascendingLabel.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));

            this.descendingLabel = new JLabel();
            this.descendingLabel.setHorizontalAlignment(SwingConstants.LEADING);
            this.descendingLabel.setHorizontalTextPosition(SwingConstants.LEFT);
            this.descendingLabel.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));

            this.normalLabel.setBorder(border);
            this.ascendingLabel.setBorder(border);
            this.descendingLabel.setBorder(border);
        }
        else {
            this.normalButton = new JButton();
            this.normalButton.setMargin(new Insets(0, 0, 0, 0));
            this.normalButton.setHorizontalAlignment(SwingConstants.LEADING);

            this.ascendingButton = new JButton();
            this.ascendingButton.setMargin(new Insets(0, 0, 0, 0));
            this.ascendingButton.setHorizontalAlignment(SwingConstants.LEADING);
            this.ascendingButton.setHorizontalTextPosition(SwingConstants.LEFT);
            this.ascendingButton.setIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, false));
            this.ascendingButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.DOWN, false, true));

            this.descendingButton = new JButton();
            this.descendingButton.setMargin(new Insets(0, 0, 0, 0));
            this.descendingButton.setHorizontalAlignment(SwingConstants.LEADING);
            this.descendingButton.setHorizontalTextPosition(SwingConstants.LEFT);
            this.descendingButton.setIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, false));
            this.descendingButton.setPressedIcon(new BevelArrowIcon(BevelArrowIcon.UP, false, true));

            this.normalButton.setBorder(border);
            this.ascendingButton.setBorder(border);
            this.descendingButton.setBorder(border);

        }

    }

    /**
     * Returns the renderer component.
     *
     * @param table      the table.
     * @param value      the value.
     * @param isSelected selected?
     * @param hasFocus   focussed?
     * @param row        the row.
     * @param column     the column.
     * @return the renderer.
     */
    public Component getTableCellRendererComponent(final JTable table,
                                                   final Object value,
                                                   final boolean isSelected,
                                                   final boolean hasFocus,
                                                   final int row, final int column) {

        if (table == null) {
            throw new NullPointerException("Table must not be null.");
        }

        final JComponent component;
        final SortableTableModel model = (SortableTableModel) table.getModel();
        final int cc = table.convertColumnIndexToModel(column);
        final boolean isSorting = (model.getSortingColumn() == cc);
        final boolean isAscending = model.isAscending();

        final JTableHeader header = table.getTableHeader();
        final boolean isPressed = (cc == this.pressedColumn);

        if (this.useLabels) {
            final JLabel label = getRendererLabel(isSorting, isAscending);
            label.setText((value == null) ? "" : value.toString());
            component = label;
        }
        else {
            final JButton button = getRendererButton(isSorting, isAscending);
            button.setText((value == null) ? "" : value.toString());
            button.getModel().setPressed(isPressed);
            button.getModel().setArmed(isPressed);
            component = button;
        }

        if (header != null) {
            component.setForeground(header.getForeground());
            component.setBackground(header.getBackground());
            component.setFont(header.getFont());
        }
        return component;
    }

    /**
     * Returns the correct button component.
     *
     * @param isSorting whether the render component represents the sort column.
     * @param isAscending whether the model is ascending.
     * @return either the ascending, descending or normal button.
     */
    private JButton getRendererButton(final boolean isSorting, final boolean isAscending) {
        if (isSorting) {
            if (isAscending) {
                return this.ascendingButton;
            }
            else {
                return this.descendingButton;
            }
        }
        else {
            return this.normalButton;
        }
    }

    /**
     * Returns the correct label component.
     *
     * @param isSorting whether the render component represents the sort column.
     * @param isAscending whether the model is ascending.
     * @return either the ascending, descending or normal label.
     */
    private JLabel getRendererLabel(final boolean isSorting, final boolean isAscending) {
        if (isSorting) {
            if (isAscending) {
                return this.ascendingLabel;
            }
            else {
                return this.descendingLabel;
            }
        }
        else {
            return this.normalLabel;
        }
    }

    /**
     * Sets the pressed column.
     *
     * @param column the column.
     */
    public void setPressedColumn(final int column) {
        this.pressedColumn = column;
    }

}


/**
 * 
 * An arrow icon that can point up or down (usually used to indicate the sort direction in a table).
 * @author David Gilbert (for Object Refinery Limited), Nobuo Tamemasa
 * @version 2.1
 *
 */
class BevelArrowIcon implements Icon {

    /** Constant indicating that the arrow is pointing up. */
    public static final int UP = 0;

    /** Constant indicating that the arrow is pointing down. */
    public static final int DOWN = 1;

    /** The default arrow size. */
    private static final int DEFAULT_SIZE = 11;

    /** Edge color 1. */
    private Color edge1;

    /** Edge color 2. */
    private Color edge2;

    /** The fill color for the arrow icon. */
    private Color fill;

    /** The size of the icon. */
    private int size;

    /** The direction that the arrow is pointing (UP or DOWN). */
    private int direction;

    /**
     * Standard constructor - builds an icon with the specified attributes.
     *
     * @param direction .
     * @param isRaisedView .
     * @param isPressedView .
     */
    public BevelArrowIcon(final int direction, 
                          final boolean isRaisedView, 
                          final boolean isPressedView) {
        if (isRaisedView) {
            if (isPressedView) {
                init(UIManager.getColor("controlLtHighlight"),
                     UIManager.getColor("controlDkShadow"),
                     UIManager.getColor("controlShadow"),
                     DEFAULT_SIZE, direction);
            }
            else {
                init(UIManager.getColor("controlHighlight"),
                     UIManager.getColor("controlShadow"),
                     UIManager.getColor("control"),
                     DEFAULT_SIZE, direction);
            }
        }
        else {
            if (isPressedView) {
                init(UIManager.getColor("controlDkShadow"),
                     UIManager.getColor("controlLtHighlight"),
                     UIManager.getColor("controlShadow"),
                     DEFAULT_SIZE, direction);
            }
            else {
                init(UIManager.getColor("controlShadow"),
                     UIManager.getColor("controlHighlight"),
                     UIManager.getColor("control"),
                     DEFAULT_SIZE, direction);
            }
        }
    }

    /**
     * Standard constructor - builds an icon with the specified attributes.
     *
     * @param edge1  the color of edge1.
     * @param edge2  the color of edge2.
     * @param fill  the fill color.
     * @param size  the size of the arrow icon.
     * @param direction  the direction that the arrow points.
     */
    public BevelArrowIcon(final Color edge1, 
                          final Color edge2, 
                          final Color fill, 
                          final int size, 
                          final int direction) {
        init(edge1, edge2, fill, size, direction);
    }

    /**
     * Paints the icon at the specified position.  Supports the Icon interface.
     *
     * @param c .
     * @param g .
     * @param x .
     * @param y .
     */
    public void paintIcon(final Component c, 
                          final Graphics g, 
                          final int x, 
                          final int y) {
        switch (this.direction) {
            case DOWN: drawDownArrow(g, x, y); break;
            case   UP: drawUpArrow(g, x, y);   break;
        }
    }

    /**
     * Returns the width of the icon.  Supports the Icon interface.
     *
     * @return the icon width.
     */
    public int getIconWidth() {
        return this.size;
    }

    /**
     * Returns the height of the icon.  Supports the Icon interface.
     * @return the icon height.
     */
    public int getIconHeight() {
        return this.size;
    }

    /**
     * Initialises the attributes of the arrow icon.
     *
     * @param edge1  the color of edge1.
     * @param edge2  the color of edge2.
     * @param fill  the fill color.
     * @param size  the size of the arrow icon.
     * @param direction  the direction that the arrow points.
     */
    private void init(final Color edge1, 
                      final Color edge2, 
                      final Color fill, 
                      final int size, 
                      final int direction) {
        this.edge1 = edge1;
        this.edge2 = edge2;
        this.fill = fill;
        this.size = size;
        this.direction = direction;
    }

    /**
     * Draws the arrow pointing down.
     *
     * @param g  the graphics device.
     * @param xo  ??
     * @param yo  ??
     */
    private void drawDownArrow(final Graphics g, final int xo, final int yo) {
        g.setColor(this.edge1);
        g.drawLine(xo, yo,   xo + this.size - 1, yo);
        g.drawLine(xo, yo + 1, xo + this.size - 3, yo + 1);
        g.setColor(this.edge2);
        g.drawLine(xo + this.size - 2, yo + 1, xo + this.size - 1, yo + 1);
        int x = xo + 1;
        int y = yo + 2;
        int dx = this.size - 6;
        while (y + 1 < yo + this.size) {
            g.setColor(this.edge1);
            g.drawLine(x, y,   x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (0 < dx) {
                g.setColor(this.fill);
                g.drawLine(x + 2, y,   x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(this.edge2);
            g.drawLine(x + dx + 2, y,   x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            x += 1;
            y += 2;
            dx -= 2;
        }
        g.setColor(this.edge1);
        g.drawLine(
            xo + (this.size / 2), yo + this.size - 1, xo + (this.size / 2), yo + this.size - 1
        );
    }

    /**
     * Draws the arrow pointing up.
     *
     * @param g  the graphics device.
     * @param xo  ??
     * @param yo  ??
     */
    private void drawUpArrow(final Graphics g, final int xo, final int yo) {
        g.setColor(this.edge1);
        int x = xo + (this.size / 2);
        g.drawLine(x, yo, x, yo);
        x--;
        int y = yo + 1;
        int dx = 0;
        while (y + 3 < yo + this.size) {
            g.setColor(this.edge1);
            g.drawLine(x, y,   x + 1, y);
            g.drawLine(x, y + 1, x + 1, y + 1);
            if (0 < dx) {
                g.setColor(this.fill);
                g.drawLine(x + 2, y,   x + 1 + dx, y);
                g.drawLine(x + 2, y + 1, x + 1 + dx, y + 1);
            }
            g.setColor(this.edge2);
            g.drawLine(x + dx + 2, y,   x + dx + 3, y);
            g.drawLine(x + dx + 2, y + 1, x + dx + 3, y + 1);
            x -= 1;
            y += 2;
            dx += 2;
        }
        g.setColor(this.edge1);
        g.drawLine(xo, yo + this.size - 3,   xo + 1, yo + this.size - 3);
        g.setColor(this.edge2);
        g.drawLine(xo + 2, yo + this.size - 2, xo + this.size - 1, yo + this.size - 2);
        g.drawLine(xo, yo + this.size - 1, xo + this.size, yo + this.size - 1);
    }

}
