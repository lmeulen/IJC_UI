package nl.detoren.ijc.ui.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableColumn;

import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.model.ExterneWedstrijdenModel;
import nl.detoren.ijc.ui.util.Utils;

public class ExternDialog extends JDialog {

	private ArrayList<Speler> spelers = null;
	private IJCController controller = null;

	private final static Logger logger = Logger.getLogger(ExternDialog.class.getName());

	public ExternDialog(Frame frame, String title) {
		super(frame, title);
		controller = IJCController.getInstance();
		setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		logger.log(Level.INFO, "Invoeren externe spelers");

		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout(1, 2));

		JTable table = new JTable(new ExterneWedstrijdenModel());
		table.setPreferredScrollableViewportSize(new Dimension(500, 70));
		table.setFillsViewportHeight(true);
//		TableColumn column = table.getColumn(0);
//		Component comp = table.getDefaultRenderer(table.getModel().getColumnClass(0))
//				.getTableCellRendererComponent(table, null, false, false, 0, 0);
		Utils.fixedColumSize(table.getColumnModel().getColumn(0), 200);

		panel.add(table, BorderLayout.LINE_START);

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		panel.add(okButton);
		panel.add(cancelButton);
		getContentPane().add(panel);
		setSize(600, 230);
		setLocationRelativeTo(frame);
	}


}