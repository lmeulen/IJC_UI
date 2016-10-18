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
 * - TODO Implementeer Configuratie editor
 */
package nl.detoren.ijc.ui.view;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import nl.detoren.ijc.Configuratie;
import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.io.GroepenReader;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.util.Utils;

/**
 * Panel met editor voor Configuratie object, waarbij deze interface dynamisch
 * wordt opgebouwd.
 * 
 * @author Leo.vanderMeulen
 *
 */
public class ConfigurationDialog extends JDialog {
	private static final long serialVersionUID = -4220297943910687398L;

	private IJCController controller;
	private Configuratie config;

	private final static Logger logger = Logger.getLogger(ConfigurationDialog.class.getName());

	public ConfigurationDialog(Frame frame, String title) {
		super(frame, title);
		logger.log(Level.INFO, "Bewerk configuratie");
		controller = IJCController.getInstance();
		config = controller.c();
		setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		getContentPane().add(createPanel());
		setSize(600, 420);
		setLocationRelativeTo(frame);
	}

	private JPanel createPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JTabbedPane tabs = new JTabbedPane();
		tabs.setTabPlacement(JTabbedPane.TOP);
		tabs.addTab("Algemeen", createPanelAlgemeen());
		tabs.addTab("Competitie", createPanelCompetitie());
		tabs.addTab("Groepen", createPanelGroepen());
		tabs.addTab("Indeling", createPanelIndeling());
		tabs.addTab("Export", createPanelExport());
		Utils.fixedComponentSize(tabs, 600, 400);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		Utils.fixedComponentSize(buttonPanel, 600, 20);
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				// Do actions
				// Naam
				// speler.setNaam(tfRondes.getText());
				setVisible(false);
				// Als nieuwe speler, dan invoegen.
				// if (!bestaandeSpeler) {
				// controller.addSpeler(speler.getGroep(), speler, locatie);
				// }
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
		});
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		panel.add(buttonPanel, BorderLayout.PAGE_START);
		panel.add(tabs, BorderLayout.CENTER);

		return panel;
	}

	public JPanel createPanelAlgemeen() {
		JPanel tabInstellingen = new JPanel(false);
		tabInstellingen.setLayout(new GridLayout2(20, 2));

		// public String appTitle = "SV De Toren - Indeling Interne Jeugd
		// Competitie";
		tabInstellingen.add(new JLabel("Applicatie titel"));
		final JTextField tfAppnaam = new JTextField(config.appTitle, 20);
		tabInstellingen.add(tfAppnaam);
		for (int i = 0; i < 19; i++) {
			JLabel left = new JLabel(" ");
			tabInstellingen.add(left);
			JLabel right = new JLabel(" ");
			tabInstellingen.add(right);
		}
		return tabInstellingen;
	}

	public JPanel createPanelCompetitie() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout2(20, 2));
		// public int perioden = 4;
		panel.add(new JLabel("Aantal periodes"));
		final JTextField tfPerioden = new JTextField((new Integer(config.perioden)).toString());
		panel.add(tfPerioden);
		// public int rondes = 8
		panel.add(new JLabel("Aantal rondes per periode"));
		final JTextField tfRondes = new JTextField((new Integer(config.rondes)).toString(),20);
		panel.add(tfRondes);
		// private String grAantalSeries =
		// "if (x == 6) { if ((y == 1) && (z == 1)) { return 2 } else { return 1
		// } }" +
		// " else if ((y == 1) && (z == 1)) { return 3 } else { return 2 } ";
		panel.add(new JLabel("Aantal series per ronde"));
		final JTextField tfGrSeries = new JTextField(config.grAantalSeries,20);
		tfGrSeries.setCaretPosition(0);
		tfGrSeries.setToolTipText("Groovy functie: x=groep, y=periode, z=ronde");
		panel.add(tfGrSeries);
		// public int aantalGroepen = 7;
		// public int hoogsteGroep = 6;
		panel.add(new JLabel("Aantal speelgroepen"));
		final JTextField tfSpeelgroepen = new JTextField((new Integer(config.rondes)).toString());
		panel.add(tfSpeelgroepen);
		for (int i = 0; i < 16; i++) {
			panel.add(new JLabel(" "));
			panel.add(new JLabel(" "));
		}
		return panel;
	}

	public JPanel createPanelGroepen() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout2(20, 3));
		// public String[] groepsnamen = { "Pionnengroep", "Paardengroep",
		// "Lopergroep", "Torengroep",
		// "Damegroep", "Koningsgroep", "Keizergroep" };
		// public int[] startPunten = {0, 10, 20, 30, 40, 50, 60 };
		// public int[] startRating = { 100, 150, 200, 300, 500, 800, 1400 };
		panel.add(new JLabel("Groepsnaam"));
		panel.add(new JLabel("Start punten groep"));
		panel.add(new JLabel("Rating nieuwe speler"));
		final JTextField[] tfGroepsnamen = new JTextField[10];
		final JTextField[] tfStartPunten = new JTextField[10];
		final JTextField[] tfStartRating = new JTextField[10];
		for (int i = 0; i < 10; ++i) {
			if (i < config.groepsnamen.length) {
				tfGroepsnamen[i] = new JTextField(config.groepsnamen[i], 20);
				tfStartPunten[i] = new JTextField(new Integer(config.startPunten[i]).toString(),10);
				tfStartRating[i] = new JTextField(new Integer(config.startRating[i]).toString(),10);
			} else {
				tfGroepsnamen[i] = new JTextField("");
				tfStartPunten[i] = new JTextField("");
				tfStartRating[i] = new JTextField("");
			}
			panel.add(tfGroepsnamen[i]);
			panel.add(tfStartPunten[i]);
			panel.add(tfStartRating[i]);
		}
		for (int i = 0; i < 9; ++i) {
			panel.add(new JLabel(" "));
			panel.add(new JLabel(" "));
			panel.add(new JLabel(" "));
		}
		return panel;
	}

	public JPanel createPanelIndeling() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout2(20, 2));

		// private String grAantalDoorschuivers = "if (y >= 4) { if (y < 8) {
		// return 4 } else { return 1 } } else { return 0 }";
		panel.add(new JLabel("Aantal doorschuivers"));
		final JTextField tfGrDoorschuivers = new JTextField(config.grAantalDoorschuivers, 30);
		tfGrDoorschuivers.setCaretPosition(0);
		tfGrDoorschuivers.setToolTipText("Groovy functie: x=periode, y=ronde, resultaat 0 is geen doorschuivers");
		panel.add(tfGrDoorschuivers);
		// private String grSorteerOpRating = "if ((x == 6) && (z > 1) && (z <
		// 7)) { true } else { false }";
		panel.add(new JLabel("Sorteer op rating voor indelen"));
		final JTextField tfGrSorteerRating = new JTextField(config.grSorteerOpRating);
		tfGrSorteerRating.setCaretPosition(0);
		tfGrSorteerRating.setToolTipText("Groovy functie: x=groep, y=periode, z=ronde");
		panel.add(tfGrSorteerRating);
		// private String grBeginTrio = "x / 2";
		panel.add(new JLabel("Sorteer op rating voor indelen"));
		final JTextField tfGrBegintrio = new JTextField(config.grBeginTrio);
		tfGrBegintrio.setCaretPosition(0);
		tfGrBegintrio.setToolTipText("Groovy functie: x=groepsgrootte");
		panel.add(tfGrBegintrio);
		// public boolean laasteRondeDoorschuivenAltijd = false;
		panel.add(new JLabel("Laatste ronde altijd doorschuiven"));
		final JCheckBox cbLaatsteRondeDoorschuiven = new JCheckBox("", config.laasteRondeDoorschuivenAltijd);
		panel.add(cbLaatsteRondeDoorschuiven);
		// public boolean specialeIndelingEersteRonde = true;
		panel.add(new JLabel("Laatste ronde altijd doorschuiven"));
		final JCheckBox cbSpeciaalRonde1 = new JCheckBox("", config.specialeIndelingEersteRonde);
		panel.add(cbSpeciaalRonde1);
		// public int indelingMaximumVerschil = 3;
		panel.add(new JLabel("Max verschil tussenspelers"));
		final JTextField tfMaxVerschil = new JTextField((new Integer(config.indelingMaximumVerschil)).toString());
		panel.add(tfMaxVerschil);
		for (int i = 0; i < 14; ++i) {
			panel.add(new JLabel(" "));
			panel.add(new JLabel(" "));
		}
		return panel;
	}

	public JPanel createPanelExport() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout2(20, 2));

		// public boolean exportTextShort = true;
		panel.add(new JLabel("Exporteer uitslag kort formaat"));
		final JCheckBox cbExportShort = new JCheckBox("", config.exportTextShort);
		panel.add(cbExportShort);
		// public boolean exportTextLong = true;
		panel.add(new JLabel("Export uitslag lang formaat"));
		final JCheckBox cbSaveLongformat = new JCheckBox("", config.exportTextLong);
		panel.add(cbSaveLongformat);
		// public boolean exportDoorschuivers = true;
		panel.add(new JLabel("Voeg doorschuivers toe aan uitslag"));
		final JCheckBox cbSaveDoorschuivers = new JCheckBox("", config.exportDoorschuivers);
		panel.add(cbSaveDoorschuivers);
		// public String exportDoorschuiversStart = "De volgende spelers spelen
		// deze week mee in deze groep:";
		panel.add(new JLabel("Header doorschuivers"));
		final JTextField tfHeaderDoor = new JTextField(config.exportDoorschuiversStart, 30);
		panel.add(tfHeaderDoor);
		// public String exportDoorschuiversStop = "Spelers no 3 en 4 schuiven
		// alleen door als de groep even wordt";
		panel.add(new JLabel("Footer doorschuivers"));
		final JTextField tfFooterDoor = new JTextField(config.exportDoorschuiversStop, 30);
		panel.add(tfFooterDoor);
		// public boolean exportKEIlijst = true;
		panel.add(new JLabel("Export KEI lijst"));
		final JCheckBox cbSaveKEI = new JCheckBox("", config.exportKEIlijst);
		panel.add(cbSaveKEI);
		// public boolean exportKNSBRating = true;
		panel.add(new JLabel("Export KNSB rating bestand"));
		final JCheckBox cbSaveKNSB = new JCheckBox("", config.exportKNSBRating);
		panel.add(cbSaveKNSB);
		// public boolean saveAdditionalStates = true;
		panel.add(new JLabel("Sla additionale statusbestanden op"));
		final JCheckBox cbSaveAdditionals = new JCheckBox("", config.saveAdditionalStates);
		panel.add(cbSaveAdditionals);
		// public String configuratieBestand = "configuratie";
		panel.add(new JLabel("Prefix configuratiebestanden"));
		final JTextField tfConfigfile = new JTextField(config.configuratieBestand, 30);
		tfConfigfile.setCaretPosition(0);
		panel.add(tfConfigfile);
		// public String statusBestand = "status";
		panel.add(new JLabel("Prefix statusbestanden"));
		final JTextField tfStatusfile = new JTextField(config.statusBestand, 30);
		tfStatusfile.setCaretPosition(0);
		panel.add(tfStatusfile);
		for (int i = 0; i < 10; ++i) {
			panel.add(new JLabel(" "));
			panel.add(new JLabel(" "));
		}
		return panel;
	}

}
