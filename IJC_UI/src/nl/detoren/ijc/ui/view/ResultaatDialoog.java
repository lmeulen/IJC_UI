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
 * - ...
 */
package nl.detoren.ijc.ui.view;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.io.GroepenReader;
import nl.detoren.ijc.ui.control.IJCController;

/**
 *
 * @author Leo van der Meulen
 */
class ResultaatDialoog extends JDialog {

	private static final long serialVersionUID = -3921269216014454438L;

	private final static Logger logger = Logger.getLogger(GroepenReader.class.getName());

	private static IJCController controller;
    JTextField[] uitslagVelden;
    JLabel[] witLabels;
    JLabel[] zwartLabels;
    int groep;
    
    ResultaatDialoog(Frame frame, String title, int g) {
        super(frame, title);
        this.groep = g;
        controller = IJCController.getInstance();
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    	logger.log(Level.INFO, "Resultaten invoeren voor " + Groep.geefNaam(g));

        ArrayList<Wedstrijd> wedstrijden = controller.getWedstrijden().getGroepswedstrijdenNiveau(groep).getWedstrijden();
        int aantal = wedstrijden.size();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(aantal + 1, 3));
        uitslagVelden = new JTextField[aantal];
        witLabels = new JLabel[aantal];
        zwartLabels = new JLabel[aantal];
        int i = 0;
        for (Wedstrijd w : wedstrijden) {
            witLabels[i] = new JLabel(w.getWit().getNaam());
            zwartLabels[i] = new JLabel(w.getZwart().getNaam());
            uitslagVelden[i] = new JTextField();
            uitslagVelden[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    JTextField jtf = (JTextField) e.getComponent();
                    if (e.getKeyChar() >= '0' && e.getKeyChar() <= '2') {
                        jtf.setText("");
                        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
                        manager.focusNextComponent();
                    } else {
                        jtf.setText("");
                    }
                }
            }
            );
            uitslagVelden[i].setMinimumSize(new Dimension(20, 10));
            uitslagVelden[i].setMaximumSize(new Dimension(20, 10));
            panel.add(witLabels[i]);
            panel.add(zwartLabels[i]);
            panel.add(uitslagVelden[i]);
            ++i;
        }
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ArrayList<Wedstrijd> wedstrijden = controller.getWedstrijden().getGroepswedstrijdenNiveau(groep).getWedstrijden();
                int i = 0;
                for (JTextField jtf : uitslagVelden) {
                    if (jtf != null && jtf.getText() != null && !jtf.getText().equals("")) {
                        System.out.println("Veld " + i + " Waarde " + Integer.parseInt(jtf.getText()));
                        wedstrijden.get(i).setUitslag012(Integer.parseInt(jtf.getText()));
                    }
                    ++i;
                }
                setVisible(false);
                dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        }
        );
        panel.add(okButton);
        panel.add(cancelButton);
        getContentPane().add(panel);
        setSize(600, (aantal+1)*23);
        setLocationRelativeTo(frame);
    }
}
