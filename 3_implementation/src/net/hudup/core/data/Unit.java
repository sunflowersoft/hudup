package net.hudup.core.data;


/**
 * Objects in framework such as {@code profile}, {@code item profile}, {@code user profile}, {@code rating matrix}, {@code interchanged attribute map} are stored in archives (files) of entire framework.
 * Each archive (file) is called {@code unit} representing a CSV file, database table, Excel sheet, etc.
 * This class just represents the name of unit but there are special classes processing on unit and so this class is identical to unit.
 * <br>
 * Unit class has variable {@link #name} representing its name and variable {@link #extra} indicating whether or not this unit is essential one. 
 * If {@code extra=true}, this unit is extra (auxiliary) unit and so it is not essential unit.
 * If {@code extra=false}, this unit is essential (important, main) unit such as rating matrix.
 * The default value of this variable is {@code extra=false}, which means that a new unit is important one by default.
 * When showing, the name of essential unit ({@code extra=false}) is shown within signs &quot;&lt;&quot; and &quot;&gt;&quot;,
 * for example, the name of rating unit is shown &quot;&lt;rating&gt;&quot;.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Unit {

	
	/**
	 * Name of unit.
	 */
	protected String name = "";
	
	
	/**
	 * If {@code true}, this unit is extra (auxiliary) unit and so it is not essential unit.
	 * If {@code false}, this unit is essential (important, main) unit such as rating matrix.
	 * The default value of this variable is {@code false}, which means that a new unit is important one by default.
	 * When showing, the name of essential unit ({@code extra=false}) is shown within signs &quot;&lt;&quot; and &quot;&gt;&quot;,
	 * for example, the name of rating unit is shown &quot;&lt;rating&gt;&quot;.
	 */
	protected boolean extra = false;
	
	
	/**
	 * Constructor with specified name and specified indicator indicating whether or not this unit is extra one. 
	 * @param name specified name.
	 * @param extra whether or not this unit is extra one.
	 */
	public Unit(String name, boolean extra) {
		this.name = name;
		this.extra = extra;
	}
	
	
	/**
	 * Constructor with specified name. By default, this unit is essential one ({@code extra=false}).
	 * @param name specified name.
	 */
	public Unit(String name) {
		this(name, false);
	}
	
	
	/**
	 * Getting the name of this unit.
	 * @return name of this unit.
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Setting the name of this unit.
	 * @param name specified name.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	
	/**
	 * Indicating whether or not this unit is extra (auxiliary) one. 
	 * If returning {@code true}, this unit is extra (auxiliary) unit and so it is not essential unit.
	 * If returning {@code false}, this unit is essential unit such as rating matrix.
	 * @return whether or not this unit is extra (auxiliary) one.
	 */
	public boolean isExtra() {
		return extra;
	}
	
	
	/**
	 * Setting whether or not this unit is extra (auxiliary) one.
	 * @param extra specified indicator: whether or not this unit is extra (auxiliary) one.
	 */
	public void setExtra(boolean extra) {
		this.extra = extra;
	}
	
	
	
	@Override
	public String toString() {
		if (extra)
			return name;
		else
			return "<" + name + ">";//Essential (important) unit is shown within the pair "<" and ">"
	}
	
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Unit)
			return this.name.equals( ((Unit)obj).name);
		else if (obj instanceof String)
			return this.name.equals( (String)obj);
		else
			return super.equals(obj);
	}


}
