/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/

package org.certae.plugins.export.mrd.wrappers;

import java.text.MessageFormat;

import org.certae.plugins.export.mrd.international.LocaleMgr;
import org.modelsphere.jack.baseDb.db.DbEnumeration;
import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.baseDb.db.DbRelationN;
import org.modelsphere.sms.db.DbSMSStereotype;
import org.modelsphere.sms.db.srtypes.SMSMultiplicity;
import org.modelsphere.sms.or.db.DbORAbsTable;
import org.modelsphere.sms.or.db.DbORAssociationEnd;
import org.modelsphere.sms.or.db.DbORColumn;
import org.modelsphere.sms.or.db.DbORDataModel;
import org.modelsphere.sms.or.db.DbORDomain;
import org.modelsphere.sms.or.db.DbORFKeyColumn;
import org.modelsphere.sms.or.db.DbORTable;
import org.modelsphere.sms.or.db.DbORTypeClassifier;


public class DbColumnWrapper {
	private static final String UNDEFINED = LocaleMgr.misc.getString("Undefined");
	private static final String YES = LocaleMgr.misc.getString("Yes");
	private static final String NO = LocaleMgr.misc.getString("No");
	private static final String RUBRIQUE_SUFFIX = "(RUBRIQUE)";
	private static final String RUBRIQUE_STEREOTYPE = "rubrique";

	private DbTableWrapper m_table;
	private DbORColumn m_dbCol;
	private String djangoType;  

	public DbColumnWrapper(DbTableWrapper table, DbORColumn column) throws DbException {
		m_table = table;
		m_dbCol = column;
	}

	public DbColumnWrapper(DbORColumn column) throws DbException {
		m_dbCol = column;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DbColumnWrapper)) {
			return false;
		}

		DbColumnWrapper that = (DbColumnWrapper) o;
		boolean equals = this.m_dbCol.equals(that.m_dbCol);
		return equals;
	}

	//find, if any, the column of 'table' whose name is 'columnName' 
	private DbORColumn findColumn(DbORTable table, String columnName) throws DbException {
		DbORColumn namedColumn = null;
		DbRelationN relN = table.getComponents();
		DbEnumeration enu = relN.elements(DbORColumn.metaClass);
		while (enu.hasMoreElements()) {
			DbORColumn c = (DbORColumn) enu.nextElement();
			if (columnName.equals(c.getName())) {
				namedColumn = c;
				break;
			}
		} //end while
			enu.close();

		return namedColumn;
	}

	private DbORTable findParentTable(DbORTable t1) throws DbException {
		DbORTable parent = null;
		DbRelationN relN = t1.getAssociationEnds();
		DbEnumeration enu = relN.elements(DbORAssociationEnd.metaClass);
		while (enu.hasMoreElements()) {
			DbORAssociationEnd end = (DbORAssociationEnd) enu.nextElement();
			int mult = end.getMultiplicity().getValue();
			if (mult == SMSMultiplicity.EXACTLY_ONE) {
				DbORAssociationEnd oppEnd = end.getOppositeEnd();
				DbORAbsTable t = oppEnd.getClassifier();

				if (t instanceof DbORTable) {
					parent = (DbORTable) t;
					break;
				}
			} //end if
		} //end while
			enu.close();

		return parent;
	}

	public DbORColumn getDbColumn() {
		return m_dbCol;
	}

	public String getDescription() {
		String description;

		try {
			description = m_dbCol.getDescription();
		} catch (DbException ex) {
			description = null;
		}

		return description;
	}

	public String getFormat() {
		String format;

		try {
			DbORTypeClassifier type = m_dbCol.getType();
			String typename = (type == null) ? UNDEFINED : type.getName();
			String len = m_dbCol.getLengthNbDecimal();
			len = (len == null) ? "" : len;
			format = typename + len;
		} catch (DbException ex) {
			format = "???";
		} //end if

		return format;
	}

	public String getFullName() {
		String tableName = m_table.toString();
		String columnName;

		try {
			columnName = m_dbCol.getName();
		} catch (DbException ex) {
			columnName = "???";
		}

		String name = tableName + "." + columnName;
		return name;
	}

	public StringWrapper getName() throws DbException {
		String s = m_dbCol.getName();
		return new StringWrapper(s);
	}

	public StringWrapper getAlias() throws DbException {
		String s = m_dbCol.getAlias();
		return new StringWrapper(s);
	}


	public StringWrapper getPhysicalName() throws DbException {
		StringWrapper sw = new StringWrapper(m_dbCol.getPhysicalName());
		return sw;
	}

	public String getType() throws DbException {
		DbORTypeClassifier type = m_dbCol.getType();
		String s = (type == null)? "?" : type.getName();
		return s;
	}

	public boolean isForeign() throws DbException {
		boolean isForeign = false;

		DbEnumeration enu = m_dbCol.getFKeyColumns().elements(DbORFKeyColumn.metaClass);
		isForeign = enu.hasMoreElements();
		enu.close();

		return isForeign;
	}

	
	public String getRefTable() throws DbException {
		String s = "" ;
		DbEnumeration enu = m_dbCol.getFKeyColumns().elements(DbORFKeyColumn.metaClass);
        if (enu.hasMoreElements()) {
            DbORFKeyColumn fColumn = (DbORFKeyColumn) enu.nextElement();
            DbORColumn c = fColumn.getSourceColumn();
    		DbORTable t1 = (DbORTable) c.getCompositeOfType(DbORTable.metaClass);
            DbTableWrapper wRef = new DbTableWrapper(null, t1);
            s = wRef.getName().getCamelCase().toString();
        }
		enu.close();
		return s;
	}

	
	public boolean isNullable() throws DbException {
		// Determine si l'instance est null
		boolean bNull; 
		try {
			bNull= m_dbCol.isNull();
		} catch (DbException ex) {
			bNull = true;
		}
		return bNull; 
	}

	public String getNullAllowed() throws DbException {
		boolean b = m_dbCol.isNull();
		return (b)? "True" : "False"; 
	}


	public Integer getLength() throws DbException {
		Integer i = m_dbCol.getLength();
		return (i == null) ? 0 : i;
	}

	public String getTypeDesc() throws DbException {

		String lengthNbDecimal= getType();

		Integer length = getLength();
		if ((length != null ) && (length != 0)) {
			lengthNbDecimal += "(" + length.toString(); 
			Integer nbDecimal = m_dbCol.getNbDecimal();
			if (nbDecimal == null) lengthNbDecimal += ")"; 
			else  lengthNbDecimal += "," + nbDecimal.toString() + ")"; 
		}
		return lengthNbDecimal;
	}


	public String getFoundationType() throws DbException {
		DbORTypeClassifier type = m_dbCol.getType();
		DbORTypeClassifier srcType = getSourceType(type);

		String s = (srcType == null) ? "???" : srcType.getPhysicalName();
		String ft;

		if ("BIT".equals(s)) {
			ft = "TypeBool";
		} else if ("DATETIME".equals(s)) {
			ft = "TypeDateTime";
		} else if ("INT".equals(s)) {
			ft = "TypeInt";
		} else if ("INTEGER".equals(s)) {
			ft = "TypeInt"; }
		
		else if ("LONG VARCHAR".equals(s)) {		ft = "TypeText"; }
		else if ("TEXT".equals(s)) { 				ft = "TypeText"; }
			
		else {
			ft = "TypeString";
		} //end 

		return ft;
	}

	private DbORTypeClassifier getSourceType(DbORTypeClassifier type) throws DbException {
		if (type instanceof DbORDomain) {
			DbORDomain domain = (DbORDomain) type;
			DbORTypeClassifier srcType = domain.getSourceType();
			return getSourceType(srcType);
		} else {
			return type;
		}
	}


	public String getModelName() throws DbException {
		DbORDataModel model = (DbORDataModel) m_dbCol.getCompositeOfType(DbORDataModel.metaClass);
		String name = model.getName();
		return name;
	}

	public String getNullable() {
		String nullable;

		try {
			nullable = m_dbCol.isNull() ? YES : NO;
		} catch (DbException ex) {
			nullable = "???";
		} //end if

		return nullable;
	}

	private DbORColumn getSuperColumnObj() throws DbException {
		DbORColumn logicalSuperColumn = null;
		DbORTable t1 = (DbORTable) m_dbCol.getCompositeOfType(DbORTable.metaClass);
		boolean rubrique = isRubrique(t1);

		if (rubrique) {
			DbORTable t2 = findParentTable(t1);
			DbORTable t0 = (t2 == null) ? null : (DbORTable) t2.getSuperCopy();
			logicalSuperColumn = (t0 == null) ? null : findColumn(t0, t1.getName());
		}

		return logicalSuperColumn;
	}

	public DbColumnWrapper getSuperColumn(DbProjectWrapper project) throws DbException {
		DbORColumn sc = (DbORColumn) m_dbCol.getSuperCopy(); //physical super-column

		//if no physical super-column, look for logical super-column
		if (sc == null) {
			sc = getSuperColumnObj();
		}

		//if neither physical nor logical super-column, return null
		if (sc == null) {
			return null;
		}

		DbORTable t = (DbORTable) sc.getCompositeOfType(DbORTable.metaClass);
		DbORDataModel dm = (DbORDataModel) t.getCompositeOfType(DbORDataModel.metaClass);

		DbDataModelWrapper dataModel = project.getDataModel(dm);
		DbTableWrapper table = dataModel.getTable(t);
		DbColumnWrapper superColumn = new DbColumnWrapper(table, sc);
		return superColumn;
	}

	public StringWrapper getSuperColName(DbProjectWrapper project) throws DbException {

		DbColumnWrapper superCol = getSuperColumn( project ); 
		if (superCol == null) {
			return new StringWrapper("");
		} else {
			return superCol.getName(); 
		}
	}

	public DbTableWrapper getTable() {
		return m_table;
	}

	public String getUdfValue(String udfName) {
		String value;

		try {
			Object o = m_dbCol.getUDF(udfName);
			value = (o == null) ? "" : o.toString();
		} catch (DbException ex) {
			value = "?";
		}

		return value;
	}

	public boolean isRubrique() {
		boolean rubrique;

		try {
			String name = m_dbCol.getName();
			DbSMSStereotype stereotype = m_dbCol.getUmlStereotype();
			rubrique = (name == null) ? false : name.endsWith(RUBRIQUE_SUFFIX);
			rubrique |= (stereotype == null) ? false : RUBRIQUE_STEREOTYPE.equals(stereotype
					.getName());
		} catch (DbException ex) {
			rubrique = false;
		}

		return rubrique;
	}

	private boolean isRubrique(DbORTable table) {
		boolean rubrique;

		try {
			String tableName = table.getName();
			DbSMSStereotype stereotype = table.getUmlStereotype();
			rubrique = (tableName == null) ? false : tableName.endsWith(RUBRIQUE_SUFFIX);
			rubrique |= (stereotype == null) ? false : RUBRIQUE_STEREOTYPE.equals(stereotype
					.getName());
		} catch (DbException ex) {
			rubrique = false;
		}

		return rubrique;
	}

	@Override
	public String toString() {
		String name;
		try {
			name = m_table.getName() + "." + m_dbCol.getName();
		} catch (DbException ex) {
			name = super.toString();
		} //end try

		return name;
	}

	//    *********************

	public final DbRelationN getPrimaryUniques() throws DbException {
		//  getPrimaryUniques??
		return m_dbCol.getPrimaryUniques()  ;
	}

	public final DbRelationN getChecks() throws DbException {
		//  getChecks??
		return m_dbCol.getChecks();
	}

	public final DbRelationN getFKeyColumns() throws DbException {
		//  getFKeyColumns??
		return m_dbCol.getFKeyColumns();
	}

	public String getDjangoType() throws DbException {
		DbORTypeClassifier type = m_dbCol.getType();
		DbORTypeClassifier srcType = getSourceType(type);

		String s = (srcType == null) ? "???" : srcType.getPhysicalName();
		String ft;

		
		if ("BIT".equals(s)) { 				ft = "BooleanField";  } 
		else if ("BOOLEAN".equals(s)) { 	ft = "BooleanField"; } 

		else if ("BYTE".equals(s)) { 				ft = "IntegerField"; }
		else if ("CHARACTER".equals(s)) { 			ft = "CharField"; }
		else if ("DOUBLE PRECISION".equals(s)) { 	ft = "FloatField"; }
		else if ("FLOAT".equals(s)) { 				ft = "FloatField"; }
		else if ("INTEGER".equals(s)) { 			ft = "IntegerField"; }
		else if ("LONG INTEGER".equals(s)) {		ft = "IntegerField"; }
		else if ("LONG VARCHAR".equals(s)) {		ft = "CharField"; }
		
		else if ("MONEY".equals(s)) { 		ft = "DecimalField"; }
		else if ("NCHAR".equals(s)) { 		ft = "CharField"; }
		else if ("NUMERIC".equals(s)) { 	ft = "DecimalField"; }
		else if ("NVARCHAR".equals(s)) { 	ft = "CharField"; }
		else if ("REAL".equals(s)) { 		ft = "FloatField"; }
		else if ("ROWID".equals(s)) { 		ft = "AutoField"; }
		
		else if ("SMALL DATE TIME".equals(s)) { ft = "DateTimeField"; }
		else if ("SMALL FLOAT".equals(s)) { 	ft = "FloatField"; }
		else if ("SMALL INTEGER".equals(s)) { 	ft = "IntegerField"; }
		else if ("SMALL MONEY".equals(s)) { 	ft = "DecimalField"; }
		
		else if ("TEXT".equals(s)) { 				ft = "CharField"; }
		else if ("TIME".equals(s)) { 				ft = "TimeField"; }
		else if ("TIMESTAMP".equals(s)) { 			ft = "TimeField"; }
		else if ("VARIABLE CHARACTER".equals(s)) { 	ft = "CharField"; }
		
		else if ("DATE".equals(s)) { 		ft = "DateField"; } 
		else if ("DATETIME".equals(s)) { 	ft = "DateTimeField"; } 
		else if ("INT".equals(s)) {			ft = "IntegerField"; }
		else if ("INTEGER".equals(s)) {		ft = "IntegerField"; }
		else if ("DECIMAL".equals(s)) {		ft = "DecimalField"; }
		else {								ft = "CharField";
		} //end 

		djangoType =ft;  

//		TypedChoiceField,	EmailField,	FileField, 	FilePathField
//		ImageField, IPAddressField, MultipleChoiceField, TypedMultipleChoiceField
//		NullBooleanField, RegexField, SlugField, URLField, 	ComboField
//		MultiValueField, SplitDateTimeField
		
		return ft;
	}

	public String getDjangoColParams() throws DbException {
		// TODO Auto-generated method stub
		// default, editable, error_messages, verbose_name

		String sReturn = ""; 
		
		if (djangoType == "" )  getDjangoType(); 

		if (djangoType == "CharField" ) {
			Integer length = getLength();
			if ((length == null ) || (length == 0)) {
				length = 50; 
				if (getFoundationType() == "TypeText") length = 500;  
				}
			sReturn = "max_length=" + length.toString() ; 
		}

		if (djangoType == "DecimalField" ) {
			Integer length = getLength();
			if ((length == null ) || (length == 0))  length = 20; 
			sReturn = "max_digits=" + length.toString() ; 

			Integer nbDecimal = m_dbCol.getNbDecimal();
			if (nbDecimal == null) nbDecimal = 0; 
			sReturn += ", decimal_places=" + nbDecimal.toString() ; 
		}

		if (!isNullable()) { 
			sReturn = (sReturn == "" )? "" : sReturn + ", " ;
			sReturn += "blank=True, null=True"; 
		} 
		
		//TODO:  Debe ser un indice unico 
		if ( m_table.isPrimary(m_dbCol)) {
			sReturn = (sReturn == "" )? "" : sReturn + ", " ;
			sReturn += "db_index=True"; 
		}

		if (!(getDescription() == null )) { 
			sReturn = (sReturn == "" )? "" : ", " + sReturn  ;
			sReturn += MessageFormat.format("help_text =", getDescription()); 
		}

		return "," + sReturn;
	}


}
