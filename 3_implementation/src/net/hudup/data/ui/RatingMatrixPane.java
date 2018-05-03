package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.DateFormatter;
import javax.swing.text.NumberFormatter;

import net.hudup.core.Constants;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Rating;
import net.hudup.core.data.ctx.ContextTemplateSchema;
import net.hudup.data.ctx.ui.ContextListTable;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RatingMatrixPane extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * 
	 */
	protected RatingValueTable tblRatingValue = null;
	
	
	/**
	 * 
	 */
	protected RatingPane paneRating = null;
	
	
	/**
	 * 
	 * @param dataset
	 */
	public RatingMatrixPane(Dataset dataset) {
		super();
		this.dataset = dataset;
		
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		tblRatingValue = new RatingValueTable();
		tblRatingValue.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				viewCell();
			}
			
		});
		
		tblRatingValue.update(dataset, null);
		body.add(new JScrollPane(tblRatingValue), BorderLayout.CENTER);
		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		paneRating = new RatingPane(dataset.getCTSchema());
		footer.add(paneRating, BorderLayout.CENTER);
	}
	
	
	/**
	 * 
	 */
	protected void viewCell() {
		paneRating.clear();
		
		int rowIndex = tblRatingValue.getSelectedRow();
		int colIndex = tblRatingValue.getSelectedColumn();
		if (rowIndex == -1 || colIndex == -1)
			return;
		
		RatingValueTM model = tblRatingValue.getRatingValueTM();
		
		int rowIdFound = -1;
		Set<Integer> rowIds = model.rowIndexes.keySet();
		for (int rowId : rowIds) {
			if (model.rowIndexes.get(rowId) == rowIndex) {
				rowIdFound = rowId;
				break;
			}
				
		}
		if (rowIdFound == -1)
			return;
		
		int colIdFound = -1;
		Set<Integer> colIds = model.columnIndexes.keySet();
		for (int colId : colIds) {
			if (model.columnIndexes.get(colId) == colIndex) {
				colIdFound = colId;
				break;
			}
				
		}
		if (colIdFound == -1)
			return;

		Rating rating = dataset.getRating(rowIdFound, colIdFound);
		if (rating != null)
			paneRating.set(rating);
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		paneRating.clear();
		tblRatingValue.clear();
	}
	
	
}



/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RatingPane extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	boolean editable = false;
	
	Rating rating = null;
	
	JFormattedTextField txtValue = null;
	
	JFormattedTextField txtDate = null;
	
	ContextListTable tblContextList = null;
	
	JPanel toolbar = null;
	
	
	/**
	 * 
	 * @param cts
	 */
	public RatingPane(ContextTemplateSchema cts) {
		super(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		left.add(new JLabel("Rated value:"));
		left.add(new JLabel("Rated date:"));
		
		JPanel center = new JPanel(new GridLayout(0, 1));
		header.add(center, BorderLayout.CENTER);
		//
		txtValue = new JFormattedTextField(new NumberFormatter());
		center.add(txtValue);
		//
		txtDate = new JFormattedTextField(
				new DateFormatter(new SimpleDateFormat(Constants.DATE_FORMAT)));
		center.add(txtDate);
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		tblContextList = new ContextListTable(cts);
		tblContextList.setEnabled(editable);
		tblContextList.setPreferredScrollableViewportSize(new Dimension(200, 50));
		body.add(new JScrollPane(tblContextList), BorderLayout.CENTER);
		
		toolbar = new JPanel();
		add(toolbar, BorderLayout.SOUTH);
		
		JButton apply = new JButton("Apply");
		toolbar.add(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				boolean apply = apply();
				if (apply) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Apply successfully", "Apply successfully", JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(
						getThis(), 
						"Fail to apply", "Fail to apply", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		
		JButton reset = new JButton("Reset");
		toolbar.add(reset);
		reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				set(rating);
			}
		});
		
		
		setEditable(false);
	}
	
	
	/**
	 * 
	 * @return this rating matrix pane
	 */
	private RatingPane getThis() {
		return this;
	}
	
	
	/**
	 * 
	 * @param rating
	 */
	public void set(Rating rating) {
		clear();
		
		if (rating != null) {
			this.rating = rating;
			txtValue.setValue(rating.value);
			txtDate.setValue(rating.ratedDate);
			tblContextList.set(rating.contexts);
		}
	}
	
	
	/**
	 * 
	 * @return whether apply successfully
	 */
	protected boolean apply() {
		if (!validateValues()) {
			JOptionPane.showMessageDialog(this, "Invalid enter data", "Invalid enter data", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if (rating != null) {
			rating.value = ((Number)txtValue.getValue()).floatValue();
			rating.ratedDate = (Date)txtDate.getValue();
			return true & tblContextList.apply();
		}
		else
			return false;
	}
	
	
	/**
	 * 
	 * @return whether values are valid
	 */
	protected boolean validateValues() {
		Object ratedValue = txtValue.getValue();
		if (ratedValue == null || !(ratedValue instanceof Number))
			return false;
		
		Object ratedDate = txtDate.getValue();
		if (ratedDate == null || !(ratedDate instanceof Date))
			return false;
		
		return true;
	}
	
	
	/**
	 * 
	 */
	public void clear() {
		txtValue.setValue(null);
		txtDate.setValue(null);
		tblContextList.clear();
	}
	
	
	/**
	 * 
	 * @return whether rating is editable
	 */
	public boolean isEditable() {
		return editable;
	}
	
	
	/**
	 * 
	 * @param editable
	 */
	public void setEditable(boolean editable) {
		this.editable = editable;
		
		txtValue.setEditable(editable);
		txtDate.setEditable(editable);
		tblContextList.setEnabled(editable);
		toolbar.setVisible(editable);
	}
	
	
	
}
