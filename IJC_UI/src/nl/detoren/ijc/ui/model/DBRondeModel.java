/**
 * Copyright (C) 2016 Leo van der Meulen
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-3.0.html
 *
 * Problemen in deze code:
 * - ...
 */
package nl.detoren.ijc.ui.model;

import java.util.List;

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import nl.detoren.ijc.db.DBRonde;
import nl.detoren.ijc.db.SpelerDatabase;

/**
 * Alle spelers uit de database. Als groep >= 0, alleen spelers
 * uit betreffende groep. Als groep == -1, alle spelers
 * @author Leo van der Meulen
 */
public class DBRondeModel extends AbstractTableModel {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

    private JComponent component;

    private SpelerDatabase database;
    private List<DBRonde> rondes;

    private String[] columnNames = {"Ronde"};

    public DBRondeModel() {
        this(null);
    }

    public DBRondeModel(JComponent component) {
        super();
        database = SpelerDatabase.getInstance();
        this.component = component;
        init();
    }

    /**
     * Reset model en lees alle spelers in de database in
     */
    public final void init() {
        rondes = database.getRondes();
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
        	case 0: //Rondenaam
        		return String.class;
            default:
                return String.class;
        }
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col].toString();
    }

    @Override
    public int getRowCount() {
        if (rondes != null) {
            return rondes.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch (col) {
        	case 0:
        		return rondes.get(row).getRondeNaam();
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false; //col == 0;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        //IJCController.getInstance().setSpelerAanwezigheid(controller.getGroepByID(groepID), row, ((Boolean) value).booleanValue());
        fireTableCellUpdated(row, col);
        component.repaint();
    }

    public Object getToolTip(int row, int col) {
        return "";
    }
}
