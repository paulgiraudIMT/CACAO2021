package abstraction.eq2Producteur2;

import java.util.LinkedList;
import java.util.Map;

import abstraction.eq8Romu.produits.Feve;

public abstract class Producteur2Aleas extends Producteur2Param  {

	public Producteur2Aleas() {
		super();
	}

	public void lesProblemes() {
		// intempéries -> destruction stock + arbres
		if (Math.random() < PROBA_INTEMPERIE) {
			// l'intemperie à lieu dans ce cas
			// on tire un pourcentage sur chaque element stocké
			for (Feve e : listeProd) {
				double pourcentageStockADetruire = Math.random() / 2. ;
				double stockActuel = qttTotale(e).getValeur();
				// pourcentage du stock qui sera détruit lors de l'intempérie 
				// il n'est pas possible que plus de la moitie du stock soit détruite
				// le nombre obtenu est contenu entre 0 et 0.5
				vente(pourcentageStockADetruire * stockActuel, e);
			}
			// on tire un pourcentage sur chaque arbre
			// for (arbre a : ){
		}
		//faire focntion revolte des prod si plus dargent pour les payer
		revolte();
		
	}
	
	
	
}
