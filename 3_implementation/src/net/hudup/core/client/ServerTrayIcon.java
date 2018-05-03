package net.hudup.core.client;

import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.rmi.RemoteException;

import javax.swing.GrayFilter;

import net.hudup.core.logistic.I18nUtil;



/**
 * When server started, there is its icon shown on system tray of current operating system such as Windows and Linux.
 * This class represents such tray icon of server.
 * It also implements {@link ServerStatusListener} so that it can update itself; for example, it changes different icons according to different statuses such as running status, paused status, and stopped status.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class ServerTrayIcon extends TrayIcon implements ServerStatusListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Reference to remote server.
	 */
	protected Server server;
	
	
	/**
	 * Icon image when server is running.
	 */
	protected Image runningImage;
	
	
	/**
	 * Icon image when server paused.
	 */
	protected Image pausedImage;

	
	/**
	 * Icon image when server stopped.
	 */
	protected Image stoppedImage;

	
	/**
	 * The tool-tip shows explanation text for server.
	 */
	protected String tooltip;
	
	
	/**
	 * Constructor with reference to server, icon image for running status, icon image for paused status,
	 * icon image for stopped status, tool-tip showing explanation text for server, and pop-up context menu.
	 * @param server reference to server.
	 * @param runningImage icon image for running status.
	 * @param pausedImage icon image for paused status.
	 * @param stoppedImage icon image for stopped status.
	 * @param tooltip tool-tip showing explanation text for server.
	 * @param popup pop-up context menu.
	 */
	public ServerTrayIcon(
			Server server, 
			Image runningImage, 
			Image pausedImage, 
			Image stoppedImage, 
			String tooltip,
			PopupMenu popup) {
		
		super(runningImage, tooltip, popup);
		
		this.server = server;
		this.runningImage = runningImage;
		this.tooltip = tooltip == null ? "" : tooltip;
		
		ImageFilter filter = new GrayFilter(true, 50);
		ImageProducer producer = new FilteredImageSource(runningImage.getSource(), filter);
		this.pausedImage = Toolkit.getDefaultToolkit().createImage(producer);
		this.stoppedImage = Toolkit.getDefaultToolkit().createImage(producer);

		try {
			server.addStatusListener(this);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateStatus();
		
	}
	
	
	/**
	 *{@link ServerTrayIcon} updates itself according to server status.
	 * For instance, {@link ServerTrayIcon} changes different icons according to different statuses such as running status, paused status, and stopped status.
	 */
	protected void updateStatus() {
		try {
			if (server.isRunning()) {
				setImage(runningImage);
				setToolTip(tooltip + " (" + getMessage("run") + ")");
			}
			else if (server.isPaused()) {
				setImage(pausedImage);
				setToolTip(tooltip + " (" + getMessage("pause") + ")");
			}
			else {
				setImage(stoppedImage);
				setToolTip(tooltip + " (" + getMessage("stop") + ")");
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public void statusChanged(ServerStatusEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		if (!evt.getShutdownHookStatus())
			updateStatus();
	}

	
	/**
	 * Getting a value (called message) of the specified key in server configuration.
	 * This method also supports internationalization (I18n) and so the returned message is localized according to the pre-defined language.
	 * @param key specified key.
	 * @return message according to key.
	 */
	protected String getMessage(String key) {
		try {
			return I18nUtil.getMessage(server.getConfig(), key);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return key;
	}
	
	
}
