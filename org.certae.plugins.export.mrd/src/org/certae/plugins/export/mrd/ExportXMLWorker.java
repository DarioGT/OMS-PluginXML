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

	private void generateProject(String projectName) 
	throws Exception {

		// Create XML DOM document (DOM Memory consuming ?).
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		DOMImplementation impl = builder.getDOMImplementation();

		// Document.
		// pattern = "<project name=\"{0}\">"; 
		xmldoc = impl.createDocument( null, "project", null);
		Element root = xmldoc.getDocumentElement();
		root.setAttribute( "name", projectName);

		//generate UDFs  
		generateUDFs(root); 

		//for data model in the project
		DbEnumeration enu = m_proj.getComponents().elements(DbORDataModel.metaClass);
		while (enu.hasMoreElements()) {
			DbORDataModel model = (DbORDataModel)enu.nextElement(); 
			generateDataModel(root, model, 0);
		} 
		enu.close(); 

		//generate LinkModel 
		generateLinkModel(root); 
		
		
	}

	// private methods    ***************************************************************************

	private void generateLinkModel(Element root) throws DOMException, DbException {

		Element e = xmldoc.createElement("linkModels");
		Node xnLinks = root.appendChild(e);

		List<DbLinkModelWrapper> linkModels = m_wProj.getLinkModels();
		for (DbLinkModelWrapper linkModel : linkModels ) {

			e = xmldoc.createElement( "linkModel");
			e.setAttribute("name", linkModel.getName().toString());
			e.setAttribute("source", linkModel.getSource().toString());
			e.setAttribute("destination", linkModel.getDestination().toString());

			Element xnLm = (Element) xnLinks.appendChild(e);

			List<DbLinkWrapper> lnks = linkModel.getLinks();
			for (DbLinkWrapper lnk: lnks) {

				e = xmldoc.createElement( "link");
				e.setAttribute("name", lnk.getName().toString());
				e.setAttribute("alias", lnk.getAlias().toString());
				e.setAttribute("destinationText", lnk.getDestinationText().toString());
				Element xnRef = (Element) xnLm.appendChild(e);

				generateColumnsRef( xnRef , lnk.getSourceColumns(), "sourceCol" ); 
				generateColumnsRef( xnRef , lnk.getDestinationColumns(), "destinationCol" ); 
				
			}

			
		}
		
	}

	private void generateUDFs(Element root) throws DbException {

		Element e = xmldoc.createElement("udfs");
		Node xnUdfs = root.appendChild(e);

		//	pattern = "<udf name=\"{0}\" type=\"{1}\" alias=\"{2}\" description=\"{3}\" />";
		List<DbUdfWrapper> uDfs = m_wProj.getUdfs();
		for (DbUdfWrapper udf : uDfs ) {

			e = xmldoc.createElement( "udf");
			e.setAttribute("name", udf.getName());
			e.setAttribute("type", udf.getType());
			e.setAttribute("alias", udf.getAlias());
			e.setAttribute("description", udf.getDescription());
			xnUdfs.appendChild(e);
		}
	}

	private void generateDataModel(Element root, DbORDataModel model, Integer idRef) throws DbException {

		// pattern = "<datamodel name=\"{0}\" idmodel=\"{1}\" idref=\"{2}\">"; 
		idModel = idModel+1; 
		Element e = xmldoc.createElement("datamodel");
		e.setAttribute("name", model.getName());
		e.setAttribute("idmodel", idModel.toString() );
		e.setAttribute("idref", idRef.toString() );
		Element xnDm = (Element) root.appendChild(e);
		idRef = idModel; 

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

		Element e = xmldoc.createElement("relations");
		Node xnRels = root.appendChild(e);
		
		DbDataModelWrapper wModel = new DbDataModelWrapper(m_wProj, model); 
		List<DbORAssociationWrapper> associations = wModel.getAssociations();

		for (DbORAssociationWrapper wRef : associations ) {

			e = xmldoc.createElement( "relation");
			e.setAttribute("LogicalName", wRef.getName().toString());
			e.setAttribute("name", wRef.getPairName());

			Element xnRel = (Element) xnRels.appendChild(e);

			generateAssociationsEnd(xnRel, wRef.getBase(), "r0"); 
			generateAssociationsEnd(xnRel, wRef.getRefe(), "r1"); 

		}
		
	}

	
	private void generateAssociationsEnd(Element xnRel, DbORAssociationEndWrapper wEnd, String nName) throws DOMException, DbException {

		Element e = xmldoc.createElement(nName);
		e.setAttribute("name", wEnd.getTableName());
		e.setAttribute("multiplicity", wEnd.getMultiplicity() );
		e.setAttribute("cardianlity", wEnd.getCardinality() );
		Element xnRef = (Element) xnRel.appendChild(e);

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

		Element e = xmldoc.createElement("tables");
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

		Element e = xmldoc.createElement("table");
		e.setAttribute("name", wTable.getName().toString());
		e.setAttribute("alias", wTable.getAlias().toString() );
		e.setAttribute("physicalName", wTable.getPhysicalName().toString() );
		e.setAttribute("superTable", wTable.getSuperTableName().toString() );
		Element xnTb = (Element) root.appendChild(e);
		
		//for each column in the table
		DbEnumeration enu = table.getComponents().elements(DbORColumn.metaClass);
		while (enu.hasMoreElements()) {
			DbORColumn col = (DbORColumn)enu.nextElement(); 
			generateColumn(xnTb, wTable , col);
		} 
		enu.close(); 

//		generateRelations(xnTb, wTable ); 
		
	}

	private void generateColumn(Element root, DbTableWrapper wTable, DbORColumn col) throws DbException {

		String s; 
		DbColumnWrapper wCol = new DbColumnWrapper(wTable, col); 

		//pattern = "<column name=\"{0}\" alias=\"{1}\" physicalName=\"{2}\" superColumn=\"{3}\" >";
		Element e = xmldoc.createElement("column");
		e.setAttribute("name", wCol.getName().toString());
		e.setAttribute("alias", wCol.getAlias().toString() );
		e.setAttribute("physicalName", wCol.getPhysicalName().toString() );
		e.setAttribute("superColumn", wCol.getSuperColName(m_wProj).toString() );
		e.setAttribute("type", wCol.getTypeDesc());
		e.setAttribute("nullable", wCol.getNullAllowed());
		
		s = (wTable.isPrimary(col))? "True" : "False"; e.setAttribute("primary", s);
 		s = (wCol.isForeign())? "True" : "False"; e.setAttribute("foreign", s);
		
		Element xnCl = (Element) root.appendChild(e);

		// Udfs
		generateColUdfs(xnCl, wCol); 

}

	private void generateColUdfs(Element xnCl, DbColumnWrapper wCol) throws DbException {
		//  pattern = "<udfs>";
		
		Element e = xmldoc.createElement("udfs");
		Node xnUdfs = xnCl.appendChild(e);
		
		//	pattern = "<udf name=\"{0}\" value=\"{1}\"/>";
		e = xmldoc.createElement("udf");
		e.setAttribute("description", wCol.getDescription());
		xnUdfs.appendChild(e);

		List<DbUdfWrapper> uDfs = m_wProj.getUdfs();
		for (DbUdfWrapper udf : uDfs ) {

			e = xmldoc.createElement( "udf");
			e.setAttribute(udf.getAlias(), wCol.getUdfValue(udf.getName()));
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
