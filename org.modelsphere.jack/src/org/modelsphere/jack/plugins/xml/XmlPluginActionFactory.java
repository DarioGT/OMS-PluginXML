/*************************************************************************

Copyright (C) 2009 by neosapiens inc.

This file is part of Open ModelSphere.

Open ModelSphere is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
or see http://www.gnu.org/licenses/.

You can redistribute and/or modify this particular file even under the
terms of the GNU Lesser General Public License (LGPL) as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

You should have received a copy of the GNU Lesser General Public License 
(LGPL) along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
or see http://www.gnu.org/licenses/.

You can reach neosapiens inc. at: 

neosapiens inc.
1236 Gaudias-Petitclerc
Qu&eacute;bec, Qc, G1Y 3G2
CANADA
Telephone: 418-561-8403
Fax: 418-650-2375
http://www.neosapiens.com/
Email: marco.savard@neosapiens.com
       gino.pelletier@neosapiens.com

 **********************************************************************/
package org.modelsphere.jack.plugins.xml;

import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.modelsphere.jack.actions.AbstractActionsStore;
import org.modelsphere.jack.awt.JackMenu;
import org.modelsphere.jack.plugins.Plugin2;
import org.modelsphere.jack.plugins.PluginAction;
import org.modelsphere.jack.plugins.xml.extensions.AbstractPluginExtension;
import org.modelsphere.jack.srtool.ApplicationContext;
import org.modelsphere.jack.srtool.DefaultMainFrame;
import org.modelsphere.jack.srtool.MainFrameMenu;
import org.modelsphere.jack.srtool.Splash;
import org.modelsphere.jack.srtool.actions.PluginDefaultAction;
import org.modelsphere.jack.srtool.popupMenu.ApplicationPopupMenu;

class XmlPluginActionFactory {
	
	private static XmlPluginActionFactory g_instance;
	static XmlPluginActionFactory getInstance() {
		if (g_instance == null) {
			g_instance = new XmlPluginActionFactory();
		}
		
		return g_instance;
	}
	
	void createPluginAction(Splash splash, XmlPluginDescriptor pluginDescriptor) {
		List<AbstractPluginExtension> extensions = pluginDescriptor.getPluginExtensions();
		
		for (AbstractPluginExtension extension : extensions) {
			extension.createPluginAction(pluginDescriptor);
		}
	} //end createPluginActiopn()
}
