package nl.detoren.ijc.ui.model;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;

public class ExterneWedstrijdenModel extends AbstractTableModel {
	private static final long serialVersionUID = -1915609937621203565L;

	private String[] columnNames = { "Speler" };
	private IJCController controller = IJCController.getInstance();
	ArrayList<Speler> inputSpelers;
	ArrayList<Speler> selected;

	public ExterneWedstrijdenModel() {
		controller = IJCController.getInstance();
		inputSpelers = new ArrayList<>();
		selected = new ArrayList<>();
		for (Speler s : controller.getGroepByID(Groep.KEIZERGROEP).getSpelers()) {
			inputSpelers.add(s);
		}
		for (Speler s : controller.getGroepByID(Groep.KONINGSGROEP).getSpelers()) {
			inputSpelers.add(s);
		}
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return selected.size();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Object getValueAt(int row, int col) {
		return selected.get(row);
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		return true;
	}
}