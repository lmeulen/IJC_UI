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

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Groepswedstrijden;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;
import nl.detoren.ijc.ui.control.IJCController;

/**
 *
 * @author Leo van der Meulen
 */
public class WedstrijdModel extends AbstractTableModel {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private IJCController controller = null;
    private JComponent component;
    private int groepID;

    private String[] columnNames = {"ID", "Wit", "", "Zwart", "Uitslag"};

    public WedstrijdModel() {
        this(0, null);
    }

    public WedstrijdModel(int groep, JComponent comp) {
        component = comp;
        groepID = groep;
        init();
    }

    public void init() {
        controller = IJCController.getInstance();
    }

    public void setGroep(int groepID) {
        this.groepID = groepID;
    }

    public int getGroep() {
        return groepID;
    }

    @Override
    public int getRowCount() {
        Wedstrijden ws = controller.getWedstrijden();
        if (ws == null) return 0;
        Groepswedstrijden gws = ws.getGroepswedstrijdenNiveau(groepID);
        if (gws != null) {
        ArrayList<Wedstrijd> w = gws.getWedstrijden();
        return w.size();
        } else{
        	return 0;
        }
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int column) {
        if (column >= 0 && column < columnNames.length) {
            return columnNames[column];
        } else {
            return "";

        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Wedstrijd ws = controller.getWedstrijden().getGroepswedstrijdenNiveau(groepID).getWedstrijden().get(rowIndex);
        switch (columnIndex) {
            case 0:
                return new Integer(ws.getId());
            case 1:
                return ws.getWit().getNaam();
            case 2:
                return "-";
            case 3:
                return ws.getZwart().getNaam();
            default:
            	if (ws.isNietReglementair()) {
	                switch (ws.getUitslag()) {
	                    case 0:
	                        return "0-0";
	                    case 1:
	                        return "1-0";
	                    case 2:
	                        return "0-1";
	                    case 3:
	                        return "\u00BD-\u00BD";
	                    default:
	                        return "0-0";
	                }
                } else {
	                switch (ws.getUitslag()) {
                    case 0:
                        return "0-0R";
                    case 1:
                        return "1-0R";
                    case 2:
                        return "0-1R";
                    case 3:
                        return "\u00BD-\u00BDR";
                    default:
                        return "0-0";
	                }
                }
        }
    }

    @Override
    public Class<?> getColumnClass(int col) {
        switch (col) {
            case 0:
                return Integer.class;
            case 1:
                return String.class;
            case 2:
                return String.class;
            case 3:
                return String.class;
            default:
                return String.class;
        }
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return col == 4;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        Wedstrijd ws = controller.getWedstrijden().getGroepswedstrijdenNiveau(groepID).getWedstrijden().get(row);
        ws.setUitslag012(Integer.valueOf((String)value));
        fireTableCellUpdated(row, col);
        component.repaint();
    }

    public boolean isDubbeleWedstrijd(int row) {
        ArrayList<Wedstrijd> wedstrijden = controller.getWedstrijden().getGroepswedstrijdenNiveau(groepID).getWedstrijden();
		if (row < wedstrijden.size()) {
            Wedstrijd wedstrijd = wedstrijden.get(row);
            if (wedstrijd != null) {
            	int wedstrijdnummer = wedstrijd.getWedstrijdnummer();
            	for (int i = 0; i < wedstrijden.size(); ++i) {
            		Wedstrijd wedstrijd2 = wedstrijden.get(i);
            		if ((i != row) && (wedstrijdnummer == wedstrijd2.getWedstrijdnummer())) {
            			return true;
            		}
            	}
            }
        }
        return false;
    }

    public int isEerderGespeeld(int row) {
        ArrayList<Wedstrijd> wedstrijden = controller.getWedstrijden().getGroepswedstrijdenNiveau(groepID).getWedstrijden();
		if (row < wedstrijden.size()) {
            Wedstrijd wedstrijd = wedstrijden.get(row);
            if (wedstrijd != null) {
            	Speler wit = wedstrijd.getWit();
            	wit = controller.getSpelerOpNaam(wit.getNaam());
            	Speler zwart = wedstrijd.getZwart();
            	zwart = controller.getSpelerOpNaam(zwart.getNaam());
            	if ((wit == null) || (zwart == null))
            			return 99;
            	return Math.min(wit.gespeeldTegen(zwart), zwart.gespeeldTegen(wit));
            }
        }
        return 99;
    }
    
    public Object getToolTip(int row, int col) {
        if (row < controller.getWedstrijden().getGroepswedstrijdenNiveau(groepID).getWedstrijden().size()) {
            Wedstrijd ws = controller.getWedstrijden().getGroepswedstrijdenNiveau(groepID).getWedstrijden().get(row);
            Speler wit = ws.getWit();
            Speler zwart = ws.getZwart();
            int rondes = this.isEerderGespeeld(row);
            String rt = "";
            if (rondes==99) {
            	rt = "meer dan 4 ";
            } else {
            	rt = "" + (4-rondes);
            }
            String tt = "<HTML><TABLE><TR><TD BORDER=1 COLSPAN=2 ALIGN=CENTER>";
            tt += wit.toString();
            tt += " heeft " + rt + " wedstrijd(en) geleden";
            tt += " tegen " + zwart.toString() + " gespeeld.</TD></TR>";
            tt += "</TABLE></HTML>";
            return tt;
        } else {
            return "";
        }
    }
}
