public class TestSemaphore {

    public static void main(String[] args) throws InterruptedException {

        // scenarioEquilibre();
        scenarioDesequilibre();
    }

    public static void scenarioEquilibre() throws InterruptedException {
        System.out.println("Scénario équilibré");

        Thread[] prods = new Thread[2];
        Thread[] cons = new Thread[3];

        for (int i = 0; i < 2; i++) {
            prods[i] = new Thread(new ProducteurConsommateur.Producteur("Prod-" + i));
        }
        for (int i = 0; i < 3; i++) {
            cons[i] = new Thread(new ProducteurConsommateur.Consommateur("Cons-" + i));
        }

        for (int i = 0; i < 2; i++) {
            prods[i].start();
        }
        for (int i = 0; i < 3; i++) {
            cons[i].start();
        }

        for (int i = 0; i < 2; i++) {
            prods[i].join();
        }
        for (int i = 0; i < 3; i++) {
            cons[i].join(); 
        }
    }

    public static void scenarioDesequilibre() throws InterruptedException {
        System.out.println("Scénario déséquilibré");

        Thread[] prods = new Thread[5];
        Thread[] cons = new Thread[2];

        for (int i = 0; i < 5; i++) {
            prods[i] = new Thread(new ProducteurConsommateur.Producteur("Prod-" + i));
        }
        for (int i = 0; i < 2; i++) {
            cons[i] = new Thread(new ProducteurConsommateur.Consommateur("Cons-" + i));
        }

        for (int i = 0; i < 5; i++) {
            prods[i].start();
        }
        for (int i = 0; i < 2; i++) {
            cons[i].start();
        }

        for (int i = 0; i < 5; i++) {
            prods[i].join();
        }
        for (int i = 0; i < 2; i++) {
            cons[i].join();
        }
    }
}