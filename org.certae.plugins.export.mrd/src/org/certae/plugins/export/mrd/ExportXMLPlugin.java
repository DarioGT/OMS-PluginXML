/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/
package org.certae.plugins.export.mrd;

import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Date;

import org.certae.plugins.export.mrd.international.LocaleMgr;
import org.modelsphere.jack.baseDb.db.Db;
import org.modelsphere.jack.gui.task.DefaultController;
import org.modelsphere.jack.gui.task.Worker;
import org.modelsphere.jack.plugins.Plugin2;
import org.modelsphere.jack.plugins.PluginAction;
import org.modelsphere.jack.plugins.PluginServices;
import org.modelsphere.jack.plugins.PluginSignature;
import org.modelsphere.jack.preference.OptionGroup;
import org.modelsphere.jack.srtool.DefaultMainFrame;
import org.modelsphere.jack.srtool.MainFrameMenu;
import org.modelsphere.sms.db.DbSMSProject;
import org.modelsphere.sms.or.db.DbORDataModel;


public class ExportXMLPlugin implements Plugin2 {

  private PluginSignature m_signature = null;
  @Override
  public PluginSignature getSignature() {
    if (m_signature == null) {
      String pluginName = LocaleMgr.misc.getString("ExportMRDPlugin");
      Calendar c = Calendar.getInstance(); 
      c.set(2011, Calendar.JUNE, 24); 
      Date date = c.getTime(); 
      m_signature = new PluginSignature(pluginName, null, "1.1",
			  "D Gomez CeRTAE, M Savard, neosapiens inc.", "http://loli.fsa.ulaval.ca/certae/",
				null, date, null, 905);
    }
    
    return m_signature;
  }
  
  @Override
  public String installAction(DefaultMainFrame frame, MainFrameMenu menuManager) {
    return null;
  }
  
  @Override
  public Class<?>[] getSupportedClasses() {
    Class<?>[] classes = new Class[] { DbSMSProject.class, DbORDataModel.class };
    return classes;
  }
  
  @Override
  public OptionGroup getOptionGroup() {
    return null;
  }

  @Override
  public PluginAction getPluginAction() {
    String actionName = LocaleMgr.misc.getString("ExportMRD") + "...";
    PluginAction action = new PluginAction(this, actionName);
    return action;
  }

  @Override
  public void execute(ActionEvent ev) throws Exception {
    
    //open the controller
    String actionName = LocaleMgr.misc.getString("ExportMRD");
    DefaultController controller = new DefaultController(actionName, false, null);
    
    //create the worker
    PluginServices.multiDbBeginTrans(Db.READ_TRANS, null);

//    DbObject[] dbos = PluginServices.getSelectedSemanticalObjects();
    Worker worker = new ExportXMLWorker();
//    Worker worker = new ExportDjangoModel();
    
    PluginServices.multiDbCommitTrans();
    
    //start the worker
    controller.start(worker);
    
  } //end execute()

@Override
public boolean doListenSelection() {
	//TD-- Auto-generated method stub
	return false;
}
}
