package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JProgressBar;



/**
 * This graphic user interface (GUI) component shows a progress dialog.
 * The dialog shows a progress bar of a ongoing process. This dialog implements the {@link ProgressListener}.
 * <br>
 * {@link ProgressListener} establishes a protocol to observe a given process. As a convention, this interface and any its implementations are called {@code progress listener} or {@code progress observer}.
 * For example, there is an application {@code A} whose process is to copy file.
 * In the progress of copying, user needs to see the progress bar.
 * So programmer must implement this interface as class {@code B} by defining the method {@link #receiveProgress(ProgressEvent)} for showing the progress bar of bytes transferring.
 * Moreover the application {@code A} needs to register the progress listener {@code B}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProgressDlg extends JDialog implements ProgressListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Progress bar of a ongoing process.
	 */
	protected JProgressBar prgRunning = null;
	
	
	/**
	 * Indicator of whether or not this dialog closes (disposes).
	 */
	protected boolean disposed = false;
	
	
	/**
	 * Constructor with parent component and modal mode.
	 * @param comp parent component.
	 * @param modal if {@code true}, the dialog blocks user inputs.
	 */
	public ProgressDlg(Component comp, boolean modal) {
		super(UIUtil.getFrameForComponent(comp), "Progressing", modal);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(200, 100);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		prgRunning = new JProgressBar();
		prgRunning.setStringPainted(true);
		prgRunning.setToolTipText("Running progress");
		prgRunning.setVisible(true);
		prgRunning.setValue(0);
		add(prgRunning, BorderLayout.SOUTH);
		
		setVisible(true);
	}


	@Override
	public void receiveProgress(ProgressEvent evt) {
		// TODO Auto-generated method stub
		if (disposed)
			return;
		
		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		
		this.prgRunning.setMaximum(progressTotal);
		if (this.prgRunning.getValue() < progressStep) 
			this.prgRunning.setValue(progressStep);
		
		System.out.println(evt.getMsg());
	}


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		
		disposed = true;
	}
	

	
}
