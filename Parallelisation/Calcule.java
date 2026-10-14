public class Calcule extends Thread {

	private int valeur;
	
	private int multiplicateur;
	
	private int index;
	
	private long resultat = 0;

	public Calcule(int valeur, int multiplicateur, int index) {
		this.valeur = valeur;
		this.multiplicateur = multiplicateur;
		this.index = index;		
	}
	
	@Override 
	public void run() {
		for(int i = 0; i < multiplicateur; i++) {
			resultat += valeur * valeur + valeur;	
		}
	}
	
	public long getResultat() {
		return resultat;
	}
}