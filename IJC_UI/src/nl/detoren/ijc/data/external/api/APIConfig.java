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

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.DestroyFailedException;

public class APIConfig {

	UUID id = UUID.randomUUID();
	int apiId;
	String userName;
	String url;
	String loginpath;
	String pagepath;
	String template;
	boolean active;
	
	// Constructor
	public APIConfig(int apiId, String aCUsername, String aCUrl, String aCLoginPath, String aCPagePath) {
		// TODO Auto-generated constructor stub
		this.apiId=apiId;
		userName=aCUsername;
		url = aCUrl;
		loginpath=aCLoginPath;
		pagepath=aCPagePath;
		template = "";
		active = true;
	}	

	public APIConfig() {
		// TODO Auto-generated constructor stub
	}

	// getter for Id
	public UUID getId() {
		return id;
	}

	// getter for apiId
	public int getAPIId() {
		return apiId;
	}

	// aetter for apiId
	public void setAPIId(int id) {
		apiId = id;
	}

	// getter for active
	public boolean getActive() {
		return active;
	}

	// setter for active
	public void setActive(boolean act) {
		this.active = act;
	}

	// setter for username
	public void setUserName(String aCUsername) {
		userName = aCUsername;
	}

	// getter for username
	public String getUserName() {
		return userName;
	}

	// getter for url
	public String getURL() {
		return url;
	}

	// setter for url
	public void setURL(String url) {
		this.url = url;
	}

	// getter for loginpath
	public String getLoginPath() {
		return loginpath;
	}

	// setter for loginpath
	public void setLoginPath (String path) {
		this.loginpath = path;
	}
	
	// getter for pagepath
	public String getPagePath() {
		return pagepath;
	}

	// setter for pagepath
	public void setPagePath (String path) {
		this.pagepath = path;
	}

	
	// getter for template
	public String getTemplate() {
		return template;
	}

	// setter for template
	public void setTemplate (String template) {
		this.template = template;
	}

    /** 
     * setPassword in KeyStore
     */
    public void setPassword(KeyStore ks, String alias, byte[] password, char[] master) throws GeneralSecurityException, DestroyFailedException {
    	SecretKey wrapper = new SecretKeySpec(password, "DSA");
    	KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(wrapper);
    	KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(master);
    	try {
    		ks.setEntry(alias, entry, pp);
    	}
    	catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		    		
    	}
    	finally {
    		pp.destroy();
    	}
    }
    
    /** 
     * getPassword 
     */
    public String getPassword(KeyStore ks, char[] salt) throws GeneralSecurityException, DestroyFailedException {
    	String password = new String(getKSPassword(ks, id.toString(), salt));
    	return password;
    }    
    
    
    /** 
     * getPassword from KeyStore
     */
    public byte[] getKSPassword(KeyStore ks, String alias, char[] master) throws GeneralSecurityException, DestroyFailedException {
    	KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(master);
    	try {
    		KeyStore.SecretKeyEntry e = (KeyStore.SecretKeyEntry) ks.getEntry(alias, pp);
    		try {
    			return e.getSecretKey().getEncoded();
    		}
    		catch (NullPointerException npe) {
    			return new byte[0];
    		}
    	}
    	finally {
    		pp.destroy();
    	}
    }

    /** 
     * setPassword in KeyStore
     */
    public void setKSPassword(KeyStore ks, String alias, byte[] password, char[] master) throws GeneralSecurityException, DestroyFailedException {
    	SecretKey wrapper = new SecretKeySpec(password, "DSA");
    	KeyStore.SecretKeyEntry entry = new KeyStore.SecretKeyEntry(wrapper);
    	KeyStore.PasswordProtection pp = new KeyStore.PasswordProtection(master);
    	try {
    		ks.setEntry(alias, entry, pp);
    	}
    	catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();		    		
    	}
    	finally {
    		pp.destroy();
    	}
    }

    /** 
	 * setter for password
     * @throws DestroyFailedException 
     * @throws GeneralSecurityException 
     */
	public void setPassword(KeyStore ks, String aCPassword, char[] salt) throws GeneralSecurityException, DestroyFailedException {
		//
		setKSPassword(ks, id.toString(), aCPassword.getBytes(), salt);
	}
		
}
