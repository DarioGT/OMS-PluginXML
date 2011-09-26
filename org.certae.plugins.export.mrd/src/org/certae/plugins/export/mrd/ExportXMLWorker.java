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
		writeXmlFile(xmldoc, defaultFolderName + "/" + projectName + ".xml" ); 

		// if success
		Controller controller = this.getController();
		if (controller.getErrorsCount() == 0) {
			String pattern = LocaleMgr.misc.getString("SuccessFile0Generated");
			String msg = MessageFormat.format(pattern, projectName); 
			controller.println(msg);
		}

	} //end runJob()

	
	private void addChildValue( Element nRoot , String nName , String nValue  ) 
	{

		Element xChild = xmldoc.createElement(nName);
	    nRoot.appendChild(xChild);
	    Text elmnt = xmldoc.createTextNode(nValue);
	    xChild.appendChild(elmnt);

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
		addChildValue (root,  "origin", "OpenModelSphere 3.2"); 

		//generate UDFs   
		generateUDFs(root); 
		
		Element e = xmldoc.createElement("domain");
		Element xDomain = (Element) root.appendChild(e);
		
//		root.setAttribute( "name", projectName);
		addChildValue (xDomain,  "code", projectName ); 

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

	private void generateLinkModel(Element root) throws DOMException, DbException {

		Element e = xmldoc.createElement("linkModels");
		Node xnLinks = root.appendChild(e);

		List<DbLinkModelWrapper> linkModels = m_wProj.getLinkModels();
		for (DbLinkModelWrapper linkModel : linkModels ) {

			e = xmldoc.createElement( "linkModel");
//			e.setAttribute("name", linkModel.getName().toString());
//			e.setAttribute("source", linkModel.getSource().toString());
//			e.setAttribute("destination", linkModel.getDestination().toString());
			Element xnLm = (Element) xnLinks.appendChild(e);

			addChildValue( e, "name", linkModel.getName().toString());
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

	private void generateUDFs(Element root) throws DbException {

		Element e = xmldoc.createElement("udps");
		Node xnUdfs = root.appendChild(e);

		//	pattern = "<udf name=\"{0}\" type=\"{1}\" alias=\"{2}\" description=\"{3}\" />";
		List<DbUdfWrapper> uDfs = m_wProj.getUdfs();
		for (DbUdfWrapper udf : uDfs ) {

			e = xmldoc.createElement( "udp");
//			e.setAttribute("name", udf.getName());
//			e.setAttribute("type", udf.getType());
//			e.setAttribute("alias", udf.getAlias());
//			e.setAttribute("description", udf.getDescription());
			xnUdfs.appendChild(e);

			addChildValue( e, "code", udf.getName());
			addChildValue( e, "type", udf.getType());
			addChildValue( e, "alias", udf.getAlias());
			addChildValue( e, "description", udf.getDescription());
		
		}
	}

	private void generateDataModel(Element root, DbORDataModel model, Integer idRef) throws DbException {

		// pattern = "<datamodel name=\"{0}\" idmodel=\"{1}\" idref=\"{2}\">"; 
		idModel = idModel+1; 
		Element e = xmldoc.createElement("model");
//		e.setAttribute("name", model.getName());
		e.setAttribute("idmodel", idModel.toString() );
		e.setAttribute("idref", idRef.toString() );
		Element xnDm = (Element) root.appendChild(e);
		idRef = idModel; 

//		
		addChildValue( e, "code", model.getName());

		// Generate Tables 
		generateTables(xnDm, model);

		//generate Associations   
		generateAssociations(xnDm, model); 
		
		//for dataModels in the model 
		DbEnumeration enu2 = model.getComponents().elements(DbORDataModel.metaClass);
		while (enu2.hasMoreElements()) {
			DbORDataModel smodel = (DbORDataModel)enu2.nextElement(); 
			generateDataModel(root, smodel, (int)idRef);
		} 
		enu2.close(); 

	}


	private void generateAssociations(Element root, DbORDataModel model) throws DbException {

		Element e = xmldoc.createElement("neighbors");
		Node xnRels = root.appendChild(e);
		
		DbDataModelWrapper wModel = new DbDataModelWrapper(m_wProj, model); 
		List<DbORAssociationWrapper> associations = wModel.getAssociations();

		for (DbORAssociationWrapper wRef : associations ) {

			e = xmldoc.createElement( "neighbor");
//			e.setAttribute("LogicalName", wRef.getName().toString());
//			e.setAttribute("name", wRef.getPairName());
			Element xnRel = (Element) xnRels.appendChild(e);

			addChildValue( e, "LogicalName", wRef.getName().toString());
			addChildValue( e, "name", wRef.getPairName());

//  		
			generateAssociationsEnd(xnRel, wRef.getBase(), "r0"); 
			generateAssociationsEnd(xnRel, wRef.getRefe(), "r1"); 

		}
		
	}

	
	private void generateAssociationsEnd(Element xnRel, DbORAssociationEndWrapper wEnd, String nName) throws DOMException, DbException {

		Element e = xmldoc.createElement(nName);
		Element xnRef = (Element) xnRel.appendChild(e);
		
		addChildValue( e, "code", wEnd.getTableName());
		addChildValue( e, "multiplicity", wEnd.getMultiplicity() );
		addChildValue( e, "cardianlity", wEnd.getCardinality() );

		
		generateColumnsRef( xnRef , wEnd.getColumns(), "refCol" ); 

		
	}

	private void generateColumnsRef(Element xnRef, List<DbColumnWrapper> columns, String tag) throws DOMException, DbException {

		for (DbColumnWrapper wColRef :columns) {

			Element e = xmldoc.createElement( tag);
			e.setAttribute("name", wColRef.getName().toString());
			xnRef.appendChild(e);

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
		addChildValue( e, "superTable", wTable.getSuperTableName().toString() );

		
		Element e1 = xmldoc.createElement("properties");
		Element xnCols = (Element) xnTb.appendChild(e1);
		
		//for each column in the table
		DbEnumeration enu = table.getComponents().elements(DbORColumn.metaClass);
		while (enu.hasMoreElements()) {
			DbORColumn col = (DbORColumn)enu.nextElement(); 
			generateColumn(xnCols, wTable , col);
		} 
		enu.close(); 

	    // Neighbors
		generateNeighbors ( xnTb , model, wTable ); 
	}

	private void generateNeighbors(Element root, DbORDataModel model,DbTableWrapper wTable) throws DbException {

		Element e = xmldoc.createElement("neighbors");
		Node xnRels = root.appendChild(e);
		
		DbDataModelWrapper wModel = new DbDataModelWrapper(m_wProj, model); 
		List<DbORAssociationWrapper> associations = wModel.getAssociations();

		for (DbORAssociationWrapper wRef : associations ) {

			String sRefe = wRef.getRefe().getTableName();
			if (sRefe == wTable.getName().toString())  {

				StringWrapper sBase = wRef.getBase().getName();

				e = xmldoc.createElement( "neighbor");
				Element xnRel = (Element) xnRels.appendChild(e);
	
				String sAux = wRef.getName().toString();
				if ( sAux.length() == 0 ) {
					sAux = wRef.getPhysicalName().getCapitalized().toString();
				}

				addChildValue( e, "code", sAux);
				addChildValue( e, "pairName", wRef.getPairName().toString() );

				addChildValue( e, "baseConcept", sBase.getCamelCase().toString());
				addChildValue( e, "destinationConcept", wTable.getName().getCamelCase().toString());
				
				//TODO:  Multiplicity
				DbORAssociationEndWrapper wNgBase = wRef.getBase(); 
				DbORAssociationEndWrapper wNgRef = wRef.getRefe(); 
				
				addChildValue( e, "multiplicity", wNgBase.getMin() );
				addChildValue( e, "cardianlity", wNgBase.getMax() );

				addChildValue( e, "multiplicity", wNgRef.getMin() );
				addChildValue( e, "cardianlity", wNgRef.getMax() );
			
			}
		}

	}
	
	
	private void generateColumn(Element root, DbTableWrapper wTable, DbORColumn col) throws DbException {

		String s; 
		DbColumnWrapper wCol = new DbColumnWrapper(wTable, col); 

		//pattern = "<column name=\"{0}\" alias=\"{1}\" physicalName=\"{2}\" superColumn=\"{3}\" >";
		Element e = xmldoc.createElement("property");
//		e.setAttribute("name", wCol.getName().toString());
//		e.setAttribute("alias", wCol.getAlias().toString() );
//		e.setAttribute("physicalName", wCol.getPhysicalName().toString() );
//		e.setAttribute("superColumn", wCol.getSuperColName(m_wProj).toString() );
//		e.setAttribute("type", wCol.getTypeDesc());
//		e.setAttribute("nullable", wCol.getNullAllowed());
//		
//		s = (wTable.isPrimary(col))? "True" : "False"; e.setAttribute("primary", s);
// 		s = (wCol.isForeign())? "True" : "False"; e.setAttribute("foreign", s);
		
		Element xnCl = (Element) root.appendChild(e);

		addChildValue( e, "code", wCol.getName().toString());
		addChildValue( e, "alias", wCol.getAlias().toString() );
		addChildValue( e, "physicalName", wCol.getPhysicalName().toString() );
		addChildValue( e, "superColumn", wCol.getSuperColName(m_wProj).toString() );
		addChildValue( e, "type", wCol.getTypeDesc());
		addChildValue( e, "nullable", wCol.getNullAllowed());
		
		s = (wTable.isPrimary(col))? "True" : "False"; addChildValue( e, "primary", s);
 		s = (wCol.isForeign())? "True" : "False"; addChildValue( e, "foreign", s);

// 		String sAux = '"' + wCol.getDescription() + '"'; 
//// 	if ((sAux != null ) && (sAux.length() > 1024)){ sAux = sAux.substring(0, 1023); } 
// 		addChildValue( e, "description", sAux);
		
		// Udfs  
 		generateColUdfs(xnCl, wCol); 
}

	private void generateColUdfs(Element xnCl, DbColumnWrapper wCol) throws DbException {
//		The UDP can be extremely long, you need to be formatted q, if 
//		added with no formatting goes straight AddTextNode therefore necessary 
//		add an attribute or add "" 
		
		Element e = xmldoc.createElement("udps");
		Node xnUdfs = xnCl.appendChild(e);
		
		//	pattern = "<udf name=\"{0}\" value=\"{1}\"/>";
		e = xmldoc.createElement("description");
		e.setAttribute("text", wCol.getDescription());
		xnUdfs.appendChild(e);

		List<DbUdfWrapper> uDfs = m_wProj.getUdfs();
		for (DbUdfWrapper udf : uDfs ) {

			e = xmldoc.createElement( udf.getAlias());
			e.setAttribute( "text", wCol.getUdfValue(udf.getName()));
			xnUdfs.appendChild(e);

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
