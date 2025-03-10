package abstraction.eq5Transformateur3;

//Manuelo et Rémi

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import abstraction.eq8Romu.contratsCadres.Echeancier;
import abstraction.eq8Romu.contratsCadres.ExemplaireContratCadre;
import abstraction.eq8Romu.contratsCadres.IAcheteurContratCadre;
import abstraction.eq8Romu.contratsCadres.IVendeurContratCadre;
import abstraction.eq8Romu.contratsCadres.SuperviseurVentesContratCadre;
import abstraction.eq8Romu.produits.Chocolat;

import abstraction.eq8Romu.produits.Feve;

import abstraction.fourni.Filiere;
import abstraction.fourni.IActeur;
import abstraction.fourni.Journal;
import abstraction.fourni.Variable;

public abstract class Transformateur3Acteur implements IActeur {
	
	protected int cryptogramme;
	private String nom;
	private String description;

	protected Journal JournalRetraitStock, JournalAjoutStock, JournalAchatContratCadre, JournalVenteContratCadre, JournalOA;
	protected Variable prix_max_fèves_HBE, prix_max_fèves_moyenne, stock_min_feves_HBE, stock_min_feves_moyenne, stock_min_confiserie, stock_min_tablettes_HBE, stock_min_tablettes_moyenne, coefficient_transformation, pourcentage_confiserie, pourcentage_tablette_moyenne, prix_min_vente_MG, prix_min_vente_EQ, prix_min_vente_confiserie, prix_tablette, prix_tablette_equi, prix_confiserie, stock_avant_transfo_HB, stock_avant_transfo_C, stock_avant_transfo_M;


	public Transformateur3Acteur() {
		this.nom = "EQ5";
		this.description = "Côte d'IMT, chocolatier de qualité";
		
		this.JournalAjoutStock = new Journal(this.getNom()+" ajout dans le stock", this);
		this.JournalRetraitStock = new Journal(this.getNom()+" retrait dans le stock", this);
		this.JournalAchatContratCadre = new Journal(this.getNom()+" achat d'un contrat cadre", this);
		this.JournalVenteContratCadre = new Journal(this.getNom()+" vente d'un contrat cadre", this);
		this.JournalOA = new Journal(this.getNom()+ "Offre d'achat", this);

		this.prix_max_fèves_HBE = new Variable("Prix max d'achat de fèves HBE", this, 1000);
		this.prix_max_fèves_moyenne = new Variable("Prix max d'achat de fèves de gamme moyenne", this, 800);
		
		this.stock_min_feves_HBE = new Variable("Stock minimal de fèves haute bio équitable", this, 1000000);
		this.stock_min_feves_moyenne = new Variable("Stock minimal de fèves de moyenne gamme", this, 1000000);
		this.stock_min_confiserie = new Variable("Stock minimal de confiseries", this, 1000000);
		this.stock_min_tablettes_HBE = new Variable("Stock minimal de tablettes haute bio équitable", this, 1000000);
		this.stock_min_tablettes_moyenne = new Variable("Stock minimal de tablettes moyenne", this, 1000000);
		
		this.prix_min_vente_MG = new Variable("Prix min vente de chocolat moyenne gamme", this, 1.5);
	    this.prix_min_vente_EQ = new Variable("Prix min vente de chocolat equitable", this, 2.3);
	    this.prix_min_vente_confiserie = new Variable("Prix min de vente confiserie", this, 1.8);
	    
		this.coefficient_transformation =  new Variable("Coefficient de transformation de fèves en chocolat (40g de fèves pour 100g de chocolat)", this, 2.5);
		this.pourcentage_confiserie = new Variable("Pourcentage de fèves de gamme moyenne transformées en confiseries", this, 0.2);
		
		this.prix_tablette = new Variable("Prix tablette moyenne", this, 6);
		this.prix_tablette_equi = new Variable("Prix tablette équitable", this, 8);
		this.prix_confiserie = new Variable("Prix confiserie", this, 7);
		
		this.stock_avant_transfo_C= new Variable("stock confiserie avant ajout de la transformation ", this, 10000000);
		this.stock_avant_transfo_HB = new Variable ("stock tablette haute bio et équitable avant ajout de la transformation", this, 10000000);
		this.stock_avant_transfo_M = new Variable ("stock de tablette moyenne avant ajout de la transformation", this, 10000000);
		
		
	}

	public String getNom() {
		return nom;
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	
	public Color getColor() {
		return new Color(233, 30, 99);
	}


	public void initialiser() {
		
	}
	
	public void actualiserJournaux() {
		this.JournalAjoutStock.ajouter("=== Etape "+Filiere.LA_FILIERE.getEtape()+" ======================");
		this.JournalRetraitStock.ajouter("=== Etape "+Filiere.LA_FILIERE.getEtape()+" ======================");
		this.JournalAchatContratCadre.ajouter("=== Etape "+Filiere.LA_FILIERE.getEtape()+" ======================");
		this.JournalVenteContratCadre.ajouter("=== Etape "+Filiere.LA_FILIERE.getEtape()+" ======================");
		this.JournalOA.ajouter("=== Etape "+Filiere.LA_FILIERE.getEtape()+" ======================");
	}


	public void next() {
		this.actualiserJournaux();
		
	    stock_avant_transfo_C.setValeur(this, this.getChocolats().get(Chocolat.CONFISERIE_MOYENNE).getValeur());
	    stock_avant_transfo_HB.setValeur(this, this.getChocolats().get(Chocolat.TABLETTE_HAUTE_BIO_EQUITABLE).getValeur());
	    stock_avant_transfo_M.setValeur(this, this.getChocolats().get(Chocolat.TABLETTE_MOYENNE).getValeur());
	    
		Variable feve = this.getFeves().get(Feve.FEVE_HAUTE_BIO_EQUITABLE);
		if(feve.getValeur()- 500>0) { //garder au minimum 500kg*/
			double transfo = feve.getValeur()-500;
			Variable choco = this.getChocolats().get(Chocolat.TABLETTE_HAUTE_BIO_EQUITABLE);
			this.retirer(Feve.FEVE_HAUTE_BIO_EQUITABLE, transfo ); //retirer le surplus de fèves 
			this.ajouter(Chocolat.TABLETTE_HAUTE_BIO_EQUITABLE, (transfo)*coefficient_transformation.getValeur()); //pour le transformer en tablette haute qualité (multiplié par le coef de transformation)
			
			//Coût de transformation
			Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme, Filiere.LA_FILIERE.getBanque(), 500*1.15*((transfo)*coefficient_transformation.getValeur()/1000)); 
			//Coûts de stockage
			Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme, Filiere.LA_FILIERE.getBanque(), 0.006*(feve.getValeur()));
			Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme, Filiere.LA_FILIERE.getBanque(), 0.006*(choco.getValeur()));
			
		}
		
		feve = this.getFeves().get(Feve.FEVE_MOYENNE);
		if(feve.getValeur()-500>0) { //garder au minimum 500kg*/
			double transfo = feve.getValeur()-500; 
			Variable tablette = this.getChocolats().get(Chocolat.TABLETTE_MOYENNE);
			Variable confiserie = this.getChocolats().get(Chocolat.CONFISERIE_MOYENNE);
			this.retirer(Feve.FEVE_MOYENNE, transfo); //retirer le surplus de fèves 
			this.ajouter(Chocolat.TABLETTE_MOYENNE, (transfo)*coefficient_transformation.getValeur()*(1-pourcentage_confiserie.getValeur())); //pour le transformer en tablette haute qualité (multiplié par le coef de transformation)
			this.ajouter(Chocolat.CONFISERIE_MOYENNE, (transfo)*coefficient_transformation.getValeur()*pourcentage_confiserie.getValeur()); 
			
			//Coût de transformation
			Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme, Filiere.LA_FILIERE.getBanque(), 500*((transfo)*coefficient_transformation.getValeur()*(1-pourcentage_confiserie.getValeur())+(transfo)*coefficient_transformation.getValeur()*pourcentage_confiserie.getValeur())/1000);
			//Coûts de stockage
			Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme, Filiere.LA_FILIERE.getBanque(), 0.006*(feve.getValeur()));
			Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme, Filiere.LA_FILIERE.getBanque(), 0.006*(tablette.getValeur()));
			Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme, Filiere.LA_FILIERE.getBanque(), 0.006*(confiserie.getValeur()));
		}
		
		//Coût de l'entrepôt
		Filiere.LA_FILIERE.getBanque().virer(this, this.cryptogramme, Filiere.LA_FILIERE.getBanque(), 1000);
		
		SuperviseurVentesContratCadre SupCCadre1 = (SuperviseurVentesContratCadre)(Filiere.LA_FILIERE.getActeur("Sup.CCadre"));
		feve = this.getFeves().get(Feve.FEVE_MOYENNE);
		if(feve.getValeur()<this.stock_min_feves_moyenne.getValeur()){
			IVendeurContratCadre vendeur = null;
			List<IVendeurContratCadre> vendeurs = SupCCadre1.getVendeurs(Feve.FEVE_MOYENNE);
			if(vendeurs.size()>0) {
				if (Filiere.LA_FILIERE.getEtape()<=12 || this.getListePrixNegocies(this.getContratsAchat()).size()==0) {
					vendeur=vendeurs.get((int)( Math.random()*vendeurs.size())); //prend un vendeur aléatoirement
					ExemplaireContratCadre contratCadre = SupCCadre1.demande((IAcheteurContratCadre)this, vendeur, Feve.FEVE_MOYENNE, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, ((this.stock_min_feves_moyenne.getValeur())-feve.getValeur()+1000000)/10), cryptogramme, false); 
					if (contratCadre!=null){
						this.JournalAchatContratCadre.ajouter("nouveau contrat cadre entre " + this + " et "+vendeur+" d'une quantité " + contratCadre.getQuantiteTotale() + "kg de " + contratCadre.getProduit() + " pendant " + contratCadre.getEcheancier() + " pour " + contratCadre.getPrix() +" euros le kilo");
					}
				} else {
					ArrayList<Double> listePrixNegocies = this.getListePrixNegocies(this.getContratsAchat());
					Double meilleurPrix = this.getMinListe(listePrixNegocies);
					vendeur = this.getVendeurAvecCePrix(meilleurPrix); //Vendeur avec le meilleur prix dans les précédents contrats
					int indexVendeur = vendeurs.indexOf(vendeur);
					IVendeurContratCadre autreVendeur = vendeurs.get(1-indexVendeur);
					ArrayList<IVendeurContratCadre> dixDerniersVendeurs = this.getVendeurs(10);
					if (!dixDerniersVendeurs.contains(autreVendeur)) {
						ExemplaireContratCadre contratCadre = SupCCadre1.demande((IAcheteurContratCadre)this, autreVendeur, Feve.FEVE_MOYENNE, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, ((this.stock_min_feves_moyenne.getValeur())-feve.getValeur()+1000000)/10), cryptogramme, false); 
						if (contratCadre!=null){
							this.JournalAchatContratCadre.ajouter("nouveau contrat cadre entre " + this + " et "+autreVendeur+" d'une quantité " + contratCadre.getQuantiteTotale() + "kg de " + contratCadre.getProduit() + " pendant " + contratCadre.getEcheancier() + " pour " + contratCadre.getPrix() +" euros le kilo");
						}
					} else {
						ExemplaireContratCadre contratCadre = SupCCadre1.demande((IAcheteurContratCadre)this, vendeur, Feve.FEVE_MOYENNE, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, ((this.stock_min_feves_moyenne.getValeur())-feve.getValeur()+1000000)/10), cryptogramme, false); 
						if (contratCadre!=null){
							this.JournalAchatContratCadre.ajouter("nouveau contrat cadre entre " + this + " et "+vendeur+" d'une quantité " + contratCadre.getQuantiteTotale() + "kg de " + contratCadre.getProduit() + " pendant " + contratCadre.getEcheancier() + " pour " + contratCadre.getPrix() +" euros le kilo");
						}
					}
				}
			}
		}

		feve=this.getFeves().get(Feve.FEVE_HAUTE_BIO_EQUITABLE);
		if(feve.getValeur()<this.stock_min_feves_HBE.getValeur()) {
			IVendeurContratCadre vendeur = null;
			List<IVendeurContratCadre> vendeurs = SupCCadre1.getVendeurs(Feve.FEVE_HAUTE_BIO_EQUITABLE);
			if(vendeurs.size()>0) {
				vendeur=vendeurs.get((int)( Math.random()*vendeurs.size())); //prend un vendeur aléatoirement
				ExemplaireContratCadre contratCadre = SupCCadre1.demande((IAcheteurContratCadre)this, vendeur, Feve.FEVE_HAUTE_BIO_EQUITABLE, new Echeancier(Filiere.LA_FILIERE.getEtape()+1, 10, (this.stock_min_feves_HBE.getValeur()-feve.getValeur()+1000000)/10), cryptogramme, false);
				if (contratCadre!=null) {
					this.JournalAchatContratCadre.ajouter(contratCadre.toString());
				}
			}
		}

	} 

	// Renvoie la liste des filières proposées par l'acteur
	public List<String> getNomsFilieresProposees() {
		ArrayList<String> filieres = new ArrayList<String>();
		return(filieres);
	}

	// Renvoie une instance d'une filière d'après son nom
	public Filiere getFiliere(String nom) {
		return Filiere.LA_FILIERE;
	}

	// Renvoie les indicateurs
	public List<Variable> getIndicateurs() {
		List<Variable> res = new ArrayList<Variable>();
		return res;
	}

	// Renvoie les paramètres
	public List<Variable> getParametres() {
		List<Variable> res = new ArrayList<Variable>();
		res.add(this.coefficient_transformation);
		res.add(this.pourcentage_confiserie);
		res.add(this.prix_max_fèves_HBE);
		res.add(this.prix_max_fèves_moyenne);
		res.add(this.stock_min_feves_HBE);
		res.add(this.stock_min_feves_moyenne);
		res.add(this.stock_min_confiserie);
		res.add(this.stock_min_tablettes_HBE);
		res.add(this.stock_min_tablettes_moyenne);
		res.add(this.prix_min_vente_MG);
		res.add(this.prix_min_vente_EQ);

		return res;
	}

	// Renvoie les journaux
	public List<Journal> getJournaux() {
		List<Journal> res=new ArrayList<Journal>();
		res.add(this.JournalAjoutStock);
		res.add(this.JournalRetraitStock);
		res.add(this.JournalAchatContratCadre);
		res.add(this.JournalVenteContratCadre);
		res.add(this.JournalOA);
		return res;
	}

	public void setCryptogramme(Integer crypto) {
		this.cryptogramme = crypto;
		
	}

	public void notificationFaillite(IActeur acteur) {
	}

	public void notificationOperationBancaire(double montant) {
	}
	// Renvoie le solde actuel de l'acteur
	public double getSolde() {
		return Filiere.LA_FILIERE.getBanque().getSolde(Filiere.LA_FILIERE.getActeur(getNom()), this.cryptogramme);
	}
	
	public abstract void retirer(Feve feve, double delta);
	public abstract void ajouter(Chocolat chocolat, double delta);
	public abstract HashMap<Feve, Variable> getFeves();

	public abstract HashMap<ExemplaireContratCadre, Integer> getContratsAchat();
	public abstract ArrayList<Double> getListePrixNegocies(HashMap<ExemplaireContratCadre, Integer> contratsAchat);
	public abstract Double getMinListe(ArrayList<Double> listePrix);
	public abstract IVendeurContratCadre getVendeurAvecCePrix (Double prix);
	public abstract ArrayList<IVendeurContratCadre> getVendeurs(int i);

	public abstract HashMap<Chocolat, Variable> getChocolats();


}

