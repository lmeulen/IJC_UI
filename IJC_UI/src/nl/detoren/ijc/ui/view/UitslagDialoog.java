package nl.detoren.ijc.ui.view;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import nl.detoren.ijc.ui.control.IJCController;

public class UitslagDialoog {

	public void createDialog() {
		JDialog dialog = new JDialog();
		dialog.setLocationByPlatform(true);
		JTextArea txtArea = new JTextArea(40,150);
//		txtArea.setAutoscrolls(true);
//		txtArea.setPreferredSize(new Dimension(1000, 500));
		txtArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		txtArea.setFont(new Font("courier new", Font.PLAIN, 12));
		txtArea.setLineWrap(false);
		JScrollPane txtAreaScroll = new JScrollPane();
		txtAreaScroll.setViewportView(txtArea);
		txtAreaScroll.setAutoscrolls(true);

		File file;
		String line = null;
		StringBuilder fileContents = new StringBuilder();
		try {
			file = new File(			IJCController.getInstance().getLaatsteExport());
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				fileContents.append(line + "\n");
			}
			reader.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		txtArea.setText(fileContents.toString());
		txtArea.setLocation(0, 0);
		dialog.add(txtAreaScroll);
		dialog.pack();
		dialog.setVisible(true);
	}
}