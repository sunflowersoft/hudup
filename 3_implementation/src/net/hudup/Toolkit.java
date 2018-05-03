package net.hudup;

import javax.swing.JOptionPane;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.data.ui.toolkit.DatasetToolkit;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Toolkit implements AccessPoint {

	
	/**
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		new Toolkit().run(args);
	}

	
	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		new Firer();
		
		RegisterTable parserReg = PluginStorage.getParserReg();
		if (parserReg.size() == 0) {
			JOptionPane.showMessageDialog(null, 
					"There is no registered dataset parser.\n Programm not run", 
					"No dataset parser", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		new DatasetToolkit();
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Toolkit";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	
}
