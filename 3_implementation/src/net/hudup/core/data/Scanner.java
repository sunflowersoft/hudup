/**
 * 
 */
package net.hudup.core.data;


/**
 * This is abstract class of scanner. There are two typical {@code Dataset} such as {@code Snapshot} and {@link Scanner}.
 * {@code Snapshot} or scanner is defined as an image of piece of {@code Dataset} and knowledge base ({@code KBase}) at certain time point.
 * This image is stored in share memory for fast access because it takes long time to access data and knowledge stored in hard disk.
 * The difference between {@code Snapshot} and {@code Scanner} that {@code Snapshot} copies whole piece of data into memory while scanner is merely a reference to such data piece.
 * {@code Snapshot} consumes much more memory but gives faster access and is more convenient.
 * {@code Snapshot} and {@code Scanner} are read-only objects because they provide only read operator.<br>
 * Another additional {@code Dataset} is {@code Pointer}.
 * {@code Pointer} does not contain any information nor provide any access means to dataset.
 * It only points to another {@code Snapshot} or {@code Scanner}.<br>
 * Although you can create your own {@code Dataset}, {@code Dataset} is often retrieved from utility class parsers that implement interface {@code DatasetParser}.
 * {@code Snapshot} is retrieved from {@code SnapshotParser}.
 * {@code Scanner} is retrieved from {@code ScannerParser}. Both {@code SnapshotParser} and {@code ScannerParser} implement interface {@code DatasetParser}.
 * {@code Pointer} is retrieved from {@code Indicator}. {@code Indicator} is {@code DatasetParser} specified to create {@code Pointer}.
 * {@code Scanner} uses {@link Provider} to retrieve information from database because {@code Scanner} does not store information in memory.
 * Note that {@code Provider}  provides most of read-write data operations (get, insert, update, delete).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class Scanner extends AbstractDataset {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * {@code Scanner} uses {@link Provider} to retrieve information from database because {@code Scanner} does not store information in memory.
	 * This method return the internal provider of scanner.
	 * @return internal provider of scanner.
	 */
	public abstract Provider getProvider();
	
	
	
}
