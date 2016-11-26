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
public class WedstrijdSpelersModel extends AbstractTableModel {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int groepID;
    private IJCController controller = null;
    private JComponent component;

    private String[] columnNames = {"#", "Naam", "R", "P", " ", "Tegenst."};

    public WedstrijdSpelersModel() {
        this(0, null);
    }

    public WedstrijdSpelersModel(int groepID, JComponent component) {
        super();
        this.groepID = groepID;
        this.component = component;
        init();
    }

    public final void init() {
        controller = IJCController.getInstance();
    }

    public void setGroep(int groepID) {
        this.groepID = groepID;
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0:
                return Integer.class;
            case 1:
                return String.class;
            case 2:
                return Integer.class;
            case 3:
                return Integer.class;
            case 4:
                return String.class;
            case 5:
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
        if (controller.getGroepByID(groepID) != null) {
            return controller.getWedstrijdGroepByID(groepID).getSpelers().size();
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
        Speler speler = controller.getWedstrijdGroepByID(groepID).getSpelers().get(row);
        switch (col) {
            case 0:
                return new Integer(speler.getId());
            case 1:
            	// Voeg voor een doorgeschoven speler een * toe aan de naam
            	boolean doorgeschoven = controller.getGroepByID(groepID).getNiveau() != speler.getGroep();
                return speler.getNaam() + (doorgeschoven ? "*" : "");
            case 2:
            	return new Integer(speler.getRating());
            case 3:
            	return new Integer(speler.getPunten());
            case 4:
                return speler.getInitialen();
            case 5:
            	return speler.getTegenstandersString();
            default:
                return "";
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        fireTableCellUpdated(row, col);
        component.repaint();
    }

    public Object getToolTip(int row, int col) {
        if (row < controller.getWedstrijdGroepByID(groepID).getSpelers().size()) {
            Speler speler = controller.getWedstrijdGroepByID(groepID).getSpelers().get(row);
            String tt = "<HTML><TABLE><TR><TD COLSPAN = 2>";
            tt += speler.toString();
            tt += "</TD></TR>";
            tt += "<TR><TD>KNSB nr</TD><TD>" + speler.getKNSBnummer() + "</TD></TR>";
            tt += "<TR><TD>Punten</TD><TD>" + speler.getPunten() + "</TD></TR>";
            tt += "<TR><TD>KEI Punten</TD><TD>" + speler.getKeipunten() + "/" + speler.getKeikansen()+"</TD></TR>";
            tt += "<TR><TD>Rating</TD><TD>" + speler.getRating() + "</TD></TR>";
            tt += "<TR><TD>Initialen</TD><TD>" + speler.getInitialen() + " - " + speler.getAfkorting3() + "</TD></TR>";
            tt += "<TR><TD>Witvoorkeur</TD><TD>" + speler.getWitvoorkeur() + "</TD></TR>";
            tt += "<TR><TD>Tegenstanders</TD><TD>" + speler.getTegenstandersString() + "</TD></TR>";
            tt += "</TABLE></HTML>";
            return tt;
        } else {
            return "";
        }
    }

    public boolean isDoorgeschoven(int row) {
        if (row < controller.getWedstrijdGroepByID(groepID).getSpelers().size()) {
            Speler speler = controller.getWedstrijdGroepByID(groepID).getSpelers().get(row);
            if (speler != null) {
            	return controller.getGroepByID(groepID).getNiveau() != speler.getGroep();
            }
        }
        return false;
    }
}
