package net.hudup.core.logistic.ui;

import java.util.EventObject;


/**
 * For example, there is an application {@code A} whose process is to copy file.
 * In the progress of copying, user needs to see the number of transferred bytes.
 * So programmer must implement the interface {@code ProgressListener} as class {@code B} by defining the method {@code ProgressListener.receiveProgress(ProgressEvent)} for showing the number of transferred bytes.
 * Class {@code B} is called a progress listener.
 * Moreover the application {@code A} needs to register the progression listener (class {@code B}).
 * In the method {@code ProgressListener.receiveProgress(ProgressEvent)}, application {@code A} passes necessary information to the progress listener {@code B}.
 * Such information is modeled as this class, called {@code progress event} including two main variables:
 * <ul>
 * <li>The variable {@link #progressTotal} indicates the total amount of works which will be finished, for example, the total number of bytes which will be transferred.</li>
 * <li>The variables {@link #progressStep} indicates the current amount of works which were done, for example, the current number of bytes which were transferred.</li>
 * </ul>
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProgressEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The total amount of works which will be finished.
	 */
	protected int progressTotal = 0;
	
	
	/**
	 * The current amount of works which were done.
	 */
	protected int progressStep = 0;

	
	/**
	 * The message which describes this event.
	 */
	protected String msg = "";
	
	
	/**
	 * Constructor with specified total amount of works and specified current amount of works.
	 * @param source a reference to the application that issues this event so that the progress listener receives this event.
	 * @param progressTotal total amount of works which will be finished.
	 * @param progressStep current amount of works which were done.
	 * @param msg message which describes this event.
	 */
	public ProgressEvent(Object source, int progressTotal, int progressStep, String msg) {
		super(source);
		// TODO Auto-generated constructor stub
		
		this.progressTotal = progressTotal;
		this.progressStep = progressStep;
		this.msg = msg == null ? "" : msg;
	}

	
	/**
	 * Getting the total amount of works which will be finished.
	 * @return the total amount of works which will be finished.
	 */
	public int getProgressTotal() {
		return progressTotal;
	}
	

	/**
	 * Getting the current amount of works which were done.
	 * @return the current amount of works which were done.
	 */
	public int getProgressStep() {
		return progressStep;
	}
	
	
	/**
	 * Getting the message which describes this event.
	 * @return the message which describes this event.
	 */
	public String getMsg() {
		return msg;
	}
	
	
}
