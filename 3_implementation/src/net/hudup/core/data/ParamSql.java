package net.hudup.core.data;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import net.hudup.core.Util;


/**
 * This class represents a SQL statement having parameters, called {@code parametric SQL}.
 * Recall that SQL is a formal language for accessing relation database.
 * It has two internal variables such as variable {@link #sql} which is a string specifying SQL statement in text and variable {@link #indexes} specifying indices of parameters inside the SQL statement.
 * The parametric SQL statement specified by the internal variable {@link #sql} has many question masks.
 * Each question mark indicates a parameter where real value is filled in when the parametric SQL statement is completed for accessing database.
 * For example, following is the code snippet of update SQL statement on rating unit (rating table) with two parameters:<br>
 * <code>
 * UPDATE rating set rating=5 where userid=? and itemid=?"
 * </code>
 * <br>
 * The two parameters are specified by two question marks and so variable {@link #indexes} is {0, 1}.
 * Such variable {@link #indexes} is called parametric indexer.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ParamSql implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This variable is a string specifying parametric SQL statement in text.
	 * The parametric SQL statement has many question masks.
	 * Each question mark indicates a parameter where real value is filled in when the parametric SQL statement is completed for accessing database.
	 * Indices of these question marks are specified by variable {@link #indexes}.
	 */
	protected String sql = "";
	
	
	/**
	 * This variable called {@code parametric indexer} specifies indices of parameters inside the parametric SQL statement referred by the variable {@link #sql}.
	 * The parametric SQL statement has many question masks.
	 * Each question mark indicates a parameter where real value is filled in when the parametric SQL statement is completed for accessing database.
	 * For ease of understanding, indices of these question marks are specified by this variable.
	 * This variable is a list and so if it is empty, the SQL statement has no parameter.
	 */
	protected List<Integer> indexes = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public ParamSql() {
		if (!(indexes instanceof Serializable))
			throw new RuntimeException("Not serializable class");
	}
	
	
	/**
	 * Constructor with parametric SQL statement.
	 * @param sql parametric SQL statement.
	 */
	public ParamSql(String sql) {
		this();
		setSql(sql);
	}
	
	
	/**
	 * Constructor with specified parametric SQL statement and specified parametric indexer.
	 * @param sql specified parametric SQL statement.
	 * @param indexes specified parametric indexer as a list, indicating indices of parameters inside the parametric SQL statement.
	 */
	public ParamSql(String sql, List<Integer> indexes) {
		this();
		set(sql, indexes);
	}
	
	
	/**
	 * Constructor with specified parametric SQL statement and specified parametric indexer.
	 * @param sql specified parametric SQL statement.
	 * @param indexes specified parametric indexer as an array, indicating indices of parameters inside the parametric SQL statement.
	 */
	public ParamSql(String sql, int[] indexes) {
		this();
		set(sql, indexes);
	}

	
	
	/**
	 * Constructor with specified parametric SQL statement and one specified parametric index.
	 * @param sql specified parametric SQL statement.
	 * @param index specified parametric index.
	 */
	public ParamSql(String sql, int index) {
		this();
		set(sql, index);
	}

	
	
	/**
	 * Getting the parametric SQL statement.
	 * @return parametric SQL statement.
	 */
	public String getSql() {
		return sql;
	}
	
	
	
	/**
	 * Setting the parametric SQL statement.
	 * @param sql specified parametric SQL statement.
	 */
	public void setSql(String sql) {
		this.sql = sql;
	}
	
	
	/**
	 * Getting the parametric indexer which indicates indices of parameters inside the parametric SQL statement.
	 * @return parametric indexer which indicates indices of parameters inside the parametric SQL statement, as a list.
	 */
	public List<Integer> getIndexes() {
		return indexes;
	}

	
	/**
	 * Setting parametric indexer which indicates indices of parameters inside the parametric SQL statement.
	 * @param indexes parametric indexer which indicates indices of parameters inside the parametric SQL statement, as a list.
	 */
	public void setIndexes(List<Integer> indexes) {
		this.indexes.clear();
		this.indexes.addAll(indexes);
	}
	
	
	/**
	 * Setting parametric indexer which indicates indices of parameters inside the parametric SQL statement.
	 * @param indexes parametric indexer which indicates indices of parameters inside the parametric SQL statement, as an array.
	 */
	public void setIndexes(int[] indexes) {
		this.indexes.clear();
		for (int index : indexes)
			this.indexes.add(index);
		
	}

	
	/**
	 * Setting parametric indexer which indicates one parameter index inside the parametric SQL statement.
	 * @param index parametric index which indicates one parameter index inside the parametric SQL statement.
	 */
	public void setIndexes(int index) {
		indexes.clear();
		indexes.add(index);
	}
	
	
	
	/**
	 * Setting parametric SQL statement and specified parametric indexer.
	 * @param sql specified parametric SQL statement.
	 * @param indexes specified parametric indexer as a list, indicating indices of parameters inside the parametric SQL statement.
	 */
	public void set(String sql, List<Integer> indexes) {
		setSql(sql);
		setIndexes(indexes);
	}
	
	
	/**
	 * Setting parametric SQL statement and specified parametric indexer.
	 * @param sql specified parametric SQL statement.
	 * @param indexes specified parametric indexer as an array, indicating indices of parameters inside the parametric SQL statement.
	 */
	public void set(String sql, int[] indexes) {
		setSql(sql);
		setIndexes(indexes);
	}
	
	
	
	/**
	 * Setting parametric SQL statement and specified parametric indexer.
	 * @param sql specified parametric SQL statement.
	 * @param index specified parametric indexer as an integer, indicating one parameter index inside the parametric SQL statement.
	 */
	public void set(String sql, int index) {
		setSql(sql);
		setIndexes(index);
	}

	
	
	/**
	 * Clearing the parametric indexer, which means that making the variable {@link #indexes} be empty list.
	 */
	public void clearIndexes() {
		indexes.clear();
	}
	
	
	/**
	 * Clearing this parametric SQL, which means that making the variable {@link #sql} be empty string and making the variable {@link #indexes} be empty list.
	 */
	public void clear() {
		sql = "";
		indexes.clear();
	}
	
	
	/**
	 * Adding an index into the parametric indexer.
	 * @param index index that is added into the parametric indexer.
	 */
	public void addIndex(int index) {
		indexes.add(index);
	}
	
	
	/**
	 * Converting the parametric indexer into the string, for example &quot;0,1,2&quot;.
	 * @param isIncOne if {@code true}, the first index in the parametric indexer {@link #indexes} is 1. If {@code false}, the first index in the parametric indexer {@link #indexes} is 0.
	 * @return text form of parametric indexer, for example &quot;0,1,2&quot;.
	 */
	public String getIndexText(boolean isIncOne) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < indexes.size(); i++) {
			if (i > 0)
				buffer.append(",");
			if (isIncOne)
				buffer.append(indexes.get(i) + 1);
			else
				buffer.append(indexes.get(i));
		}
		
		return buffer.toString();
	}
	
	
	/**
	 * Creating the SQL statement for database connection.
	 * This method is so special that it will be replaced or removed in the future.
	 * @param conn database connection represented by {@link Connection}.
	 * @return SQL statement for database connection.
	 */
	public PreparedStatement createStatement(Connection conn) {
		try {
			return conn.prepareStatement(sql);
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	
}
