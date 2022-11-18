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

public class API {

	int	ID;
	String type;
	Double versionMin;
	Double versionMax;
	boolean debugLog;
	boolean active;

	// Constructor
	public API(int cId, String cType, double cVersionMin, double cVersionMax) {
		// TODO Auto-generated constructor stub
		ID =cId;
		type=cType;
		versionMin = cVersionMin;
		versionMax = cVersionMax;		
	}

	// getter for type
	public String getType() {
		return type;
	}

	// getter for versionMin
	public double getVersionMin() {
		return versionMin;
	}
	
	// getter for versionMax
	public double getVersionMax() {
		return versionMax;
	}
	
	// getter for debugLog
	public boolean getDebugLog() {
		return debugLog;
	}
	
	// getter for debugLog
	public boolean getActive() {
		return active;
	}

}
