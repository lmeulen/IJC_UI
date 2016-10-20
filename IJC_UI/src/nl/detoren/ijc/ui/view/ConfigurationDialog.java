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
 */
package nl.detoren.ijc.ui.view;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
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
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.util.Utils;

/**
 * Panel met editor voor Configuratie object.
 * 
 */
public class ConfigurationDialog extends JDialog {
	private static final long serialVersionUID = -4220297943910687398L;

	private Configuratie config;

	private final static Logger logger = Logger.getLogger(ConfigurationDialog.class.getName());

	private JTextField tfAppnaam;
	private JTextField tfVerenigingNaam;
	private JTextField tfCompetititeNaam;
	private JTextField tfCompetitieLocatie;
	private JTextField tfContactNaam;
	private JTextField tfContactEmail;
	private JTextField tfPerioden;
	private JTextField tfRondes;
	private JTextField tfGrSeries;
	private JTextField tfSpeelgroepen;
	private JTextField[] tfGroepsnamens;
	private JTextField[] tfStartPuntens;
	private JTextField[] tfStartRatings;
	private JTextField tfGrDoorschuivers;
	private JCheckBox cbFyuzzyIndeling;
	private JTextField tfGrSorteerRating;
	private JTextField tfGrBegintrio;
	private JCheckBox cbLaatsteRondeDoorschuiven;
	private JCheckBox cbSpeciaalRonde1;
	private JTextField tfMaxVerschil;
	private JCheckBox cbExportShort;
	private JCheckBox cbSaveLongformat;
	private JCheckBox cbSaveDoorschuivers;
	private JTextField tfHeaderDoor;
	private JTextField tfFooterDoor;
	private JCheckBox cbSaveKEI;
	private JCheckBox cbSaveKNSB;
	private JCheckBox cbSaveAdditionals;
	private JTextField tfConfigfile;
	private JTextField tfStatusfile;

	public ConfigurationDialog(Frame frame, String title) {
		super(frame, title);
		logger.log(Level.INFO, "Bewerk configuratie");
		config = IJCController.c();
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
				storeValues();
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
		tabInstellingen.add(new JLabel("Vereniging"));
		tfVerenigingNaam = new JTextField(config.verenigingNaam, 20);
		tabInstellingen.add(tfVerenigingNaam);

		tabInstellingen.add(new JLabel("Competitie Naam"));
		tfCompetititeNaam = new JTextField(config.competitieNaam, 20);
		tabInstellingen.add(tfCompetititeNaam);

		tabInstellingen.add(new JLabel("Locatie"));
		tfCompetitieLocatie = new JTextField(config.competitieLocatie, 20);
		tabInstellingen.add(tfCompetitieLocatie);

		tabInstellingen.add(new JLabel("Contactpersoon - Naam"));
		tfContactNaam = new JTextField(config.contactPersoonNaam, 20);
		tabInstellingen.add(tfContactNaam);
		
		tabInstellingen.add(new JLabel("Contactpersoon - Email"));
		tfContactEmail= new JTextField(config.contactPersoonEmail, 20);
		tabInstellingen.add(tfContactEmail);
		
		//addTextInput(tabInstellingen, tfContactEmail, "Contactpersoon - Email", config.contactPersoonEmail);
		// TODO Competitienaam en Competitie locatie toevoegen
		
		tabInstellingen.add(new JLabel("Applicatie titel"));
		tfAppnaam = new JTextField(config.appTitle, 20);
		tabInstellingen.add(tfAppnaam);
		
		for (int i = 0; i < 14; i++) {
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
		tfPerioden = new JTextField((new Integer(config.perioden)).toString());
		panel.add(tfPerioden);
		// public int rondes = 8
		panel.add(new JLabel("Aantal rondes per periode"));
		tfRondes = new JTextField((new Integer(config.rondes)).toString(),20);
		panel.add(tfRondes);
		// private String grAantalSeries =
		// "if (x == 6) { if ((y == 1) && (z == 1)) { return 2 } else { return 1
		// } }" +
		// " else if ((y == 1) && (z == 1)) { return 3 } else { return 2 } ";
		panel.add(new JLabel("Aantal series per ronde"));
		tfGrSeries = new JTextField(config.grAantalSeries,20);
		tfGrSeries.setCaretPosition(0);
		tfGrSeries.setToolTipText("Groovy functie: x=groep, y=periode, z=ronde");
		panel.add(tfGrSeries);
		// public int aantalGroepen = 7;
		panel.add(new JLabel("Aantal speelgroepen"));
		tfSpeelgroepen = new JTextField((new Integer(config.aantalGroepen)).toString());
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
		tfGroepsnamens = new JTextField[10];
		tfStartPuntens = new JTextField[10];
		tfStartRatings = new JTextField[10];
		for (int i = 0; i < 10; ++i) {
			if (i < config.groepsnamen.length) {
				tfGroepsnamens[i] = new JTextField(config.groepsnamen[i], 20);
				tfStartPuntens[i] = new JTextField(new Integer(config.startPunten[i]).toString(),10);
				tfStartRatings[i] = new JTextField(new Integer(config.startRating[i]).toString(),10);
			} else {
				tfGroepsnamens[i] = new JTextField("");
				tfStartPuntens[i] = new JTextField("");
				tfStartRatings[i] = new JTextField("");
			}
			panel.add(tfGroepsnamens[i]);
			panel.add(tfStartPuntens[i]);
			panel.add(tfStartRatings[i]);
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
		tfGrDoorschuivers = new JTextField(config.grAantalDoorschuivers, 30);
		tfGrDoorschuivers.setCaretPosition(0);
		tfGrDoorschuivers.setToolTipText("Groovy functie: x=periode, y=ronde, resultaat 0 is geen doorschuivers");
		panel.add(tfGrDoorschuivers);
		// private boolean fuzzyIndeling
		panel.add(new JLabel("Gebruik fuzzy algoritme (experimenteel)"));
		cbFyuzzyIndeling = new JCheckBox("", config.fuzzyIndeling);
		panel.add(cbFyuzzyIndeling);
		// private String grSorteerOpRating = "if ((x == 6) && (z > 1) && (z <
		// 7)) { true } else { false }";
		panel.add(new JLabel("Sorteer op rating voor indelen"));
		tfGrSorteerRating = new JTextField(config.grSorteerOpRating);
		tfGrSorteerRating.setCaretPosition(0);
		tfGrSorteerRating.setToolTipText("Groovy functie: x=groep, y=periode, z=ronde");
		panel.add(tfGrSorteerRating);
		// private String grBeginTrio = "x / 2";
		panel.add(new JLabel("Beginpunt trio"));
		tfGrBegintrio = new JTextField(config.grBeginTrio);
		tfGrBegintrio.setCaretPosition(0);
		tfGrBegintrio.setToolTipText("Groovy functie: x=groepsgrootte");
		panel.add(tfGrBegintrio);
		// public boolean laasteRondeDoorschuivenAltijd = false;
		panel.add(new JLabel("Laatste ronde altijd doorschuiven"));
		cbLaatsteRondeDoorschuiven = new JCheckBox("", config.laasteRondeDoorschuivenAltijd);
		panel.add(cbLaatsteRondeDoorschuiven);
		// public boolean specialeIndelingEersteRonde = true;
		panel.add(new JLabel("Speciale indeling eerste ronde"));
		cbSpeciaalRonde1 = new JCheckBox("", config.specialeIndelingEersteRonde);
		panel.add(cbSpeciaalRonde1);
		// public int indelingMaximumVerschil = 3;
		panel.add(new JLabel("Max verschil tussenspelers"));
		tfMaxVerschil = new JTextField((new Integer(config.indelingMaximumVerschil)).toString());
		panel.add(tfMaxVerschil);
		for (int i = 0; i < 13; ++i) {
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
		cbExportShort = new JCheckBox("", config.exportTextShort);
		panel.add(cbExportShort);
		// public boolean exportTextLong = true;
		panel.add(new JLabel("Export uitslag lang formaat"));
		cbSaveLongformat = new JCheckBox("", config.exportTextLong);
		panel.add(cbSaveLongformat);
		// public boolean exportDoorschuivers = true;
		panel.add(new JLabel("Voeg doorschuivers toe aan uitslag"));
		cbSaveDoorschuivers = new JCheckBox("", config.exportDoorschuivers);
		panel.add(cbSaveDoorschuivers);
		// public String exportDoorschuiversStart = "De volgende spelers spelen
		// deze week mee in deze groep:";
		panel.add(new JLabel("Header doorschuivers"));
		tfHeaderDoor = new JTextField(config.exportDoorschuiversStart, 30);
		panel.add(tfHeaderDoor);
		// public String exportDoorschuiversStop = "Spelers no 3 en 4 schuiven
		// alleen door als de groep even wordt";
		panel.add(new JLabel("Footer doorschuivers"));
		tfFooterDoor = new JTextField(config.exportDoorschuiversStop, 30);
		panel.add(tfFooterDoor);
		// public boolean exportKEIlijst = true;
		panel.add(new JLabel("Export KEI lijst"));
		cbSaveKEI = new JCheckBox("", config.exportKEIlijst);
		panel.add(cbSaveKEI);
		// public boolean exportKNSBRating = true;
		panel.add(new JLabel("Export KNSB rating bestand"));
		cbSaveKNSB = new JCheckBox("", config.exportKNSBRating);
		panel.add(cbSaveKNSB);
		// public boolean saveAdditionalStates = true;
		panel.add(new JLabel("Sla additionale statusbestanden op"));
		cbSaveAdditionals = new JCheckBox("", config.saveAdditionalStates);
		panel.add(cbSaveAdditionals);
		// public String configuratieBestand = "configuratie";
		panel.add(new JLabel("Prefix configuratiebestanden"));
		tfConfigfile = new JTextField(config.configuratieBestand, 30);
		tfConfigfile.setCaretPosition(0);
		panel.add(tfConfigfile);
		// public String statusBestand = "status";
		panel.add(new JLabel("Prefix statusbestanden"));
		tfStatusfile = new JTextField(config.statusBestand, 30);
		tfStatusfile.setCaretPosition(0);
		panel.add(tfStatusfile);
		for (int i = 0; i < 10; ++i) {
			panel.add(new JLabel(" "));
			panel.add(new JLabel(" "));
		}
		return panel;
	}

	private void storeValues() {
		// private JTextField tfAppnaam;
		updateTextConfig(config, "appTitle", tfAppnaam.getText(), 5);
		// private JTextField tfVerenigingNaam;
		updateTextConfig(config, "verenigingNaam", tfVerenigingNaam.getText(), 5);
		// private JTextField tfCompetititeNaam;
		updateTextConfig(config, "competitieNaam", tfCompetititeNaam.getText(), 5);
		// private JTextField tfCompetitieLocatie;
		updateTextConfig(config, "competitieLocatie", tfCompetitieLocatie.getText(), 5);
		// private JTextField tfContactNaam;
		updateTextConfig(config, "contactPersoonNaam", tfContactNaam.getText(), 5);
		// private JTextField tfContactEmail;
		updateTextConfig(config, "contactPersoonEmail", tfContactEmail.getText(), 5);

		// private JTextField tfPerioden;
		updateIntConfig(config, "perioden", tfPerioden.getText(), 1, 10);
		// private JTextField tfRondes;
		updateIntConfig(config, "rondes", tfRondes.getText(), 1, 99);
		// private JTextField tfGrSeries;
		updateTextConfig(config,"grAantalSeries", tfGrSeries.getText(),2);
		// private JTextField tfSpeelgroepen;
		updateIntConfig(config, "aantalGroepen", tfSpeelgroepen.getText(), 1, 10);
		// private JTextField[] tfGroepsnamens;
		// private JTextField[] tfStartPuntens;
		// private JTextField[] tfStartRatings;
		config.groepsnamen = new String[10];
		config.startPunten = new int[10];
		config.startRating = new int[10];
		for (int i = 0; i < config.aantalGroepen; ++i) {
			config.groepsnamen[i] = tfGroepsnamens[i].getText();
			config.startPunten[i] = Integer.parseInt(tfStartPuntens[i].getText());
			config.startRating[i] = Integer.parseInt(tfStartRatings[i].getText());
		}
		for (int i = config.aantalGroepen; i < 10; ++i) {
			config.groepsnamen[i] = "";
			config.startPunten[i] = 0;
			config.startRating[i] = 0;
		}
		// private JTextField tfGrDoorschuivers;
		updateTextConfig(config,"grAantalDoorschuivers", tfGrDoorschuivers.getText(),2);
		//private JCheckbox cbFyuzzyIndeling
		config.fuzzyIndeling = cbFyuzzyIndeling.isSelected();
		// private JTextField tfGrSorteerRating;
		updateTextConfig(config,"grSorteerOpRating", tfGrSorteerRating.getText(),2);
		// private JTextField tfGrBegintrio;
		updateTextConfig(config,"grBeginTrio", tfGrBegintrio.getText(),2);
		// private JCheckBox cbLaatsteRondeDoorschuiven;
		config.laasteRondeDoorschuivenAltijd = cbLaatsteRondeDoorschuiven.isSelected();
		// private JCheckBox cbSpeciaalRonde1;
		config.specialeIndelingEersteRonde = cbSpeciaalRonde1.isSelected();
		// private JTextField tfMaxVerschil;
		updateIntConfig(config, "indelingMaximumVerschil", tfMaxVerschil.getText(), 0, 99);
		// private JCheckBox cbExportShort;
		config.exportTextShort = cbExportShort.isSelected();
		// private JCheckBox cbSaveLongformat;
		config.exportTextLong = cbSaveLongformat.isSelected();
		// private JCheckBox cbSaveDoorschuivers;
		config.exportDoorschuivers = cbSaveDoorschuivers.isSelected();
		// private JTextField tfHeaderDoor;
		updateTextConfig(config,"exportDoorschuiversStart", tfHeaderDoor.getText(),10);
		// private JTextField tfFooterDoor
		updateTextConfig(config,"exportDoorschuiversStop", tfFooterDoor.getText(),10);
		// private JCheckBox cbSaveKEI;
		config.exportKEIlijst = cbSaveKEI.isSelected();
		// private JCheckBox cbSaveKNSB;
		config.exportKNSBRating = cbSaveKNSB.isSelected();
		// private JCheckBox cbSaveAdditionals;
		config.saveAdditionalStates = cbSaveAdditionals.isSelected();
		// private JTextField tfConfigfile;
		updateTextConfig(config,"configuratieBestand", tfConfigfile.getText(),5);
		// private JTextField tfStatusfile;
		updateTextConfig(config,"statusBestand", tfStatusfile.getText(),5);
	}
	
	private static void updateTextConfig(Configuratie c, String fieldname, String value, int minlengte) {
		String msg = "Saving value \'" + value + "\' to field " + fieldname;
		logger.log(Level.INFO, msg);
		if ((value != null) && (value.length() >= minlengte)) {
			try {
				Field field = c.getClass().getField(fieldname);
				field.set(c, value);
			} catch (Exception e) {
			}
		}
	}

	private static void updateIntConfig(Configuratie c, String fieldname, String value, int min, int max) {
		try {
			String msg = "Saving value \'" + value + "\' to field " + fieldname;
			logger.log(Level.INFO, msg);
			int nieuw = Integer.parseInt(value);
			if ((nieuw >= min) && (nieuw <= max)) {
				Field field = c.getClass().getField(fieldname);
				field.set(c, nieuw);
			}
		} catch (Exception e) {
		}
	}

}
