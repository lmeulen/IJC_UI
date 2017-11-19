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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.ui.control.IJCController;

public class KNSBNummerDialoog extends JDialog {
	private IJCController controller = null;
    JLabel[] naamVelden;
    JTextField[] knsbVelden;

	private final static Logger logger = Logger.getLogger(ExternDialog.class.getName());

	public KNSBNummerDialoog(Frame frame, String title) {
        super(frame, "Invoer KNSB nummers voor niet-KNSB spelers");
        controller = IJCController.getInstance();
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    	logger.log(Level.INFO, "KNSB Nummers invoeren.");

    	ArrayList<Speler> spelers = controller.getNietKNSBLeden();
    	if (spelers == null) spelers = new ArrayList<>(); 
    	int aantal = spelers.size();
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(aantal, 3));
        knsbVelden = new JTextField[aantal];
        naamVelden = new JLabel[aantal];
        for (int i = 0; i < aantal; ++i) {
            naamVelden[i] = new JLabel();
            if (i < spelers.size()) naamVelden[i].setText(spelers.get(i).getNaam()); 
            naamVelden[i].setMinimumSize(new Dimension(20, 10));
            naamVelden[i].setMaximumSize(new Dimension(20, 10));
            panel.add(naamVelden[i]);

            knsbVelden[i] = new JTextField();
            if (i < spelers.size()) knsbVelden[i].setText(spelers.get(i).getNaam()); 
            knsbVelden[i].setMinimumSize(new Dimension(20, 10));
            knsbVelden[i].setMaximumSize(new Dimension(20, 10));
            panel.add(knsbVelden[i]);
        }
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                controller.wisExterneSpelers();
                for (JTextField jtf : knsbVelden) {
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
        setSize(600, (aantal + 1) * 16);
        setLocationRelativeTo(frame);
    }

}
