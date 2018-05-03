package net.hudup.core.data.ui;

import javax.swing.JPanel;

import net.hudup.core.data.PropList;
import net.hudup.core.data.SysConfig;


/**
 * The graphic user interface (GUI) component as panel {@link JPanel} shows a system properties list represented by {@link SysConfig}.
 * It also allows user to modify, save, and load the system property list {@link SysConfig}.
 * This pane extends the {@link PropPane}. It also overrides the method {@link #apply()} so as to save automatically the system property list.
 * Note, the system property list has the internal variable as URI string pointing to where to save (store, write) itself.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SysConfigPane extends PropPane {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public void update(PropList propList) {
		if (!(propList instanceof SysConfig))
			throw new RuntimeException("Invalid parameter");
		
		super.update(propList);
	}
	
	
	/**
	 * Getting the system property list represented by {@link SysConfig}.
	 * @return system property list represented by {@link SysConfig}.
	 */
	private SysConfig getSysConfig() {
		return (SysConfig) this.tblProp.getPropTableModel().getPropList();
	}
	
	
	@Override
	public boolean apply() {
		boolean apply = super.apply();
		
		if (apply) {
			SysConfig config = getSysConfig();
			config.save();
			
		}
		
		return apply;
	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		SysConfig config = getSysConfig();
		config.reset();
		config.save();
		
		this.tblProp.update(config);
	}

}
