package abstraction.eq7Distributeur2;


import abstraction.eq8Romu.produits.ChocolatDeMarque;

public interface IStocks {
	
	
	public double getStockChocolatDeMarque(ChocolatDeMarque chocolatDeMarque);
	
	public double getStockChocolatDeMarque(ChocolatDeMarque chocolatDeMarque, int etape);
	
	public double getQuantiteTotaleStocks();
	
	public void ajouterChocolatDeMarque(ChocolatDeMarque chocolatDeMarque ,double quantité );
	
	public void supprimerChocolatDeMarque(ChocolatDeMarque chocolatDeMarque ,double quantité );
	
	public void jeterChocolatPerime();
	
	public void getCoutStockage();
	
	
	
	
		
}
