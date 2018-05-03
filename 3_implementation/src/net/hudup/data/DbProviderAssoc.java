package net.hudup.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.Keys;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderAssocAbstract;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.xURI;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DbProviderAssoc extends ProviderAssocAbstract {

	
	/**
	 * 
	 */
	protected Connection conn = null;
	
	
	/**
	 * 
	 * @param config
	 */
	public DbProviderAssoc(DataConfig config) {
		super(config);
		this.conn = DbProviderAssoc.createConnection(config);
	}

	
	/**
	 * 
	 * @return {@link Connection}
	 */
	public Connection getConnection() {
		return conn;
	}
	
	
	/**
	 * 
	 * @param type
	 * @return SQL type of {@link Type}
	 */
	public static int toSqlType(Type type) {
		switch (type) {
		case bit:
			return Types.INTEGER; //Types.BIT
		case nominal:
			return Types.INTEGER;
		case integer:
			return Types.INTEGER;
		case real:
			return Types.REAL;
		case string:
			return Types.VARCHAR;
		case date:
			return Types.DATE;
		case object:
			return Types.JAVA_OBJECT;
		default:
			return Types.JAVA_OBJECT;
		}
		
		
	}

	
	/**
	 * 
	 * @param type
	 * @return SQL type name of {@link Type}
	 */
	public String toSqlTypeName(Type type) {
		int sqlType = toSqlType(type);
		String sqlTypeName = "";
		
		try {
			DatabaseMetaData meta = conn.getMetaData();
		    ResultSet rs = meta.getTypeInfo();
		    
		    while (rs.next()) {
		    	int dbType = rs.getInt("DATA_TYPE");
		    	
		    	if (dbType == sqlType) {
		    		sqlTypeName = rs.getString("TYPE_NAME");
		    		break;
		    	}
		    }
		    
		    if (!sqlTypeName.isEmpty()) {
		    	if(type == Type.string)
		    		sqlTypeName += "(256)";
		    }
		    
		    rs.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return sqlTypeName;
	}

	
	/**
	 * 
	 * @param sqlType
	 * @return {@link Type} from SQL type
	 */
	public static Attribute.Type fromSqlType(int sqlType) {
		if (sqlType == Types.ARRAY) //java.lang.reflect.Array
			return Attribute.Type.object;
		else if (sqlType == Types.BIGINT) //Long
			return Attribute.Type.integer;
		else if (sqlType == Types.BINARY) //String, byte[]
			return Attribute.Type.object;
		else if (sqlType == Types.BIT) //Boolean
			return Attribute.Type.bit;
		else if (sqlType == Types.BLOB) // java.sql.Blob
			return Attribute.Type.object;
		else if (sqlType == Types.BOOLEAN) // Boolean
			return Attribute.Type.bit;
		else if (sqlType == Types.CHAR) // String
			return Attribute.Type.string;
		else if (sqlType == Types.CLOB) //java.sql.Clob
			return Attribute.Type.object;
		else if (sqlType == Types.DATALINK)
			return Attribute.Type.object; 
		else if (sqlType == Types.DATE)  // java.sql.Date
			return Attribute.Type.date; 
		else if (sqlType == Types.DECIMAL)  // Double
			return Attribute.Type.real;
		else if (sqlType == Types.DISTINCT)
			return Attribute.Type.object;
		else if (sqlType == Types.DOUBLE) // Double
			return Attribute.Type.real;
		else if (sqlType == Types.FLOAT) // Float
			return Attribute.Type.real;
		else if (sqlType == Types.INTEGER) // Integer
			return Attribute.Type.integer;
		else if (sqlType == Types.JAVA_OBJECT) // Object
			return Attribute.Type.object;
		else if (sqlType == Types.LONGNVARCHAR) // String
			return Attribute.Type.string;
		else if (sqlType == Types.LONGVARBINARY) // String, byte[]
			return Attribute.Type.object;
		else if (sqlType == Types.LONGVARCHAR) // String
			return Attribute.Type.string;
		else if (sqlType == Types.NULL)
			return Attribute.Type.object;
		else if (sqlType == Types.NUMERIC) // Double
			return Attribute.Type.real;
		else if (sqlType == Types.OTHER)
			return Attribute.Type.object;
		else if (sqlType == Types.REAL) // Double
			return Attribute.Type.real;
		else if (sqlType == Types.REF) //java.sql.Ref
			return Attribute.Type.real;
		else if (sqlType == Types.SMALLINT) // Integer
			return Attribute.Type.integer;
		else if (sqlType == Types.STRUCT) // java.sql.Struct
			return Attribute.Type.object;
		else if (sqlType == Types.TIME) // java.sql.Time
			return Attribute.Type.date;
		else if (sqlType == Types.TIMESTAMP) // java.sql.Timestamp
			return Attribute.Type.date;
		else if (sqlType == Types.TINYINT) // Integer
			return Attribute.Type.integer;
		else if (sqlType == Types.VARBINARY) // String, byte[]
			return Attribute.Type.object;
		else if (sqlType == Types.VARCHAR)
			return Attribute.Type.string;
		else
			return Attribute.Type.object;
			
	}

	
	/**
	 * 
	 * @param string
	 * @return normalized DBMS SQL string
	 */
	public String norm(String string) {
		xURI store = config.getStoreUri();
		if (store == null)
			return "\"" + string + "\"";
		
		DataDriver driver = DataDriver.create(store);
		if (driver.getInnerClassName().equals(DataDriver.MYSQL_DRIVER_CLASS_NAME))
			return "`" + string + "`";
		else
			return "\"" + string + "\"";
	}
	
	
	/**
	 * 
	 * @param list
	 * @return condition text of list of {@link Attribute}
	 */
	private String toAndConditionText(List<Attribute> list) {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				buffer.append(" and ");
			
			buffer.append(norm(list.get(i).getName()) + " = ?");
		}
		
		return buffer.toString();
	}
	
	
	/**
	 * 
	 * @param list
	 * @return list of indices of attributes
	 */
	private List<Integer> getIndexes(List<Attribute> list) {
		List<Integer> indexes = Util.newList();
		
		for (int i = 0; i < list.size(); i++) {
			indexes.add(list.get(i).getIndex());
		}
		
		return indexes;
	}

	
	/**
	 * 
	 * @return create SQL
	 */
	public String genConfigCreateSql() {
		return "create table " + norm(config.getConfigUnit()) + " ( " + 
				norm(DataConfig.ATTRIBUTE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				"primary key (" + norm(DataConfig.ATTRIBUTE_FIELD) + ") )";
	}
	
	
	/**
	 * 
	 * @param att
	 * @return select SQL
	 */
	public String genConfigSelectSql(String att) {
		return  "select * from " + norm(config.getConfigUnit()) + 
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String genConfigSelectSql() {
		return "select * from " + norm(config.getConfigUnit());
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genMinRatingSelectSql() {
		return "select " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " from " + norm(config.getConfigUnit()) + 
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + DataConfig.MIN_RATING_FIELD + "'";
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genMaxRatingSelectSql() {
		return "select " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " from " + norm(config.getConfigUnit()) + 
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + DataConfig.MAX_RATING_FIELD + "'";
	}

	
	/**
	 * 
	 * @param att
	 * @param value
	 * @return insert SQL
	 */
	public String genConfigInsertSql(String att, String value) {
		return 	"insert into " + norm(config.getConfigUnit()) + 
				" (" + norm(DataConfig.ATTRIBUTE_FIELD) + ", " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + ")" +
				" values ('" + att + "', '" + value + "')";
	}

	
	/**
	 * 
	 * @return insert SQL
	 */
	public ParamSql genConfigInsertSql() {
		return new ParamSql(
				"insert into " + norm(config.getConfigUnit()) + 
				" (" + norm(DataConfig.ATTRIBUTE_FIELD) + ", " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + ")" +
				" values (?, ?)",
				new int[] {0, 1});
	}

	
	/**
	 * 
	 * @param att
	 * @param value
	 * @return update SQL
	 */
	public String genConfigUpdateSql(String att, String value) {
		return 	"update " + norm(config.getConfigUnit()) + 
				" set " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " = '" + value + "' " +
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}

	
	/**
	 * 
	 * @return update SQL
	 */
	public ParamSql genConfigUpdateSql() {
		return new ParamSql(
				"update " + norm(config.getConfigUnit()) + 
				" set " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " = ?" + 
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = ?",
				new int[] {1, 0});
	}

	
	/**
	 * 
	 * @param att
	 * @return delete SQL
	 */
	public String genConfigDeleteSql(String att) {
		return 
			"delete from " + norm(config.getConfigUnit()) + " where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}
	
	
	/**
	 * 
	 * @return delete SQL
	 */
	public ParamSql genConfigDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getConfigUnit()) + " where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = ?",
				new int[] {0});
	}

	
	/**
	 * 
	 * @return create SQL
	 */
	public String genRatingCreateSql() {
		return 	"create table " + norm(config.getRatingUnit()) + " ( " + 
				norm(DataConfig.USERID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.ITEMID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.RATING_FIELD) + " " + toSqlTypeName(Type.real) + " not null, " +
				norm(DataConfig.RATING_DATE_FIELD) + " " + toSqlTypeName(Type.date) + ", " +
				"primary key (" + norm(DataConfig.USERID_FIELD) + ", " + norm(DataConfig.ITEMID_FIELD) + ") )";
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genRatingSelectSql() {
		return "select * from " + norm(config.getRatingUnit());
	}


	/**
	 * 
	 * @return insert SQL
	 */
	public ParamSql genRatingInsertSql() {
		return new ParamSql(
				"insert into " + norm(config.getRatingUnit()) + 
				" (" + norm(DataConfig.USERID_FIELD) + ", " + norm(DataConfig.ITEMID_FIELD) + ", " + norm(DataConfig.RATING_FIELD) + ", " + norm(DataConfig.RATING_DATE_FIELD) + 
				") values (?, ?, ?, ?)",
				new int[] {0, 1, 2, 3});
	}

	
	/**
	 * 
	 * @return update SQL
	 */
	public ParamSql genRatingUpdateSql() {
		return new ParamSql(
				"update " + norm(config.getRatingUnit()) + 
			   " set " + norm(DataConfig.RATING_FIELD) + " = ?" + ", " + norm(DataConfig.RATING_DATE_FIELD) + " = ?" +
			   " where " + norm(DataConfig.USERID_FIELD) + " = ?" + 
			   " and " + norm(DataConfig.ITEMID_FIELD) + " = ?",
			   new int[] {2, 3, 0, 1});
	}

	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return delete SQL
	 */
	public String genRatingDeleteSql(int userId, int itemId) {
		return 
			"delete from " + norm(config.getRatingUnit()) + 
			" where " + norm(DataConfig.USERID_FIELD) + " = " + userId + " and " + 
						norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}
	
	
	/**
	 * 
	 * @return delete SQL
	 */
	public ParamSql genRatingDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getRatingUnit()) + 
				" where " + norm(DataConfig.USERID_FIELD) + " = ?" + " and " + norm(DataConfig.ITEMID_FIELD) + " = ?",
				new int[] {0, 1});
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String genUserRatingSelectSql(int userId) {
		return genRatingSelectSql() + " where " + norm(DataConfig.USERID_FIELD) + " = " + userId;
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genUserIdsRatingSelectSql() {
		return "select distinct " + norm(DataConfig.USERID_FIELD) + " from " + norm(config.getRatingUnit());
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return select SQL
	 */
	public String genUserItemRatingSelectSql(int userId, int itemId) {
		return genRatingSelectSql() + " where " + 
				norm(DataConfig.USERID_FIELD) + " = " + userId + " and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}
	
	
	/**
	 * 
	 * @param userId
	 * @return delete SQL
	 */
	public String genUserRatingDeleteSql(int userId) {
		return "delete from " + norm(config.getRatingUnit()) + " where " + norm(DataConfig.USERID_FIELD) + " = " + userId;
		
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genUserIdsProfileSelectSql() {
		return "select " + norm(DataConfig.USERID_FIELD) + " from " + norm(config.getUserUnit());
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genMaxUserIdProfileSelectSql() {
		return "select max(" + norm(DataConfig.USERID_FIELD) + ") from " + norm(config.getUserUnit());
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String genUserProfileSelectSql() {
		return "select * from " + norm(config.getUserUnit());
	}
	
	
	/**
	 * 
	 * @param userId
	 * @return select SQL
	 */
	public String genUserProfileSelectSql(int userId) {
		return genUserProfileSelectSql() + " where " + norm(DataConfig.USERID_FIELD) + " = " + userId;
	}
	
	
	/**
	 * 
	 * @param userId
	 * @return delete SQL
	 */
	public String genUserProfileDeleteSql(int userId) {
		return 
			"delete from " + norm(config.getUserUnit()) + " where " + norm(DataConfig.USERID_FIELD) + " = " + userId; 
	}

	
	/**
	 * 
	 * @return delete SQL
	 */
	public ParamSql genUserProfileDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getUserUnit()) + " where " + norm(DataConfig.USERID_FIELD) + " = ?",
				new int[] {0}); 
	}

	
	/**
	 * 
	 * @param itemId
	 * @return select SQL
	 */
	public String genItemRatingSelectSql(int itemId) {
		return genRatingSelectSql() + " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genItemIdsRatingSelectSql() {
		return "select distinct " + norm(DataConfig.ITEMID_FIELD) + " from " + norm(config.getRatingUnit());
	}

	
	/**
	 * 
	 * @param itemId
	 * @return delete SQL
	 */
	public String genItemRatingDeleteSql(int itemId) {
		return "delete from " + norm(config.getRatingUnit()) + " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
		
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String genItemIdsProfileSelectSql() {
		return "select " + norm(DataConfig.ITEMID_FIELD) + " from " + norm(config.getItemUnit());
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String genMaxItemIdProfileSelectSql() {
		return "select max(" + norm(DataConfig.ITEMID_FIELD) + ") from " + norm(config.getItemUnit());
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genItemProfileSelectSql() {
		return "select * from " + norm(config.getItemUnit());
	}

	
	/**
	 * 
	 * @param itemId
	 * @return select SQL
	 */
	public String genItemProfileSelectSql(int itemId) {
		return genItemProfileSelectSql() + " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}

	
	/**
	 * 
	 * @return delete SQL
	 */
	public String genItemProfileDeleteSql(int itemId) {
		return 
			"delete from " + norm(config.getItemUnit()) + " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId; 
	}

	
	/**
	 * 
	 * @return delete SQL
	 */
	public ParamSql genItemProfileDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getItemUnit()) + " where " + norm(DataConfig.ITEMID_FIELD) + " = ?",
				new int[] {0}); 
	}

	
	/**
	 * 
	 * @return create SQL
	 */
	public String genNominalCreateSql() {
		return "create table " + norm(config.getNominalUnit()) + " ( " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.NOMINAL_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + " " + toSqlTypeName(Type.integer) + ", " +
				"primary key (" + 
					norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + ", " + 
					norm(DataConfig.ATTRIBUTE_FIELD) + ", " + 
					norm(DataConfig.NOMINAL_INDEX_FIELD) + ") )";
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genNominalSelectSql() {
		return "select * from " + norm(config.getNominalUnit());
	}

	
	/**
	 * 
	 * @param refUnit
	 * @param att
	 * @return select SQL
	 */
	public String genNominalSelectSql(String refUnit, String att) {
		return 
			"select * from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}
	
	
	/**
	 * 
	 * @param refUnit
	 * @param att
	 * @param nominal_index
	 * @return select SQL
	 */
	public String genNominalSelectSql(String refUnit, String att, int nominal_index) {
		return 
			"select * from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "' and " + 
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " = " + nominal_index;
	}
	
	
	/**
	 * 
	 * @param refUnit
	 * @param att
	 * @param nominal_index
	 * @param nominal_value
	 * @param parent_index
	 * @return insert SQL
	 */
	public String genNominalInsertSql(
			String refUnit, String att, int nominal_index, String nominal_value, int parent_index) {
		
		return 
			"insert into " + norm(config.getNominalUnit()) + " (" + 
			norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + ", " + 
			norm(DataConfig.ATTRIBUTE_FIELD) + ", " + 
			norm(DataConfig.NOMINAL_INDEX_FIELD) + ", " + 
			norm(DataConfig.NOMINAL_VALUE_FIELD) + ", " + 
			norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + ") values ("+ 
			"'" + refUnit + "', '" + att + "', " + nominal_index + ", '" + nominal_value + "', " + parent_index + ")";
	}
	
	
	/**
	 * 
	 * @return insert SQL
	 */
	public ParamSql genNominalInsertSql() {
		
		return new ParamSql(
				"insert into " + norm(config.getNominalUnit()) + " (" + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + ", " + 
				norm(DataConfig.ATTRIBUTE_FIELD) + ", " + 
				norm(DataConfig.NOMINAL_INDEX_FIELD) + ", " + 
				norm(DataConfig.NOMINAL_VALUE_FIELD) + ", " + 
				norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + ") values (?, ?, ?, ?, ?)",
				new int[] {0, 1, 2, 3, 4});
		
	}

	
	/**
	 * 
	 * @param refUnit
	 * @param att
	 * @param nominal_index
	 * @param nominal_value
	 * @param parent_index
	 * @return update SQL
	 */
	public String genNominalUpdateSql(
			String refUnit, String att, int nominal_index, String nominal_value, int parent_index) {
		return 
			"update " + norm(config.getNominalUnit()) + " set " + 
				norm(DataConfig.NOMINAL_VALUE_FIELD) + " = '" + nominal_value + "', " +
				norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + " = " + parent_index + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " + 
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "' and " + 
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " = " + nominal_index;
	}


	/**
	 * 
	 * @return update SQL
	 */
	public ParamSql genNominalUpdateSql() {
		return new ParamSql(
				"update " + norm(config.getNominalUnit()) + " set " + 
					norm(DataConfig.NOMINAL_VALUE_FIELD) + " = ?, " +
					norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + " = ?" + 
				" where " + 
					norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = ? and " + 
					norm(DataConfig.ATTRIBUTE_FIELD) + " = ? and " + 
					norm(DataConfig.NOMINAL_INDEX_FIELD) + " = ?",
				new int[] {3, 4, 0, 1, 2});
	}

	
	/**
	 * 
	 * @param refUnit
	 * @param att
	 * @param nominal_index
	 * @return delete SQL
	 */
	public String genNominalDeleteSql(String refUnit, String att, int nominal_index) {
		return 
			"delete from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "' and " +
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " = " + nominal_index;
	}
	
	
	/**
	 * 
	 * @param refUnit
	 * @param att
	 * @return delete SQL
	 */
	public String genNominalDeleteSql(String refUnit, String att) {
		return 
			"delete from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}

	
	/**
	 * 
	 * @param refUnit
	 * @return delete SQL
	 */
	public String genNominalDeleteSql(String refUnit) {
		return 
			"delete from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "'";
	}

	
	/**
	 * 
	 * @return delete SQL
	 */
	public ParamSql genNominalDeleteSql() {
		return new ParamSql(
			"delete from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = ? and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = ? and " +
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " = ?",
			new int[] {0, 1, 2});
	}

	
	/**
	 * 
	 * @return create SQL
	 */
	public String genAttributeMapCreateSql() {
		return "create table " + norm(config.getAttributeMapUnit()) + " ( " + 
				norm(DataConfig.INTERNAL_UNIT_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.EXTERNAL_UNIT_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				"primary key (" + 
					norm(DataConfig.INTERNAL_UNIT_FIELD) + ", " + 
					norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + ", " + 
					norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + ") )";
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genAttributeMapNoParamSelectSql() {
		return "select * from " + norm(config.getAttributeMapUnit());
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public ParamSql genAttributeMapSelectSql() {
		return new ParamSql(
			"select * from " + norm(config.getAttributeMapUnit()) + 
			" where " + 
				norm(DataConfig.INTERNAL_UNIT_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? ",
			new int[] {0, 1, 2});
	}
	
	
	/**
	 * 
	 * @return select SQL by external values
	 */
	public ParamSql genAttributeMapSelectSqlByExternal() {
		return new ParamSql(
			"select * from " + norm(config.getAttributeMapUnit()) + 
			" where " + 
				norm(DataConfig.EXTERNAL_UNIT_FIELD) + " = ? and " + 
				norm(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD) + " = ? and " + 
				norm(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? ",
			new int[] {0, 1, 2});
	}

	
	/**
	 * 
	 * @return insert SQL
	 */
	public ParamSql genAttributeMapInsertSql() {
		return new ParamSql(
			"insert into " + norm(config.getAttributeMapUnit()) + " (" +
				norm(DataConfig.INTERNAL_UNIT_FIELD) + ", " +
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + ", " +
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + ", " +
				norm(DataConfig.EXTERNAL_UNIT_FIELD) + ", " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD) + ", " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD) + ") values (?, ?, ?, ?, ?, ?)",
			new int[] {0, 1, 2, 3, 4, 5});
		
	}
	
	
	/**
	 * 
	 * @return update SQL
	 */
	public ParamSql genAttributeMapUpdateSql() {
		return new ParamSql("update " + norm(config.getAttributeMapUnit()) + 
			" set " + 
				norm(DataConfig.EXTERNAL_UNIT_FIELD) + " = ?, " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD) + " = ?, " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? " +
			" where " + 
				norm(DataConfig.INTERNAL_UNIT_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? ",
			new int[] {3, 4, 5, 0, 1, 2});
		
	}
	
	
	/**
	 * 
	 * @return delete SQL
	 */
	public ParamSql genAttributeMapDeleteSql() {
		return new ParamSql("delete from " + norm(config.getAttributeMapUnit()) + 
			" where " + 
				norm(DataConfig.INTERNAL_UNIT_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? ",
			new int[] {0, 1, 2});
		
	}

	
	/**
	 * 
	 * @param internalUnit
	 * @return delete SQL
	 */
	public String genAttributeMapDeleteSql(String internalUnit) {
		return 
			"delete from " + norm(config.getAttributeMapUnit()) + 
			" where " + norm(DataConfig.INTERNAL_UNIT_FIELD) + " = '" + internalUnit + "'";
		
	}

	
	/**
	 * 
	 * @return create SQL
	 */
	public String genAccountCreateSql() {
		return "create table " + norm(config.getAccountUnit()) + " ( " + 
				norm(DataConfig.ACCOUNT_NAME_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				"primary key (" + norm(DataConfig.ACCOUNT_NAME_FIELD) + ") )";
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String genAccountSelectSql() {
		return "select * from " + norm(config.getAccountUnit());
	}
	
	
	/**
	 * 
	 * @return select SQL
	 */
	public String genAccountSelectSql(String accName) {
		return	"select * from " + norm(config.getAccountUnit()) + 
				" where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = '" + accName + "'";
	}

	
	/**
	 * 
	 * @param accName
	 * @param accPwd
	 * @param privs
	 * @return insert SQL
	 */
	public String genAccountInsertSql(String accName, String accPwd, int privs) {
		return 
			"insert into " + norm(config.getAccountUnit()) + 
			" (" + norm(DataConfig.ACCOUNT_NAME_FIELD) + ", " + norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + ", " + norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + ")" +
			" values ('" + accName + "', '" + accPwd + "', '" + privs + "')";
	}

	
	/**
	 * 
	 * @return insert SQL
	 */
	public ParamSql genAccountInsertSql() {
		return new ParamSql(
				"insert into " + norm(config.getAccountUnit()) + 
				" (" + norm(DataConfig.ACCOUNT_NAME_FIELD) + ", " + norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + ", " + norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + ")" +
				" values (?, ?, ?)",
				new int[] {0, 1, 2});
	}

	
	/**
	 * 
	 * @param accName
	 * @param accPwd
	 * @param privs
	 * @return update SQL
	 */
	public String genAccountUpdateSql(String accName, String accPwd, int privs) {
		return 	
			"update " + norm(config.getAccountUnit()) + 
			" set " + norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + " = '" + accPwd + "', " + 
			norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + " = '" + privs + "'" + 
			" where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = '" + accName + "'";
	}

	
	/**
	 * 
	 * @return update SQL
	 */
	public ParamSql genAccountUpdateSql() {
		return new ParamSql("update " + norm(config.getAccountUnit()) + 
				" set " + norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + " = ?, " + 
				norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + " = ?" + 
				" where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = ?",
				new int[] {1, 2, 0});
	}

	
	/**
	 * 
	 * @return delete SQL
	 */
	public String genAccountDeleteSql(String accName) {
		return 
			"delete from " + norm(config.getAccountUnit()) + " where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = '" + accName + "'";
	}

	
	/**
	 * 
	 * @return delete SQL
	 */
	public ParamSql genAccountDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getAccountUnit()) + " where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = ?",
				new int[] {0});
	}

	
	/**
	 * 
	 * @param profileUnit
	 * @param attList
	 * @return create SQL
	 */
	public String genProfileCreateSql(String profileUnit, AttributeList attList) {
		Keys keys = attList.getKeys();
		
		StringBuffer sql = new StringBuffer();
		sql.append("create table " + norm(profileUnit) + " (");
		
		for (int i = 0; i < attList.size(); i++) {
			Attribute att = attList.get(i);
			if (i > 0)
				sql.append(", ");
			
			if (keys.contains(att))
				sql.append(norm(att.getName()) + " " + toSqlTypeName(att.getType()) + " not null");
			else
				sql.append(norm(att.getName()) + " " + toSqlTypeName(att.getType()));
			
			if (att.isAutoInc())
				sql.append(" AUTO_INCREMENT");
		}
		
		if (keys.size() == 0)
			sql.append(" )");
		else {
			sql.append(", primary key (");
			for (int i = 0; i < keys.size(); i++) {
				Attribute key = keys.get(i);
				
				if (i > 0)
					sql.append(", ");
				sql.append( norm(key.getName()) );
			}
			sql.append(") )");
		}
		
		return sql.toString();
	}
	
	
	/**
	 * 
	 * @param profileUnit
	 * @return select SQL
	 */
	public String genProfileSelectSql(String profileUnit) {
		return "select * from " + norm(profileUnit);
	}
	

	/**
	 * 
	 * @param profileUnit
	 * @param conditionAttList
	 * @return select SQL
	 */
	public ParamSql genProfileSelectSql(String profileUnit, AttributeList conditionAttList) {
		Keys keys = conditionAttList.getKeys();
		
		if (keys.size() > 0) 
			return new ParamSql("select * from " + norm(profileUnit) + " where " + 
				toAndConditionText(keys.getList()),
				getIndexes(keys.getList()));
		else
			return new ParamSql("select * from " + norm(profileUnit) + " where " + 
				toAndConditionText(conditionAttList.getList()),
				getIndexes(conditionAttList.getList()) );
	}

	
	/**
	 * 
	 * @param profileUnit
	 * @param condition
	 * @return {@link ParamSql}
	 */
	public ParamSql genProfileSelectSql2(String profileUnit, Profile condition) {
		Keys keys = condition.getNotMissingAtts();
		
		if (keys.size() > 0) 
			return new ParamSql("select * from " + norm(profileUnit) + " where " + 
				toAndConditionText(keys.getList()),
				getIndexes(keys.getList()));
		else
			return new ParamSql(genProfileSelectSql(profileUnit), new int[] { });
	}

	
	/**
	 * 
	 * @param profileUnit
	 * @param profileIdName
	 * @return select SQL
	 */
	public String genProfileIdsSelectSql(String profileUnit, String profileIdName) {
		return "select " + norm(profileIdName) + " from " + norm(profileUnit);
	}
	
	
	/**
	 * 
	 * @param profileUnit
	 * @param profileIdName
	 * @return select SQL
	 */
	public String genMaxIdProfileSelectSql(String profileUnit, String profileIdName) {
		return "select max(" + norm(profileIdName) + ") from " + norm(profileUnit);
	}
	
	
	/**
	 * 
	 * @param profileUnit
	 * @param attList
	 * @return insert SQL
	 */
	public ParamSql genProfileInsertSql(
			String profileUnit, 
			AttributeList attList) {
		
		int n = attList.size();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into " + norm(profileUnit) + " (");
		List<Integer> indexes = Util.newList();
		
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sql.append(", ");
			
			Attribute att = attList.get(i);
			sql.append(norm(att.getName()));
			if (!att.isAutoInc())
				indexes.add(att.getIndex());
		}
		
		sql.append(") values (" );
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sql.append(", ");
			
			Attribute att = attList.get(i);
			if (!att.isAutoInc())
				sql.append("?");
			else
				sql.append("NULL");
		}
		sql.append(")");
		
		return new ParamSql(sql.toString(), indexes);
	}


	/**
	 * 
	 * @param profileUnit
	 * @param conditionAttList
	 * @return update SQL
	 */
	public ParamSql genProfileUpdateSql(
			String profileUnit, 
			AttributeList conditionAttList) {

		Keys keys = conditionAttList.getKeys();
		
		StringBuffer sql = new StringBuffer("update " + norm(profileUnit) + " set ");
		List<Integer> indexes = Util.newList();
		
		int k = 0;
		int n = conditionAttList.size();
		for (int i = 0; i < n; i++) {
			Attribute att = conditionAttList.get(i);
			
			if (!keys.contains(att)) {
				if (k > 0)
					sql.append(", ");
				
				sql.append(norm(att.getName()) + " = ?");
				indexes.add(att.getIndex());
				k++;
			}
		}
		
		sql.append(" where ");
		for (int i = 0; i < keys.size(); i++) {
			Attribute key = keys.get(i);
			
			if (i > 0)
				sql.append(" and ");
			sql.append(norm(key.getName()) + " = ?");
			
			indexes.add(key.getIndex());
		}
		
		
		return new ParamSql(sql.toString(), indexes);
	}

	
	/**
	 * 
	 * @param profileUnit
	 * @return delete SQL
	 */
	public String genProfileDeleteSql(String profileUnit) {
		return "delete from " + norm(profileUnit);
	}

	
	/**
	 * 
	 * @param profileUnit
	 * @param conditionAttList
	 * @return delete SQL
	 */
	public ParamSql genProfileDeleteSql(String profileUnit, AttributeList conditionAttList) {
		Keys keys = conditionAttList.getKeys();
		
		if (keys.size() > 0) 
			return new ParamSql("delete from " + norm(profileUnit) + " where " + 
				toAndConditionText(keys.getList()),
				getIndexes(keys.getList()));
		else
			return new ParamSql("delete from " + norm(profileUnit) + " where " + 
				toAndConditionText(conditionAttList.getList()),
				getIndexes(conditionAttList.getList()) );
	}

	
	/**
	 * 
	 * @param profileUnit
	 * @param condition
	 * @return delete SQL
	 */
	public ParamSql genProfileDeleteSql2(String profileUnit, Profile condition) {
		Keys keys = condition.getNotMissingAtts();
		
		if (keys.size() > 0) 
			return new ParamSql("delete from " + norm(profileUnit) + " where " + 
				toAndConditionText(keys.getList()),
				getIndexes(keys.getList()));
		else
			return new ParamSql(genProfileDeleteSql(profileUnit), new int[] { });
	}

	
	/**
	 * Templates are stored in table whose key is template id and whose fields are name, type and parent
	 * 
	 * @return create SQL
	 */
	public String genContextTemplateCreateSql() {
		return "create table " + norm(getConfig().getContextTemplateUnit()) + " ( " + 
				norm(DataConfig.CTX_TEMPLATEID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.CTX_NAME_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.CTX_TYPE_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.CTX_PARENT_FIELD) + " " + toSqlTypeName(Type.integer) + ", " +
				"primary key (" + norm(DataConfig.CTX_TEMPLATEID_FIELD) + ") )";
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String genContextTemplateSelectSql() {
		return "select * from " + norm(getConfig().getContextTemplateUnit());
	}
	

	/**
	 * 
	 * @param ctxTemplateId
	 * @return select SQL
	 */
	public String genContextTemplateSelectSql(int ctxTemplateId) {
		return "select * from " + norm(getConfig().getContextTemplateUnit()) + 
				" where " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = " + ctxTemplateId;
	}

	
	/**
	 * 
	 * @return insert SQL
	 */
	public ParamSql genContextTemplateInsertSql() {
		return new ParamSql( 
			"insert into " + norm(getConfig().getContextTemplateUnit()) + 
			" (" +
				norm(DataConfig.CTX_TEMPLATEID_FIELD) + ", " + 
				norm(DataConfig.CTX_NAME_FIELD) + ", " + 
				norm(DataConfig.CTX_TYPE_FIELD) + ", " + 
				norm(DataConfig.CTX_PARENT_FIELD) + 
			") values (?, ?, ?, ?) ",
			new int[] { 0, 1, 2, 3 });
	}

	
	/**
	 * 
	 * @return update SQL
	 */
	public ParamSql genContextTemplateUpdateSql() {
		return new ParamSql(
				"update " + norm(getConfig().getContextTemplateUnit()) + 
				" set " + norm(DataConfig.CTX_NAME_FIELD) + " = ?, " +
				norm(DataConfig.CTX_TYPE_FIELD) + " = ?, "  +
				norm(DataConfig.CTX_PARENT_FIELD) + " = ? "  +
				" where " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ?",
				new int [] { 1, 2, 3, 0 }
			);
	}
	
	
	/**
	 * 
	 * @return {@link ParamSql}
	 */
	public ParamSql genContextTemplateDeleteSql() {
		return new ParamSql(
				"delete from " + norm(getConfig().getContextTemplateUnit()) + 
				" where " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ?",
				new int [] { 0 }
			);
	}

	
	/**
	 * 
	 * @return create SQL
	 */
	public String genContextCreateSql() {
		return "create table " + norm(config.getContextUnit()) + " ( " + 
			norm(DataConfig.USERID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.ITEMID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.CTX_TEMPLATEID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.CTX_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + ", " +
			"primary key (" + 
				norm(DataConfig.USERID_FIELD) + ", " + 
				norm(DataConfig.ITEMID_FIELD) + ", " + 
				norm(DataConfig.CTX_TEMPLATEID_FIELD) + ") )";
	}

	
	/**
	 * 
	 * @return select SQL
	 */
	public String genContextSelectSql() {
		return "select * from " + norm(config.getContextUnit());
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @param ctxTemplateId
	 * @return select SQL
	 */
	public String genContextSelectSql(int userId, int itemId, int ctxTemplateId) {
		return "select * from " + norm(config.getContextUnit()) + 
				" where " + norm(DataConfig.USERID_FIELD) + " = " + userId +
				" and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId +	
				" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = " + ctxTemplateId;	
	}

	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return select SQL
	 */
	public String genContextSelectSql(int userId, int itemId) {
		return "select * from " + norm(config.getContextUnit()) + 
				" where " + norm(DataConfig.USERID_FIELD) + " = " + userId +
				" and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;	
	}

	
	/**
	 * 
	 * @return insert SQL
	 */
	public ParamSql genContextInsertSql() {
		return new ParamSql( 
			"insert into " + norm(config.getContextUnit()) + 
			" (" +
				norm(DataConfig.USERID_FIELD) + ", " + 
				norm(DataConfig.ITEMID_FIELD) + ", " + 
				norm(DataConfig.CTX_TEMPLATEID_FIELD) + ", " + 
				norm(DataConfig.CTX_VALUE_FIELD) + 
			") values (?, ?, ?, ?) ",
			new int[] { 0, 1, 2, 3 });
	}

	
	/**
	 * 
	 * @return update SQL
	 */
	public ParamSql genContextUpdateSql() {
		return new ParamSql(
				"update " + norm(config.getContextUnit()) + 
				" set " + norm(DataConfig.CTX_VALUE_FIELD) + " = ? " +
				" where " + norm(DataConfig.USERID_FIELD) + " = ? " +
				" and " + norm(DataConfig.ITEMID_FIELD) + " = ? " +
				" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ? " ,
				new int [] { 3, 0, 1, 2 }
			);
	}

	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @param ctxTemplateId
	 * @return delete SQL
	 */
	public String genContextDeleteSql(int userId, int itemId, int ctxTemplateId) {
		return "delete from " + norm(config.getContextUnit()) + 
		" where " + norm(DataConfig.USERID_FIELD) + " = " + userId +
		" and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId +
		" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = " + ctxTemplateId;
		
	}
	
	
	/**
	 * 
	 * @param userId
	 * @param itemId
	 * @return delete SQL
	 */
	public String genContextDeleteSql(int userId, int itemId) {
		return "delete from " + norm(config.getContextUnit()) + 
		" where " + norm(DataConfig.USERID_FIELD) + " = " + userId +
		" and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}

	
	/**
	 * 
	 * @return update SQL
	 */
	public ParamSql genContextDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getContextUnit()) + 
				" where " + norm(DataConfig.USERID_FIELD) + " = ? " +
				" and " + norm(DataConfig.ITEMID_FIELD) + " = ? " +
				" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ? " ,
				new int [] { 0, 1, 2 }
			);
	}

	
	/**
	 * 
	 * @param userId
	 * @return delete SQL
	 */
	public String genUserContextDeleteSql(int userId) {
		return "delete from " + norm(config.getContextUnit()) + 
			   " where " + norm(DataConfig.USERID_FIELD) + " = " + userId;
	}
	
	
	/**
	 * 
	 * @param itemId
	 * @return delete SQL
	 */
	public String genItemContextDeleteSql(int itemId) {
		return "delete from " + norm(config.getContextUnit()) + 
			   " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}

	
	/**
	 * 
	 * @param unit
	 * @return select SQL
	 */
	public Map<Integer, String> genAttributeBoundSql(String unit) {
		Map<Integer, String> map = Util.newMap();
		
		if (config.getContextUnit() != null && unit.equals(config.getContextUnit())) {
			map.put(2, genContextTemplateSelectSql());
		}
		else {
			AttributeList attList = getAttributes(unit);
			for (int i = 0; i < attList.size(); i++) {
				Attribute att = attList.get(i);
				if (att.getType() != Type.nominal)
					continue;
				
				String attName = att.getName();
				String sql = "select " + norm(DataConfig.NOMINAL_INDEX_FIELD) + 
						" from " + norm(config.getNominalUnit()) + 
						" where " + 
							norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + unit + "' and " +
							norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + attName + "' ";
				
				map.put(i, sql);
			}
			
		}
		
		return map;
	}

	
	/**
	 * 
	 * @param unit
	 * @return select SQL
	 */
	public String genSelectSql(String unit) {
		Map<String, String> map = Util.newMap();
		
		String configUnit = config.getConfigUnit();
		if (configUnit != null)
			map.put(configUnit, genConfigSelectSql());
		
		String ratingUnit = config.getRatingUnit();
		if (ratingUnit != null)
			map.put(ratingUnit, genRatingSelectSql());
		
		String userUnit = config.getUserUnit();
		if (userUnit != null)
			map.put(userUnit, genUserProfileSelectSql());
		
		String itemUnit = config.getItemUnit();
		if (itemUnit != null)
			map.put(itemUnit, genItemProfileSelectSql());
		
		String nominalUnit = config.getNominalUnit();
		if (nominalUnit != null)
			map.put(nominalUnit, genNominalSelectSql());
		
		String accountUnit = config.getAccountUnit();
		if (accountUnit != null)
			map.put(accountUnit, genAccountSelectSql());

		String attributeMapUnit = config.getAttributeMapUnit();
		if (attributeMapUnit != null)
			map.put(attributeMapUnit, genAttributeMapNoParamSelectSql());

		String contextTemplateUnit = config.getContextTemplateUnit();
		if (contextTemplateUnit != null)
			map.put(contextTemplateUnit, genContextTemplateSelectSql());

		String contextUnit = config.getContextUnit();
		if (contextUnit != null)
			map.put(contextUnit, genContextSelectSql());

		if (map.containsKey(unit))
			return map.get(unit);
		else
			return genProfileSelectSql(unit);
		
	}
	
	
	/**
	 * 
	 * @param unit
	 * @return insert SQL
	 */
	public ParamSql genInsertSql(String unit) {
		Map<String, ParamSql> map = Util.newMap();
		
		String configUnit = config.getConfigUnit();
		if (configUnit != null)
			map.put(configUnit, genConfigInsertSql());
		
		String ratingUnit = config.getRatingUnit();
		if (ratingUnit != null)
			map.put(ratingUnit, genRatingInsertSql());
		
		String userUnit = config.getUserUnit();
		if (userUnit != null) {
			map.put(userUnit, genProfileInsertSql(userUnit, getAttributes(config.getUserUnit())));
		}
		
		String itemUnit = config.getItemUnit();
		if (itemUnit != null) {
			map.put(itemUnit, genProfileInsertSql(itemUnit, getAttributes(config.getItemUnit())));
		}
		
		String nominalUnit = config.getNominalUnit();
		if (nominalUnit != null)
			map.put(nominalUnit, genNominalInsertSql());
		
		String accountUnit = config.getAccountUnit();
		if (accountUnit != null)
			map.put(accountUnit, genAccountInsertSql());

		String attributeMapUnit = config.getAttributeMapUnit();
		if (attributeMapUnit != null)
			map.put(attributeMapUnit, genAttributeMapInsertSql());

		String contextTemplateUnit = config.getContextTemplateUnit();
		if (contextTemplateUnit != null)
			map.put(contextTemplateUnit, genContextTemplateInsertSql());

		String contextUnit = config.getContextUnit();
		if (contextUnit != null)
			map.put(contextUnit, genContextInsertSql());

		if (map.containsKey(unit))
			return map.get(unit);
		else {
			AttributeList attList = getAttributes(unit);
			return genProfileInsertSql(unit, attList);
		}
		
	}

	
	/**
	 * 
	 * @param unit
	 * @return update SQL
	 */
	public ParamSql genUpdateSql(String unit) {
		Map<String, ParamSql> map = Util.newMap();
		
		String configUnit = config.getConfigUnit();
		if (configUnit != null)
			map.put(configUnit, genConfigUpdateSql());
		
		String ratingUnit = config.getRatingUnit();
		if (ratingUnit != null)
			map.put(ratingUnit, genRatingUpdateSql());
		
		String userUnit = config.getUserUnit();
		if (userUnit != null) {
			map.put(userUnit, genProfileUpdateSql(userUnit, getAttributes(config.getUserUnit())));
		}

		String itemUnit = config.getItemUnit();
		if (itemUnit != null) {
			map.put(itemUnit, genProfileUpdateSql(itemUnit, getAttributes(config.getItemUnit())));
		}
		
		String nominalUnit = config.getNominalUnit();
		if (nominalUnit != null)
			map.put(nominalUnit, genNominalUpdateSql());
		
		String accountUnit = config.getAccountUnit();
		if (accountUnit != null)
			map.put(accountUnit, genAccountUpdateSql());

		String attributeMapUnit = config.getAttributeMapUnit();
		if (attributeMapUnit != null)
			map.put(attributeMapUnit, genAttributeMapUpdateSql());

		String contextTemplateUnit = config.getContextTemplateUnit();
		if (contextTemplateUnit != null)
			map.put(contextTemplateUnit, genContextTemplateUpdateSql());

		String contextUnit = config.getContextUnit();
		if (contextUnit != null)
			map.put(contextUnit, genContextUpdateSql());

		if (map.containsKey(unit))
			return map.get(unit);
		else {
			AttributeList attList = getAttributes(unit);
			return genProfileUpdateSql(unit, attList);
		}
		
	}


	/**
	 * 
	 * @param unit
	 * @return delete SQL
	 */
	public ParamSql genDeleteSql(String unit) {
		Map<String, ParamSql> map = Util.newMap();
		
		String configUnit = config.getConfigUnit();
		if (configUnit != null)
			map.put(configUnit, genConfigDeleteSql());
		
		String ratingUnit = config.getRatingUnit();
		if (ratingUnit != null)
			map.put(ratingUnit, genRatingDeleteSql());
		
		String userUnit = config.getUserUnit();
		if (userUnit != null) {
			map.put(userUnit, genUserProfileDeleteSql());
		}

		String itemUnit = config.getItemUnit();
		if (itemUnit != null) {
			map.put(itemUnit, genItemProfileDeleteSql());
		}
		
		String nominalUnit = config.getNominalUnit();
		if (nominalUnit != null)
			map.put(nominalUnit, genNominalDeleteSql());
		
		String accountUnit = config.getAccountUnit();
		if (accountUnit != null)
			map.put(accountUnit, genAccountDeleteSql());

		String attributeMapUnit = config.getAttributeMapUnit();
		if (attributeMapUnit != null)
			map.put(attributeMapUnit, genAttributeMapDeleteSql());

		String contextTemplateUnit = config.getContextTemplateUnit();
		if (contextTemplateUnit != null)
			map.put(contextTemplateUnit, genContextTemplateDeleteSql());

		String contextUnit = config.getContextUnit();
		if (contextUnit != null)
			map.put(contextUnit, genContextDeleteSql());

		if (map.containsKey(unit))
			return map.get(unit);
		else {
			AttributeList attList = getAttributes(unit);
			return genProfileDeleteSql(unit, attList);
		}
		
	}

	
	@Override
	public boolean createUnit(String unitName, AttributeList attList) {
		// TODO Auto-generated method stub
		String createSql = genProfileCreateSql(unitName, attList);
		
		return executeUpdate(createSql);
	}


	@Override
	public boolean deleteUnitData(String unitName) {
		// TODO Auto-generated method stub
		return executeUpdate("delete from " + norm(unitName));
	}


	@Override
	public boolean dropUnit(String unitName) {
		return executeUpdate("drop table " + norm(unitName) );
	}

	
	@Override
	public UnitList getUnitList() {
		UnitList tblList = new UnitList();
		try {
			UnitList defaultUnitList = DataConfig.getDefaultUnitList();

			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getTables(null, null, null, null);
			while (rs.next()) {
				String unitName = rs.getString("TABLE_NAME");
				Unit unit = new Unit(unitName);
				if (!defaultUnitList.contains(unitName))
					unit.setExtra(true);
					
				tblList.add(unit);
			}
			
			rs.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Get database metadata error: " + e.getMessage());
		}
		
		return tblList;
	}
	

	@Override
	public NominalList getNominalList(String filterUnit, String attName) {
		NominalList nominalList = new NominalList();
		UnitList unitList = getUnitList();
		if (!unitList.contains(config.getNominalUnit()))
			return nominalList;
		
		ResultSet rs = null;
		try {
			
			String sql = genNominalSelectSql(filterUnit, attName);
			rs = executeQuery(sql);
			
			while(rs.next()) {
				String nominalValue = rs.getString(DataConfig.NOMINAL_VALUE_FIELD);
				int nominalindex = rs.getInt(DataConfig.NOMINAL_INDEX_FIELD);
				int parentindex = rs.getInt(DataConfig.NOMINAL_PARENT_INDEX_FIELD);
				if (rs.wasNull())
					parentindex = Nominal.NULL_INDEX;
				
				nominalList.add(new Nominal(nominalValue, nominalindex, parentindex));
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			closeResultSet(rs);
		}
		
		return nominalList;
	}
	

	/**
	 * 
	 * @param unitName
	 * @return list of primary keys
	 */
	private List<String> getPrimaryKeys(String unitName) {
		List<String> keys = Util.newList();
		try {
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet rs = metadata.getPrimaryKeys(null, null, unitName);
			while (rs.next()) {
				String key = rs.getString("COLUMN_NAME"); 
				keys.add(key);
			}
			rs.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return keys;
	}

	
	/**
	 * 
	 * @param rs
	 * @return {@link AttributeList}
	 */
	private AttributeList getAttributes(ResultSet rs) {
		AttributeList attList = new AttributeList();
		
		try {
			ResultSetMetaData metadata = rs.getMetaData();
			int n = metadata.getColumnCount();
			for (int i = 1; i <= n; i++) {
				List<String> keys = getPrimaryKeys(metadata.getTableName(i));
				
				String name = metadata.getColumnLabel(i); 
				Type type = fromSqlType(metadata.getColumnType(i));
				Attribute att = new Attribute(name, type);
				
				if (type == Type.integer) {
					String unitName = metadata.getTableName(i);
					if (unitName != null && !unitName.isEmpty()) {
						NominalList nominalList = getNominalList(unitName, name);
						if (nominalList.size() > 0)
							att = new Attribute(name, nominalList);
					}
				}
				
				if (keys.contains(name))
					att.setKey(true);
				
				attList.add(att);
			}
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return attList;
	}
	
	
	@Override
	public AttributeList getAttributes(String profileUnit) {
		AttributeList attList = new AttributeList();
		
		try {
			List<String> keys = getPrimaryKeys(profileUnit);
			
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet rs = metadata.getColumns(null, null, profileUnit, null);
			while (rs.next()) {
				String name = rs.getString("COLUMN_NAME"); 
				Type type = fromSqlType(rs.getInt("DATA_TYPE"));
				 
				Attribute att = new Attribute(name, type);
				if (type == Type.integer) {
					NominalList nominalList = getNominalList(profileUnit, name);
					if (nominalList.size() > 0)
						att = new Attribute(name, nominalList);
				}
				
				if (keys.contains(name))
					att.setKey(true);
				
				attList.add(att);
			}
			rs.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return attList;
	}

	
	@Override
	public AttributeList getAttributes(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		
		AttributeList attributes = new AttributeList();
		ResultSet rs = executeQuery(selectSql, condition);
		try {
			attributes = getAttributes(rs);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			closeResultSet(rs);
		}
		
		
		return attributes;
	}


	@Override
	public boolean containsProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		ParamSql selectSql = genProfileSelectSql2(profileUnit, profile);
		
		return exists(selectSql, profile);
	}


	@Override
	public Profile getProfile(String profileUnit, Profile condition) {
		ResultSet rs = null;
		Profile returnProfile = null;
		try {
			if (condition != null)
				rs = executeQuery(genProfileSelectSql2(profileUnit, condition), 
						condition);
			else
				rs = executeQuery(genProfileSelectSql(profileUnit));
			
			if (rs != null && rs.next())
				returnProfile = getProfile(rs);
			
			closeResultSet(rs);
			rs = null;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			closeResultSet(rs);
		}
		
		return returnProfile;
	}

	
	/**
	 * 
	 * @param rs
	 * @return {@link Profile}
	 */
	private Profile getProfile(ResultSet rs) {
		AttributeList attRef = getAttributes(rs);
		
		Profile profile = new Profile(attRef);
		
		for (int i = 0; i < attRef.size(); i++) {
			Attribute att = attRef.get(i);
			
			String name = att.getName();
			Type type = att.getType();
			Object value = null;
			
			try {
				if (type == Type.bit)
					value = new Byte(rs.getByte(name));
				else if (type == Type.integer)
					value = new Integer(rs.getInt(name));
				else if (type == Type.real)
					value = new Float(rs.getFloat(name));
				else if (type == Type.string)
					value = rs.getString(name);
				else if (type == Type.date)
					value = rs.getDate(name);
				else 
					value = rs.getObject(name);
				
				if (rs.wasNull())
					profile.setMissing(i);
				else
					profile.setValue(i, value);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return profile;
	}

	
	@Override
	public Fetcher<Profile> getProfiles(String profileUnit, Profile condition) {
		ResultSet rs = null;
		if (condition != null)
			rs = executeQuery(genProfileSelectSql2(profileUnit, condition), 
					condition);
		else
			rs = executeQuery(genProfileSelectSql(profileUnit));
		
		if (rs == null)
			return new MemFetcher<Profile>();
		
		return new DbFetcher<Profile>(rs) {

			@Override
			public Profile create(ResultSet rs) {
				// TODO Auto-generated method stub
				return getProfile(rs);
			}
			
		};
	}
	
	
	@Override
	public Fetcher<Profile> getProfiles(ParamSql sql, Profile condition) {
		// TODO Auto-generated method stub
		ResultSet rs = executeQuery(sql, condition);
		if (rs == null)
			return new MemFetcher<Profile>();
		
		return new DbFetcher<Profile>(rs) {

			@Override
			public Profile create(ResultSet rs) {
				// TODO Auto-generated method stub
				return getProfile(rs);
			}
			
		};
	}


	@Override
	public Fetcher<Integer> getProfileIds(String profileUnit) {
		// TODO Auto-generated method stub
		AttributeList attributes = getAttributes(profileUnit);
		final Attribute idAtt = attributes.getId();
		if (idAtt == null || idAtt.getType() != Type.integer)
			return new MemFetcher<Integer>();
		
		ResultSet rs = executeQuery(genProfileIdsSelectSql(profileUnit, idAtt.getName()));
		
		if (rs == null)
			return new MemFetcher<Integer>();
		
		return new DbFetcher<Integer>(rs) {

			@Override
			public Integer create(ResultSet rs) {
				// TODO Auto-generated method stub
				try {
					int id = rs.getInt(idAtt.getName());
					if (rs.wasNull() || id < 0)
						return null;
					else
						return id;
				} 
				catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return null;
			}
			
		};
	}


	@Override
	public int getProfileMaxId(String profileUnit) {
		// TODO Auto-generated method stub
		Attribute id = getAttributes(profileUnit).getId();
		if (id == null)
			return -1;
		
		String maxSql = genMaxIdProfileSelectSql(profileUnit, id.getName());
		return (int) (max(new ParamSql(maxSql), null) + 0.5);
	}


	@Override
	public boolean insertProfile(String profileUnit, Profile profile) {
		ParamSql insertSql = genProfileInsertSql(profileUnit, 
				profile.getAttRef());
		
		return executeUpdate(insertSql, profile);
	}
	
	
	@Override
	public boolean updateProfile(String profileUnit, Profile profile) {
		ParamSql updateSql = genProfileUpdateSql(profileUnit, 
				profile.getAttRef());
		
		return executeUpdate(updateSql, profile);
	}

	
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) {
		// TODO Auto-generated method stub
		ParamSql deleteSql = genProfileDeleteSql2(profileUnit, 
				condition);
		
		return executeUpdate(deleteSql, condition);
	}

	
	/**
	 * 
	 * @param selectSql
	 * @return {@link ResultSet}
	 */
	private ResultSet executeQuery(String selectSql) {
		try {
			Statement stm = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stm.executeQuery(selectSql);
			
			return rs;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param selectSql
	 * @param condition
	 * @return {@link ResultSet}
	 */
	private ResultSet executeQuery(ParamSql selectSql, Profile condition) {
		
		if (selectSql.getIndexes().size() == 0 || condition == null)
			return executeQuery(selectSql.getSql());
		
		try {
			AttributeList attList = condition.getAttRef();
			
			PreparedStatement stm = conn.prepareStatement(selectSql.getSql(), 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY );
			
			List<Integer> indexes = selectSql.getIndexes();
			for (int i = 0; i < indexes.size(); i++) {
				int index = indexes.get(i);
				Attribute att = attList.get(index);
				Type type = att.getType();
				
				if (condition.isMissing(index)) {
					stm.setNull(i + 1, toSqlType(type));
					continue;
				}
				
				switch (type) {
				case bit:
					stm.setByte(i + 1, (byte)condition.getValueAsInt(index));
					break;
				case nominal:
					stm.setInt(i + 1, condition.getValueAsInt(index));
					break;
				case integer:
					stm.setInt(i + 1, condition.getValueAsInt(index));
					break;
				case real:
					stm.setFloat(i + 1, condition.getValueAsReal(index));
					break;
				case string:
					stm.setString(i + 1, condition.getValueAsString(index));
					break;
				case date:
					Date date =  (Date)condition.getValue(index);
					stm.setDate(i + 1, new java.sql.Date(date.getTime()));
					break;
				case object:
					stm.setObject(i + 1, condition.getValue(index));
					break;
				default:
					stm.setObject(i + 1, condition.getValue(index));
				}
			}
			
			return stm.executeQuery();
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * 
	 * @param updateSql
	 * @return whether update successfully
	 */
	private boolean executeUpdate(String updateSql) {
		try {
			Statement stm = conn.createStatement();
			stm.executeUpdate(updateSql);
			stm.close();
			
			return true;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	/**
	 * 
	 * @param updateSql
	 * @param profile
	 * @return whether update successfully
	 */
	private boolean executeUpdate(ParamSql updateSql, Profile profile) {
		
		if (updateSql.getIndexes().size() == 0 || profile == null)
			return executeUpdate(updateSql.getSql());
		
		try {
			AttributeList attList = profile.getAttRef();
			
			PreparedStatement stm = conn.prepareStatement(updateSql.getSql());
			
			List<Integer> indexes = updateSql.getIndexes();
			for (int i = 0; i < indexes.size(); i++) {
				
				int index = indexes.get(i);
				Attribute att = attList.get(index);
				Type type = att.getType();
				
				if (profile.isMissing(index)) {
					stm.setNull(i + 1, DbProviderAssoc.toSqlType(type));
					continue;
				}
				
				switch (type) {
				case bit:
					stm.setByte(i + 1, (byte)profile.getValueAsInt(index));
					break;
				case nominal:
					stm.setInt(i + 1, profile.getValueAsInt(index));
					break;
				case integer:
					stm.setInt(i + 1, profile.getValueAsInt(index));
					break;
				case real:
					stm.setFloat(i + 1, profile.getValueAsReal(index));
					break;
				case string:
					stm.setString(i + 1, profile.getValueAsString(index));
					break;
				case date:
					Date date =  (Date)profile.getValue(index);
					stm.setDate(i + 1, new java.sql.Date(date.getTime()));
					break;
				case object:
					stm.setObject(i + 1, profile.getValue(index));
					break;
				default:
					stm.setObject(i + 1, profile.getValue(index));
				}
			}
			
			stm.executeUpdate();
			stm.close();
			
			return true;
		}
		catch (Throwable e) {
			e.printStackTrace();
			
		}
		return false;
	
		
	}
	

	/**
	 * 
	 * @param selectSql
	 * @param condition
	 * @return maximum value
	 */
	private float max(ParamSql selectSql, Profile condition) {
		
		
		float max = Constants.UNUSED;
		try {
			
			ResultSet rs = executeQuery(selectSql, condition);
			
			while(rs.next()) {
				max = rs.getFloat(1);
				break;
			}
			
			closeResultSet(rs);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return max;
		
	}

	
	/**
	 * 
	 * @param selectSql
	 * @param condition
	 * @return whether exists {@link ResultSet}
	 */
	private boolean exists(ParamSql selectSql, Profile condition) {
		
		try {
			
			ResultSet rs = executeQuery(selectSql, condition);
			if (rs == null)
				return false;
			
			boolean contain = rs.next();
			closeResultSet(rs);
			
			return contain;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return false;
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (conn != null) {
			try {
				conn.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		conn = null;
		
		config = null;
	}


	/**
	 * 
	 * @return {@link Connection}
	 */
	public static Connection createConnection(DataConfig config) {
		String username = config.getStoreAccount();
		HiddenText password = config.getStorePassword();
		
		try {
			
			String url = config.getStoreUri().toString();
			if (username == null || password == null)
				return DriverManager.getConnection(url);
			else
				return DriverManager.getConnection(url, username, password.getText());
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * 
	 * @param rs
	 */
	public static void closeResultSet(ResultSet rs) {
		if (rs == null)
			return;
		
		try {
			Statement stm = rs.getStatement();
			if (stm != null && !stm.isClosed())
				stm.close();
			
			rs.close();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	

}
