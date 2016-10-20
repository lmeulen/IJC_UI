package nl.detoren.ijc.ui.control;

import nl.detoren.ijc.data.groepen.Groepen;
import nl.detoren.ijc.data.wedstrijden.Wedstrijden;

public interface GroepenIndelerInterface {

	/**
	 * Maak de groepsindeling voordat de wedstrijden worden bepaald. Spelers die afwezig zijn, worden uit de speellijst
	 * verwijderd. Indien van toepassing, worden 3 of 4 spelers doorgescheven naar een hogere groep. De hogere groep
	 * eindigt altijd met een even aantal spelers. Bij oneven spelers worden er dus 3 doorgeschoven en bij even spelers
	 * mogen er 4 doorschuiven.
	 *
	 * @param aanwezigheidsGroepen Overzicht spelers per groen met aanwezigheidsinfo
	 * @return de wedstrijdgroepen
	 */
	Groepen maakGroepsindeling(Groepen aanwezigheidsGroepen);

	/**
	 * Werk de groepsindeling van één groep bij voordat de wedstrijden worden bepaald. Spelers die afwezig zijn, worden 
	 * uit de speellijst verwijderd. Indien van toepassing, worden 3 of 4 spelers doorgescheven naar een hogere groep. 
	 * De hogere groep eindigt altijd met een even aantal spelers. Bij oneven spelers worden er dus 3 doorgeschoven en 
	 * bij even spelers mogen er 4 doorschuiven.
	 * @param aanwezigheidsGroepen Overzicht spelers per groep met aanwezigheidsinfo
	 * @param wedstrijdGroepen Huidige wedstrijdgroepen
	 * @param groepID Specificeert de groep die ge-update moet worden
	 * @return de betreffende wedstrijdgroep
	 */
	Groepen maakGroepsindeling(Groepen aanwezigheidsGroepen, Groepen wedstrijdGroepen, int groepID);

	/**
	 Maak het westrijdschema voor een avond
	 @param groepen
	 @param periode
	 @param ronde
	 @return 
	 */
	Wedstrijden maakWedstrijdschema(Groepen groepen);

	/**
	 * Update wedstrijden voor één groep. Wedstrijden voor alle andere groepen blijven
	 * ongewijzigd.
	 * @param wedstrijden Huidige wedstrijden voor alle groepen
	 * @param wedstrijdgroepen Huidige wedstrijdgroepen voor all groepen
	 * @param groepID ID van groep om opnieuw te bepalen
	 * @return update van wedstrijden met nieuwe wedstrijden voor specifieke groep
	 */
	Wedstrijden updateWedstrijdschema(Wedstrijden wedstrijden, Groepen wedstrijdgroepen, int groepID);

}