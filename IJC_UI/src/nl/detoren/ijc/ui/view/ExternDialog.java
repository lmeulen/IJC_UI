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
package nl.detoren.ijc.ui.view;

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
import javax.swing.JTextField;

import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;

public class ExternDialog extends JDialog {
	private static final long serialVersionUID = -6694326748574261596L;
	private IJCController controller = null;
    JTextField[] spelerVelden;

	private final static Logger logger = Logger.getLogger(ExternDialog.class.getName());

	public ExternDialog(Frame frame, String title) {
        super(frame, "Invoer externe spelers (invoer naam of initialen");
        controller = IJCController.getInstance();
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    	logger.log(Level.INFO, "Externe resultaten invoeren.");

    	ArrayList<Speler> spelers = controller.getExterneSpelers();
    	if (spelers == null) spelers = new ArrayList<>(); 
    	int aantal = 10;
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(aantal, 1));
        spelerVelden = new JTextField[aantal];
        for (int i = 0; i < aantal; ++i) {
            spelerVelden[i] = new JTextField();
            if (i < spelers.size()) spelerVelden[i].setText(spelers.get(i).getNaam()); 
            spelerVelden[i].setMinimumSize(new Dimension(20, 10));
            spelerVelden[i].setMaximumSize(new Dimension(20, 10));
            panel.add(spelerVelden[i]);
        }
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controller.wisExterneSpelers();
                for (JTextField jtf : spelerVelden) {
                    if (jtf != null && jtf.getText() != null && !jtf.getText().equals("")) {
                    	logger.log(Level.INFO, "Extern gespeeld door (invoer) :" + jtf.getText());
                        Speler s = controller.addExterneSpeler(jtf.getText());
                    	logger.log(Level.INFO, "Extern gespeeld door (Speler) :" + s.getNaam());
                    }
                }
                setVisible(false);
                dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                setVisible(false);
                dispose();
            }
        }
        );
        panel.add(okButton);
        panel.add(cancelButton);
        getContentPane().add(panel);
        setSize(600, (aantal + 1) * 23);
        setLocationRelativeTo(frame);
    }
}
