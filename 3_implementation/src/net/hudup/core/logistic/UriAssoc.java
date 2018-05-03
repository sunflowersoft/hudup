package net.hudup.core.logistic;

import java.awt.Component;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.channels.ByteChannel;
import java.nio.file.Path;
import java.util.List;

import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;


/**
 * This interface proposes rules (methods) processing on objects identified by Uniform Resource Identifier (URI) as normal file when URI refers to any resource on net system.
 * For example, creating, copying, renaming, deleting, getting reader, getting writer on objects (for example, files). Each object in {@link UriAssoc} is identified by URI or {@link Path}. It is possible to consider {@link Path} as particular case of URI. Note, URI is represented by {@link xURI} instead of {@link URI}.
 * As a convention, a class that implements this interface are called <i>URI associator</i>.
 * Note, URI associators are utility classes. There are two types of object in URI associator: store (like directory, folder in file system) and archive (like file in file system). Store contains archives and other stores.
 * In general, URI associators are very necessary to input-output operators.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public interface UriAssoc extends AutoCloseable {

	
	/**
	 * Initializing an URI associator by connecting to a URI based on a configuration.
	 * @param config Specified configuration containing a root URI.
	 * @return whether connect successfully
	 */
	boolean connect(DataConfig config);
	
	
	/**
	 * Creating a new {@link Path} from specified URI.
	 * @param uri Specified {@link URI}.
	 * @return {@link Path} according to {@link URI}.
	 */
	Path newPath(xURI uri);
	
	
	/**
	 * Creating a new {@link Path} from specified string of path.
	 * @param path specified string of path.
	 * @return new  {@link Path} from specified string of path.
	 */
	Path newPath(String path);
	
	
	/**
	 * Checking whether the specified URI exists.
	 * @param uri Specified URI.
	 * @return Whether the specified URI exists.
	 */
	boolean exists(xURI uri);
	
	
	/**
	 * Checking whether the specified URI is &quot;store&quot;. The concept &quot;store&quot; is similar to the concept &quot;directory&quot; in file system.
	 * @param uri Specified URI
	 * @return Whether the specified URI is &quot;store&quot;
	 */
	boolean isStore(xURI uri);
	
	
	/**
	 * Checking whether the specified URI is &quot;archive&quot;. The concept &quot;archive&quot; is similar to the concept &quot;file&quot; in file system.
	 * @param uri Specified URI
	 * @return whether specified URI is archive
	 */
	boolean isArchive(xURI uri);
	
	
	/**
	 * Creating an object identified by specified URI. Such object may be &quot;store&quot; or &quot;archive&quot;, dependent on the parameter <i>isStore</i>.
	 * @param uri Specified URI
	 * @param isStore Whether the object is &quot;store&quot; or &quot;archive&quot;
	 * @return Whether creating successfully
	 */
	boolean create(xURI uri, boolean isStore);
	
	
	/**
	 * Clearing content of the object identified by specified URI with regard to specified filter.
	 * @param uri Specified URI
	 * @param filter Specified filter represented by {@link UriFilter}
	 * @return Whether clear content successfully
	 */
	boolean clearContent(xURI uri, UriFilter filter);
	
	
	/**
	 * Deleting the object identified by specified URI with regard to specified filter.
	 * @param uri Specified URI
	 * @param filter Specified filter represented by {@link UriFilter}
	 * @return Whether delete successfully
	 */
	boolean delete(xURI uri, UriFilter filter);
	
	
	/**
	 * Copying the object from source URI to destination URI with regard to specified filter.
	 * @param src Source URI
	 * @param dst Destination URI
	 * @param moved Whether or not moving the object
	 * @param filter Specified filter represented by {@link UriFilter}
	 * @return Whether copy successfully
	 */
	boolean copy(xURI src, xURI dst, boolean moved, UriFilter filter);

	
	/**
	 * Copying and renaming the object from source URI to destination URI.
	 * @param src Source URI
	 * @param dst Destination URI
	 * @return whether rename successfully
	 */
	boolean rename(xURI src, xURI dst);
	
	
	/**
	 * Retrieving the list of URI (s) referring objects inside the specified store with regard to specified filter.
	 * @param store The store containing retrieved objects.
	 * @param filter Specified filter represented by {@link UriFilter}
	 * @return List of URI (s) referring objects inside the specified store
	 */
	List<xURI> getUriList(xURI store, UriFilter filter);
	
	
	/**
	 * Retrieving the input stream of the resource object identified by specified URI. Such input stream is used for reading later.
	 * @param uri Specified URI
	 * @return Input stream of the resource object identified by specified URI
	 */
	InputStream getInputStream(xURI uri);
	
	
	/**
	 * Retrieving the output stream of the object identified by specified URI. Such output stream is used for writing later.
	 * @param uri Specified URI
	 * @param append Whether or not &quot;append&quot; mode is applied for writing. In &quot;append&quot; mode, content is added continuously to the end of the archive (file). Otherwise, the archive (file) is made empty and then content is put into the archive (file) from beginning.  
	 * @return Output stream of the resource object identified by specified URI
	 */
	OutputStream getOutputStream(xURI uri, boolean append);	

	
	/**
	 * Retrieving the {@link Reader} of the resource object identified by specified URI. Such {@link Reader} is used for reading later.
	 * @param uri Specified URI
	 * @return {@link Reader} of the resource object identified by specified URI
	 */
	Reader getReader(xURI uri);

	
	/**
	 * Retrieving the {@link Writer} of the resource object identified by specified URI. Such {@link Writer} is used for writing later.
	 * @param uri specified URI of the resource object.
	 * @param append Whether or not &quot;append&quot; mode is applied for writing. In &quot;append&quot; mode, content is added continuously to the end of the archive (file). Otherwise, the archive (file) is made empty and then content is put into the archive (file) from beginning.  
	 * @return {@link Writer} of the resource object identified by specified URI
	 */
	Writer getWriter(xURI uri, boolean append);

	
	/**
	 * Retrieving the read {@link ByteChannel} of the resource object identified by specified URI. Such read {@link ByteChannel} is used for reading later.
	 * @param uri specified URI of the resource object.
	 * @return read {@link ByteChannel} of the resource object identified by specified URI.
	 */
	ByteChannel getReadChannel(xURI uri);
	
	
	/**
	 * Retrieving the writing {@link ByteChannel} of the resource object identified by specified URI. Such read {@link ByteChannel} is used for reading later.
	 * @param uri specified URI of the resource object.
	 * @param append Whether or not &quot;append&quot; mode is applied for writing. In &quot;append&quot; mode, content is added continuously to the end of the archive (file). Otherwise, the archive (file) is made empty and then content is put into the archive (file) from beginning.
	 * @return writing {@link ByteChannel} of the resource object identified by specified URI.
	 */
	ByteChannel getWriteChannel(xURI uri, boolean append);

	
	/**
	 * Getting the store (directory) of the resource object referred by the specified URI.
	 * @param uri specified URI.
	 * @return store (directory) of the resource object referred by the specified URI.
	 */
	xURI getStoreOf(xURI uri);
	
	
	/**
	 * This method shows a graphic user interface (GUI) allowing users to select object (archive-file, store-directory) they want. Such GUI is called <i>choice dialog</i>.
	 * Such chosen object is returned by its referred URI.
	 * @param comp The graphic user interface (GUI) component works as a parent component of choice dialog.
	 * @param open If true then, choice dialog is <i>open</i> dialog allowing users to choose and open files. Otherwise, choice dialog is <i>save</i> dialog allowing users to choose and save objects.
	 * @param exts The specified array of archive (file) extensions which are used to filter objects that users select, for example, &quot;*.hdp&quot;, &quot;*.xls&quot;. Each extension has a description. The respective array of extension descriptions is specified by the parameter {@code descs}.
	 * @param descs The specified array of descriptions, for example, &quot;Hudup file&quot;, &quot;Excel 97-2003&quot;. Note that each extension has a description and the respective array of file extensions is represented by the parameter {@code exts}.
	 * @param curStore Current store (directory) to open <i>choice dialog</i>
	 * @return URI represented by {@link xURI} of chosen object.
	 */
	xURI chooseUri(Component comp, boolean open, String[] exts, String[] descs, xURI curStore);
	
	
	/**
	 * This method is similar to the method {@link #chooseUri(Component, boolean, String[], String[], xURI)} when it also allows users to select object except that it only lists default objects for choice.
	 * In current implementation, defaults objects are archives (files) having extensions: &quot;*.*&quot; (all files), &quot;*.hdp&quot; (Hudup files), and &quot;*.xls&quot; (97-2003 Excel files). 
	 * @param comp The graphic user interface (GUI) component works as a parent component of choice dialog.
	 * @param open If true then, choice dialog is <i>open</i> dialog allowing users to choose and open files. Otherwise, choice dialog is <i>save</i> dialog allowing users to choose and save objects.
	 * @param curStore Current store (directory) to open <i>choice dialog</i>.
	 * @return URI represented by {@link xURI} of chosen object.
	 */
	xURI chooseDefaultUri(Component comp, boolean open, xURI curStore);
	
	
	/**
	 * This method shows <i>choice dialog</i> allowing users to select only store (directory).
	 * @param comp The graphic user interface (GUI) component works as a parent component of choice dialog.
	 * @return URI represented by {@link xURI} of chosen store (directory).
	 */
	xURI chooseStore(Component comp);
	
	
}
