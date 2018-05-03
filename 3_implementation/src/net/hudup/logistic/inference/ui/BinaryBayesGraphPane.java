package net.hudup.logistic.inference.ui;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JPanel;

import net.hudup.logistic.inference.BnetBinaryGraph;
import InferenceGraphs.InferenceGraphNode;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BinaryBayesGraphPane extends JPanel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	protected BnetBinaryGraph bayesGraph = null;
	
	
	private mxGraph viewGraph = null;
	
	
	@SuppressWarnings("unchecked")
	public BinaryBayesGraphPane(BnetBinaryGraph bayesGraph) {
		setLayout(new BorderLayout());
		
		this.bayesGraph = bayesGraph;
		this.viewGraph = new mxGraph();
		add(new mxGraphComponent(this.viewGraph), BorderLayout.CENTER);
		
		viewGraph.getModel().beginUpdate();
		
		try {
			InferenceGraphNode rootNode = bayesGraph.getRootNode();
			Object source = this.viewGraph.insertVertex(
					null, 
					rootNode.get_name(), rootNode.get_name(), 
					rootNode.get_pos_x(), rootNode.get_pos_y(), 
					BnetBinaryGraph.WIDTH, BnetBinaryGraph.HEIGHT);
			
			Vector<InferenceGraphNode> nodes = bayesGraph.get_nodes();
			for (InferenceGraphNode node : nodes) {
				if (node == rootNode) 
					continue;
				
				Object target = this.viewGraph.insertVertex(
					null, 
					node.get_name(), node.get_name(), 
					node.get_pos_x(), node.get_pos_y(), 
					BnetBinaryGraph.WIDTH, BnetBinaryGraph.HEIGHT);
				
				this.viewGraph.insertEdge(null, null, null, source, target);
			}
			
		}
		finally {
			this.viewGraph.getModel().endUpdate();
		}
	}
	
	
}
