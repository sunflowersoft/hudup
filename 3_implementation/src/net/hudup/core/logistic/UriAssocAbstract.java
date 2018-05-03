package net.hudup.core.logistic;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;


/**
 * This class is the abstract URI associator which defines most of simple methods of the interface {@link UriAssoc}.
 * The default URI associator {@link UriAssocDefault} extends from this abstract class.  
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public abstract class UriAssocAbstract implements UriAssoc {

	
	/**
	 * The root store (root directory) of this abstract URI associator.
	 * {@link UriAssocAbstract} uses {@link Path} instead of {@link xURI}. It is possible to consider {@link Path} is particular case of {@link xURI}.
	 */
	protected Path store = null;
	
	
	/**
	 * Default constructor
	 */
	public UriAssocAbstract() {
		
	}
	
	
	/**
	 * Normalizing the specified URI. For example, the URI referring to compressed file contains character &quot;!&quot; and so it is necessary to remove such character &quot;!&quot; from the URI so that we get a normalized URI. 
	 * @param uri specified URI
	 * @return normalized URI
	 */
	private static xURI normalize(xURI uri) {
		if (uri == null)
			return null;
		
		if (DataDriver.isCompressed(uri)) {
			xURI newUri = null;
			try {
				xSubURI subUri = DataDriver.createSubURI(uri);
				String subUriText = subUri.brief.toString();
				subUriText = subUriText.replace("!/", "/");
				subUriText = subUriText.replace("!", "");
				newUri = xURI.create(subUriText);
			}
			catch (Exception e) {
				e.printStackTrace();
				newUri = null;
			}
			
			return newUri;
		}
		else
			return uri;
	}

	
	@Override
	public boolean connect(DataConfig config) {
		// TODO Auto-generated method stub
		close();
		
		xURI uri = normalize(config.getStoreUri());
		Path path = newPath(uri);
		if (Files.isDirectory(path) || isArchive(uri) || DataDriver.isCompressed(config.getStoreUri())) {
			store = path;
			return true;
		}
		else {
			store = path.getParent();
			if (store != null && !Files.exists(store)) {
				try {
					Files.createDirectories(store);
				} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
			
			return true;
		}
	}

	
	@Override
	public boolean exists(xURI uri) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		Path path = newPath(uri);
		return Files.exists(path);
	}

	
	@Override
	public boolean isStore(xURI uri) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		Path path = newPath(uri);
		if (store != null && path.equals(store))
			return true;
		else
			return Files.isDirectory(path);
	}

	
	@Override
	public boolean create(xURI uri, boolean isStore) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		if (exists(uri))
			return false;

		Path path = newPath(uri);
		try {
			if (isStore)
				return Files.createDirectories(path) != null;
			else {
				Files.newOutputStream(path, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.TRUNCATE_EXISTING).close();
				return true;
			}
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	
	@Override
	public boolean clearContent(xURI uri, UriFilter filter) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		if (!exists(uri))
			return false;
		else if (isStore(uri)) {
			List<xURI> uriList = getUriList(uri, filter);
			for (xURI item : uriList) {
				if (isStore(item))
					clearContent(item, filter);
				
				Path path = newPath(item);
				try {
					Files.deleteIfExists(path);
				} 
				catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return true;
		}
		else {
			Path path = newPath(uri);
			try {
				Files.deleteIfExists(path);
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return create(uri, false);
		}
	}

	
	@Override
	public boolean delete(xURI uri, UriFilter filter) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		if (!exists(uri))
			return false;
		if (isStore(uri))
			clearContent(uri, filter);
		
		Path path = newPath(uri);
		try {
			Files.deleteIfExists(path);
			return true;
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	
	@Override
	public boolean copy(xURI src, xURI dst, final boolean moved, final UriFilter filter) {
		// TODO Auto-generated method stub
		src = normalize(src);
		dst = normalize(dst);
		if (isStore(src)) {
			if (!exists(dst))
				create(dst, true);
			else if (!isStore(dst))
				return false;
			
			final xURI finalDst = dst;
			uriListProcess(
					this,
					src, 
					filter, 
					new UriProcessor() {
						
						@Override
						public void uriProcess(xURI uri) throws Exception {
							// TODO Auto-generated method stub
							xURI dstItem = finalDst.concat(uri.getLastName()); 
							copy(uri, dstItem, moved, filter);
						}
					});
			
			if (moved)
				delete(src, null);
			return true;
		}
		else if (exists(dst) && isStore(dst)) {
			xURI dstItem = dst.concat(src.getLastName()); 
			return copy(src, dstItem, moved, filter);
		}
		else {
			Path srcPath = newPath(src);
			Path dstPath = newPath(dst);
			try {
				
				if (moved) {
					if (exists(dst))
						return Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING) != null;
					else
						return Files.move(srcPath, dstPath) != null;
				}
				else {
					if (exists(dst))
						return Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING) != null;
					else
						return Files.copy(srcPath, dstPath) != null;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return false;
		} // end if
		
		
	}


	@Override
	public boolean rename(xURI src, xURI dst) {
		// TODO Auto-generated method stub
		src = normalize(src);
		dst = normalize(dst);
		Path srcPath = newPath(src);
		Path dstPath = newPath(dst);
		
		try {
			return Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING) != null;
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}

	
	@Override
	public List<xURI> getUriList(xURI store, final UriFilter filter) {
		// TODO Auto-generated method stub
		store = normalize(store);
		if (!exists(store))
			return Util.newList();
		
		final List<Path> pathList = Util.newList();

		Path storePath = newPath(store);
		pathListProcess(
			storePath, 
			new DirectoryStream.Filter<Path>() {
				
				@Override
				public boolean accept(Path entry) throws IOException {
					// TODO Auto-generated method stub
					if (filter == null)
						return true;
					else
						return filter.accept(xURI.create(entry));
				}
			},
			
			new PathProcessor() {
				
				@Override
				public void pathProcess(Path path) throws Exception {
					// TODO Auto-generated method stub
					pathList.add(path);
				}
			});
		
		
		List<xURI> uriList = Util.newList();
		for (Path path : pathList)
			uriList.add(xURI.create(path));
		
		return uriList;
	}

	
	@Override
	public InputStream getInputStream(xURI uri) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			return Files.newInputStream(path);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	@Override
	public OutputStream getOutputStream(xURI uri, boolean append) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			if (append)
				return Files.newOutputStream(path, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.APPEND);
			else
				return Files.newOutputStream(path, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.TRUNCATE_EXISTING);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	@Override
	public Reader getReader(xURI uri) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			return Files.newBufferedReader(path, Charset.defaultCharset());
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	@Override
	public Writer getWriter(xURI uri, boolean append) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			if (append)
				return Files.newBufferedWriter(path, Charset.defaultCharset(), 
						StandardOpenOption.CREATE, 
						StandardOpenOption.APPEND);
			else
				return Files.newBufferedWriter(path, Charset.defaultCharset(), 
						StandardOpenOption.CREATE, 
						StandardOpenOption.TRUNCATE_EXISTING);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	@Override
	public ByteChannel getReadChannel(xURI uri) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			return Files.newByteChannel(path, StandardOpenOption.READ);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	@Override
	public ByteChannel getWriteChannel(xURI uri, boolean append) {
		// TODO Auto-generated method stub
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			if (append)
				return Files.newByteChannel(path, 
						StandardOpenOption.WRITE, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.APPEND);
			else
				return Files.newByteChannel(path, 
						StandardOpenOption.WRITE, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.TRUNCATE_EXISTING);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	@Override
	public xURI getStoreOf(xURI uri) {
		uri = normalize(uri);
		Path path = newPath(uri);
		Path parent = path.getParent();
		if (parent == null)
			return null;
		else if (Files.isDirectory(parent))
			return xURI.create(parent);
		else
			return getStoreOf(xURI.create(parent));
	}

	
	/**
	 * Retrieving current store (current directory) such as (.)
	 * @return current store (current directory)
	 */
	public xURI getCurrentStore() {
		return xURI.create(new File("."));
	}
	
	
	/**
	 * Firstly, this static method retrieves a list of URI (s) referring objects inside the specified store with regard to specified filter. For each retrieved URI, some task is processed on the object referred by such URI and so the specified {@link UriProcessor} is responsible for doing such task.
	 * This method also uses the specified URI associator {@link UriAssoc} as the main associator.
	 * @param assoc specified URI associator {@link UriAssoc} as the main associator.
	 * @param store URI of specified store
	 * @param filter Specified filter represented by {@link UriFilter}
	 * @param processor Specified {@link UriProcessor} is responsible for doing such task on the object referred by each retrieved URI
	 */
	public static void uriListProcess(UriAssoc assoc, xURI store, UriFilter filter, UriProcessor processor) {
		store = normalize(store);
		
		if (!assoc.exists(store))
			return;
		
		List<xURI> uriList = assoc.getUriList(store, filter);
		
		for (xURI uri : uriList) {
			
			try {
				
				processor.uriProcess(uri);
				
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
		} // end for
		
	}

	
	/**
	 * This interface is used for processing some task on specified {@link Path}. Please see its method {@link #pathProcess(Path)}.
	 * This interface is the input parameter of the method {@link UriAssocAbstract#pathListProcess(Path, java.nio.file.DirectoryStream.Filter, PathProcessor)} of the abstract URI associator {@link UriAssocAbstract}.
	 * @author Loc Nguyen
	 * @version 11.0
	 *
	 */
	private static interface PathProcessor {

		
		/**
		 * Processing some task on specified path. Programmer is responsible for defining this method according to some purpose.
		 * @param path Specified path represented by {@link Path}. It is possible to consider {@link Path} is particular case of {@link xURI}. 
		 * @throws Exception
		 */
		void pathProcess(Path path) throws Exception;
		
		
	}
	
	
	/**
	 * Firstly, this method retrieves a list of path (s) referring objects inside the specified store with regard to specified filter. For each retrieved path, some task is processed on the object referred by such path and so the specified {@link PathProcessor} is responsible for doing such task.
	 * @param store Specified store represented by {@link Path}. It is possible to consider {@link Path} is particular case of {@link xURI}.
	 * @param filter Specified filter represented by {@link UriFilter}
	 * @param processor Specified {@link PathProcessor} is responsible for doing such task on the object referred by each retrieved path
	 */
	private static void pathListProcess(
			Path store, 
			DirectoryStream.Filter<Path> filter, 
			PathProcessor processor) {
		
		if (!Files.exists(store))
			return;
		
		try {
			if (!Files.isDirectory(store))
				return;
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		DirectoryStream<Path> ds = null;
		try {
			ds = filter != null ? 
					Files.newDirectoryStream(store, filter) : Files.newDirectoryStream(store);
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}
		
		for (Path path : ds) {
			
			try {
				
				processor.pathProcess(path);
				
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
		
		try {
			ds.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	@Override
	public xURI chooseDefaultUri(Component comp, boolean open, xURI curStore) {
		curStore = normalize(curStore);
		
		return chooseUri(
				comp, 
				open, 
				new String[] {
						Constants.DEFAULT_EXT,
						"xls"
					}, 
				new String[] {
						"Hudup file (*." + Constants.DEFAULT_EXT + ")",
						"Excel 97-2003 (*.xls)"
					},
				curStore);
	}
	
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		this.store = null;
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


}
