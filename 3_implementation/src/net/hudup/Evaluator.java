package net.hudup;

import javax.swing.JOptionPane;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.evaluate.NoneWrapperMetricList;
import net.hudup.evaluate.ui.EvalCompoundGUI;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Evaluator implements AccessPoint {

	
	/**
	 * 
	 */
	public final static String RECOMMEND_EVALUATOR = "Recommender";
	
	
	/**
	 * 
	 */
	public final static String ESTIMATE_EVALUATOR = "Estimator";
	
	
	/**
	 * 
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		new Evaluator().run(args);
	}


	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		new Firer() {

			@Override
			public void discover(String prefix) {
				// TODO Auto-generated method stub
				RegisterTable metricReg = PluginStorage.getMetricReg();
				NoneWrapperMetricList metricList = NoneWrapperMetricList.defaultCreate();
				for (int i = 0; i < metricList.size(); i++) {
					boolean registered = metricReg.register(metricList.get(i));
					if (registered)
						logger.info("Registered algorithm: " + metricList.get(i).getName());
				}

				super.discover(prefix);
			}
			
		};
		
		RegisterTable algReg = PluginStorage.getRecommenderReg();
		if (algReg.size() == 0) {
			JOptionPane.showMessageDialog(null, 
					"There is no registered algorithm.\n Programm not run", 
					"No algorithm", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		RegisterTable parserReg = PluginStorage.getParserReg();
		if (parserReg.size() == 0) {
			JOptionPane.showMessageDialog(null, 
					"There is no registered dataset parser.\n Programm not run", 
					"No dataset parser", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		
		new EvalCompoundGUI(false);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Evaluator";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	
}


