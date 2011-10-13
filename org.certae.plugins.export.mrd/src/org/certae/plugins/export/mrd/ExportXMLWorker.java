/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/
package org.certae.plugins.export.mrd;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.certae.plugins.export.mrd.international.LocaleMgr;
import org.certae.plugins.export.mrd.wrappers.DbColumnWrapper;
import org.certae.plugins.export.mrd.wrappers.DbDataModelWrapper;
import org.certae.plugins.export.mrd.wrappers.DbLinkModelWrapper;
import org.certae.plugins.export.mrd.wrappers.DbLinkWrapper;
import org.certae.plugins.export.mrd.wrappers.DbORAssociationEndWrapper;
import org.certae.plugins.export.mrd.wrappers.DbORAssociationWrapper;
import org.certae.plugins.export.mrd.wrappers.DbProjectWrapper;
import org.certae.plugins.export.mrd.wrappers.DbTableWrapper;
import org.certae.plugins.export.mrd.wrappers.DbUdfWrapper;
import org.certae.plugins.export.mrd.wrappers.StringWrapper;
import org.modelsphere.jack.baseDb.db.Db;
import org.modelsphere.jack.baseDb.db.DbEnumeration;
import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.baseDb.db.DbProject;
import org.modelsphere.jack.gui.task.Controller;
import org.modelsphere.jack.gui.task.Worker;
import org.modelsphere.jack.plugins.PluginServices;
import org.modelsphere.sms.db.DbSMSProject;
import org.modelsphere.sms.or.db.DbORColumn;
import org.modelsphere.sms.or.db.DbORDataModel;
import org.modelsphere.sms.or.db.DbORTable;
import org.modelsphere.sms.preference.DirectoryOptionGroup;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text; 
import org.w3c.dom.Comment; 

public class ExportXMLWorker extends Worker {

	private Integer idModel; 
	private DbSMSProject m_proj ;  
	private DbProjectWrapper m_wProj ;  
	private Document xmldoc;

	// Script all properties 
	private Boolean scriptAll = false; 

	
	public ExportXMLWorker() {
		idModel = 0; 
	}

	@Override
	protected String getJobTitle() {
		String title = LocaleMgr.misc.getString("ExportMRD");
		return title;
	}

	@Override
	protected void runJob() throws Exception {

		String defaultFolderName = DirectoryOptionGroup.getDefaultWorkingDirectory();

		// Read
		PluginServices.multiDbBeginTrans(Db.READ_TRANS, null);

		DbProject dbProject = PluginServices.getCurrentProject();  
		m_proj = (DbSMSProject) dbProject;
		m_wProj = new DbProjectWrapper(m_proj);  
		String projectName = m_wProj.getName().toString(); 

		generateProject(projectName);
		PluginServices.multiDbCommitTrans();

		// Serialize
		writeXmlFile(xmldoc, defaultFolderName + "/" + projectName + ".exp" ); 

		// if success
		Controller controller = this.getController();
		if (controller.getErrorsCount() == 0) {
			String pattern = LocaleMgr.misc.getString("SuccessFile0Generated");
			String msg = MessageFormat.format(pattern, projectName); 
			controller.println(msg);
		}

	} //end runJob()

	
	private void addChildValue( Element nRoot , String nName , String nValue , String vDefault )
	{ 
		
		if  (!( nValue.equals(vDefault) ) || scriptAll ) {
			addChildValue( nRoot , nName , nValue  );
		}
		
	}

	private void addChildValue( Element nRoot , String nName , String nValue  ) 
	{

		
		if (( nValue.length() > 0 ) || scriptAll )  { 

			Element xChild = xmldoc.createElement(nName);
		    nRoot.appendChild(xChild);
		    Text elmnt = xmldoc.createTextNode(nValue);
		    xChild.appendChild(elmnt);

		}
		
//		  //create a comment and put it in the xChild element
//			Element xChild = xmldoc.createElement("Ejemplo");
//			root.appendChild(xChild);
//			Comment comment = xmldoc.createComment("Comentario"); 
//			xChild.appendChild(comment);
	//	
//		  //create child element, add an attribute, and add to xChild  
//			Element child = xmldoc.createElement("child");
//			child.setAttribute("name", "value");
//			xChild.appendChild(child);
	//	
//		  //add a text element to the child
//			Text text = xmldoc.createTextNode("Filler, ... I could have had a foo!");
//			child.appendChild(text);
	//			
//	      //   ******** <nodo>Employee:spring</nodo>
//		    Document doc = builder.newDocument();
//		    Element rootx = xmldoc.createElement("nodo");
//		       xmldoc.appendChild(rootx);
//		    Text elmnt= xmldoc.createTextNode("Employee");
//		       rootx.appendChild(elmnt);
//		    Text childElement = xmldoc.createTextNode(":spring");
//		          root.appendChild(childElement);

			//   ********
		
	}
	
	
	private void addChildAttr( Element nRoot , String nName , String nValue  ) 
	{
		if (( nValue.trim().length() > 0 ) || scriptAll )  { 
			nRoot.setAttribute(nName, nValue);
		}
	}		

	
	private void generateProject(String projectName) 
	throws Exception {

		// Create XML DOM document (DOM Memory consuming ?).
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		// Document.
		// pattern = "<project name=\"{0}\">"; 
		xmldoc = impl.createDocument( null, "domains", null);
		Element root = xmldoc.getDocumentElement();

		
		Element e = xmldoc.createElement("domain");
		Element xDomain = (Element) root.appendChild(e);
		
//		root.setAttribute( "name", projectName);
		addChildValue (xDomain,  "code", projectName ); 
		addChildValue (xDomain,  "origin", "OpenModelSphere 3.2"); 

//		generate UDFs   
		generateUDFs(xDomain); 

		
//		Models
		Element e1 = xmldoc.createElement("models");
		Element xModels = (Element) xDomain.appendChild(e1);
		

		//for data model in the project
		DbEnumeration enu = m_proj.getComponents().elements(DbORDataModel.metaClass);
		while (enu.hasMoreElements()) {
			DbORDataModel model = (DbORDataModel)enu.nextElement(); 
			generateDataModel(xModels , model, 0);
		} 
		enu.close(); 

		//generate LinkModel 
		generateLinkModel(xDomain); 
		
		
	}

	// private methods    ***************************************************************************

	private void generateLinkModel(Element xDomain) throws DOMException, DbException {

		List<DbLinkModelWrapper> linkModels = m_wProj.getLinkModels();
		if (linkModels.isEmpty()) return ; 
		
		Element e = xmldoc.createElement("linkModels");
		Node xnLinks = xDomain.appendChild(e);

		for (DbLinkModelWrapper linkModel : linkModels ) {

			e = xmldoc.createElement( "linkModel");
//			e.setAttribute("name", linkModel.getName().toString());
//			e.setAttribute("source", linkModel.getSource().toString());
//			e.setAttribute("destination", linkModel.getDestination().toString());
			Element xnLm = (Element) xnLinks.appendChild(e);

			addChildValue( e, "code", linkModel.getName().toString());
			addChildValue( e, "source", linkModel.getSource().toString());
			addChildValue( e, "destination", linkModel.getDestination().toString());

			
			List<DbLinkWrapper> lnks = linkModel.getLinks();
			for (DbLinkWrapper lnk: lnks) {

				e = xmldoc.createElement( "link");
//				e.setAttribute("name", lnk.getName().toString());
//				e.setAttribute("alias", lnk.getAlias().toString());
//				e.setAttribute("destinationText", lnk.getDestinationText().toString());
				Element xnRef = (Element) xnLm.appendChild(e);

				addChildValue( e, "code", lnk.getName().toString());
				addChildValue( e, "alias", lnk.getAlias().toString());
				addChildValue( e, "destinationText", lnk.getDestinationText().toString());

				generateColumnsRef( xnRef , lnk.getSourceColumns(), "sourceCol" ); 
				generateColumnsRef( xnRef , lnk.getDestinationColumns(), "destinationCol" ); 
				
			}

			
		}
		
	}

	private void generateUDFs(Element xDomain) throws DbException {

		List<DbUdfWrapper> uDfs = m_wProj.getUdfs();
		if (uDfs.isEmpty()) return ; 
		
		Element e = xmldoc.createElement("udpDefinitions");
		Node xnUdfs = xDomain.appendChild(e);

		//	pattern = "<udf name=\"{0}\" type=\"{1}\" alias=\"{2}\" description=\"{3}\" />";
		for (DbUdfWrapper udf : uDfs ) {

			e = xmldoc.createElement( "udpDefinition");
//			e.setAttribute("name", udf.getName());
//			e.setAttribute("type", udf.getType());
//			e.setAttribute("alias", udf.getAlias());
//			e.setAttribute("description", udf.getDescription());
			xnUdfs.appendChild(e);

			addChildValue( e, "code", udf.getName());
			addChildValue( e, "baseType", udf.getType());
			addChildValue( e, "alias", udf.getAlias());
			addChildValue( e, "description", udf.getDescription());
		
		}
	}

	private void generateDataModel(Element root, DbORDataModel model, Integer idRef) throws DbException {

		// pattern = "<datamodel name=\"{0}\" idmodel=\"{1}\" idref=\"{2}\">"; 
		idModel = idModel+1; 
		Element e = xmldoc.createElement("model");
		Element xnDm = (Element) root.appendChild(e);

//		e.setAttribute("idModel", idModel.toString() );
//		e.setAttribute("idRef", idRef.toString() );
		addChildValue( e, "idModel", idModel.toString() );
		addChildValue( e, "idRef", idRef.toString() );

		idRef = idModel; 

//		
		addChildValue( e, "code", model.getName());

		// Generate Tables 
		generateTables(xnDm, model);

		//generate Associations   
		//generateAssociations(xnDm, model); 
		
		//for dataModels in the model 
		DbEnumeration enu2 = model.getComponents().elements(DbORDataModel.metaClass);
		while (enu2.hasMoreElements()) {
			DbORDataModel smodel = (DbORDataModel)enu2.nextElement(); 
			generateDataModel(root, smodel, (int)idRef);
		} 
		enu2.close(); 

	}


	private void generateAssociations(Element root, DbORDataModel model) throws DbException {

		Element e = xmldoc.createElement("relationships");
		Node xnRels = root.appendChild(e);
		
		DbDataModelWrapper wModel = new DbDataModelWrapper(m_wProj, model); 
		List<DbORAssociationWrapper> associations = wModel.getAssociations();

		for (DbORAssociationWrapper wRef : associations ) {

			e = xmldoc.createElement( "relationship");
			Element xnRel = (Element) xnRels.appendChild(e);

			String sAux = wRef.getName().toString();
			if ( sAux.length() == 0 ) {
				sAux = wRef.getPairName().toString();
				addChildValue( e, "code", sAux);
			} else {
				addChildValue( e, "code", sAux);
//				addChildValue( e, "pairName", wRef.getPairName().toString() );
			}

//  		
			generateAssociationsEnd(xnRel, wRef.getBase(), "r0", "base"); 
			generateAssociationsEnd(xnRel, wRef.getRefe(), "r1", "ref"); 

		}
		
	}

	
	private void generateAssociationsEnd(Element xnRel, DbORAssociationEndWrapper wEnd, String nName, String preFix) 
		throws DOMException, DbException {

//		Element e = xmldoc.createElement(nName);
//		Element xnRef = (Element) xnRel.appendChild(e);
		
		addChildValue( xnRel, preFix + "Concept", wEnd.getTableName());
		addChildValue( xnRel, preFix + "Min", wEnd.getMin());
		addChildValue( xnRel, preFix + "Max", wEnd.getMax());
		
//		generateColumnsRef( xnRef , wEnd.getColumns(), "refCol" ); 
		
	}

	private void generateColumnsRef(Element xnRef, List<DbColumnWrapper> columns, String tag) throws DOMException, DbException {

		for (DbColumnWrapper wColRef :columns) {

			addChildValue( xnRef, tag,  wColRef.getName().toString()); 
//			Element e = xmldoc.createElement( tag);
//			e.setAttribute("name", wColRef.getName().toString());
//			xnRef.appendChild(e);

		}
		
	}

	private void generateTables(Element xnDm, DbORDataModel model) throws DbException  { 

		Element e = xmldoc.createElement("concepts");
		Element xnTables = (Element) xnDm.appendChild(e);
		
		//for each table in the data model
		DbEnumeration enu = model.getComponents().elements(DbORTable.metaClass);
		while (enu.hasMoreElements()) {
			DbORTable table = (DbORTable)enu.nextElement(); 
			generateTable(xnTables, model, table);
		} 
		enu.close(); 

	}
	
	
	private void generateTable(Element root, DbORDataModel model, DbORTable table) throws DbException {

//		pattern = "<table name=\"{0}\" alias=\"{1}\" physicalName=\"{2}\" superTable=\"{3}\" >"; 

		DbDataModelWrapper wModel = new DbDataModelWrapper(m_wProj, model); 
		DbTableWrapper wTable = new DbTableWrapper( wModel, table); 

		Element e = xmldoc.createElement("concept");
		Element xnTb = (Element) root.appendChild(e);

		addChildValue( e, "code", wTable.getName().toString());
		addChildValue( e, "alias", wTable.getAlias().toString() );
		addChildValue( e, "physicalName", wTable.getPhysicalName().toString() );
		addChildValue( e, "superConcept", wTable.getSuperTableName().toString() );

		String sDesc = wTable.getDescription(); 
		if (sDesc.length() > 0) { 
			Element xDesc = xmldoc.createElement("description");
			xDesc .setAttribute("text", sDesc );
			e.appendChild(xDesc );
		}

		
		Element e1 = xmldoc.createElement("properties");
		Element xnCols = (Element) xnTb.appendChild(e1);
		
		//for each column in the table
		DbEnumeration enu = table.getComponents().elements(DbORColumn.metaClass);
		while (enu.hasMoreElements()) {
			DbORColumn col = (DbORColumn)enu.nextElement(); 
			generateColumn(xnCols, wTable , col);
		} 
		enu.close(); 

	    // 
		generateForeigns( xnTb , model, wTable ); 
	}

	private void generateForeigns(Element root, DbORDataModel model,DbTableWrapper wTable) throws DbException {

		DbDataModelWrapper wModel = new DbDataModelWrapper(m_wProj, model); 
		List<DbORAssociationWrapper> associations = wModel.getAssociations();
		if (associations.isEmpty()) return ; 

		Element e = xmldoc.createElement("foreigns");
		Node xnRels = root.appendChild(e);
		
		for (DbORAssociationWrapper wRef : associations ) {

			String sRefe = wRef.getRefe().getTableName();
			if (sRefe == wTable.getName().toString())  {

				StringWrapper sBase = wRef.getBase().getName();

				e = xmldoc.createElement( "foreign");
				Element xnRel = (Element) xnRels.appendChild(e);
	
				String sAux = wRef.getName().toString();
				if ( sAux.length() == 0 ) {
					sAux = wRef.getPairName().toString();
					addChildValue( e, "code", sAux);
				} else {
					addChildValue( e, "code", sAux);
					addChildValue( e, "alias", wRef.getPairName().toString() );
				}

				addChildValue( e, "baseConcept", sBase.toString());
//				addChildValue( e, "refConcept", wTable.getName().getCamelCase().toString());
				
				// Multiplicity
				addChildValue( e, "baseMin", wRef.getBase().getMin() );
				addChildValue( e, "baseMax", wRef.getBase().getMax() );

				addChildValue( e, "refMin", wRef.getRefe().getMin() );
				addChildValue( e, "refMax", wRef.getRefe().getMax() );
			
			}
		}

	}
	
	private void generateColumn(Element root, DbTableWrapper wTable, DbORColumn col) throws DbException {

		String s; 
		DbColumnWrapper wCol = new DbColumnWrapper(wTable, col); 

		//pattern = "<column name=\"{0}\" alias=\"{1}\" physicalName=\"{2}\" superColumn=\"{3}\" >";
		Element e = xmldoc.createElement("property");
		Element xnCl = (Element) root.appendChild(e);

		addChildValue( e, "code", wCol.getName().toString());
		addChildValue( e, "alias", wCol.getAlias().toString() );
		addChildValue( e, "physicalName", wCol.getPhysicalName().toString() );

		addChildValue( e, "superProperty", wCol.getSuperColName(m_wProj).toString() );
//		addChildValue( e, "type", wCol.getTypeDesc());
		addChildValue( e, "baseType", wCol.getType());
		
		s = (wCol.getLength() == 0)? "" : wCol.getLength().toString();  addChildValue( e, "length",s );
		s = (wCol.getDecLength() == 0)? "" : wCol.getDecLength().toString(); addChildValue( e, "decLength", s , "0");
		addChildValue( e, "isNullable", wCol.getNullAllowed(), "True");
		
		s = (wTable.isPrimary(col))? "True" : "False"; addChildValue( e, "isUnique", s, "False");

		if (wCol.isForeign()) {
	 		addChildValue( e, "isForeign", "True");
	 		addChildValue( e, "foreignConcept", wCol.getRefTable());
		}
//		else {
//	 		addChildValue( e, "foreign", "False");
//	 		addChildValue( e, "foreignConcept", "");
//		}

// 		String sAux = '"' + wCol.getDescription() + '"'; 
//// 	if ((sAux != null ) && (sAux.length() > 1024)){ sAux = sAux.substring(0, 1023); } 
// 		addChildValue( e, "description", sAux);
		
		//	pattern = "<udf name=\"{0}\" value=\"{1}\"/>";
		String sDesc = wCol.getDescription(); 
		if (sDesc.length() > 0) { 
			Element xDesc = xmldoc.createElement("description");
			xDesc.setAttribute("text", sDesc );
			e.appendChild(xDesc );
		}
		
		// Udfs  
 		generateColUdfs(xnCl, wCol); 
}

	private void generateColUdfs(Element xnCl, DbColumnWrapper wCol) throws DbException {
//		The UDP can be extremely long, need to be formatted q, if 
//		added with no formatting goes straight AddTextNode therefore necessary 
//		add an attribute or add "" 
		
		List<DbUdfWrapper> uDfs = m_wProj.getUdfs();
		if ( uDfs.isEmpty())  return ;   
			
		Element e = xmldoc.createElement("udps");
		Node xnUdfs = xnCl.appendChild(e);
		
		for (DbUdfWrapper udf : uDfs ) {
			String nValue = wCol.getUdfValue(udf.getName()); 
			if (nValue.length() > 0 ) {
			e = xmldoc.createElement( udf.getAlias());
				e.setAttribute( "text", nValue);
			xnUdfs.appendChild(e);
			}
		}

		if (!(xnUdfs.hasAttributes() || xnUdfs.hasChildNodes() )) {
			xnCl.removeChild( e );  
		}

	}
	private void writeXmlFile(Document doc, String filename) {
		try {

			// Prepare the DOM document for writing
			doc.setXmlStandalone(true); 
			Source source = new DOMSource(doc);

			// Prepare the output file
			File file = new File(filename);
			Result result = new StreamResult(file);

			// Write the DOM document to the file
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
			xformer.setOutputProperty(OutputKeys.INDENT,"yes");
			xformer.transform(source, result);

		} catch (TransformerConfigurationException e) {
		} catch (TransformerException e) {
		}
	}	

}
