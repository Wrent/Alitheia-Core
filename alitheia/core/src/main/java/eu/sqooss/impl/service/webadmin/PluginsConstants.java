package eu.sqooss.impl.service.webadmin;

public interface PluginsConstants {
	// Request parameters
	public static final String REQPARACTION = "action";
	public static final String REQPARHASHCODE = "pluginHashcode";
	public static final String REQPARPROPNAME = "propertyName";
	public static final String REQPARPROPDESC = "propertyDescription";
	public static final String REQPARPROPTYPE = "propertyType";
	public static final String REQPARPROPVALUE = "propertyValue";
	public static final String REQPARSHOWPROP = "showProperties";
	public static final String REQPARSHOWACTV = "showActivators";
	// Recognized "action" parameter's values
	public static final String ACTVALINSTALL = "installPlugin";
	public static final String ACTVALUNINSTALL = "uninstallPlugin";
	public static final String ACTVALSYNC = "syncPlugin";
	public static final String ACTVALREQADDPROP = "createProperty";
	public static final String ACTVALREQUPDPROP = "updateProperty";
	public static final String ACTVALCONADDPROP = "confirmProperty";
	public static final String ACTVALCONREMPROP = "removeProperty";
}
