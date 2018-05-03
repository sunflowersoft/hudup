/**
 * 
 */
package net.hudup.evaluate.ui;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.Recommender;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.UriAdapter.AdapterWriteChannel;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.CounterClock;
import net.hudup.evaluate.EstimateEvaluator;
import net.hudup.evaluate.Evaluator;
import net.hudup.evaluate.EvaluatorConfig;
import net.hudup.evaluate.EvaluatorListener;
import net.hudup.evaluate.Metrics;
import net.hudup.evaluate.ProgressListener;

import org.apache.log4j.Logger;

/**
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AbstractEvaluateGUI extends JPanel implements EvaluatorListener, ProgressListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(AbstractEvaluateGUI.class);

	
	/**
	 * 
	 */
	protected Evaluator evaluator = null;

	
	/**
	 * 
	 */
	protected Metrics result = null;
	
	
	/**
	 * 
	 */
	protected Map<String, ByteChannel> ioChannels = Util.newMap();
	
	
	/**
	 * 
	 */
	protected CounterClock counterClock = null;
	
	
	/**
	 * 
	 * @param config
	 */
	public AbstractEvaluateGUI(EvaluatorConfig config) {
		this(config, false);
		
	}
	
	
	/**
	 * 
	 * @param config
	 * @param estimateMode
	 */
	public AbstractEvaluateGUI(EvaluatorConfig config, boolean estimateMode) {
		if (estimateMode)
			this.evaluator = new EstimateEvaluator(config);
		else
			this.evaluator = new Evaluator(config);
		this.evaluator.addEvaluatorListener(this);
		this.evaluator.addProgressListener(this);
		
		counterClock = new CounterClock();
	}
	
	
	/**
	 * 
	 * @param key
	 * @return message according to key
	 */
	protected String getMessage(String key) {
		return I18nUtil.getMessage(evaluator.getConfig(), key);
		
	}
	
	
	/**
	 * 
	 * @return list of algorithms
	 */
	protected abstract List<Alg> getCurrentAlgList();
	
	
	/**
	 * 
	 */
	public abstract void pluginChanged();
	
	
	/**
	 * 
	 * @return {@link Evaluator}
	 */
	public Evaluator getEvaluator() {
		return evaluator;
	}
	
	
	/**
	 * 
	 */
	protected abstract void refresh();
	
	
	/**
	 * 
	 */
	protected abstract void clear();
	
	
	/**
	 * 
	 */
	protected abstract void run();
	
	
	/**
	 * 
	 */
	protected void pauseResume() {
		if (evaluator.isPaused()) {
			evaluator.resume();
			counterClock.resume();
			updateMode();
		}
		else if (evaluator.isRunning()) {
			evaluator.pause();
			counterClock.pause();
			updateMode();
		}
		
	}

	
	/**
	 * 
	 */
	protected void stop() {
		evaluator.stop();
		counterClock.stop();
		updateMode();
	}
	
	
	/**
	 * 
	 */
	protected void forceStop() {
		evaluator.forceStop();
		counterClock.stop();
		
		List<Alg> list = getCurrentAlgList();
		for (Alg alg : list) {
			if (alg instanceof Recommender)
				((Recommender)alg).unsetup();
		}
		
		updateMode();
	}
	
	
	/**
	 * 
	 */
	protected abstract void updateMode();
	
	
	/**
	 * 
	 */
	protected void closeIOChannels() {
			
		Set<String> keys = ioChannels.keySet();
		for (String key : keys) {
			try {
				ioChannels.get(key).close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ioChannels.clear();
		
	}
	
	
	/**
	 * 
	 * @param key
	 * @return whether close channel successfully
	 */
	protected boolean closeIOChannel(String key) {
		if (!ioChannels.containsKey(key))
			return false;
		
		try {
			ByteChannel channel = ioChannels.get(key);
			channel.close();
			ioChannels.remove(key);
			
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	
	/**
	 * 
	 * @param store
	 * @param key
	 * @param append
	 * @return byte channel
	 */
	protected ByteChannel getIOChannel(xURI store, String key, boolean append) {
		if (ioChannels.containsKey(key))
			return ioChannels.get(key);
		
		xURI uri = store.concat(key);
		AdapterWriteChannel channel = new AdapterWriteChannel(uri, append);
		ioChannels.put(key, channel);
		return channel;
	}
	
	
	/**
	 * 
	 */
	public void dispose() {
		stop();
		clear();
		closeIOChannels();
	}
	
	
	/**
	 * Switching evaluator via two options: {@link Evaluator} and {@link EstimateEvaluator}
	 */
	protected void switchEvaluator() {
		stop();
		evaluator.removeEvaluatorListener(this);
		evaluator.removeProgressListener(this);
		
		EvaluatorConfig config = evaluator.getConfig();
		if (evaluator instanceof EstimateEvaluator)
			evaluator = new Evaluator(config);
		else
			evaluator = new EstimateEvaluator(config);
		
		evaluator.addEvaluatorListener(this);
		evaluator.addProgressListener(this);
		
		updateMode();
	}
	
	
	/**
	 * 
	 */
	protected void metricsOption() {
		if (evaluator.isStarted()) {
			logger.error("Evaluator started, it is impossible to set up metric list");
			return;
		}
		
		MetricsOptionDlg dlg = new MetricsOptionDlg(this, evaluator.getMetricList());
		List<Metric> selectedMetricList = dlg.getSelectedMetricList();
		
		if (selectedMetricList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"No metrics option selected", 
					"No metrics option selected", 
					JOptionPane.WARNING_MESSAGE);
		}
		
		evaluator.setMetricList(selectedMetricList);
	}
	
	

	
	
	
	
}



