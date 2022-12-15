/**
 * Copyright (C) 2016-2022 Lars Dam
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
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.DestroyFailedException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import nl.detoren.ijc.data.external.api.APIConfig;
import nl.detoren.ijc.data.groepen.Groep;
import nl.detoren.ijc.data.wedstrijden.Wedstrijd;
import nl.detoren.ijc.io.GroepenReader;
import nl.detoren.ijc.ui.control.IJCController;

/**
 *
 * @author Lars Dam
 */
class PasswordDialoog extends JDialog {

	//private static final long serialVersionUID = -3921269216014454438L;

	private final static Logger logger = Logger.getLogger(GroepenReader.class.getName());

	private static IJCController controller;
    JPasswordField oldPassword;
    JPasswordField newPassword1;
    JPasswordField newPassword2;
    APIConfig apiconfig;
    JLabel lblStatus = new JLabel("");;
    
    PasswordDialoog(Frame frame, String title) {
        super(frame, title);
        controller = IJCController.getInstance();
        setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    	logger.log(Level.INFO, "Change Password Dialog");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2));
        oldPassword = new JPasswordField();
        newPassword1 = new JPasswordField();
        newPassword1.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
            	if (Arrays.equals(newPassword1.getPassword(), newPassword2.getPassword())) {
            		lblStatus.setText("");
            	} else {
            		lblStatus.setText("Wachtwoorden komen niet overeen");
            	}
            }
        });
        newPassword2 = new JPasswordField();
        newPassword2.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
            	if (Arrays.equals(newPassword1.getPassword(), newPassword2.getPassword())) {
            		lblStatus.setText("");
            	} else {
            		lblStatus.setText("Wachtwoorden komen niet overeen");
            	}
            }
        });
        panel.add(new JLabel("Oude wachtwoord"));
        panel.add(oldPassword);
        panel.add(new JLabel("Nieuwe wachtwoord"));
        panel.add(newPassword1);
        panel.add(new JLabel("Nogmaals nieuwe wachtwoord"));
        panel.add(newPassword2);
        panel.add(new JLabel(""));
        panel.add(lblStatus);
        panel.add(new JLabel(""));
    	logger.log(Level.INFO, "Added password fields");
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	// Do Check and Saving
            	// Check old password
            	try {
					if (controller.checkPassword(apiconfig.getId().toString(), controller.getSalt(), oldPassword.getPassword())) {
						logger.log(Level.INFO, "oldPassword is OK");
						if (Arrays.equals(newPassword1.getPassword(), newPassword2.getPassword())) {
							logger.log(Level.INFO, "newPasswords are equal");                		
							//if (savenewPassword(newPassword1)) {
							try {
								if (controller.setPassword(apiconfig.getId().toString(), (new String(newPassword1.getPassword()).getBytes()), controller.getSalt())) {
									logger.log(Level.INFO, "newPassword is Set");
									setVisible(false);
									dispose();
								} else {
									FoutMelding.melding("Wachtwoord instellen in mislukt!. Probeer het opnieuw.\r\nMocht het probleem blijven, neem contact op met de ontwikkelaars.");
								}
							} catch (GeneralSecurityException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (DestroyFailedException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					} else {
						FoutMelding.melding("Oud Wachtwoord niet correct. Bent u het wachtwoord kwijt?\r\nVerwijder dan de API en maak een nieuwe aan");
					}
				} catch (GeneralSecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (DestroyFailedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        });
        panel.add(new JLabel(""));
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
            	logger.log(Level.INFO, "Action performed in Cancel");
                setVisible(false);
                dispose();
            }
        });
        panel.add(saveButton);
        panel.add(cancelButton);
    	logger.log(Level.INFO, "Buttons added");
        getContentPane().add(panel);
    	logger.log(Level.INFO, "Added Panel");
        setSize(600, 150);
        setLocationRelativeTo(frame);
    	logger.log(Level.INFO, "Dialog ready");
    }
    
    public void setAPIConfig(APIConfig apiconfig) {
    	this.apiconfig = apiconfig;
    }
    
    public boolean checkoldPassword(char[] pass1, char[] pass2) {
    	if (Arrays.equals(pass1, pass2)) {
    		return true;
    	} else {
    		return false;
    	}
    }
}
