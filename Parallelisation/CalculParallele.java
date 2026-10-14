public class CalculParallele {
	
	
	private static final int[] DONNEES = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    private static final int MULTIPLICATEUR = 1000;
	
	 public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Calcul Parallele ===");
		
		Calcule[] threads = new Calcule[DONNEES.length]; 

        long debut = System.nanoTime();
        long sommeTotal = 0;


		for (int i = 0; i < DONNEES.length; i++) {
			threads[i] = new Calcule(DONNEES[i], MULTIPLICATEUR, i);
			
        }		
		
		for (int j = 0; j < DONNEES.length; j++) {
			threads[j].start();
			
        }		
		
		for (int k = 0; k < DONNEES.length; k++) {
			threads[k].join();
			
        }
		
		for (int l = 0; l < DONNEES.length; l++) {
			sommeTotal += threads[l].getResultat();
			
        }
		
		

        long duree = (System.nanoTime() - debut) / 1_000_000;
        System.out.println("Résultat total : " + sommeTotal);
        System.out.println("Durée : " + duree + " ms");
    }
}