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

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;

/**
 *
 * @author Leo van der Meulen
 */
public class SpelersModel extends AbstractTableModel {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private int groepID;

    private IJCController controller;
    private JComponent component;

    private String[] columnNames = {"Aanw", "Nr.", "Naam", "Afk", "Rating", "Punten"};

    public SpelersModel() {
        this(0, null);
    }

    public SpelersModel(int groep, JComponent component) {
        super();
        this.groepID = groep;
        this.component = component;
        init();
    }

    public final void init() {
        controller = IJCController.getInstance();
    }

    public void setGroepID(int id) {
        this.groepID = id;
    }

    public int getGroepID() {
        return groepID;
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
        	case 0:
        		return Boolean.class;
            case 1:
                return Integer.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            case 4:
                return Integer.class;
            case 5:
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
        if (controller.getGroepByID(groepID) != null) {
            return controller.getGroepByID(groepID).getSpelers().size();
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
        Speler speler = controller.getGroepByID(groepID).getSpelers().get(row);
        switch (col) {
        	case 0:
        		return speler.isAanwezig();
            case 1:
                return new Integer(speler.getId());
            case 2:
                return speler.getNaam();
            case 3:
                return speler.getInitialen();
            case 4:
                return new Integer(speler.getRating());
            case 5:
                return new Integer(speler.getPunten());
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
        IJCController.getInstance().setSpelerAanwezigheid(controller.getGroepByID(groepID), row, ((Boolean) value).booleanValue());
        fireTableCellUpdated(row, col);
        component.repaint();
    }

    public Object getToolTip(int row, int col) {
        Speler speler = controller.getGroepByID(groepID).getSpelers().get(row);
        String tt = "<HTML><TABLE><TR><TD BORDER=1 COLSPAN=2 ALIGN=CENTER>";
        tt += speler.toString();
        tt += "</TD></TR>";
        tt += "<TR><TD>UUID</TD><TD>" + speler.getUid() + "</TD></TR>";
        tt += "<TR><TD>KNSB nr</TD><TD>" + speler.getKNSBnummer() + "</TD></TR>";
        tt += "<TR><TD>Punten</TD><TD>" + speler.getPunten() + "</TD></TR>";
        tt += "<TR><TD>KEI Punten</TD><TD>" + speler.getKeipunten() + "/" + speler.getKeikansen()+"</TD></TR>";
        tt += "<TR><TD>Rating</TD><TD>" + speler.getRating() + "</TD></TR>";
        tt += "<TR><TD>Initialen</TD><TD>" + speler.getInitialen() + " - " + speler.getAfkorting3() + "</TD></TR>";
        tt += "<TR><TD>Witvoorkeur</TD><TD>" + speler.getWitvoorkeur() + "</TD></TR>";
        tt += "<TR><TD>Tegenstanders</TD><TD>";
        for (String tgn : speler.getTegenstanders()) {
            tt += tgn + " ";
        }
        tt += "</TD></TR>";
        tt += "</TABLE></HTML>";
        return tt;
    }
}
