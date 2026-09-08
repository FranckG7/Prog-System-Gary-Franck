public class Gradient {
    public static void main(String[] args) {
        Image img = new Image(200, 100);

        // Génération du dégradé de bleu
        for (int y = 0; y < hauteur; y++) {
            for (int x = 0; x < largeur; x++) {
                int bleu = 0; // quel calcule ?
                img.setPixel(x, y, 0, 0, bleu);
            }
        }

        try {
            img.save("gradient.ppm");
            System.out.println("Dégradé créé avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du dégradé : " + e.getMessage());
        }
    }
}