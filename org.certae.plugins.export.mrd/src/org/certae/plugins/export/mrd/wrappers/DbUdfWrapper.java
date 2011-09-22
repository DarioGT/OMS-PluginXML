/*************************************************************************
Copyright (C) 2011  CERTAE  Universite Laval  
Dario Gomez 
 **********************************************************************/
package org.certae.plugins.export.mrd.wrappers;

import org.modelsphere.jack.baseDb.db.DbException;
import org.modelsphere.jack.baseDb.db.DbUDF;

public class DbUdfWrapper {
    private DbUDF m_dbUdf;

    public DbUdfWrapper(DbUDF dbUdf) {
        m_dbUdf = dbUdf;
    }

    public String getName() {
        String name;
        try {
            name = m_dbUdf.getName();
        } catch (DbException ex) {
            name = "";
        } //end try 

        return name;
    }

    public String getType() {
        String s;
        try {
            s = m_dbUdf.getValueType().toString();
        } catch (DbException ex) {
            s = "";
        } //end try 

        return (s == null)? "": s;
    }

    public String getAlias() {
        String s;
        try {
            s = m_dbUdf.getAlias();
        } catch (DbException ex) {
            s = "";
        } //end try 

        if ((s==null) ||( s == "" )) s =  getName(); 
        
        StringWrapper sw = new StringWrapper(s);
        return sw.getCamelCase().toString();
    }

    public String getDescription() {
        String s;
        try {
            s = m_dbUdf.getDescription();
        } catch (DbException ex) {
            s = "";
        } //end try 

        return (s == null)? "": s;
    }

/*    
    public String getNamesAndValues(Matrix.Row row) {
        String namesAndValues = "";

        for (Column column : row.getColumns()) {
            DbDataModelWrapper dataModel = column.getDataModel();
            Cell cell = column.isSummation() ? null : row.m_cells.get(dataModel);

            if (column.isSummation()) {
                List<Cell> summatedCells = Cell.summateCells(row.m_cells, dataModel);
                namesAndValues += getSummatedNamesAndValues(summatedCells);
            } else if (cell == null) {
                namesAndValues += ";;;" + Matrix.V_SEPARATOR + ";";
            } else {
                String nameAndValue = cell.getNameAndValue(this);
                namesAndValues += ";" + nameAndValue + ";" + Matrix.V_SEPARATOR + ";";
            } //end if
        } //end for

        return namesAndValues;
    } //get getNamesAndValues()

    private String getSummatedNamesAndValues(List<Cell> summatedCells) {
        String summatedNamesAndValues;

        int nb = summatedCells.size();
        if (nb == 0) {
            summatedNamesAndValues = ";;;" + Matrix.V_SEPARATOR + ";";
        } else if (nb == 1) {
            String nameAndValue = summatedCells.get(0).getNameAndValue(this);
            summatedNamesAndValues = ";" + nameAndValue + ";" + Matrix.V_SEPARATOR + ";";
        } else {
            summatedNamesAndValues = ";" + summateNamesAndValues(summatedCells) + ";"
                    + Matrix.V_SEPARATOR + ";";
        } //end if

        return summatedNamesAndValues;
    }

    private String summateNamesAndValues(List<Cell> summatedCells) {
        String guiName = getName();
        String guiValue = "";
        String nameAndValue = guiName + ";" + guiValue;;
        return nameAndValue;
    }
*/
    
} //end DbUdfWrapper
