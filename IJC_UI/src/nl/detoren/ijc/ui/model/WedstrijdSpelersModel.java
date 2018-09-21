/**
 * Copyright (C) 2016-2018 Leo van der Meulen & Lars Dam
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
 * @author Leo van der Meulen, Lars Dam
 */
public class WedstrijdSpelersModel extends AbstractTableModel {

    /**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private int groepID;
    private IJCController controller = null;
    private JComponent component;
	private static final String indigohtml = "<font color=rgb(75,0,130)>";
	private static final String purplehtml = "<font color=rgb(128,0,128)>";
	private static final String violetredhtml = "<font color=rgb(199,21,133)>";
	private static final String deeppinkhtml = "<font color=rgb(255,20,147)>";
	private static final String warninghtml = "<font color=rgb(255,0,0)>";

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

    @SuppressWarnings("static-access")
	@Override
    public Object getValueAt(int row, int col) {
        Speler speler = controller.getWedstrijdGroepByID(groepID).getSpelers().get(row);
        switch (col) {
            case 0:
                return new Integer(speler.getId());
            case 1:
            	// Voeg voor een doorgeschoven speler een * toe aan de naam
            	boolean doorgeschoven = controller.getGroepByID(groepID).getNiveau() != speler.getGroep();
            	int aantalRondes = controller.c().bepaalAantalSeries(groepID, controller.getGroepen().getPeriode(), controller.getGroepen().getRonde());
            	if (((row == 0) || row == controller.getWedstrijdGroepByID(groepID).getAantalSpelers()-1) && ((this.getRowCount() & 1) == 1) && (aantalRondes & 1) == 1) {
            		return "<html>" + warninghtml + speler.getNaam() + (doorgeschoven ? "*" : "") + "</font></html>";
            	} else {
            		return speler.getNaam() + (doorgeschoven ? "*" : "");
            	}
            case 2:
            	return new Integer(speler.getRating());
            case 3:
            	return new Integer(speler.getPunten());
            case 4:
                return speler.getInitialen();
            case 5:
            	return "<html>" + getTegenstanderhtml(speler) + "</html>";
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
            tt += getTegenstanderhtml(speler);
            tt += "</TD></TR>";
            tt += "</TABLE></HTML>";
            return tt;
        } else {
            return "";
        }
    }

    public String getTegenstanderhtml(Speler speler) {
    	String[] tegenstanders = speler.getTegenstandersString().split("(?<=\\G.{3})");
    	String htmlstring = "";
    	for (String tegenstander : tegenstanders){
    		String tegen = tegenstander.substring(0,2);
    		int rr = speler.gespeeldTegen(controller.getSpelerOpInitialen(tegen));
    		switch(rr){
    		case 0:
    			htmlstring+=indigohtml + tegenstander + "</font>";
    			break;
    		case 1:
    			htmlstring+=purplehtml + tegenstander + "</font>";
    			break;
    		case 2:
    			htmlstring+=violetredhtml + tegenstander + "</font>";
    			break;
    		case 3:
    			htmlstring+=deeppinkhtml + tegenstander + "</font>";
    			break;
    		default:
    			htmlstring+=tegenstander;
    		}
    	}
    	return htmlstring;
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
