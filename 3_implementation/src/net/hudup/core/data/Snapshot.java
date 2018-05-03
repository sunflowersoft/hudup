/**
 * 
 */
package net.hudup.core.data;

import net.hudup.core.data.ctx.CTSMultiProfiles;


/**
 * This is abstract class of snapshot. There are two typical {@code Dataset} such as {@code Snapshot} and {@code Scanner}.
 * {@link Snapshot} or scanner is defined as an image of piece of {@code Dataset} and knowledge base ({@code KBase}) at certain time point.
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
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class Snapshot extends AbstractDataset {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public Dataset catchup() {
		// TODO Auto-generated method stub
		return this;
	}
	
	
	/**
	 * This utility method puts a specified rating that a specified user gives on a specified item into rating matrix of snapshot.
	 * @param userId specified user identification (user ID).
	 * @param itemId specified item identification (user ID).
	 * @param rating specified rating.
	 */
	public abstract void putRating(int userId, int itemId, Rating rating);
	
	
	/**
	 * Assigning all built-in variables of the specified snapshot into variables of this snapshot.
	 * In other words, this snapshot is a wrapper of variables in specified snapshot. So two snapshots are identical.
	 * There are only reference assignments and so there is no deep cloning. 
	 * @param snapshot specified snapshot.
	 */
	public abstract void assign(Snapshot snapshot);
	
	
	/**
	 * Every context template represented by {@code ContextTemplate} interface owns a profile table and each value represented by {@code ContextValue} of template corresponds to a row in this table.
	 * Each row in such template profile table is represented by {@code Profile} class.
	 * A class that implements this interface {@code CTSMultiProfiles} contains all profiles of all templates.
	 * This method returns the built-in {@code CTSMultiProfiles} of this snapshot.
	 * @return built-in {@link CTSMultiProfiles} of this snapshot.
	 */
	public abstract CTSMultiProfiles getCTSMultiProfiles();
	
	
}
