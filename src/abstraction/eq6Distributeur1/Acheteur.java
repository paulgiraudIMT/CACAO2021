package abstraction.eq6Distributeur1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import abstraction.eq8Romu.contratsCadres.Echeancier;
import abstraction.eq8Romu.contratsCadres.ExemplaireContratCadre;
import abstraction.eq8Romu.contratsCadres.IAcheteurContratCadre;
import abstraction.eq8Romu.contratsCadres.IVendeurContratCadre;
import abstraction.eq8Romu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eq8Romu.produits.Chocolat;
import abstraction.eq8Romu.produits.ChocolatDeMarque;
import abstraction.fourni.Filiere;
import abstraction.fourni.IActeur;
import abstraction.fourni.Journal;

public class Acheteur extends Vendeur implements IAcheteurContratCadre {

	protected LinkedList<ChocolatDeMarque> produitTG;
	protected List<ChocolatDeMarque> pasTG;
	protected SuperviseurVentesContratCadre superviseur;
	protected int i;
	protected int j;
	private Journal journalAchats;

	//Elsa

	public Acheteur() {
		super();
		this.journalAchats = new Journal("Journal achats", this);
	}


	//Louis

	/**
	 * initialiser les journaux
	 */

	public void initialiser() {
		super.initialiser();
		journaux.add(journalAchats);
		journalAchats.ajouter("tout les contrats cadres conclus");
	}



	//Louis

	/**
	 * Est appelée au début de chaque tour, on tire au sort un transformateur pour chaque ChocolatDeMarque de notre catalogue et on initialise un contrat cadre d’une durée de un step. 
	 */

	public void next() {
		pasTG = this.chocolatVendu();
		produitTG=new LinkedList<ChocolatDeMarque>();
		this.superviseur=(SuperviseurVentesContratCadre)Filiere.LA_FILIERE.getActeur("Sup.CCadre");
		choixTG();
		//System.out.println("chocoVendu " +chocolatVendu().toString());
		for (ChocolatDeMarque produit : this.getCatalogue()) {
			List<IActeur> vendeurs = new LinkedList<IActeur>();
			for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
				if (acteur!= this && acteur instanceof IVendeurContratCadre && ((IVendeurContratCadre)acteur).vend(produit)) {
					vendeurs.add(acteur);
				}
			}

			if (vendeurs.size()!=0) {
				//System.out.println(vendeurs.toString());
				int rnd = new Random().nextInt(vendeurs.size());
				IActeur vendeur = vendeurs.get(rnd);
				//System.out.println(maxQuantite(produit));
				if (maxQuantite(produit)>superviseur.QUANTITE_MIN_ECHEANCIER) {
					superviseur.demande((IAcheteurContratCadre)this, ((IVendeurContratCadre)vendeur), produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, Filiere.LA_FILIERE.getEtape()+2, maxQuantite(produit)), cryptogramme, false);
				}
			}
		}

		super.next();
	}

	//au bout d'un moment on achete rien
	//on achete trop peu et pas à cote d'imt
	//tg = cote d'imt



	//Elsa
	/**
	 * Permet de négocier la quantité de produit voulu. 
	 * On essaie de ne pas dépasser une quantité de produit égale à 15% de plus que ce qui s’est vendu au tour précédent (grâce à l’historique) en enlevant la quantité de ce même produit qu’il nous reste en stock. 
	 * On vérifie aussi que cette quantité est supérieure à la quantité minimale demandée par le superviseur. 
	 * On liste tous les produits de la même marque et de la même catégorie et on regarde quel produit on a acheté au meilleur
	 	prix au tour précédent. On augmente notre maxQuantité du produit le moins cher et on diminue celle des produit les 
	 	plus cher.
	 */

	@Override
	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
		i++;
		Echeancier e = contrat.getEcheancier();
		
		double maxQuantite = maxQuantite((ChocolatDeMarque) contrat.getProduit());
		
		
		if (e.getQuantite(e.getStepFin())>maxQuantite) {
			if(maxQuantite*(0.90+i/100)>((SuperviseurVentesContratCadre)Filiere.LA_FILIERE.getActeur("Sup.CCadre")).QUANTITE_MIN_ECHEANCIER) {
				e.set(e.getStepDebut(), maxQuantite*(0.90+i/100));
				}
			
			else {
				e.set(e.getStepDebut(), ((SuperviseurVentesContratCadre)Filiere.LA_FILIERE.getActeur("Sup.CCadre")).QUANTITE_MIN_ECHEANCIER);
			}
		}
		else {
			
			e.set(e.getStepDebut(), e.getQuantite(e.getStepFin()));
		}

		return e;
	
}


	//Elsa

	/**
	 * Permet de négocier le prix d’achat des produits avec les transformateurs. 
	 * Nous allons fixer un prix maximal d’achat qui est de 75% de notre prix de vente. 
	 * Si le produit est en tête de gondole, nous allons chercher à l’acheter à 70% de notre prix de vente (car plus attractif). 
	 * Ensuite à chaque tour de négociation nous allons augmenter le prix jusqu’à obtenir une entente avec le transformateur ou arrêter si il dépasse notre prix maximal.
	 */

	@Override
	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		i=0;
		//System.out.println("prix");
		double maxPrix= this.prix.get((ChocolatDeMarque)contrat.getProduit()).getValeur()*0.75;
		if (contrat.getTeteGondole()) {
			maxPrix=0.9*maxPrix;
		}

		if (contrat.getPrix()>maxPrix) {
			j++;
			return maxPrix*(0.85+j/100);}
		else {
			return contrat.getPrix(); 
		}
	}



	//Louis

	/**
	 * Permet de modifier le stock en utilisant ajouterStock(Object produit, double quantite, boolean tg) de la classe stocks.
	 * Elle permet aussi de mettre à jour le journal des achats.
	 */

	@Override
	public void receptionner(Object produit, double quantite, ExemplaireContratCadre contrat) {
		//System.out.println(quantiteEnVenteTG());
		this.superviseur=(SuperviseurVentesContratCadre)Filiere.LA_FILIERE.getActeur("Sup.CCadre");
		ajouterStock((ChocolatDeMarque)produit, quantite,contrat.getTeteGondole());
		journalAchats.ajouter("achat de "+quantite+" "+produit.toString()+" a "+contrat.getVendeur().toString()+" pour un prix de "+contrat.getPrix());
		if (!contrat.getTeteGondole() && quantite*0.1>superviseur.QUANTITE_MIN_ECHEANCIER) {
			//System.out.println(quantite*0.1);
			//superviseur.demande((IAcheteurContratCadre)this, ((IVendeurContratCadre)contrat.getVendeur()), produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, Filiere.LA_FILIERE.getEtape()+2, quantite*0.1), this.cryptogramme, true);
			
		}
	}



	//Louis

	/**
	 * Définit la quantité maximale du ChocolatDeMarque “choco” que l’on souhaite acheter.
	 * @param choco
	 * @return
	 */

	public double maxQuantite(ChocolatDeMarque choco) {
		//J'achete au maximum 15% de plus que ce que j'ai vendu moins ce qu'il me reste en stock
		double max=(this.quantiteChocoVendue.get(choco)-this.stock.get(choco).getValeur())*1.15;
		if(max<0) {
			return 0;
		}
		List<String> listTypeChoco=new LinkedList<String>();
		List<Double> listPrixChoco=new LinkedList<Double>();
		
		for (ChocolatDeMarque choco2: this.getCatalogue()) {
			if (choco2.getCategorie()==(choco.getCategorie())) {
				if (choco2.getGamme()==(choco.getGamme())) {
					listTypeChoco.add(choco2.toString());
					listPrixChoco.add(prix.get(choco2).getValeur());
					int minIndex=listPrixChoco.indexOf(Collections.min(listPrixChoco));
					if (choco==choco2){
						max=max*1.5;
					}else {
						max=max*0.7;
					}
				}
	
			}		
		return max;
		}
	}




	//Louis

	/**
	 * Renvoie la liste des chocolats qui sont vendus par les transformateurs durant le step en cours.
	 * @return
	 */

	public List<ChocolatDeMarque> chocolatVendu() {
		ArrayList<ChocolatDeMarque> chocoVendu = new ArrayList<ChocolatDeMarque>();
		for (ChocolatDeMarque choco : this.getCatalogue()) {
			for (IVendeurContratCadre transfo : getTransformateurs()) {
				if (transfo.vend(choco) && !chocoVendu.contains(choco)) {
					chocoVendu.add(choco);
				}
			}
		}
		return chocoVendu;
	}



	//Louis

	/**
	 * Renvoie la liste des transformateurs de la filière
	 * @return
	 */

	public List<IVendeurContratCadre> getTransformateurs(){
		LinkedList<IVendeurContratCadre> transf = new LinkedList<IVendeurContratCadre>();
		for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
			if (acteur!= this && acteur instanceof IVendeurContratCadre) {
				transf.add((IVendeurContratCadre)acteur);
			}
		}
		return transf;
	}



	//Louis

	/**
	 * Permet de créer une liste contenant les produits que nous allons mettre en tête de gondole. 
	 * Pour cela, nous prenons les chocolats que nous vendons le moins et nous vérifions que la quantité que nous achèterons si le contrat est accepté est inférieur à 10% de la quantité totale que nous aurions dans ce cas. 
	 * Remarque: la vente en tête de gondole est désactivée pour cette version, car la proportion mise en rayon excède tout de même parfois les 10% de la quantité totale vendue.
	 */

	public void choixTG() {
		if (pasTG.size()==0) {
			return;
		}
		ChocolatDeMarque plusVendu = pasTG.get(0);

		for (ChocolatDeMarque choco : chocolatVendu()) {
			if(produitTG.size()==0 || (produitTG.size()!=0 && !produitTG.contains(choco))) {
				if (this.quantiteChocoVendue.get(choco)>this.quantiteChocoVendue.get(plusVendu)) {
					plusVendu=choco;
				}
			}
		}

		if(verifTG(plusVendu)) {
			produitTG.add(plusVendu);
			pasTG.remove(pasTG.indexOf(plusVendu));
			choixTG();
		}

	}

	public boolean verifTG(ChocolatDeMarque choco) {

		double maxQuantiteProduitTG = 0;
		if (produitTG.size()!=0) {
			for (ChocolatDeMarque futurTG : produitTG) {
				maxQuantiteProduitTG += maxQuantite(futurTG);
			}
		}

		if(maxQuantite(choco) + maxQuantiteProduitTG + quantiteEnVenteTG() < 0.1*quantiteEnVente()) {
			return true;
		}
		return false;
	}


}
