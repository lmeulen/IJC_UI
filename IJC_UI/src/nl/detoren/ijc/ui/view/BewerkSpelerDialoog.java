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
 * - MINOR Toevoegen geschiedenis string? 
 * - ...
 */
package nl.detoren.ijc.ui.view;

import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.io.GroepenReader;
import nl.detoren.ijc.ui.control.IJCController;

/**
 *
 * @author Leo van der Meulen
 */
public class BewerkSpelerDialoog extends JDialog {

	private static final long serialVersionUID = -5297394315846599903L;

	private final static Logger logger = Logger.getLogger(GroepenReader.class.getName());
	/**
	 * 
	 */
	private static IJCController controller;
    private Speler speler;
    boolean bestaandeSpeler;
    final int locatie;

    public BewerkSpelerDialoog(Frame frame, String title, Speler s, boolean bestaand, int loc) {
        super(frame, title);
        this.speler = s;
        this.bestaandeSpeler = bestaand;
        this.locatie = loc;
    	logger.log(Level.INFO, "Bewerk speler " + s.toPrintableString());
        controller = IJCController.getInstance();
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(createPanel());
        setSize(300, 15 * 23);
        setLocationRelativeTo(frame);
    }

    private JPanel createPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(17, 2));
        //ID
        panel.add(new JLabel("ID"));
        final JTextField tfID = new JTextField((new Integer(speler.getId())).toString());
        tfID.setEditable(false);
        panel.add(tfID);
        //Naam
        panel.add(new JLabel("Naam"));
        final JTextField tfNaam = new JTextField(speler.getNaam());
        panel.add(tfNaam);
        // Initialen
        panel.add(new JLabel("Initialen"));
        final JTextField tfInit = new JTextField(speler.getInitialen());
        panel.add(tfInit);
        // Wit voorkeur
        panel.add(new JLabel("Witvoorkeur"));
        final JTextField tfWit = new JTextField((new Integer((int)speler.getWitvoorkeur())).toString());
        panel.add(tfWit);
        // Groep
        panel.add(new JLabel("Groep"));
        final JTextField tfGroep = new JTextField(Groep.geefNaam(speler.getGroep()));
        tfGroep.setEditable(false);
        panel.add(tfGroep);
        // Rating
        panel.add(new JLabel("Rating"));
        final JTextField tfRating = new JTextField((new Integer(speler.getRating())).toString());
        panel.add(tfRating);
        // Punten
        panel.add(new JLabel("Punten"));
        final JTextField tfPunten = new JTextField((new Integer(speler.getPunten())).toString());
        panel.add(tfPunten);
        // KEIPunten
        panel.add(new JLabel("KEI Punten"));
        final JTextField tfKeiPunten = new JTextField((new Integer(speler.getKeipunten())).toString());
        panel.add(tfKeiPunten);
        // Punten
        panel.add(new JLabel("KEI Kansen"));
        final JTextField tfKeiKansen = new JTextField((new Integer(speler.getKeikansen())).toString());
        panel.add(tfKeiKansen);
        // KNSB
        panel.add(new JLabel("KNSB Nummer"));
        final JTextField tfKNSB = new JTextField((new Integer(speler.getKNSBnummer())).toString());
        panel.add(tfKNSB);
        // Tegenstanders
        panel.add(new JLabel("Tegenstanders"));
        final JTextField tfTegenstander1 = new JTextField(speler.getTegenstanders()[0]);
        panel.add(tfTegenstander1);
        panel.add(new JLabel(""));
        final JTextField tfTegenstander2 = new JTextField(speler.getTegenstanders()[1]);
        panel.add(tfTegenstander2);
        panel.add(new JLabel(""));
        final JTextField tfTegenstander3 = new JTextField(speler.getTegenstanders()[2]);
        panel.add(tfTegenstander3);
        panel.add(new JLabel(""));
        final JTextField tfTegenstander4 = new JTextField(speler.getTegenstanders()[3]);
        panel.add(tfTegenstander4);
        // Afwezigheidspunten
        panel.add(new JLabel("Afwezigheidspunt"));
        final JCheckBox cbAfwezigPunt = new JCheckBox("", speler.isAfwezigheidspunt());
        panel.add(cbAfwezigPunt);
        // Aanwezig
        panel.add(new JLabel("Aanwezig"));
        final JCheckBox cbAanwezig = new JCheckBox("", speler.isAanwezig());
        cbAanwezig.setEnabled(false);
        panel.add(cbAanwezig);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Do actions
                // Naam
                speler.setNaam(tfNaam.getText());
                // Initialen
                speler.setInitialen(tfInit.getText());
                // Rating
                int rating = Integer.parseInt(tfRating.getText());
                speler.setRating(rating);
                // Punten
                int punten = Integer.parseInt(tfPunten.getText());
                speler.setPunten(punten);
                // KEI Punten
                int keipunten = Integer.parseInt(tfKeiPunten.getText());
                speler.setKeipunten(keipunten);
                // Kei kansen
                int keikansen = Integer.parseInt(tfKeiKansen.getText());
                speler.setKeikansen(keikansen);
                // KNSB
                int knsb = Integer.parseInt(tfKNSB.getText());
                speler.setKNSBnummer(knsb);
                // Tegenstanders
                String[] tgn = new String[4];
                tgn[0] = tfTegenstander1.getText();
                tgn[1] = tfTegenstander2.getText();
                tgn[2] = tfTegenstander3.getText();
                tgn[3] = tfTegenstander4.getText();
                speler.setTegenstanders(tgn);
                // Afwezigheidspunt
                setVisible(false);
                // Als nieuwe speler, dan invoegen.
                if (!bestaandeSpeler) {
                    controller.addSpeler(speler.getGroep(), speler, locatie);
                }
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
        return panel;
    }
}
