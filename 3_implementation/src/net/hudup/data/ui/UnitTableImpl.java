package net.hudup.data.ui;

import java.awt.Component;
import java.sql.SQLException;
import java.util.Map;

import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Provider;
import net.hudup.data.DbProviderAssoc;

import org.apache.log4j.Logger;

import quick.dbtable.Column;
import quick.dbtable.DBTable;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitTableImpl extends DBTable implements UnitTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(UnitTable.class);

	
	/**
	 * 
	 */
	public UnitTableImpl() {
		super();
		// TODO Auto-generated constructor stub
		setEditable(true);
		createControlPanel();
	}


	@Override
	public void update(Provider provider, String unit) {
		if (provider == null)
			return;
		
		if (! (provider.getAssoc() instanceof DbProviderAssoc) ) {
			logger.error("UnitTable not support none-database provider");
			return;
		}
		
		DbProviderAssoc dbAssoc = (DbProviderAssoc) provider.getAssoc();
		setConnection(dbAssoc.getConnection());

		String select = dbAssoc.genSelectSql(unit);
		setSelectSql(select);
		
		ParamSql insert = dbAssoc.genInsertSql(unit);
		clearAllInsertSql();
		addInsertSql(insert.getSql(), insert.getIndexText(true));
		
		ParamSql update = dbAssoc.genUpdateSql(unit);
		clearAllUpdateSql();
		addUpdateSql(update.getSql(), update.getIndexText(true));
		
		ParamSql delete = dbAssoc.genDeleteSql(unit);
		clearAllDeleteSql();
		addDeleteSql(delete.getSql(), delete.getIndexText(true));
		
		try {
			refresh();
			
			Map<Integer, String> boundSql = dbAssoc.genAttributeBoundSql(unit);
			
			if (boundSql.size() > 0) {
				int n = getColumnCount();
				for (int i = 0; i < n; i++) {
					if (boundSql.containsKey(i)) {
						Column column = getColumn(i);
						String sql = boundSql.get(i);
						column.setBoundSql(sql);
					}
				}
			}
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		setEditable(true);
	}
	
	
	@Override
	public void clear() {
		clearAllDeleteSql();
		clearAllInsertSql();
		clearAllUpdateSql();
		removeAllRows();
		
		try {
			close();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		try {
			super.refresh();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}
	
	
	
	
}
