/**
 * Copyright (C) 2016 Leo van der Meulen
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation version 3.0
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * See: http://www.gnu.org/licenses/gpl-2.0.html
 *  
 * Problemen in deze code:
 * - TODO Te laat binnenkomende doorgeschoven speler kunnen toevoegen aan hogere groep
 * - MINOR Vierde kolom met nieuwe groepstand na verwerken uitslagen 
 * - MINOR Als een uitslag ingevuld, aanwezigheid etc vastzetten
 * - MINOR Wisselen groepsblad werkt soms niet meer (nog niet reproduceerbaar) 
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
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.model.SpelersModel;
import nl.detoren.ijc.ui.model.WedstrijdModel;
import nl.detoren.ijc.ui.model.WedstrijdSpelersModel;
import nl.detoren.ijc.ui.util.Utils;

/**
 * Structure of the GUI: JFrame Hoofdscherm (this) JPanel hoofdPanel JTabbedPane
 * tabs JPanel panels[i] JScrollPane leftScrollPane[i] JTable
 * aanwezigheidsTabel[i] JScrollPane centerScrollPane[i] JTable
 * centerScrollPane[i] JScrollPane rightScrollPane[i] JTable rightScrollPane[i]
 *
 * @author Leo van der Meulen
 */
public class Hoofdscherm extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2154845989579570030L;

	private final static Logger logger = Logger.getLogger(Hoofdscherm.class.getName());

	private static final Font courierFont = new Font("Courier", Font.PLAIN, 11);
	
	private JPanel hoofdPanel;
	private JTabbedPane tabs;
	private JPanel[] panels;
	private JLabel rondeLabel;
	private JButton automatischButton;
	private JScrollPane[] leftScrollPane;
	private JScrollPane[] centerScrollPane;
	private JScrollPane[] rightScrollPane;
	private JTable[] aanwezigheidsTabel;
	private JTable[] wedstrijdspelersTabel;
	private JTable[] updatedSpelersTabel;
	private JTable[] wedstrijdenTabel;
	private int aantal;

	private IJCController controller;

	/**
	 * Creates new form MainWindow
	 */
	public Hoofdscherm() {
		initComponents();
	}

	private void initComponents() {
		controller = IJCController.getInstance();
		aantal = Groep.getAantalGroepen();
		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("SV De Toren - Indeling Interne Jeugd Competitie");

		hoofdPanel = new javax.swing.JPanel();
		addButtons();
		addMenubar();

		tabs = new JTabbedPane();
		tabs.setTabPlacement(JTabbedPane.TOP);

		panels = new JPanel[aantal];
		leftScrollPane = new JScrollPane[aantal];
		centerScrollPane = new JScrollPane[aantal];
		rightScrollPane = new JScrollPane[aantal];
		aanwezigheidsTabel = new JTable[aantal];
		wedstrijdspelersTabel = new JTable[aantal];
		updatedSpelersTabel = new JTable[aantal];
		wedstrijdenTabel = new JTable[aantal];

		for (int i = 0; i < aantal; ++i) {
			panels[i] = makePanel();
			fillGroupPanel(panels[i], i);
			tabs.addTab(Groep.geefNaam(i), null, panels[i], "Gegevens van  " + Groep.geefNaam(i));
		}

		hoofdPanel.add(tabs);
		this.add(hoofdPanel);

		pack();

		this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                    controller.saveState(false);
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
			public void actionPerformed(ActionEvent e) {
				// action
				controller.setAutomatisch(!controller.isAutomatisch());
				if (controller.isAutomatisch()) {
					automatischButton.setBackground(Color.GREEN);
					controller.maakGroepsindeling();
				} else {
					automatischButton.setBackground(Color.RED);
				}
				hoofdPanel.repaint();
			}
		});
		automatischButton.setBackground(controller.isAutomatisch() ? Color.green : Color.red);
		buttonPane.add(automatischButton);

		// Button voor bepalen wedstrijdgroepen
		final JButton wgButton = new JButton("1a. Maak wedstrijdgroep");
		wgButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setAutomatisch(false);
				int groep = tabs.getSelectedIndex();
				controller.maakGroepsindeling(groep);
				hoofdPanel.repaint();
			}
		});
		buttonPane.add(wgButton);

		// Button voor maken speelschema
		final JButton ssButton = new JButton("1b. Maak speelschema");
		ssButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAutomatisch(false);
				int groep = tabs.getSelectedIndex();
				controller.maakWedstrijden(groep);
				hoofdPanel.repaint();
			}
		});
		buttonPane.add(ssButton);

		// Button voor bewerken speelschema
		final JButton bsButton = new JButton("1c. Bewerk speelschema");
		bsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAutomatisch(false);
				// ResultaatDialoog
				hoofdPanel.repaint();
				int groep = tabs.getSelectedIndex();
				WedstrijdschemaDialoog dialoog = new WedstrijdschemaDialoog(new JFrame(), "Wedstrijden", groep);
				dialoog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						System.out.println("closing...");
						hoofdPanel.repaint();
						// do something...
					}

				});
				dialoog.setVisible(true);
			}
		});
		buttonPane.add(bsButton);

		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));

		final JButton exportButton = new JButton("2. Export");
		exportButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ResultaatDialoog
				updateAutomatisch(false);
				controller.exportToExcel();
				hoofdPanel.repaint();
			}
		});
		buttonPane.add(exportButton);

		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));

		final JButton guButton = new JButton("3a. Uitslagen");
		guButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// ResultaatDialoog
				hoofdPanel.repaint();
				updateAutomatisch(false);
				int groep = tabs.getSelectedIndex();
				ResultaatDialoog rd = new ResultaatDialoog(new JFrame(), "Wedstrijdresultaten: 1=wit wint, 0=zwart wint, 2=remise", groep);
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
		buttonPane.add(guButton);

		final JButton geButton = new JButton("3b. Extern");
		geButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Implementeer selectie lijst externe spelers
				// ResultaatDialoog
				hoofdPanel.repaint();
				updateAutomatisch(false);
				ExternDialog ed = new ExternDialog(new JFrame(), "Externe spelers");
				ed.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						System.out.println("closing...");
						hoofdPanel.repaint();
						// do something...
					}

				});
				ed.setVisible(true);
			}
		});
		buttonPane.add(geButton);

		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));
		buttonPane.add(new JSeparator(SwingConstants.VERTICAL));

		final JButton usButton = new JButton("4. Update stand");
		usButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.setAutomatisch(false);
				controller.verwerkUitslagen();
				hoofdPanel.repaint();
			}
		});
		buttonPane.add(usButton);

		buttonPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		buttonPane.setBackground(Color.white);

		rondeLabel = new JLabel();
		updateRondeLabel();
		//hoofdPanel.add(new JLabel("IJC De Toren"));
		hoofdPanel.add(buttonPane);
		hoofdPanel.add(rondeLabel);
	}

	/**
	 * Update het label met ronde en periode informatie 
	 */
	public void updateRondeLabel() {
		String rondeText = "<html>Periode: " + controller.getGroepen().getPeriode() + "<BR>";
		rondeText += "Ronde: " + controller.getGroepen().getRonde() + "</HTML>";

		rondeLabel.setText(rondeText);
	}
	
	public void updateAutomatisch(boolean newState) {
		controller.setAutomatisch(newState);
		if (controller.isAutomatisch()) {
			automatischButton.setBackground(Color.GREEN);
		} else {
			automatischButton.setBackground(Color.RED);
		}
	}

	private void addMenubar() {
		// Menu bar met 1 niveau
		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");
		menubar.add(menu);
		// File menu
		JMenuItem item = new JMenuItem("Open");
		item.setAccelerator(KeyStroke.getKeyStroke('O', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		Hoofdscherm hs = this;
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Open");
				// Create a file chooser
				final JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File(System.getProperty("user.dir")));
				// In response to a button click:
				int returnVal = fc.showOpenDialog(hs);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					// This is where a real application would open the file.
					System.out.println("Opening: " + file.getAbsolutePath() + ".");
					controller.leesGroepen(file.getAbsolutePath());
					controller.setAutomatisch(true);
					controller.maakGroepsindeling();
					updateRondeLabel();
					hs.repaint();
				} else {
					System.out.println("Openen bestand geannuleerd");
				}
			}
		});
		menu.add(item);
		item = new JMenuItem("Save");
		item.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Save state");
				controller.saveState(false);
			}
		});
		menu.add(item);
		menu.addSeparator();
		item = new JMenuItem("Exit        ");
		item.setAccelerator(KeyStroke.getKeyStroke('Q', Toolkit.getDefaultToolkit ().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Exit from meu");
				controller.saveState(false);
				System.exit(EXIT_ON_CLOSE);
			}
		});
		menu.add(item);
		this.setJMenuBar(menubar);
	}

	public JTable getAanwezigheidsTabel(int i) {
		if ((i >= 0) && (i < aantal)) {
			return aanwezigheidsTabel[i];
		} else {
			return null;
		}
	}

	public JTable getWedstrijdspelersTabel(int i) {
		if ((i >= 0) && (i < aantal)) {
			return wedstrijdspelersTabel[i];
		} else {
			return null;
		}
	}

	public JTable getWedstrijdenTabel(int i) {
		if ((i >= 0) && (i < aantal)) {
			return wedstrijdenTabel[i];
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
		fixedComponentSize(this, 1150, 670);
		fixedComponentSize(hoofdPanel, 1040, 580);
		fixedComponentSize(tabs, 1020, 560);
		for (int i = 0; i < aantal; ++i) {
			fixedComponentSize(panels[i], 1020, 500);
			fixedComponentSize(leftScrollPane[i], 320, 500);
			fixedComponentSize(centerScrollPane[i], 320, 500);
			fixedComponentSize(rightScrollPane[i], 320, 500);
			fixedComponentSize(aanwezigheidsTabel[i], 320, 475);
			fixedComponentSize(wedstrijdspelersTabel[i], 320, 475);
			fixedComponentSize(updatedSpelersTabel[i], 320, 475);
			fixedComponentSize(wedstrijdenTabel[i], 320, 475);
			// Fix the size of the displayed tables
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(0), 40);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(1), 30);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(2), 160);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(3), 40);
			fixedColumSize(aanwezigheidsTabel[i].getColumnModel().getColumn(4), 45);

			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(0), 20);
			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(1), 170);
			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(2), 30);
			fixedColumSize(wedstrijdspelersTabel[i].getColumnModel().getColumn(3), 95);

			fixedColumSize(updatedSpelersTabel[i].getColumnModel().getColumn(0), 20);
			fixedColumSize(updatedSpelersTabel[i].getColumnModel().getColumn(1), 160);
			fixedColumSize(updatedSpelersTabel[i].getColumnModel().getColumn(2), 40);
			fixedColumSize(updatedSpelersTabel[i].getColumnModel().getColumn(3), 40);

			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(0), 35);
			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(1), 120);
			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(2), 10);
			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(3), 120);
			fixedColumSize(wedstrijdenTabel[i].getColumnModel().getColumn(4), 30);
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

	protected void fillGroupPanel(JPanel panel, final int i) {
		leftScrollPane[i] = new javax.swing.JScrollPane();
		centerScrollPane[i] = new javax.swing.JScrollPane();
		rightScrollPane[i] = new javax.swing.JScrollPane();

		aanwezigheidsTabel[i] = new JTable(new SpelersModel(i, panel)) {
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

		aanwezigheidsTabel[i].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = aanwezigheidsTabel[i].rowAtPoint(e.getPoint());
				if (r >= 0 && r < aanwezigheidsTabel[i].getRowCount()) {
					aanwezigheidsTabel[i].setRowSelectionInterval(r, r);
				} else {
					aanwezigheidsTabel[i].clearSelection();
				}

				int rowindex = aanwezigheidsTabel[i].getSelectedRow();
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
							Speler nieuw = new Speler(s);
							nieuw.setId(0);
							nieuw.setNaam("");
							nieuw.setInitialen("");
							String[] tgn = { "--", "--", "--", "--" };
							nieuw.setTegenstanders(tgn);
							nieuw.setWitvoorkeur(0);
							nieuw.setAfwezigheidspunt(false);
							nieuw.setAanwezig(true);
							nieuw.setKeikansen(0);
							nieuw.setKeipunten(0);
							nieuw.setKNSBnummer(1234567);
							if (s2 != null) {
								nieuw.setPunten((s.getPunten() + s2.getPunten()) / 2);
								nieuw.setRating((s.getRating() + s2.getRating()) / 2);
							} else {
								// Onderaan altijd laagste rating
								nieuw.setRating(200);
							}
							BewerkSpelerDialoog rd = new BewerkSpelerDialoog(new JFrame(), "Bewerk Speler", nieuw,
									false, s.getId());
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
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		wedstrijdspelersTabel[i] = new JTable(new WedstrijdSpelersModel(i, panel)) {
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
				return c;
			}
		};

		wedstrijdspelersTabel[i].addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				int r = wedstrijdspelersTabel[i].rowAtPoint(e.getPoint());
				if (r >= 0 && r < wedstrijdspelersTabel[i].getRowCount()) {
					wedstrijdspelersTabel[i].setRowSelectionInterval(r, r);
				} else {
					wedstrijdspelersTabel[i].clearSelection();
				}

				int rowindex = wedstrijdspelersTabel[i].getSelectedRow();
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
							hoofdPanel.repaint();
						}
					});
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		updatedSpelersTabel[i] = new JTable(new WedstrijdSpelersModel(i, panel)) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				return c;
			}
		};

		wedstrijdenTabel[i] = new JTable(new WedstrijdModel(i, panel)) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);

				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				return c;
			}
		};

		leftScrollPane[i].setViewportView(aanwezigheidsTabel[i]);
		centerScrollPane[i].setViewportView(wedstrijdspelersTabel[i]);
		rightScrollPane[i].setViewportView(wedstrijdenTabel[i]);

		JPanel ibt = new JPanel();
		ibt.add(new JTextField("Aanwezigheid spelers in de " + Groep.geefNaam(i)), BorderLayout.NORTH);
		ibt.add(leftScrollPane[i], BorderLayout.SOUTH);
		ibt.setBorder(new EmptyBorder(1, 1, 1, 1));
		panel.add(ibt, BorderLayout.LINE_START);
		//panel.add(leftScrollPane[i], BorderLayout.LINE_START);
		JPanel ibt2 = new JPanel();
		ibt2.add(new JTextField("Spelers die deze ronde spelen in de " + Groep.geefNaam(i)), BorderLayout.NORTH);
		ibt2.add(centerScrollPane[i], BorderLayout.SOUTH);
		ibt2.setBorder(new EmptyBorder(1, 1, 1, 1));
		panel.add(ibt2, BorderLayout.LINE_START);
		//panel.add(centerScrollPane[i], BorderLayout.CENTER);
		JPanel ibt3 = new JPanel();
		ibt3.add(new JTextField("Wedstrijden deze ronde in de " + Groep.geefNaam(i)), BorderLayout.NORTH);
		ibt3.add(rightScrollPane[i], BorderLayout.SOUTH);
		ibt3.setBorder(new EmptyBorder(1, 1, 1, 1));
		panel.add(ibt3, BorderLayout.LINE_START);
		//panel.add(rightScrollPane[i], BorderLayout.LINE_END);
		panel.setBorder(new EmptyBorder(1, 1, 1, 1));

		pack();

	}

	public void printSizeStatistics() {
		// System.out.println("Frame : " + getSize().getWidth() + "," +
		// getSize().getHeight());
		// System.out.println(" Panel : " + hoofdPanel.getSize().getWidth() +
		// "," + hoofdPanel.getSize().getHeight());
		// System.out.println(" Tabs : " + tabs.getSize().getWidth() + "," +
		// tabs.getSize().getHeight());
		// System.out.println(" Panel : " +
		// leftScrollPane[1].getSize().getWidth() + "," +
		// leftScrollPane[1].getSize().getHeight());
		// System.out.println(" Panel : " +
		// centerScrollPane[1].getSize().getWidth() + "," +
		// leftScrollPane[1].getSize().getHeight());
		// System.out.println(" Panel : " +
		// rightScrollPane[1].getSize().getWidth() + "," +
		// leftScrollPane[1].getSize().getHeight());
	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		printSizeStatistics();
		super.processWindowEvent(e); // To change body of generated methods,
										// choose Tools | Templates.
	}

}
