public class TestMutex {
	
		private static final int NB_THREADS = 9;
		private static final int INCREMENTS = 1111111;
		private static final int ATTENDU = NB_THREADS * INCREMENTS;
		private static final int NB_TESTS = 50;
	
	public static void main(String[] args) throws InterruptedException {
		
		
		
		int nbSucces = 0;
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
		
		for(int i = 0; i < 50; i++) {
			CompteurSecurise.resetCompteur();
			Thread[] threads = new Thread[NB_THREADS];
			
			for (int j = 0; j < NB_THREADS; j++) {
				threads[j] = new Thread(new CompteurSecurise.Incrementeur("Thread-" + j, INCREMENTS));
			}
			for (int j = 0; j < NB_THREADS; j++) {
				threads[j].start();
			}
			for (int j = 0; j < NB_THREADS; j++) {
				threads[j].join();
			}
			
			int resultat = CompteurSecurise.getCompteur();
            System.out.println("Test " + (i + 1) + " - Compteur obtenu : " + resultat + " / " + ATTENDU + " attendu");

            if (resultat == ATTENDU) {
                nbSucces++;
            }
            if (resultat < min) {
				min = resultat;
			}
			
			if (resultat > max) {
				max = resultat;
			}
			
        }

        System.out.println("Réussites : " + nbSucces + " / " + NB_TESTS + " (" + ((double) nbSucces / NB_TESTS * 100) + " %)");
        System.out.println("Valeur min : " + min);
        System.out.println("Valeur max : " + max);
	}

}