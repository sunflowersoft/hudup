package net.hudup.core.logistic;

import java.awt.Component;
import java.io.File;
import java.nio.file.Path;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.hudup.core.Constants;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.swing.TFileChooser;
import de.schlichtherle.truezip.nio.file.TPath;


/**
 * This class is default implementation of the interface {@link UriAssoc}. In other words, it is the default URI associator.
 * In current implementation, {@link UriAssocDefault} uses the programming library TrueZip for processing on file system, compressed file, HTTP.
 * TrueZip is developed by Schlichtherle IT Services, available at <a href="https://christian-schlichtherle.bitbucket.io/truezip/">https://christian-schlichtherle.bitbucket.io/truezip</a>
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class UriAssocDefault extends UriAssocAbstract {

	
	/**
	 * Default constructor
	 */
	public UriAssocDefault() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public boolean isArchive(xURI uri) {
		// TODO Auto-generated method stub
		TPath path = (TPath) newPath(uri); 
		return path.isArchive();
	}

	
	@Override
	public Path newPath(xURI uri) {
		// TODO Auto-generated method stub
		return new TPath(uri.getURI()).toAbsolutePath();
	}


	@Override
	public Path newPath(String path) {
		// TODO Auto-generated method stub
		return new TPath(path).toAbsolutePath();
	}

	
	@Override
	public xURI chooseUri(Component comp, boolean open, 
			String[] exts, String[] descs, xURI curStore) {
		
		ChosenUriResult result = chooseUriResult(
				comp, 
				open, 
				exts, 
				descs,
				curStore);
		
        if (result == null)
			return null;
        
        xURI uri = result.getChosenUri();
        String ext = uri.getLastNameExtension();
        if (open == false && ext == null) {
        	ext = result.getChosenExt();
        	if (ext == null)
        		ext = Constants.DEFAULT_EXT;
        	uri = xURI.create(uri.toString() + "." + ext);
        }
        
        return uri;
	}
	
	
	@Override
	public xURI chooseStore(Component comp) {
		
		TFileChooser uc = newUriChooser();
		
		xURI curStore = getCurrentStore();
		File curDir = null;
		if (curStore == null)
			curDir = null;
		else if (curStore.getScheme() == null)
			curDir = new File(curStore.getURI().toString());
		else
			curDir = new File(curStore.getURI());
		
		if (curDir != null)
			uc.setCurrentDirectory(curDir);
		uc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int ret = uc.showOpenDialog(comp);
        if (ret != JFileChooser.APPROVE_OPTION)
        	return null;
		
        return xURI.create(uc.getSelectedFile());
	}

	
	/**
	 * Creating the file chooser.The most important function of such chooser is to show a graphic user interface (GUI) allowing users to select files they want.
	 * @return File chooser represented by {@link TFileChooser}
	 */
	private TFileChooser newUriChooser() {
		return new TFileChooser();
	}

	
	/**
	 * This method shows a graphic user interface (GUI) allowing users to select files they want. Such GUI is called <i>choice dialog</i>.
	 * Such chosen files are returned as the class {@link ChosenUriResult}.
	 * @param comp The graphic user interface (GUI) component works as a parent component of choice dialog. 
	 * @param open If true then, choice dialog is <i>open</i> dialog allowing users to choose and open files. Otherwise, choice dialog is <i>save</i> dialog allowing users to choose and save files. 
	 * @param exts The specified array of archive (file) extensions which are used to filter objects that users select, for example, &quot;*.hdp&quot;, &quot;*.xls&quot;. Each extension has a description. The respective array of extension descriptions is specified by the parameter {@code descs}.  
	 * @param descs The specified array of descriptions, for example, &quot;Hudup file&quot;, &quot;Excel 97-2003&quot;. Note that each extension has a description and the respective array of file extensions is represented by the parameter {@code exts}. The combination of parameter {@code exts} and parameter {@code descs} forms filters for selection such as &quot;Hudup file (*.hdp)&quot; and &quot;Excel 97-2003 (*.xls)&quot;.
	 * @param mode The specified mode sets the <i>choice dialog</i> to show only archives (files) or only store (directories) or both archives (files) and store (directories). 
	 * @param curDir Current store (directory) to open <i>choice dialog</i>
	 * @return Chosen files are returned as the class {@link ChosenUriResult}
	 */
	private ChosenUriResult chooseUriResult(
			Component comp, 
			boolean open, 
			final String[] exts, 
			final String[] descs,
			xURI curStore) {
		
		TFileChooser uc = newUriChooser(); //This is so-called choice dialog
		if (curStore == null || !exists(curStore))
			curStore = getCurrentStore();
		
		File curDir = null;
		if (curStore == null)
			curDir = null;
		else if (curStore.getScheme() == null)
			curDir = new File(curStore.getURI().toString());
		else
			curDir = new File(curStore.getURI());
		
		if (curDir != null)
			uc.setCurrentDirectory(curDir);
		
		if (exts != null && descs != null) {
			for (int i = 0; i < exts.length; i++) {
				uc.addChoosableFileFilter(new ChosenFileFilter(exts[i], descs[i]));
			}
		}
		
		uc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int ret = open ? 
				uc.showOpenDialog(comp) : uc.showSaveDialog(comp);
        if (ret != JFileChooser.APPROVE_OPTION)
        	return null;
        
        TFile file = uc.getSelectedFile();
        FileFilter filter = uc.getFileFilter();
        if (file == null)
        	return null;
        
        xURI uri = null;
        if (file.isArchive()) {
        	TFile parent = file.getParentFile();
        	uri = xURI.create(parent).concat(file.getName());
        }
        else
        	uri = xURI.create(file.toURI());
        
        if ( (filter != null) && (filter instanceof ChosenFileFilter) )
        	return new ChosenUriResult(uri, ((ChosenFileFilter)filter).getExt());
        else
        	return new ChosenUriResult(uri, null);
	}
	
	
	/**
	 * The class {@link ChosenUriResult} represents chosen files are returned by method {@link UriAssocDefault#chooseUriResult(Component, boolean, String[], String[], xURI)}.
	 * It contains the URI and extension of chosen archive (file).
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private static class ChosenUriResult {
		
		/**
		 * The URI of chosen archive (file).
		 */
		private xURI chosenUri = null;
		
		
		/**
		 * The extension of chosen archive (file).
		 */
		private String chosenExt = null;
		
		
		/**
		 * Constructor with URI and extension of chosen archive (file).
		 * @param chosenFile
		 * @param chosenExt
		 */
		public ChosenUriResult(xURI chosenUri, String chosenExt) {
			this.chosenUri = chosenUri;
			this.chosenExt = chosenExt;
		}
		
		
		/**
		 * Getting the URI of chosen archive (file).
		 * @return chosen {@link File}
		 */
		public xURI getChosenUri() {
			return chosenUri;
		}
		
		
		/**
		 * Getting the extension of chosen archive (file).
		 * @return file extension
		 */
		public String getChosenExt() {
			return chosenExt;
		}
		
	}
	
	
	/**
	 * This class is used for filtering file or directories shown in choice dialog allowing users to choose objects.
	 * This class implements the interface {@link FileFilter} in which the most important method {@link ChosenFileFilter#accept(File)} must be defined for filtering.
	 * If this method returns true, the file will be accepted to be shown in choice dialog. Otherwise, the file is rejected to be shown in choice dialog.
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private static class ChosenFileFilter extends FileFilter {

		/**
		 * The extension of files will be accepted for filtering, for example, &quot;*.hdp&quot;, &quot;*.xls&quot;.
		 */
		private String ext = null;
		
		
		/**
		 * The description of files will be accepted for filtering. Each extension is attached to a particular description, for example, &quot;Hudup file&quot;, &quot;Excel 97-2003&quot;.
		 */
		private String desc = null;
		
		
		/**
		 * Constructor with extension and description of files which will be accepted to be shown in choice dialog. In other words, such files will be filtered.
		 * @param ext Extension of files which will be accepted for filtering
		 * @param desc Description of files which will be accepted for filtering
		 */
		public ChosenFileFilter(String ext, String desc) {
			this.ext = ext;
			this.desc = desc;
		}

		
		
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			
			String ext = xURI.create(f).getLastNameExtension();
			if (ext != null && 
					ext.toLowerCase().equals(this.ext.toLowerCase()))
				return true;
			
			return false;
		}

		
		
		@Override
		public String getDescription() {
			return desc;
		}
	
		
		/**
		 * Getting description of files which will be accepted for filtering.
		 * @return file extension
		 */
		public String getExt() {
			return ext;
		}
		
		
	}

	
}
