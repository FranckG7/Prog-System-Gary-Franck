import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class ProducteurConsommateur {

    private static final int TAILLE_BUFFER = 5;
    private static final Queue<Integer> buffer = new LinkedList<>();

    private static final Semaphore placesLibres = new Semaphore(TAILLE_BUFFER);
    private static final Semaphore elementsDisponibles = new Semaphore(0);
    private static final Semaphore mutexBuffer = new Semaphore(1);

    private static int totalProduits = 0;
    private static int totalConsommes = 0;

    private static int genererProduit() {
        return (int) (Math.random() * 100) + 1;
    }

    static class Producteur implements Runnable {
        private final String nom;

        public Producteur(String nom) {
            this.nom = nom;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                try {
                    int produit = genererProduit();

                    placesLibres.acquire();
                    mutexBuffer.acquire();

                    buffer.add(produit);
                    totalProduits++;
                    System.out.println(nom + " a produit : " + produit + " | Buffer : " + buffer.size() + "/" + TAILLE_BUFFER);

                    mutexBuffer.release();
                    elementsDisponibles.release();

                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class Consommateur implements Runnable {
        private final String nom;

        public Consommateur(String nom) {
            this.nom = nom;
        }

        @Override
        public void run() {
            for (int i = 0; i < 7; i++) {
                try {
                    elementsDisponibles.acquire();
                    mutexBuffer.acquire();

                    int produit = buffer.poll();
                    totalConsommes++;
                    System.out.println(nom + " a consommé : " + produit + " | Buffer : " + buffer.size() + "/" + TAILLE_BUFFER);

                    mutexBuffer.release();
                    placesLibres.release();

                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}