package nl.detoren.ijc.ui.control;

import java.util.ArrayList;

import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.groepen.Speler;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;

public class Status {
    public boolean automatisch = true;
    public  Groepen groepen;
    public Groepen wedstrijdgroepen;
    public Wedstrijden wedstrijden;
    public Groepen resultaatVerwerkt;
    public ArrayList<Speler> externGespeeld;

}
