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
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.TableCellRenderer;

import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.io.GroepenReader;
import nl.detoren.ijc.ui.model.SerieModel;
import nl.detoren.ijc.ui.model.SpelersIndelenModel;
import nl.detoren.ijc.ui.util.GBC;
import nl.detoren.ijc.ui.util.Utils;

/**
 *
 * @author Leo van der Meulen
 */
public class WedstrijdschemaDialoog extends JDialog {

	private static final long serialVersionUID = -294319141048482367L;

	private final static Logger logger = Logger.getLogger(GroepenReader.class.getName());

	private  int groep;
    private  JTable spelersTabel;
    private  JTable[] serieTabel;
    private JScrollPane leftScrollPane;
    private JScrollPane[] centerScrollPane;
    private JPanel centerPanel;
    private JPanel rightPanel;

    WedstrijdschemaDialoog(Frame frame, String title, int groepID) {
    	groep = groepID;
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();

        logger.log(Level.INFO, "Dialoog voor groep " + Groep.geefNaam(groepID));

        // Spelerstabel
        leftScrollPane = new JScrollPane();
        spelersTabel = new JTable(new SpelersIndelenModel(groep, panel)) {
			private static final long serialVersionUID = 1L;

			@Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                // Tooltip
                if (c instanceof JComponent) {
                    JComponent jc = (JComponent) c;
                    SpelersIndelenModel model = (SpelersIndelenModel) getModel();
                    jc.setToolTipText(model.getToolTip(row, column).toString());
                }

                //  Alternate row color
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
                }
                return c;
            }
        };
        spelersTabel.setDragEnabled(true);
        spelersTabel.setDropMode(DropMode.USE_SELECTION);
        spelersTabel.setTransferHandler(new TransferHandler() {
            /**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
            protected Transferable createTransferable(JComponent source) {
                SpelersIndelenModel model = (SpelersIndelenModel) ((JTable) source).getModel();
                String value = model.getValueAt(((JTable) source).getSelectedRow(), 0).toString();
                System.out.println("Dragging: " + value);
                return new StringSelection(value);
            }

            @Override
            public int getSourceActions(JComponent c) {
                return COPY;
            }
        });
        //spelersTabel.setTransferHandler(new TS());
        leftScrollPane.setViewportView(spelersTabel);
        Utils.fixedComponentSize(leftScrollPane, 340, 500);
        Utils.fixedColumSize(spelersTabel.getColumnModel().getColumn(0), 20);
        Utils.fixedColumSize(spelersTabel.getColumnModel().getColumn(1), 160);
        Utils.fixedColumSize(spelersTabel.getColumnModel().getColumn(2), 40);
        Utils.fixedColumSize(spelersTabel.getColumnModel().getColumn(3), 40);
        Utils.fixedColumSize(spelersTabel.getColumnModel().getColumn(4), 40);
        Utils.fixedColumSize(spelersTabel.getColumnModel().getColumn(5), 40);

        panel.add(leftScrollPane, BorderLayout.LINE_START);

        // WedstrijdTabellen
        serieTabel = new JTable[4];
        for (int i = 0; i < 4; ++i) {
            createSerieTabel(i, panel);
        }
        centerScrollPane = new JScrollPane[4];
        for (int i = 0;
                i < 4; ++i) {
            centerScrollPane[i] = new JScrollPane();
            Utils.fixedComponentSize(centerScrollPane[i], 340, 280);
            centerScrollPane[i].setViewportView(serieTabel[i]);
        }

        centerPanel = new JPanel();

        centerPanel.setLayout(new GridBagLayout());
        //centerPanel.add(new JLabel("Serie 1"), new GBC(0, 0).setFill(GridBagConstraints.VERTICAL));
        JPanel topPanel = new JPanel();
        topPanel.add(new JLabel("Serie 1"));
        topPanel.add(new JButton("Create"));	// TODO Add functionality to Create button
        topPanel.add(new JButton("Wis"));		// TODO Add functionality to Wis button
        centerPanel.add(topPanel, new GBC(0, 0).setFill(GridBagConstraints.VERTICAL));
        centerPanel.add(centerScrollPane[0], new GBC(1, 0).setFill(GridBagConstraints.VERTICAL));

        topPanel = new JPanel();
        topPanel.add(new JLabel("Serie 2"));
        topPanel.add(new JButton("Create")); // TODO Add functionality to Create button
        topPanel.add(new JButton("Wis"));	 // TODO Add functionality to Wis button
        centerPanel.add(topPanel, new GBC(2, 0).setFill(GridBagConstraints.VERTICAL));

        centerPanel.add(centerScrollPane[1], new GBC(3, 0).setFill(GridBagConstraints.VERTICAL));
        panel.add(centerPanel, BorderLayout.LINE_END);

        rightPanel = new JPanel();
        rightPanel.setLayout(new GridBagLayout());

        topPanel = new JPanel();
        topPanel.add(new JLabel("Serie 3"));
        topPanel.add(new JButton("Create"));  	// TODO Implement create button
        topPanel.add(new JButton("Wis"));		// TODO Implement Wis button
        rightPanel.add(topPanel, new GBC(0, 0).setFill(GridBagConstraints.VERTICAL));
        rightPanel.add(centerScrollPane[2], new GBC(1, 0).setFill(GridBagConstraints.VERTICAL));

        topPanel = new JPanel();
        topPanel.add(new JLabel("Triowedstrijden"));
        topPanel.add(new JButton("Create"));
        topPanel.add(new JButton("Wis"));
        rightPanel.add(topPanel, new GBC(2, 0).setFill(GridBagConstraints.VERTICAL));
        rightPanel.add(centerScrollPane[3], new GBC(3, 0).setFill(GridBagConstraints.VERTICAL));

        panel.add(rightPanel, BorderLayout.LINE_END);

        getContentPane().add(panel);
        setSize(1100, 700);
    }

    private void createSerieTabel(final int index, JPanel panel) {
        serieTabel[index] = new JTable(new SerieModel(groep, index, panel)) {
            /**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                //  Alternate row color
                if (!isRowSelected(row)) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : Color.LIGHT_GRAY);
                }
                return c;
            }
        };
        serieTabel[index].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                int r = serieTabel[index].rowAtPoint(e.getPoint());
                if (r >= 0 && r < serieTabel[index].getRowCount()) {
                    serieTabel[index].setRowSelectionInterval(r, r);
                } else {
                    serieTabel[index].clearSelection();
                }

                final int rowindex = serieTabel[index].getSelectedRow();
                if (rowindex < 0) {
                    return;
                }
                final SerieModel model = (SerieModel) serieTabel[index].getModel();
                final Wedstrijd w = model.getWedstrijd(rowindex);
                if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
                    JPopupMenu popup = new JPopupMenu();
                    JMenuItem menuItem = new JMenuItem("Verwijder wedstrijd");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            model.verwijderWedstrijd(rowindex);
                            model.forceRepaint();
                        }
                    });
                    popup.add(menuItem);
                    menuItem = new JMenuItem("Wis Spelers");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            w.setSpelers(Speler.dummySpeler(groep), Speler.dummySpeler(groep));
                            model.forceRepaint();
                        }
                    });
                    popup.add(menuItem);
                    menuItem = new JMenuItem("Wissel Spelers");
                    menuItem.addActionListener(new ActionListener() {

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            w.wisselSpelers();
                            model.forceRepaint();
                        }
                    });
                    popup.add(menuItem);
                    popup.show(e.getComponent(), e.getX(), e.getY());

                }
            }
        });
        serieTabel[index].setDragEnabled(true);
//        if (i < 3) {
            serieTabel[index].setDropMode(DropMode.ON_OR_INSERT_ROWS);
//        } else {
//            serieTabel[i].setDropMode(DropMode.ON);
//        }
        serieTabel[index].setFillsViewportHeight(true);
        serieTabel[index].setTransferHandler(new TransferHandler() {
            /**
			 *
			 */
			private static final long serialVersionUID = 1L;

			@Override
            /*
             Transfer het ID van de geselecteerde speler
             */
            protected Transferable createTransferable(JComponent source) {
                return new StringSelection((String) ((JTable) source).getModel().getValueAt(((JTable) source).getSelectedRow(), ((JTable) source).getSelectedColumn()));
            }

            @Override
            public boolean canImport(TransferHandler.TransferSupport support) {
                return true;
            }

            @Override
            /**
             Insert: Vervang de speler van een wedstrijd door de gedropte speler
             New: Creer nieuwe wedstrijd met gedropte speler en een dummy tegenstander
             */
            public boolean importData(TransferHandler.TransferSupport support) {
                try {
                    TransferHandler.DropLocation location = support.getDropLocation();
                    JTable jt = (JTable) support.getComponent();
                    JTable.DropLocation dl = (JTable.DropLocation) location;
                    SerieModel model = (SerieModel) jt.getModel();
                    int row = dl.getRow();// jt.getSelectedRow();
                    int col = dl.getColumn(); // jt.getSelectedColumn();
                    String val = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                    if (!dl.isInsertRow()) {
                        // Update een wedstrijd met een andere speler
                        System.out.println("Dropping at row " + row + ", column " + col + ", Value " + val);
                        if (col < 2) {
                            model.setSpeler(row, Integer.parseInt(val), true);
                        } else if (col > 2) {
                            model.setSpeler(row, Integer.parseInt(val), false);
                        }
                    } else {
                        System.out.println("Inserting at row " + row + ", column " + col + ", Value " + val);
                        // Nieuwe wedstrijd
                        model.insertWedstrijd(row);
                        // met geselecteerde speler
                        if (col < 2) {
                            model.setSpeler(row, Integer.parseInt(val), true);
                        } else if (col > 2) {
                            model.setSpeler(row, Integer.parseInt(val), false);
                        }
                    }
                    return true;
                } catch (Exception ex) {
                    System.out.println("Paste failed: " + ex.toString());
                    return false;
                }
            }
        });

        Utils.fixedColumSize(serieTabel[index].getColumnModel().getColumn(0), 30);
        Utils.fixedColumSize(serieTabel[index].getColumnModel().getColumn(1), 130);
        Utils.fixedColumSize(serieTabel[index].getColumnModel().getColumn(2), 15);
        Utils.fixedColumSize(serieTabel[index].getColumnModel().getColumn(3), 30);
        Utils.fixedColumSize(serieTabel[index].getColumnModel().getColumn(4), 130);

    }
}
