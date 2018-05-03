/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;

import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplateSchema;

/**
 * This is abstract class of pointer. There are two typical {@link Dataset} such as {@link Snapshot} and {@link Scanner}.
 * {@link Snapshot} or scanner is defined as an image of piece of {@link Dataset} and knowledge base ({@code KBase}) at certain time point.
 * This image is stored in share memory for fast access because it takes long time to access data and knowledge stored in hard disk.
 * The difference between {@link Snapshot} and {@link Scanner} that {@link Snapshot} copies whole piece of data into memory while scanner is merely a reference to such data piece.
 * {@link Snapshot} consumes much more memory but gives faster access and is more convenient.
 * {@link Snapshot} and {@link Scanner} are read-only objects because they provide only read operator.<br>
 * Another additional {@link Dataset} is {@link Pointer}.
 * {@link Pointer} does not contain any information nor provide any access means to dataset.
 * It only points to another {@link Snapshot}, {@link Scanner}, or {@code KBase}.<br>
 * Although you can create your own {@link Dataset}, {@link Dataset} is often retrieved from utility class parsers that implement interface {@code DatasetParser}.
 * {@link Snapshot} is retrieved from {@code SnapshotParser}.
 * {@link Scanner} is retrieved from {@code ScannerParser}. Both {@code SnapshotParser} and {@code ScannerParser} implement interface {@code DatasetParser}.
 * {@link Pointer} is retrieved from {@code Indicator}. {@code Indicator} is {@code DatasetParser} specified to create {@link Pointer}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class Pointer extends AbstractDataset {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public Pointer() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public Fetcher<Integer> fetchUserIds() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public int getUserId(Serializable externalUserId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public int getItemId(Serializable externalItemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Rating getRating(int userId, int itemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingVector getUserRating(int userId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingVector getItemRating(int itemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingMatrix createUserMatrix() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingMatrix createItemMatrix() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile getUserProfile(int userId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public AttributeList getUserAttributes() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile getItemProfile(int itemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public AttributeList getItemAttributes() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile profileOf(Context context) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Dataset catchup() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Dataset selectByContexts(ContextList contexts) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}
	
	
}
