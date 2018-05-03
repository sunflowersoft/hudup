package net.hudup;

import static net.hudup.core.Constants.ROOT_PACKAGE;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import net.hudup.core.AccessPoint;
import net.hudup.core.Util;
import net.hudup.core.logistic.ui.TooltipTextArea;
import net.hudup.core.logistic.ui.UIUtil;

import org.reflections.Reflections;



/**
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class Starter {

	
	/**
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		
		List<AccessPoint> apList = Util.newList();
		String pkg = ROOT_PACKAGE.trim();
		if (pkg.startsWith("/"))
			pkg = pkg.substring(1);
		if (pkg.endsWith("/"))
			pkg = pkg.substring(0, pkg.length()-1);
		pkg = pkg.replace('/', '.');
		
		Reflections reflections = new Reflections(pkg);
		Set<Class<? extends AccessPoint>> apClasses = reflections.getSubTypesOf(AccessPoint.class);
		for (Class<? extends AccessPoint> apClass : apClasses) {
			if (!AccessPoint.class.isAssignableFrom(apClass))
				continue;
			
			if (apClass.isInterface() || apClass.isMemberClass() || apClass.isAnonymousClass())
				continue;
			
			int modifiers = apClass.getModifiers();
			if ( (modifiers & Modifier.ABSTRACT) != 0 || (modifiers & Modifier.PUBLIC) == 0)
				continue;
			
			try {
				AccessPoint ap = (AccessPoint) Util.newInstance(apClass);
				apList.add(ap);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		if (apList.size() == 0) {
			JOptionPane.showMessageDialog(
					null, 
					"There is no access point", 
					"There is no access point", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		
		Collections.sort(apList, new Comparator<AccessPoint>() {

			@Override
			public int compare(AccessPoint o1, AccessPoint o2) {
				// TODO Auto-generated method stub
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		AccessPoint initialAP = null;
		for (AccessPoint ap : apList) {
			if (ap instanceof Evaluator) {
				initialAP = ap;
				break;
			}
		}
		
		final JDialog dlgStarter = new JDialog((JFrame)null, "Hudup framework starter", true);
		dlgStarter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dlgStarter.setSize(600, 300);
		dlgStarter.setLocationRelativeTo(null);
		
        Image image = UIUtil.getImage("start-32x32.png");
        if (image != null)
        	dlgStarter.setIconImage(image);
        
        dlgStarter.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        dlgStarter.add(header, BorderLayout.NORTH);
        
        header.add(new JLabel("Please choose an access point and press \"Run\" button"), BorderLayout.NORTH);
        
        final JComboBox<AccessPoint> cmbAccessPoint = new JComboBox<AccessPoint>(apList.toArray(new AccessPoint[0]));
        cmbAccessPoint.setSelectedItem(initialAP);
        header.add(cmbAccessPoint, BorderLayout.CENTER);
        
        JButton btnRun = new JButton("Run");
        btnRun.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				AccessPoint ap = (AccessPoint) cmbAccessPoint.getSelectedItem();
				dlgStarter.dispose();
				ap.run(args);
			}
		});
        header.add(btnRun, BorderLayout.EAST);
        
        JPanel body = new JPanel(new BorderLayout());
        dlgStarter.add(body, BorderLayout.CENTER);
        
        JTextArea txtTooltip = new TooltipTextArea();
        body.add(new JScrollPane(txtTooltip), BorderLayout.CENTER);
        
        dlgStarter.setVisible(true);
	}
	
	

}
