package abstraction.eq6Distributeur1;

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



	public Acheteur() {
		super();

		this.journalAchats = new Journal("Journal achats", this);
	}

	public double maxQuantite(ChocolatDeMarque choco) {
		//J'achete 15% de plus que ce que j'ai vendu moins ce qu'il me reste en stock
		return (this.quantiteChocoVendue.get(choco)-this.stock.get(choco).getValeur())*1.15;
	}

	//Elsa, Louis
	@Override
	public Echeancier contrePropositionDeLAcheteur(ExemplaireContratCadre contrat) {
		this.superviseur=(SuperviseurVentesContratCadre)Filiere.LA_FILIERE.getActeur("Sup.CCadre");
		j=0;
		Echeancier e = contrat.getEcheancier();
		int step=Filiere.LA_FILIERE.getEtape();

		double maxQuantite= maxQuantite((ChocolatDeMarque)contrat.getProduit()); 
		if (e.getQuantite(e.getStepFin())>maxQuantite) {
			if(maxQuantite*(0.90+i/100) > superviseur.QUANTITE_MIN_ECHEANCIER) {
				e.set(step, maxQuantite*(0.90+i/100));
			}

			else {
			//	e.set(step, superviseur.QUANTITE_MIN_ECHEANCIER+1);
			}
		}
		else {

			e.set(step, e.getQuantite(e.getStepFin()));
		}

		i++;
		return e;

	}

	/*	//Elsa
	public void choixTG() {
		produitTG= new LinkedList<ChocolatDeMarque>();
		double sommeQuantite=0;
		double sommeTG=0;
		Map<ChocolatDeMarque,Double> maxQuantites= new HashMap<ChocolatDeMarque,Double>();
		for (ChocolatDeMarque produit : this.getCatalogue()) {
			maxQuantites.put(produit, maxQuantite(produit));
			sommeQuantite+=maxQuantite;
		}
		while (sommeTG<0.08*sommeQuantite) {
			int rnd = new Random().nextInt(this.getCatalogue().size());
			sommeTG+=maxQuantites.get(this.getCatalogue().get(rnd));
			produitTG.add(this.getCatalogue().get(rnd));
		}
	}*/



	//louis
	//fonction recursive qui remplit la liste produitTG avec les ChocolatDeMarque a mettre en tete de gondole
	public void choixTG() {
		if (pasTG.size()==0) {
			return;
		}
		ChocolatDeMarque moinsVendu = pasTG.get(0);

		for (ChocolatDeMarque choco : getCatalogue()) {
			if(produitTG.size()==0 || (produitTG.size()!=0 && !produitTG.contains(choco))) {
				if (this.quantiteChocoVendue.get(choco)<this.quantiteChocoVendue.get(moinsVendu)) {
					moinsVendu=choco;
				}
			}
		}

		double maxQuantiteProduitTG = 0;
		if (produitTG.size()!=0) {
			for (ChocolatDeMarque futurTG : produitTG) {
				maxQuantiteProduitTG += maxQuantite(futurTG);
			}
		}

		if(maxQuantite(moinsVendu) + maxQuantiteProduitTG + quantiteEnVenteTG() < 0.1*quantiteEnVente()) {
			produitTG.add(moinsVendu);
			pasTG.remove(pasTG.indexOf(moinsVendu));
			choixTG();
		}

	}

	//Louis
	public void next() {
		pasTG = this.getCatalogue();
		produitTG=new LinkedList<ChocolatDeMarque>();
		this.superviseur=(SuperviseurVentesContratCadre)Filiere.LA_FILIERE.getActeur("Sup.CCadre");
		choixTG();
		//System.out.println("produitTG " +produitTG.toString());
		for (ChocolatDeMarque produit : this.getCatalogue()) {
			List<IActeur> vendeurs = new LinkedList<IActeur>();
			for (IActeur acteur : Filiere.LA_FILIERE.getActeurs()) {
				if (acteur!= this && acteur instanceof IVendeurContratCadre && ((IVendeurContratCadre)acteur).vend(produit)) {
					vendeurs.add(acteur);
					
				}
			}
			if (vendeurs.size()!=0) {
				int rnd = new Random().nextInt(vendeurs.size());
				IActeur vendeur = vendeurs.get(rnd);
				if (maxQuantite(produit) > superviseur.QUANTITE_MIN_ECHEANCIER) {
					if (produitTG.contains(produit)) {
						superviseur.demande((IAcheteurContratCadre)this, ((IVendeurContratCadre)vendeur), produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, Filiere.LA_FILIERE.getEtape()+2, maxQuantite(produit)), cryptogramme, true);
						//System.out.println("vente tg");
					}
					else {
						superviseur.demande((IAcheteurContratCadre)this, ((IVendeurContratCadre)vendeur), produit, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, Filiere.LA_FILIERE.getEtape()+2, maxQuantite(produit)), cryptogramme, false);
					}
				}
			}
		}
		super.next();
	}
	
	//Louis
	public void initialiser() {
		super.initialiser();
		journaux.add(journalAchats);
		journalAchats.ajouter("tout les contrats cadres conclus");
	}

	
	//Elsa
	
	@Override
	public double contrePropositionPrixAcheteur(ExemplaireContratCadre contrat) {
		i=0;
		double maxPrix= this.prix.get((ChocolatDeMarque)contrat.getProduit())*0.75;
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

	//L
	@Override
	public void receptionner(Object produit, double quantite, ExemplaireContratCadre contrat) {
		ajouterStock((ChocolatDeMarque)produit, quantite,contrat.getTeteGondole());
		journalAchats.ajouter("achat de "+quantite+" "+produit.toString()+" a "+contrat.getVendeur().toString()+" pour un prix de "+contrat.getPrix());
	}



}
