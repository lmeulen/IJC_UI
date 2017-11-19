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
 *
 */
package nl.detoren.ijc.ui.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import nl.detoren.ijc.SpelerDBImport;
import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.control.Status;
import nl.detoren.ijc.ui.model.SpelersModel;
import nl.detoren.ijc.ui.model.WedstrijdModel;
import nl.detoren.ijc.ui.model.WedstrijdSpelersModel;
import nl.detoren.ijc.ui.util.Utils;

/**
 * Structure of the GUI:
 * JFrame Hoofdscherm (this)
 * 		JPanel ButtonPane
 * 		JPanel hoofdPanel
 * 			JTabbedPane tabs
 * 				JPanel panels[i]
 * 					JScrollPane leftScrollPane[i]
 * 						JTable aanwezigheidsTabel[i]
 * 					JScrollPane centerScrollPane[i]
 * 						JTable centerScrollPane[i]
 * 					JScrollPane rightScrollPane[i]
 * 						JTable rightScrollPane[i]
 *
 * @author Leo van der Meulen
 */
public class Hoofdscherm extends JFrame {

	//Colors and fonts
	private static final Color light_green = new Color(200, 255, 200);
	private static final Color light_red = new Color(255, 200, 200);
	private static final Font courierFont = new Font("Courier New", Font.PLAIN, 11);
	private static final Color indigo = new Color(75,0,130);
	private static final Color purple = new Color(128,0,128);
	private static final Color violetred = new Color(199,21,133);
	private static final Color deeppink = new Color(255,20,147);

	private static final long serialVersionUID = -2154845989579570030L;
	private final static Logger logger = Logger.getLogger(Hoofdscherm.class.getName());


	private JPanel hoofdPanel;
	private JTabbedPane tabs;
	private JPanel[] panels;
	private JLabel rondeLabel;
	private JButton automatischButton;
	private JButton wedstrijdgroepButton;
	private JButton speelschemaButton;
	private JButton bewerkspeelschemaButton;
	private JButton exportButton;
	private JButton uitslagButton;
	private JButton externenButton;
	private JButton updatestandButton;
	private JTextField[] jTFZWbalansvoor;
	private JTextField[] jTFZWbalansna;
	private JScrollPane[] leftScrollPane;
	private JScrollPane[] centerLeftScrollPane;
	private JScrollPane[] centerRightScrollPane;
	private JTable[] aanwezigheidsTabel;
	private JTable[] wedstrijdspelersTabel;
	private JTable[] wedstrijdenTabel;
	private int aantal;

	private IJCController controller;

	/**
	 * Creates new form MainWindow
	 */
	public Hoofdscherm() {
		initComponents();
        initSizes();
	}

	private void initComponents() {
		controller = IJCController.getInstance();
		aantal = Groep.getAantalGroepen();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle(IJCController.c().verenigingNaam + " - " + IJCController.c().appTitle);

		hoofdPanel = new javax.swing.JPanel();
		addButtons();
		addMenubar();

		tabs = new JTabbedPane();
		tabs.setTabPlacement(JTabbedPane.TOP);

		panels = new JPanel[aantal];
		leftScrollPane = new JScrollPane[aantal];
		jTFZWbalansvoor = new JTextField[aantal];
		jTFZWbalansna = new JTextField[aantal];
		centerLeftScrollPane = new JScrollPane[aantal];
		centerRightScrollPane = new JScrollPane[aantal];
		aanwezigheidsTabel = new JTable[aantal];
		wedstrijdspelersTabel = new JTable[aantal];
		wedstrijdenTabel = new JTable[aantal];

		for (int i = 0; i < aantal; ++i) {
			panels[i] = makePanel();
			fillGroupPanel(panels[i], i);
			tabs.addTab(Groep.geefNaam(i), null, panels[i], "Gegevens van  " + Groep.geefNaam(i));
		}

		hoofdPanel.add(tabs);
		this.add(hoofdPanel);

		updateUpdateStandButton();

		pack();

		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent event){
                    controller.saveState(false, null);
                }
        });

	}

	private void addButtons() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));

		// Button voor automatisch doorvoeren wijzigingen ja/nee
		automatischButton = new JButton("Auto");
		automatischButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				actieAutomatisch();
			}
		});
		buttonPane.add(automatischButton);

		// Button voor bepalen wedstrijdgroepen
		wedstrijdgroepButton = new JButton("1a. Maak wedstrijdgroep");
		wedstrijdgroepButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieMaakWedstrijdgroep();
			}
		});
		buttonPane.add(wedstrijdgroepButton);

		// Button voor maken speelschema
		speelschemaButton = new JButton("1b. Maak speelschema");
		speelschemaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evetn) {
				actieMaakSpeelschema();
			}
		});
		buttonPane.add(speelschemaButton);

		// Button voor bewerken speelschema
		bewerkspeelschemaButton = new JButton("1c. Bewerk speelschema");
		bewerkspeelschemaButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				actieBewerkSchema();
			}
		});
		buttonPane.add(bewerkspeelschemaButton);

		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));

		exportButton = new JButton("2. Export");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				actieExport();
			}
		});
		buttonPane.add(exportButton);

		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));

		uitslagButton = new JButton("3a. Uitslagen");
		uitslagButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				actieVoerUitslagenIn();
			}
		});
		buttonPane.add(uitslagButton);

		externenButton = new JButton("3b. Extern");
		externenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieExterneSpelers();
			}
		});
		buttonPane.add(externenButton);

		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));

		updatestandButton = new JButton("4. Update stand");
		updatestandButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieUpdateStand();
			}
		});
		buttonPane.add(updatestandButton);

		updateUpdateStandButton();

		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPane.setBackground(Color.white);

		rondeLabel = new JLabel();
		updateRondeLabel();
		updateAutomatisch(controller.isAutomatisch());
		//hoofdPanel.add(new JLabel("IJC De Toren"));
		hoofdPanel.add(buttonPane);
		hoofdPanel.add(rondeLabel);
	}

	/**
	 * Bepaal de kleur van de Update Stand button.
	 * Deze is groen als alle wedstrijden een uitslag hebben.
	 * Geef tevens de groepen een * in hun tabnaam die alle uitslagen ingevoerd hebben.
	 */
	public void updateUpdateStandButton() {
		if (controller.getWedstrijden() != null)
		updatestandButton.setBackground((controller.getWedstrijden() != null) && (controller.getWedstrijden().isUitslagBekend())?light_green:hoofdPanel.getBackground());
		if (tabs != null) {
			for (int i = 0; i < tabs.getTabCount(); i++) {
				if (IJCController.getI().getWedstrijden()!= null) {
				if (!(IJCController.getI().getWedstrijden().getGroepswedstrijdenNiveau(i) == null)) {
					if (IJCController.getI().getWedstrijden().getGroepswedstrijdenNiveau(i).isUitslagBekend()) {
						tabs.setTitleAt(i, Groep.geefNaam(i) + "*");
					} else {
						tabs.setTitleAt(i, Groep.geefNaam(i));
					}
				} else {
					tabs.setTitleAt(i, Groep.geefNaam(i) + "*");
				}
				}
			}
		}
		hoofdPanel.repaint();
	}

	/**
	 * Update het label met ronde en periode informatie
	 */
	public void updateRondeLabel() {
		String rondeText = "<html>Periode: " + controller.getGroepen().getPeriode() + "<BR>";
		rondeText += "Ronde: " + controller.getGroepen().getRonde() + "</HTML>";
		rondeLabel.setText(rondeText);
	}

	/**
	 * Update textfield met ZW balans voor spelen ronde
	 */
	public void updateZWbalansvoor(int index) {
		String bal = String.format(Locale.US, "%.0f", controller.getWedstrijdGroepByID(index).getZWbalansvoor());
		String ZWbalansText = "ZW Balans voor deze ronde is " + bal;
		jTFZWbalansvoor[index].setText(ZWbalansText);
		this.repaint();
	}

	/**
	 * Update textfield met ZW balans voor spelen ronde
	 */
	public void updateZWbalansvoor() {
		for (int index=0;index<aantal;index++) {
			updateZWbalansvoor(index);
		}
	}

	public void updateZWbalansna(int index) {
		String bal = String.format(Locale.US, "%.0f", controller.getWedstrijdGroepByID(index).getZWbalansna());
		String ZWbalansText = "ZW Balans na deze ronde is " + bal + "";
		jTFZWbalansna[index].setText(ZWbalansText);
	}

	public void updateZWbalansna() {
		for (int index=0;index<aantal;index++) {
			updateZWbalansna(index);
		}
	}

		public void updateAutomatisch(boolean newState) {
		controller.setAutomatisch(newState);
		if (controller.isAutomatisch()) {
			automatischButton.setBackground(Color.GREEN);
			bewerkspeelschemaButton.setBackground(light_green);
			speelschemaButton.setBackground(light_green);
			wedstrijdgroepButton.setBackground(light_green);

		} else {
			automatischButton.setBackground(Color.RED);
			bewerkspeelschemaButton.setBackground(light_red);
			speelschemaButton.setBackground(light_red);
			wedstrijdgroepButton.setBackground(light_red);
		}
	}

	private void addMenubar() {
		// Menu bar met 1 niveau
		JMenuBar menubar = new JMenuBar();
		JMenu filemenu = new JMenu("Bestand");
		// File menu
		JMenuItem item = new JMenuItem("Openen...");
		item.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		Hoofdscherm hs = this;
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				// In response to a button click:
				int returnVal = fc.showOpenDialog(hs);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
					controller.leesBestand(file.getAbsolutePath());
					//updateAutomatisch(true);
					//controller.maakGroepsindeling();
					updateRondeLabel();
					updateUpdateStandButton();
					updateAutomatisch(controller.isAutomatisch());
					hs.repaint();
				}
			}
		});
		filemenu.add(item);
		item = new JMenuItem("Opslaan");
		item.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.saveState(true, "save");
			}
		});
		filemenu.add(item);
		filemenu.addSeparator();
		item = new JMenuItem("Instellingen...");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieInstellingen();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke('I', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		filemenu.add(item);
		filemenu.addSeparator();
		item = new JMenuItem("Afsluiten");
		item.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.saveState(false, null);
				System.exit(EXIT_ON_CLOSE);
			}
		});
		filemenu.add(item);
		menubar.add(filemenu);

		JMenu spelermenu = new JMenu("Speler");

		item = new JMenuItem("Nieuwe speler");
		item.setAccelerator(KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieNieuweSpeler(null, null);
				hoofdPanel.repaint();
			}
		});
		spelermenu.add(item);
		menubar.add(spelermenu);

		item = new JMenuItem("Importeer spelers");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				// In response to a button click:
				if (fc.showOpenDialog(hs) == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					logger.log(Level.INFO, "Opening: " + file.getAbsolutePath() + ".");
					controller.importeerSpelers(file.getAbsolutePath());
					hs.repaint();
				}
			}
		});
		spelermenu.add(item);

		item = new JMenuItem("Speler geschiedenis");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SpelersScherm().setVisible(true);
			}
		});
		spelermenu.add(item);

		spelermenu.addSeparator();
		item = new JMenuItem("Wis Zwart/Wit voorkeur");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.wisZwartWitVoorkeur();
			}
		});
		spelermenu.add(item);

		menubar.add(spelermenu);

		JMenu indelingMenu = new JMenu("Indeling");
		item = new JMenuItem("Automatisch aan/uit");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				actieAutomatisch();
			}
		});

		indelingMenu.add(item);
		item = new JMenuItem("Maak wedstrijdgroep");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieMaakWedstrijdgroep();
			}
		});

		indelingMenu.add(item);
		item = new JMenuItem("Maak speelschema");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evetn) {
				actieMaakSpeelschema();
			}
		});
		indelingMenu.add(item);
		item = new JMenuItem("Bewerk speelschema");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				updateAutomatisch(false);
				// ResultaatDialoog
				actieBewerkSchema();
			}
		});

		indelingMenu.add(item);
		indelingMenu.addSeparator();
		item = new JMenuItem("Export");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				actieExport();
			}
		});
		indelingMenu.add(item);
		indelingMenu.addSeparator();
		item = new JMenuItem("Vul uitslagen in");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieVoerUitslagenIn();
			}
		});
		indelingMenu.add(item);
		item = new JMenuItem("Externe spelers");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieExterneSpelers();
			}
		});
		indelingMenu.add(item);
		item = new JMenuItem("Maak nieuwe stand");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieUpdateStand();
			}
		});
		indelingMenu.add(item);
		indelingMenu.addSeparator();
		item = new JMenuItem("Volgende ronde");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actieVolgendeRonde();
			}
		});
		indelingMenu.add(item);
		menubar.add(indelingMenu);

		JMenu overigmenu = new JMenu("Overig");

		item = new JMenuItem("Reset punten");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.resetPunten();
				hoofdPanel.repaint();
			}
		});

		overigmenu.add(item);
		menubar.add(overigmenu);

		this.setJMenuBar(menubar);
	}

	public JTable getAanwezigheidsTabel(int index) {
		if ((index >= 0) && (index < aantal)) {
			return aanwezigheidsTabel[index];
		} else {
			return null;
		}
	}

	public JTable getWedstrijdspelersTabel(int index) {
		if ((index >= 0) && (index < aantal)) {
			return wedstrijdspelersTabel[index];
		} else {
			return null;
		}
	}

	public JTable getWedstrijdenTabel(int index) {
		if ((index >= 0) && (index < aantal)) {
			return wedstrijdenTabel[index];
		} else {
			return null;
		}
	}

	/**
	 * Structure of the GUI: JFrame Hoofdscherm (this) 1050 x 650 * buttonpane *
	 * JPanel hoofdPanel 1040 x 565 * JTabbedPane tabs 1020 x 560 * JPanel
	 * panels[i] 1020 x 600 * JScrollPane leftScrollPane[i] 338 x 600 * JTable
	 * aanwezigheidsTabel[i] 338 x 500 * JScrollPane centerScrollPane[i] 338 x
	 * 500 * JTable centerScrollPane[i] 338 x 600 * JScrollPane
	 * rightScrollPane[i] 338 x 500 * JTable rightScrollPane[i] 338 x 600 *
	 */
	public void initSizes() {
		logger.log(Level.INFO, "Maak alle componenten van het juiste formaat");
		// Fix the layout of the components on the screen.
//		fixedComponentSize(this, 1320, 670);
		fixedComponentSize(this, 1150, 670);
		fixedComponentSize(hoofdPanel, 1040, 580);
		fixedComponentSize(tabs, 1020, 560);
		for (int i = 0; i < aantal; ++i) {
			fixedComponentSize(panels[i], 1020, 500);
			fixedComponentSize(leftScrollPane[i], 320, 485);
			fixedComponentSize(centerLeftScrollPane[i], 320, 485);
			fixedComponentSize(centerRightScrollPane[i], 330, 485);
			fixedComponentSize(aanwezigheidsTabel[i], 320, 675);
			fixedComponentSize(wedstrijdspelersTabel[i], 320, 675);
			fixedComponentSize(wedstrijdenTabel[i], 320, 475);
			// Fix the size of the displayed tables
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(0), 38);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(1), 22);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(2), 135);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(3), 22);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(4), 40);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(5), 42);

			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(0), 17);
			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(1), 125);
			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(2), 33);
			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(3), 20);
			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(4), 20);
			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(5), 90);

			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(0), 25);
			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(1), 120);
			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(2), 10);
			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(3), 120);
			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(4), 33);
		}
	}

	private void fixedComponentSize(Component c, int width, int height) {
		Utils.fixedComponentSize(c, width, height);
	}

	private void fixedColumSize(TableColumn c, int width) {
		Utils.fixedColumSize(c, width);
	}

	protected JPanel makePanel() {
		JPanel panel = new JPanel(false);
		panel.setLayout(new GridLayout(1, 4));
		return panel;
	}

	protected void fillGroupPanel(JPanel panel, final int index) {
		leftScrollPane[index] = new javax.swing.JScrollPane();
		leftScrollPane[index].setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		centerLeftScrollPane[index] = new javax.swing.JScrollPane();
		centerLeftScrollPane[index].setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jTFZWbalansvoor[index] = new JTextField();
		centerRightScrollPane[index] = new javax.swing.JScrollPane();
		centerRightScrollPane[index].setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

		aanwezigheidsTabel[index] = new JTable(new SpelersModel(index, panel)) {
			private static final long serialVersionUID = -8293073016982337108L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				// Tooltip
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					SpelersModel model = (SpelersModel) getModel();
					jc.setToolTipText(model.getToolTip(row, column).toString());
				}

				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				return c;
			}
		};

		aanwezigheidsTabel[index].getTableHeader().addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        int col = aanwezigheidsTabel[index].columnAtPoint(e.getPoint());
		        String name = aanwezigheidsTabel[index].getColumnName(col);
		        System.out.println("Column index selected " + col + " " + name);
		        int groepID = tabs.getSelectedIndex();
		        switch (col) {
		        case 0:
		        	logger.log(Level.INFO, "Zet aanwezigheid alle spelers");
		        	controller.setAlleSpelersAanwezigheid(groepID);
		        	if (controller.isAutomatisch()) {
		        		controller.maakGroepsindeling();
		        	}
		        	updateZWbalansvoor();
		        	updateZWbalansna();
		        	repaint();
		        	break;
		        case 4:
		        	logger.log(Level.INFO, "Sorteer op rating in de groep");
		        	controller.sorteerGroepOpRating(groepID);
		        	break;
		        case 5:
		        	logger.log(Level.INFO, "Sorteer op punte in de groep");
		        	controller.sorteerGroepOpPunten(groepID);
		        	break;
		        }
		    }
		});

		aanwezigheidsTabel[index].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = aanwezigheidsTabel[index].rowAtPoint(e.getPoint());
				if (r >= 0 && r < aanwezigheidsTabel[index].getRowCount()) {
					aanwezigheidsTabel[index].setRowSelectionInterval(r, r);
				} else {
					aanwezigheidsTabel[index].clearSelection();
				}

				int rowindex = aanwezigheidsTabel[index].getSelectedRow();
				if (rowindex < 0) {
					return;
				}
				final int groepID = tabs.getSelectedIndex();
				final Speler s = controller.getGroepByID(groepID).getSpelerByID(rowindex + 1);
				final Speler s2 = controller.getGroepByID(groepID).getSpelerByID(rowindex + 2);
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					JPopupMenu popup = new JPopupMenu();
					JMenuItem menuItem = new JMenuItem("Bewerk speler");
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							BewerkSpelerDialoog rd = new BewerkSpelerDialoog(new JFrame(), "Bewerk Speler", s, true,
									s.getId());
							rd.addWindowListener(new WindowAdapter() {
								@Override
								public void windowClosed(WindowEvent e) {
									System.out.println("closing...");
									hoofdPanel.repaint();
									// do something...
								}

							});
							rd.setVisible(true);
						}

					});
					popup.add(menuItem);

					menuItem = new JMenuItem("Voeg speler toe, na ...");
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							actieNieuweSpeler(s, s2);
						}

					});
					popup.add(menuItem);

					menuItem = new JMenuItem("Verwijder Speler");
					popup.add(menuItem);
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// JDialog.setDefaultLookAndFeelDecorated(true);
							String tekst = "Weet u zeker dat \"" + s.getNaam() + "\" verwijderd moet worden?";
							String[] options = { "Ja", "Nee" };
							int response = JOptionPane.showOptionDialog(null, tekst, "Bevestig", 0,
									JOptionPane.WARNING_MESSAGE, null, options, null);
							if (response == JOptionPane.YES_OPTION) {
								controller.verwijderSpeler(groepID, s, s.getId() - 1);
							}
							hoofdPanel.repaint();
						}
					});

					popup.addSeparator();
					menuItem = new JMenuItem("Doorschuiven Speler");
					popup.add(menuItem);
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							controller.doorschuiven(groepID, s.getId()-1);
							hoofdPanel.repaint();
						}
					});

					menuItem = new JMenuItem("Terugschuiven Speler");
					popup.add(menuItem);
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							controller.terugschuiven(groepID, s.getId()-1);
							hoofdPanel.repaint();
						}
					});

					popup.show(e.getComponent(), e.getX(), e.getY());
				}
				updateZWbalansvoor();
				updateZWbalansna();
				hoofdPanel.repaint();
			}
		});

		wedstrijdspelersTabel[index] = new JTable(new WedstrijdSpelersModel(index, panel)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				WedstrijdSpelersModel model = (WedstrijdSpelersModel) getModel();
				// Tooltip
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					jc.setToolTipText(model.getToolTip(row, column).toString());
				}

				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				// Alternative font
				if (column > 1) {
					c.setFont(courierFont);
				}
				if (model.isDoorgeschoven(row)) {
					c.setFont(new Font(c.getFont().getName(), Font.ITALIC, c.getFont().getSize()));
					c.setForeground(Color.BLUE);
				} else {
					c.setForeground(Color.BLACK);
				}
				if (column == 5) {
					//c.setForeground(indigo);
				}
				return c;
			}
		};

		wedstrijdspelersTabel[index].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = wedstrijdspelersTabel[index].rowAtPoint(e.getPoint());
				if (r >= 0 && r < wedstrijdspelersTabel[index].getRowCount()) {
					wedstrijdspelersTabel[index].setRowSelectionInterval(r, r);
				} else {
					wedstrijdspelersTabel[index].clearSelection();
				}

				int rowindex = wedstrijdspelersTabel[index].getSelectedRow();
				if (rowindex < 0) {
					return;
				}
				final int groepID = tabs.getSelectedIndex();
				final Speler s = controller.getWedstrijdGroepByID(groepID).getSpelerByID(rowindex + 1);
				if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
					JPopupMenu popup = new JPopupMenu();
					JMenuItem menuItem = new JMenuItem("Verwijder Speler");
					popup.add(menuItem);
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							// JDialog.setDefaultLookAndFeelDecorated(true);
							String tekst = "Weet u zeker dat \"" + s.getNaam() + "\" verwijderd moet worden?";
							String[] options = { "Ja", "Nee" };
							int response = JOptionPane.showOptionDialog(null, tekst, "Bevestig", 0,
									JOptionPane.WARNING_MESSAGE, null, options, null);
							if (response == JOptionPane.YES_OPTION) {
								controller.verwijderWedstrijdSpeler(groepID, s, s.getId() - 1);
							}
							updateZWbalansvoor(index);
							updateZWbalansna(index);
							hoofdPanel.repaint();
						}
					});
					menuItem = new JMenuItem("Speler naar hogere groep");
					popup.add(menuItem);
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							controller.spelerNaarHogereGroep(groepID, s, s.getId() - 1);
							updateZWbalansvoor(index);
							updateZWbalansna(index);
							hoofdPanel.repaint();
						}
					});
					menuItem = new JMenuItem("Speler naar lagere groep");
					popup.add(menuItem);
					menuItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent e) {
							controller.spelerNaarLagereGroep(groepID, s, s.getId() - 1);
							updateZWbalansvoor(index);
							updateZWbalansna(index);
							hoofdPanel.repaint();
						}
					});
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		wedstrijdenTabel[index] = new JTable(new WedstrijdModel(index, panel)) {
			/**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				WedstrijdModel model = (WedstrijdModel) getModel();
				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				// Alternatief font bij dubbele wedstrijden
				if (model.isDubbeleWedstrijd(row)) {
					c.setForeground(Color.RED);
				} else 
					if (model.isEerderGespeeld(row) < 99) {
					//c.setForeground(Color.BLUE);

					switch (model.isEerderGespeeld(row)) {
					case 1:
						c.setForeground(indigo);
						break;
					case 2:
						c.setForeground(purple);
						break;
					case 3:
						c.setForeground(violetred);
						break;
					case 4:
						c.setForeground(deeppink);
						break;
					}
				} else {
					c.setForeground(Color.BLACK);
				}
				JComponent jc = (JComponent) c;
				jc.setToolTipText(model.getToolTip(row, column).toString());
				return c;
			}
		};
		wedstrijdenTabel[index].getModel().addTableModelListener(new TableModelListener() {

			@Override
			public void tableChanged(TableModelEvent arg0) {
				updateUpdateStandButton();
				updateZWbalansvoor(index);
				updateZWbalansna(index);
				hoofdPanel.repaint();
			}

		});


		leftScrollPane[index].setViewportView(aanwezigheidsTabel[index]);
		centerLeftScrollPane[index].setViewportView(wedstrijdspelersTabel[index]);
		centerRightScrollPane[index].setViewportView(wedstrijdenTabel[index]);

		JPanel ibt = new JPanel();
		ibt.setLayout(new BoxLayout(ibt, BoxLayout.PAGE_AXIS));
		JTextField jTFaanwezigheid = new JTextField("Aanwezigheid in de " + Groep.geefNaam(index));
		jTFaanwezigheid.setBackground(ibt.getBackground());
		jTFaanwezigheid.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		ibt.add(jTFaanwezigheid, BorderLayout.NORTH);
		jTFZWbalansvoor[index] = new JTextField("ZW Balans");
		jTFZWbalansvoor[index].setBackground(ibt.getBackground());
		jTFZWbalansvoor[index].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		ibt.add(jTFZWbalansvoor[index], BorderLayout.NORTH);
		ibt.add(leftScrollPane[index], BorderLayout.SOUTH);
		ibt.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(ibt, BorderLayout.LINE_START);
		updateZWbalansvoor(index);

		JPanel ibt2 = new JPanel();
		ibt2.setLayout(new BoxLayout(ibt2, BoxLayout.PAGE_AXIS));
		JTextField jTFwedstrijdgroep = new JTextField("Spelers die spelen in de " + Groep.geefNaam(index));
		jTFwedstrijdgroep.setBackground(ibt2.getBackground());
		jTFwedstrijdgroep.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		ibt2.add(jTFwedstrijdgroep, BorderLayout.NORTH);
		JTextField jTFdummy = new JTextField("");
		jTFdummy.setBackground(ibt2.getBackground());
		jTFdummy.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		ibt2.add(jTFdummy, BorderLayout.NORTH);
		ibt2.add(centerLeftScrollPane[index], BorderLayout.SOUTH);
		ibt2.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(ibt2, BorderLayout.LINE_START);

		JPanel ibt3 = new JPanel();
		ibt3.setLayout(new BoxLayout(ibt3, BoxLayout.PAGE_AXIS));
		JTextField jTFwedstrijden = new JTextField("Wedstrijden in de " + Groep.geefNaam(index));
		jTFwedstrijden.setBackground(ibt3.getBackground());
		jTFwedstrijden.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		ibt3.add(jTFwedstrijden, BorderLayout.NORTH);
		jTFZWbalansna[index] = new JTextField("ZW Balans");
		jTFZWbalansna[index].setBackground(ibt3.getBackground());
		jTFZWbalansna[index].setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		ibt3.add(jTFZWbalansna[index], BorderLayout.NORTH);
		ibt3.add(centerRightScrollPane[index], BorderLayout.SOUTH);
		ibt3.setBorder(new EmptyBorder(5, 5, 5, 5));
		panel.add(ibt3, BorderLayout.LINE_START);
		updateZWbalansna(index);

		panel.setBorder(new EmptyBorder(1, 1, 1, 1));
		pack();

	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
	}

	public void actieExport() {
		updateAutomatisch(false);
		controller.exportWedstrijdschema();
		controller.saveState(true, "export");
		hoofdPanel.repaint();
	}

	/**
	 * Dialoog voor het bewerken van het speelschema
	 */
	public void actieBewerkSchema() {
		updateAutomatisch(false);
		hoofdPanel.repaint();
		int groep = tabs.getSelectedIndex();
		WedstrijdschemaDialoog dialoog = new WedstrijdschemaDialoog(new JFrame(), "Wedstrijden", groep);
		dialoog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("closing...");
				hoofdPanel.repaint();
			}

		});
		dialoog.setVisible(true);
	}

	public void actieAutomatisch() {
		updateAutomatisch(!controller.isAutomatisch());
		if (controller.isAutomatisch()) controller.maakGroepsindeling();
		hoofdPanel.repaint();
	}

	public void actieNieuweSpeler(final Speler s, final Speler s2) {
		int groepID = tabs.getSelectedIndex();
		Speler nieuw = new Speler();
		nieuw.setGroep(groepID);
		if ((s != null) && (s2 != null)) {
			nieuw.setPunten((s.getPunten() + s2.getPunten()) / 2);
			nieuw.setRating((s.getRating() + s2.getRating()) / 2);
		} else {
			// Onderaan altijd standaard rating
			nieuw.setRating(IJCController.c().startRating[groepID]);
			nieuw.setPunten(IJCController.c().startPunten[groepID]);
		}
		int locatie = (s != null) ? s.getId() : 0;
		BewerkSpelerDialoog rd = new BewerkSpelerDialoog(new JFrame(), "Bewerk Speler", nieuw,
				false, locatie);
		rd.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				controller.getGroepByID(groepID).renumber();
				hoofdPanel.repaint();
			}

		});
		rd.setVisible(true);
	}

	public void actieMaakWedstrijdgroep() {
		controller.setAutomatisch(false);
		controller.maakGroepsindeling(tabs.getSelectedIndex());
		hoofdPanel.repaint();
	}

	public void actieMaakSpeelschema() {
		updateAutomatisch(false);
		controller.maakWedstrijden(tabs.getSelectedIndex());
		updateZWbalansvoor(tabs.getSelectedIndex());
		updateZWbalansna(tabs.getSelectedIndex());
		hoofdPanel.repaint();
	}

	public void actieVoerUitslagenIn() {
		hoofdPanel.repaint();
		updateAutomatisch(false);
		ResultaatDialoog rd = new ResultaatDialoog(new JFrame(),
				"Wedstrijdresultaten: 1=wit wint, 0=zwart wint, 2=remise (7/8/9 reglementaire uitslag)", tabs.getSelectedIndex());
		rd.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				updateUpdateStandButton();
				hoofdPanel.repaint();
			}

		});
		rd.setVisible(true);
	}

	public void actieExterneSpelers() {
		hoofdPanel.repaint();
		updateAutomatisch(false);
		ExternDialog ed = new ExternDialog(new JFrame(), "Externe spelers");
		ed.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("closing...");
				hoofdPanel.repaint();
			}
		});
		ed.setVisible(true);
	}

	public void actieUpdateStand() {
		controller.setAutomatisch(false);
		controller.verwerkUitslagen();
		hoofdPanel.repaint();
		new UitslagDialoog().createDialog();
	}

	public void actieVolgendeRonde() {
		SpelerDBImport dbi = new SpelerDBImport();
		Status s = controller.getStatus();
		if (s.resultaatVerwerkt != null) {
			dbi.importStatusObjectWithDBSession(s);
			controller.volgendeRonde();
			updateAutomatisch(true);
			updateRondeLabel();
			updateZWbalansvoor();
			updateZWbalansna();
			updateUpdateStandButton();
		}
		hoofdPanel.repaint();
	}

	public void actieInstellingen() {
		hoofdPanel.repaint();
		ConfigurationDialog dialoog = new ConfigurationDialog(new JFrame(), "Configuratie");
		dialoog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("closing...");
				hoofdPanel.repaint();
			}

		});
		dialoog.setVisible(true);
	}
}
