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
import java.util.logging.Logger;


import nl.detoren.ijc.ui.view.Hoofdscherm;

public class APIConfigs {

    public ArrayList<APIConfig> apiconfigs;

	private final static Logger logger = Logger.getLogger(Hoofdscherm.class.getName());
    
    public APIConfigs() {
        apiconfigs = new ArrayList<APIConfig>();
        // tijdelijke niet uit configuratie maar hardcoded
        //APIConfig eerste= new APIConfig(1, "ijcdestellingapi", "/@login", "jeugd/ijc");
        //apiconfigs.add(eerste);
    }
    
    public int size() {
    	return apiconfigs.size();
    }   
 
}
