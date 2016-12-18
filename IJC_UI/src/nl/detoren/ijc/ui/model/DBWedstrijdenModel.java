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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import nl.detoren.ijc.db.DBRonde;
import nl.detoren.ijc.db.DBSpeler;
import nl.detoren.ijc.db.DBWedstrijd;
import nl.detoren.ijc.db.SpelerDatabase;

/**
 * Alle spelers uit de database. Als groep >= 0, alleen spelers uit betreffende
 * groep. Als groep == -1, alle spelers
 *
 * @author Leo van der Meulen
 */
public class DBWedstrijdenModel extends AbstractTableModel {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private JComponent component;

	private SpelerDatabase database;
	private List<DBWedstrijd> wedstrijden;

	private String[] columnNames = { "Seizoen", "Periode", "Ronde", "Speler", "Kleur", "Tegenstander", "Resultaat" };

	public DBWedstrijdenModel() {
		this(null);
	}

	public DBWedstrijdenModel(JComponent component) {
		super();
		database = SpelerDatabase.getInstance();
		this.component = component;
		init();
	}

	/**
	 * Reset model en lees alle wedstrijden in de database in
	 */
	public final void init() {
		wedstrijden = database.getWedstrijden();
	}

	/**
	 * Laat alleen wedstrijden voor geselecteerde speler
	 *
	 * @param id
	 */
	public void setSpeler(DBSpeler speler) {
		wedstrijden = speler != null ? database.getWedstrijdenVoorSpeler(speler) : null;
		sorteer();
	}

	/**
	 * Laad wedstrijden voor set van spelers
	 * @param spelers
	 */
	public void setSpelers(List<DBSpeler> spelers) {
		if ((spelers != null) && (spelers.size() > 0)) {
			wedstrijden = new ArrayList<>();
			for (DBSpeler speler : spelers) {
				wedstrijden.addAll(database.getWedstrijdenVoorSpeler(speler));
			}
		} else {
			wedstrijden = database.getWedstrijden();
		}
		sorteer();
	}

	/**
	 * Laat alleen wedstrijden voor geselecteerde ronde
	 *
	 * @param id
	 */
	public void setRonde(DBRonde ronde) {
		wedstrijden = ronde != null ? database.getWedstrijdenVoorRonde(ronde) : null;
		sorteer();
	}

	/**
	 * Laat alleen wedstrijden voor geselecteerde ronde
	 *
	 * @param id
	 */
	public void setRondeEnSpeler(DBRonde r, DBSpeler s) {
		wedstrijden = ((r != null) && (s != null)) ? database.getWedstrijdenVoorSpeler(s, r) : null;
		sorteer();
	}

	@Override
	public Class<?> getColumnClass(int col) {
		switch (col) {
		case 0: // Seizoen
			return String.class;
		case 1: // Periode
			return String.class;
		case 2: // Ronde
			return String.class;
		case 3: // Speler
			return String.class;
		case 4: // Kleur
			return String.class;
		case 5: // Tegenstander
			return String.class;
		case 6: // Resultaat
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
		if (wedstrijden != null) {
			return wedstrijden.size();
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
		// {"Seizoen", "Periode", "Ronde", "Speler", "Kleur", "Tegenstander",
		// "Resultaat"};
		DBWedstrijd wedstrijd = wedstrijden.get(row);
		switch (col) {
		case 0:
			return wedstrijd.getRonde().getSeizoen();
		case 1:
			return wedstrijd.getRonde().getPeriode();
		case 2:
			return wedstrijd.getRonde().getRonde();
		case 3:
			DBSpeler s = wedstrijd.getSpeler();
			return s != null ? s.getNaam() : null;
		case 4:
			return wedstrijd.getKleur();
		case 5:
			DBSpeler tgn = wedstrijd.getTegenstander();
			return tgn != null ? tgn.getNaam() : "";
		case 6:
			return wedstrijd.getResultaat();
		default:
			return "";
		}
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return false; // col == 0;
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		// IJCController.getInstance().setSpelerAanwezigheid(controller.getGroepByID(groepID),
		// row, ((Boolean) value).booleanValue());
		fireTableCellUpdated(row, col);
		component.repaint();
	}

	public Object getToolTip(int row, int col) {
		return "";
	}

	public void sorteer() {
		Collections.sort(wedstrijden, new Comparator<DBWedstrijd>() {

			@Override
			public int compare(DBWedstrijd w1, DBWedstrijd w2) {
				// TODO Auto-generated method stub
				int result = w2.getRonde().rondeIdentifier() - w1.getRonde().rondeIdentifier();
				if (result != 0)
					return result;
				return w1.getSpeler().getNaam().compareTo(w2.getSpeler().getNaam());
			}
		});
	}
}
