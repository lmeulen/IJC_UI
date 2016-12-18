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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import nl.detoren.ijc.db.DBRonde;
import nl.detoren.ijc.db.DBSpeler;
import nl.detoren.ijc.db.SpelerDatabase;

/**
 * Alle spelers uit de database. Als groep >= 0, alleen spelers
 * uit betreffende groep. Als groep == -1, alle spelers
 * @author Leo van der Meulen
 */
public class DBSpelerModel extends AbstractTableModel {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

    private JComponent component;

    private SpelerDatabase database;
    private List<DBSpeler> spelers;
    private boolean[] selectie;

    private String[] columnNames = {"Select", "Naam", "KNSB"};

    public DBSpelerModel() {
        this(null);
    }

    public DBSpelerModel(JComponent component) {
        super();
        database = SpelerDatabase.getInstance();
        this.component = component;
        init();
    }

    /**
     * Reset model en lees alle spelers in de database in
     */
    public final void init() {
        spelers = database.getSpelers("naam");
    	selectie = new boolean[spelers != null ? spelers.size() : 0];
    }

    /**
     * Laat alleen spelers zien die in de ingestelde groep spelen
     * in de laatste ingevoerde ronde
     * @param id
     */
    public void setGroep(int id) {
    	spelers = id > 0 ? database.getSpelers(id) : null;
    	selectie = new boolean[spelers != null ? spelers.size() : 0];
    }

    /**
     * Laat alleen spelers zien die in de ingestelde ronde
     * hebben gespeeld.
     * @param id
     */
    public void setRonde(DBRonde ronde) {
        spelers = ronde != null ? database.getSpelers(ronde) : database.getSpelers();
    	selectie = new boolean[spelers != null ? spelers.size() : 0];
    }

    public void setGroepEnRonde(int id, DBRonde ronde) {
		spelers = ((ronde != null) && (id > 0)) ? database.getSpelers(id, ronde) : database.getSpelers();
    	selectie = new boolean[spelers != null ? spelers.size() : 0];
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
        	case 0: //Select
        		return Boolean.class;
            case 1: // Naam
                return String.class;
            case 2: // KNSB
                return Integer.class;
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
        if (spelers != null) {
            return spelers.size();
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
        DBSpeler speler = spelers.get(row);
        switch (col) {
        	case 0:
        		return selectie[row];
            case 1:
                return speler.getNaam();
            case 2:
                return speler.getKnsbnummer();
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 0;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        //IJCController.getInstance().setSpelerAanwezigheid(controller.getGroepByID(groepID), row, ((Boolean) value).booleanValue());
    	selectie[row] = ((Boolean) value).booleanValue();
        fireTableCellUpdated(row, col);
        component.repaint();
    }

    public Object getToolTip(int row, int col) {
        return "";
    }

    public List<DBSpeler> getSelectedSpelers() {
    	ArrayList<DBSpeler> result = new ArrayList<>();
    	if (selectie.length > 0) {
    		for (int i = 0; i < selectie.length; ++i) {
    			if (selectie[i])
    				result.add(spelers.get(i));
    		}
    	}
    	return result;
    }
}
