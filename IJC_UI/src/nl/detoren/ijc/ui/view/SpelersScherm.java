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

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

import nl.detoren.ijc.db.DBRonde;
import nl.detoren.ijc.db.DBSpeler;
import nl.detoren.ijc.db.SpelerDatabase;
import nl.detoren.ijc.ui.control.IJCController;
import nl.detoren.ijc.ui.graph.LineGraph;
import nl.detoren.ijc.ui.model.DBRondeModel;
import nl.detoren.ijc.ui.model.DBSpelerModel;
import nl.detoren.ijc.ui.model.DBWedstrijdenModel;
import nl.detoren.ijc.ui.util.Utils;

/**
 * Structure of the GUI: JFrame Hoofdscherm (this) JPanel hoofdPanel JPanel
 * buttonPanel JButton resetButton JPanel dataPanel JScrollPane rondesPane
 * JTable rondesTabel JScrollPane spelersPane JTable spelersTabel JPanel
 * gegevensPanel JPanel graphPanel JScrollPane wedstrijdenPane JTable
 * wedstrijdenTabel
 *
 * @author Leo van der Meulen
 */
public class SpelersScherm extends JFrame {

	// Colors and fonts
	private static final Color light_green = new Color(200, 255, 200);
	private static final Color light_red = new Color(255, 200, 200);
	private static final Color light_yellow = new Color(255, 204, 153);
	// private static final Font courierFont = new Font("Courier New",
	// Font.PLAIN, 11);

	private static final long serialVersionUID = -1L;
	private final static Logger logger = Logger.getLogger(SpelersScherm.class.getName());

	private JPanel hoofdPanel;
	private JPanel gegevensPanel;
	private JPanel dataPanel;
	private JPanel graphPanel;
	private JScrollPane rondesPane;
	private JScrollPane spelersPane;
	private JScrollPane wedstrijdenPane;
	private JTable spelersTabel;
	private JTable wedstrijdenTabel;
	private JTable rondesTabel;

	private DBRondeModel rondeModel;
	private DBSpelerModel spelersModel;
	private DBWedstrijdenModel wedstrijdenModel;

	private IJCController controller;
	private SpelerDatabase db;

	/**
	 * Creates new form MainWindow
	 */
	public SpelersScherm() {
		logger.log(Level.INFO, "Constructor");
		db = SpelerDatabase.getInstance();
		db.openDatabase();
		initComponents();
		initSizes();
		this.repaint();
	}

	private void initComponents() {
		logger.log(Level.INFO, "Init components");
		//setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
		setTitle(IJCController.c().verenigingNaam + " - " + "Spelers");

		hoofdPanel = new JPanel();
		// hoofdPanel.setLayout(new GridLayout(1, 3));
		// addButtons();
		addMenubar();

		dataPanel = new JPanel();
		// dataPanel.setLayout(new GridLayout(1,3));

		rondesPane = new JScrollPane();
		spelersPane = new JScrollPane();
		dataPanel.add(rondesPane);
		dataPanel.add(spelersPane);

		gegevensPanel = new JPanel();
		gegevensPanel.setLayout(new ExtendedGridLayout(2, 1));
		graphPanel = new JPanel();
		wedstrijdenPane = new JScrollPane();
		gegevensPanel.add(graphPanel);
		gegevensPanel.add(wedstrijdenPane);
		dataPanel.add(gegevensPanel);

		fillRondePane();

		hoofdPanel.add(dataPanel);

		this.add(hoofdPanel);

		pack();

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent event) {
				//System.exit(0);
			}
		});

	}

	private void addMenubar() {
		// Menu bar met 1 niveau
		JMenuBar menubar = new JMenuBar();
		JMenu filemenu = new JMenu("Bestand");
		// File menu
		JMenuItem item = new JMenuItem("Afsluiten");
		item.setAccelerator(KeyStroke.getKeyStroke('X', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.saveState(false, null);
				System.exit(EXIT_ON_CLOSE);
			}
		});
		filemenu.add(item);
		menubar.add(filemenu);
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
		fixedComponentSize(this, 1200, 670);
		fixedComponentSize(hoofdPanel, 1190, 660);
		fixedComponentSize(dataPanel, 1190, 650);
		fixedComponentSize(rondesPane, 90, 200);
		fixedComponentSize(spelersPane, 280, 600);
		fixedComponentSize(wedstrijdenPane, 560, 200);
		fixedComponentSize(graphPanel, 710, 400);

		// Fix the size of the displayed tables
		fixedColumSize(rondesTabel.getColumnModel().getColumn(0), 95);

		fixedColumSize(spelersTabel.getColumnModel().getColumn(0), 30);
		fixedColumSize(spelersTabel.getColumnModel().getColumn(1), 160);
		fixedColumSize(spelersTabel.getColumnModel().getColumn(2), 70);
		// {"Seizoen", "Periode", "Ronde", "Speler", "Kleur", "Tegenstander",
		// "Resultaat"};
		fixedColumSize(wedstrijdenTabel.getColumnModel().getColumn(0), 60);
		fixedColumSize(wedstrijdenTabel.getColumnModel().getColumn(1), 60);
		fixedColumSize(wedstrijdenTabel.getColumnModel().getColumn(2), 60);
		fixedColumSize(wedstrijdenTabel.getColumnModel().getColumn(3), 160);
		fixedColumSize(wedstrijdenTabel.getColumnModel().getColumn(4), 60);
		fixedColumSize(wedstrijdenTabel.getColumnModel().getColumn(5), 160);
		fixedColumSize(wedstrijdenTabel.getColumnModel().getColumn(6), 70);
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

	protected void fillRondePane() {
		logger.log(Level.INFO, "Maak de verschillende panes and viewports");

		rondeModel = new DBRondeModel(rondesPane);
		rondesTabel = new JTable(rondeModel) {
			private static final long serialVersionUID = -1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				// Tooltip
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					jc.setToolTipText(((DBRondeModel) getModel()).getToolTip(row, column).toString());
				}
				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				if (rondeModel.isSelectedRonde(row)) {
					c.setForeground(Color.BLUE);
				} else {
					c.setForeground(Color.BLACK);
				}
				return c;
			}
		};

		rondesTabel.addMouseListener(new java.awt.event.MouseAdapter() {
		    @Override
		    public void mouseClicked(java.awt.event.MouseEvent evt) {
		        int row = rondesTabel.rowAtPoint(evt.getPoint());
		        int col = rondesTabel.columnAtPoint(evt.getPoint());
		        if (row >= 0 && col >= 0) {
		            System.out.println("Clicked on row " + row + ", col " + col + " of rondes");
		            rondeModel.setSelectedRonde(row);
		            DBRonde selRonde = rondeModel.getSelectedRond();
	            	spelersModel.setRonde(selRonde);
	            	spelersModel.fireTableChanged(null);
	            	initSizes();
		            if (selRonde != null) {
		            	System.out.println("Selected ronde : " + selRonde);
		            } else {
		            	System.out.println("Selected ronde : -");
		            }
		        }
		        hoofdPanel.revalidate();
		        hoofdPanel.repaint();
		    }
		});
		spelersModel = new DBSpelerModel(rondesPane);
		spelersTabel = new JTable(spelersModel) {
			private static final long serialVersionUID = -1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				// Tooltip
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					jc.setToolTipText(((DBSpelerModel) getModel()).getToolTip(row, column).toString());
				}
				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				return c;
			}
		};

		spelersTabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				List<DBSpeler> selectectSpelers = ((DBSpelerModel) spelersTabel.getModel()).getSelectedSpelers();
				wedstrijdenModel.setSpelers(selectectSpelers);
				if (selectectSpelers != null && selectectSpelers.size() > 0) {
					LineGraph g2 = new LineGraph("", "Ronde", "Rating", true);
					//g2.initialize(createXYDataset(selectectSpelers));
					g2.initialize(createCategoryDataset(selectectSpelers));
					fixedComponentSize(g2, 700,380);
					logger.log(Level.INFO, "Adding graph");
					graphPanel.removeAll();
					graphPanel.add(g2);
					hoofdPanel.revalidate();
				}
				hoofdPanel.repaint();
			}
		});

		wedstrijdenModel = new DBWedstrijdenModel(rondesPane);
		wedstrijdenTabel = new JTable(wedstrijdenModel) {
			private static final long serialVersionUID = -1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				// Tooltip
				if (c instanceof JComponent) {
					JComponent jc = (JComponent) c;
					jc.setToolTipText(((DBWedstrijdenModel) getModel()).getToolTip(row, column).toString());
				}
				// Alternate row color
				if (!isRowSelected(row)) {
					c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
				}
				if (column == 6) {
					String val = (((DBWedstrijdenModel) getModel()).getValueAt(row, column)).toString();
					if (val.equals("WINST")) {
						c.setBackground(light_green);
					} else if (val.equals("VERLIES")) {
						c.setBackground(light_red);
					} else if (val.equals("REMISE")) {
						c.setBackground(light_yellow);
					}
				}
				return c;
			}
		};

		rondesPane.setViewportView(rondesTabel);
		spelersPane.setViewportView(spelersTabel);
		wedstrijdenPane.setViewportView(wedstrijdenTabel);

		pack();

	}

	@Override
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
	}

	public void actieTemplate() {
		// do Stuff
		hoofdPanel.repaint();
	}

//	@SuppressWarnings("rawtypes")
//	private XYDataset createXYDataset(List<DBSpeler> spelers) {
//		XYSeriesCollection dataset = new XYSeriesCollection();
//		for (DBSpeler speler : spelers) {
//			List result = db.query("select (ronde.periode*10+ronde.ronde), h.rating "
//					+ "from DBHistorie h where speler.id = " + speler.getId());
//			// Remark bij L.P.Dam 8-2-2017 - Voorstel :
//			// List result = db.query("select (ronde.periode*10+ronde.ronde), h.rating "
//			//		+ "from DBHistorie h where speler.uid = " + speler.getUid());
//			//
//			XYSeries serie = new XYSeries(speler.getAfkorting());
//			for (int i = 0; i < result.size(); ++i) {
//				Object o[] = (Object[]) result.get(i);
//				serie.add(((Integer) o[0]).intValue(), ((Integer) o[1]).intValue());
//			}
//			dataset.addSeries(serie);
//		}
//		return dataset;
//	}

	@SuppressWarnings("rawtypes")
	private CategoryDataset createCategoryDataset(List<DBSpeler> spelers) {
		DefaultCategoryDataset cat = new DefaultCategoryDataset();
		for (DBSpeler speler : spelers) {
			List result = db.query("select (ronde.periode*10+ronde.ronde), h.rating "
					+ "from DBHistorie h where speler.id = " + speler.getId());
			// Remark bij L.P.Dam 8-2-2017 - Voorstel :
			// List result = db.query("select (ronde.periode*10+ronde.ronde), h.rating "
			//		+ "from DBHistorie h where speler.uid = " + speler.getUid());
			//
			for (int i = 0; i < result.size(); ++i) {
				Object o[] = (Object[]) result.get(i);
				Double val = new Double(((Integer) o[1]).intValue());
				cat.addValue((Number) val, speler.getAfkorting(), ((Integer) o[0]).intValue());
			}
		}
		return cat;
	}

}
