/**
 * Copyright (C) 2022 Lars Dam
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
package nl.detoren.ijc.data.external.api;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;

import nl.detoren.ijc.ui.util.Utils;
import nl.detoren.ijc.ui.view.Bevesting;
import nl.detoren.ijc.ui.view.Hoofdscherm;

public class APIs {

    private ArrayList<API> apis;

	private final static Logger logger = Logger.getLogger(Hoofdscherm.class.getName());
    
    public APIs() {
        apis = new ArrayList<API>();
        API plone52 = new API(1,"Plone", 5.2,6.0);
        apis.add(plone52);
        }
    
    public void export(String url, String path, String username, String password, int periode, int ronde) {
     	for (API api: apis) {
    		this.export(api, url, path, username, password, periode, ronde);
    	}
    }

    public void verwijderGebruikers(String url, String username, String password) {
     	for (API api: apis) {
    		this.verwijderGebruikers(api, url, username, password);
    	}
    }
    
    public int size() {
    	return apis.size();
    }   

    
    public void verwijderGebruikers(API api, String url, String username, String password) {
		String userList = "";
		Token plone52Token = null;
		int statusCode = 0;
		int deleted =0;
		switch(api.type) {
			case "Plone":
				if (api.versionMin == 5.2) {
					if (Utils.internet_connectivity()) {
						try {
							plone52Token = Plone52.login(url, username, password);
						}
						catch (Exception e) {
							logger.log(Level.INFO, "Could not login for external api : " + api.getType() + api.getVersionMin());
							return;
						}
						logger.log(Level.INFO, "Login for external api : " + api.getType() + api.getVersionMin() + " succesful. Token retrieved!");
						logger.log(Level.INFO, "Token :" + plone52Token.getToken());
						try {
							userList = Plone52.userList(plone52Token);
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						logger.log(Level.INFO, "Userlist opgehaald");
						JSONArray jArray = new JSONArray(userList);
						for(int index=0; index<jArray.length(); index++) {
							JSONObject user = (JSONObject) jArray.get(index);
							String strId = (String) user.get("id");
							String strFullName = "";
							try {
								strFullName = (String) user.get("fullname");
							}
							catch (Exception e) {
								
							}

							// Delete by keyword (hard coded)
							String keyword = "http";
							if (strFullName.contains(keyword)) {
								// logger.log(Level.INFO, keyword + " FOUND in fullname for user : " + strId);
								try {
									statusCode = Plone52.delete(plone52Token,strId);
								} catch (Exception e1) {
									e1.printStackTrace();
								}
								if (statusCode != 204) {
									logger.log(Level.INFO, "Deletion of user " + strId + " failed! StatusCode : " + statusCode);
								} else {
									// logger.log(Level.INFO, "Deletion of user " + strId + " succesful!");
									deleted++;
								}
							} else {
								// logger.log(Level.INFO, keyword + " not found in fullname for user : " + strId);
							}
							
							
							String strUser = (String) user.get("id");
							int res = Bevesting.YesNoCancel("Verwijderen gebruiker " + strUser + "?");
							switch (res) {
								case 0: // YES
									try {
										statusCode = Plone52.delete(plone52Token, strUser);
									} catch (Exception e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									if (statusCode != 204) {
										logger.log(Level.INFO, "Deletion of user " + strUser + " failed! StatusCode : " + statusCode);
									} else {
										logger.log(Level.INFO, "Deletion of user " + strUser + " succesful!");
									}
									break;
								case 1: // NO
									break;
								default: //CANCEL
									return;
							}
				 
						}
						logger.log(Level.INFO, "Removed " + deleted + " users");
					}
				}
		}
    }    
    
    public void export(API api, String url, String path, String username, String password, int periode, int ronde) {
		String response = "";
		Token plone52Token = null;
		switch(api.type) {
		case "Plone":
			if (api.versionMin == 5.2) {
				if (Utils.internet_connectivity()) {
					try {
						plone52Token = Plone52.login(url, username, password);
					}
					catch (Exception e) {
				    	logger.log(Level.INFO, "Could not login for external api : " + api.getType() + api.getVersionMin());
				    	return;
				    }
					logger.log(Level.INFO, "Login for external api : " + api.getType() + api.getVersionMin() + " succesful. Token retrieved!");
					logger.log(Level.INFO, "Token :" + plone52Token.getToken());
					try {
						response = Plone52.createpage(plone52Token, url, path, periode, ronde);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	logger.log(Level.INFO, "Response van Plone52 request is : " + response);
			    }					
			}	
		}		
	}
}
