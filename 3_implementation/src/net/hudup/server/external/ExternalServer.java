package net.hudup.server.external;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DefaultExternalQuery;
import net.hudup.server.DefaultServer;
import net.hudup.server.external.ui.ExternalServerCP;
import net.hudup.server.external.ui.SetupExternalServerWizard;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalServer extends DefaultServer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 * @param config
	 */
	public ExternalServer(ExternalServerConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
		
	}


	@Override
	protected void serverTasks() {
		// TODO Auto-generated method stub
		
		ExternalConfig externalConfig = ((ExternalServerConfig) config).getExternalConfig();
		if (externalConfig == null || externalConfig.size() == 0) {
			super.serverTasks();
			return;
		}
		
		trans.lockRead();
		try {
			ExternalQuery query = new DefaultExternalQuery();
			query.setup(config, externalConfig);
			query.importData(null);
			query.close();
			
			logger.info("External server imported external data successfully (in server tasks)");
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("External server fail to import external data (in server tasks), caused by " + e.getMessage());
			
		}
		finally {
			trans.unlockRead();
		}
		
		super.serverTasks();
	}


	@Override
	protected void showCP() {
		try {
			new ExternalServerCP(this);
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("External server fail to show control panel, caused by " + e.getMessage());
		}
	}

	
	/**
	 * 
	 * @return {@link ExternalServer}
	 */
	public static ExternalServer create() {
		return create(xURI.create(ExternalServerConfig.serverConfig));
	}

	
	/**
	 * 
	 * @param srvConfigUri
	 * @return {@link ExternalServer}
	 */
	public static ExternalServer create(xURI srvConfigUri) {
		boolean require = requireSetup(srvConfigUri);
		
		if (!require)
			return new ExternalServer(new ExternalServerConfig(srvConfigUri));
		else {
	        Image image = UIUtil.getImage("server-32x32.png");
			int confirm = JOptionPane.showConfirmDialog(
					null, 
					"External server not set up yet\n Do you want to setup server?", 
					"Setup external server", 
					JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.INFORMATION_MESSAGE, 
					image == null ? null : new ImageIcon(image));
			
			if (confirm != JOptionPane.OK_OPTION) {
				logger.info("External server not created");
				return null;
			}
			
			ExternalServerConfig config = new ExternalServerConfig(srvConfigUri);
			SetupExternalServerWizard dlg = new SetupExternalServerWizard(null, config);
			
			if (!dlg.isFinished()) {
				logger.info("External server not created");
				return null;
			}
			
			require = requireSetup(srvConfigUri);
			if (require) {
				logger.info("External server not created");
				return null;
			}
			
			return new ExternalServer(new ExternalServerConfig(srvConfigUri));
		}
		
	}
	
	
	
	
}
