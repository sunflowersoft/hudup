package net.hudup.listener.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.listener.RemoteInfoList;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfoDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * 
	 */
	private RemoteInfoPane paneRemoteInfo = null;
	
	
	/**
	 * 
	 */
	private RemoteInfoList result = null;
	
	
	
	/**
	 * 
	 * @param comp
	 * @param rInfoList
	 */
	public RemoteInfoDlg(Component comp, RemoteInfoList rInfoList) {
		super(UIUtil.getFrameForComponent(comp), "Remote information dialog", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		paneRemoteInfo = new RemoteInfoPane(rInfoList);
		body.add(paneRemoteInfo, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				result = paneRemoteInfo.getRemoteInfoList();
				dispose();
			}
		});
		
		JButton cancel = new JButton("Cancel");
		footer.add(cancel);
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				result = null;
				dispose();
			}
		});

		
		setVisible(true);
	}
	
	
	/**
	 * 
	 * @return {@link RemoteInfoList}
	 */
	public RemoteInfoList getResult() {
		return result;
	}
	
	
	
}
