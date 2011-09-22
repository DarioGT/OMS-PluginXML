package org.certae.plugins.export.mrd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.certae.plugins.export.mrd.international.LocaleMgr;
import org.certae.plugins.export.mrd.wrappers.DbColumnWrapper;
import org.certae.plugins.export.mrd.wrappers.DbDataModelWrapper;
import org.certae.plugins.export.mrd.wrappers.DbORAssociationWrapper;
import org.certae.plugins.export.mrd.wrappers.DbProjectWrapper;
import org.certae.plugins.export.mrd.wrappers.DbTableWrapper;
import org.certae.plugins.export.mrd.wrappers.StringWrapper;
import org.modelsphere.jack.baseDb.db.Db;
import org.modelsphere.jack.baseDb.db.DbEnumeration;
import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.baseDb.db.DbProject;
import org.modelsphere.jack.gui.task.Controller;
import org.modelsphere.jack.gui.task.Worker;
import org.modelsphere.jack.io.IndentWriter;
import org.modelsphere.jack.plugins.PluginServices;
import org.modelsphere.sms.db.DbSMSProject;
import org.modelsphere.sms.or.db.DbORColumn;
import org.modelsphere.sms.or.db.DbORDataModel;
import org.modelsphere.sms.or.db.DbORTable;
import org.modelsphere.sms.preference.DirectoryOptionGroup;


public class ExportDjangoModel extends Worker {

  private int idModel; 
  private DbSMSProject m_proj ;  
  private DbProjectWrapper m_wProj ;  
  
  IndentWriter iwA; FileWriter fwA; 

  
  public ExportDjangoModel() {
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

    // Dg
    PluginServices.multiDbBeginTrans(Db.READ_TRANS, null);
    DbProject dbProject = PluginServices.getCurrentProject();
    String projectName = dbProject.getName() + ".py";
    PluginServices.multiDbCommitTrans();
    //--------------------------

    File defaultFolder = new File(defaultFolderName);
    File outputFile = new File(defaultFolder, "model_"  + projectName );
    
    FileWriter fwM = new FileWriter(outputFile);
    IndentWriter iwM = new IndentWriter(fwM, true, 4);
    iwM.setSpacesOnly(true);

    //
    iniFileAdmin(projectName);

    // Dg
    PluginServices.multiDbBeginTrans(Db.READ_TRANS, null);
    //--------------------------

    // Asegurar la raiz del arbol y comenzar a recorrer los diagramas
    m_proj = (DbSMSProject) dbProject;
    m_wProj = new DbProjectWrapper(m_proj);  
    
    
    generateProject(iwM );

    // Dg
    PluginServices.multiDbCommitTrans();
    //--------------------------
    
    fwM.close();  iwM.close(); 
    fwA.close();  iwA.close(); 
    
   
    //if success
    Controller controller = this.getController();
    if (controller.getErrorsCount() == 0) {
      String pattern = LocaleMgr.misc.getString("SuccessFile0Generated");
      String msg = MessageFormat.format(pattern, outputFile); 
      controller.println(msg);
    }
  } //end runJob()

  private void generateProject(IndentWriter iw)
    throws DbException {
	  
    iniFileModel(iw);
	  
    //for data model in the project
    DbEnumeration enu = m_proj.getComponents().elements(DbORDataModel.metaClass);
    while (enu.hasMoreElements()) {
      DbORDataModel model = (DbORDataModel)enu.nextElement(); 
      generateDataModel(iw, model, 0);
    } 
    enu.close(); 
    
    //write closing tag
    iw.unindent();

  }

private void iniFileModel(IndentWriter iw) throws DbException {
	//write opening tag
	iw.println( "# This is an auto-generated model module by CeRTAE OMS PlugIn");
	
	String pattern = "# for project : \"{0}\" >"; 
    StringWrapper msg = new StringWrapper( MessageFormat.format(pattern, m_wProj.getName()));  
    iw.println( msg );

	iw.println( "# You'll have to do the following manually to clean this up:");
	iw.println( "#     * Rearrange models' order");
	iw.println( "");
	iw.println( "from django.db import models" );
	iw.println( "from django.utils.encoding import force_unicode") ;  
	iw.println( "");
}

private void iniFileAdmin(String projectName) throws DbException, IOException {
	
	String defaultFolderName = DirectoryOptionGroup.getDefaultWorkingDirectory();

	File defaultFolder = new File(defaultFolderName);
    File outputFileA = new File(defaultFolder, "admin_"  + projectName );
    fwA = new FileWriter(outputFileA);
    iwA = new IndentWriter(fwA, true, 4);
    iwA.setSpacesOnly(true);

	//write opening tag
	iwA.println( "# This is an auto-generated model module by CeRTAE OMS PlugIn");
	
	String pattern = "# for project : \"{0}\" >"; 
    StringWrapper msg = new StringWrapper( MessageFormat.format(pattern, projectName));  
    iwA.println( msg );
	iwA.println( "");

	iwA.println( "from django.contrib import admin"); 
	iwA.println( "from models import *");

	iwA.println( "");
}

  //
  // private methods
  //

  private void generateDataModel(IndentWriter iw, DbORDataModel model, int idRef) throws DbException {

	  //write opening tag
    String pattern = "#datamodel name=\"{0}\" idmodel=\"{1}\" idref=\"{2}\">"; 
    
    // Dg
    idModel = idModel+1; 
    String msg = MessageFormat.format(pattern, model.getName(), idModel, idRef);  
    iw.println( msg);
    idRef = idModel; 
    
    //for each table in the data model
    DbEnumeration enu = model.getComponents().elements(DbORTable.metaClass);
    while (enu.hasMoreElements()) {
      DbORTable table = (DbORTable)enu.nextElement(); 
      generateTable(iw, model, table);
    } 
    enu.close(); 
    iw.println("");
    
    //for data model in the model 
    DbEnumeration enu2 = model.getComponents().elements(DbORDataModel.metaClass);
    while (enu2.hasMoreElements()) {
      DbORDataModel smodel = (DbORDataModel)enu2.nextElement(); 
      generateDataModel(iw, smodel, (int)idRef);
    } 
    enu2.close(); 
  
  }

  private void generateTable(IndentWriter iw, DbORDataModel model, DbORTable table) throws DbException {

	//write opening tag
    String pattern = "class {0}(models.Model):"; 
    DbDataModelWrapper wModel = new DbDataModelWrapper(m_wProj, model); 
    DbTableWrapper wTable = new DbTableWrapper( wModel, table); 
    
    String sTable = wTable.getName().getCapitalized().toString(); 
    String msg = MessageFormat.format(pattern, sTable );  
    iw.println( msg);
    iw.indent(); 

    generateAdminReg(wTable);
    
    
    //Auto Primary Key 
    pattern = "id{0} = models.AutoField(primary_key=True, editable=False)"; 
    msg = MessageFormat.format(pattern, wTable.getName().getCapitalized());  
    iw.println( msg);
    
    //for each column in the table
    DbEnumeration enu = table.getComponents().elements(DbORColumn.metaClass);
    while (enu.hasMoreElements()) {
      DbORColumn col = (DbORColumn)enu.nextElement(); 
      generateColumn(iw, wTable , col);
    } 
    enu.close(); 
   
    
    // foreigns 
    String sRels = "'"; 
    
	List<DbORAssociationWrapper> associations = wModel.getAssociations();
	for (DbORAssociationWrapper wRef : associations ) {

		String sRefe = wRef.getRefe().getTableName();
		if (sRefe == wTable.getName().toString())  {

			StringWrapper sBase = wRef.getBase().getName();
			String sRel = sBase.getCamelCase().toString(); 

			String params = wRef.getBase().getMultiplicity(); 
			params = (params == "OPTIONAL")? ", blank=True, null=True": ""; 
		    pattern = "{0} = models.ForeignKey(''{1}''{2})"; 

			// La relation est dupliqué 
			if ( sRels.contains("'" + sRel  + "'")) {
				sRel = sRel + "1";  
				params = params + ", related_name='+'";
			}
			sRels = sRels + sRel + "'";
		    
		    msg = MessageFormat.format(pattern, sRel, sBase.getCapitalized(),params);  
		    iw.println( msg);
		}

	}
    
	
    //write closing tag
    iw.println("def __unicode__(self):");
    iw.indent();

	String keys = ""; 
	msg = "";
    pattern  = "force_unicode(self.{0})";

    enu = table.getComponents().elements(DbORColumn.metaClass);
    while (enu.hasMoreElements()) {
      DbORColumn col = (DbORColumn)enu.nextElement(); 
      DbColumnWrapper wCol = new DbColumnWrapper(wTable, col);

      if (wTable.isPrimary(col) ){
	      if (wCol.isForeign()) {
		      msg = MessageFormat.format(pattern, wCol.getRefTable());
		      if (keys.contains(msg)) msg = "";  
	      }
	      else {
		      msg = MessageFormat.format(pattern, wCol.getName().getCamelCase());
	      }
	      if  (msg != "" ) { 
		      if  (keys == "" ) { keys = msg; } 
		      else {  keys += " + ' ' + " + msg; };
	      }
      }
    }; 
    enu.close(); 
    
    iw.println("return " + keys);
    iw.unindent();
    
    // Unique Key  
	keys = ""; 	msg = "";
    pattern  = "\"{0}\"";

    enu = table.getComponents().elements(DbORColumn.metaClass);
    while (enu.hasMoreElements()) {
      DbORColumn col = (DbORColumn)enu.nextElement(); 
      DbColumnWrapper wCol = new DbColumnWrapper(wTable, col);

      if (wTable.isPrimary(col) ){
	      if (wCol.isForeign()) {
		      msg = MessageFormat.format(pattern, wCol.getRefTable());
		      if (keys.contains(msg)) msg = "";  
	      }
	      else {
		      msg = MessageFormat.format(pattern, wCol.getName().getCamelCase());
	      }
	      if  (msg != "" ) { 
		      keys += msg + ", " ; 
	      }
      }
    }; 
    enu.close(); 
    if (keys != "") {
        iw.println("class Meta: " );
        iw.indent();
        iw.println("unique_together= ((" + keys + "),)" );
        iw.unindent();

    }
    
    iw.unindent();
    iw.println("");
  }

private void generateAdminReg(DbTableWrapper wTable) throws DbException {
	String pattern = "admin.site.register({0})";;
    String msg = MessageFormat.format(pattern, wTable.getName().getCapitalized() );  
    iwA.println( msg);
}
  

 private void  generateColumn(IndentWriter iw, DbTableWrapper wTable, DbORColumn col) throws DbException {
    	
    String pattern = "{0} = models.{1}(verbose_name=u''{2}''{3})";
    
    DbColumnWrapper wCol = new DbColumnWrapper(wTable, col);
    
    if  (!wCol.isForeign()) {
        String msg = MessageFormat.format(pattern, wCol.getName().getCamelCase(), 
        		wCol.getDjangoType(),
        		wCol.getName().toISO(),
        		wCol.getDjangoColParams()
        		);  
        iw.println( msg);
    	
    }
    
 }
  
  
}
