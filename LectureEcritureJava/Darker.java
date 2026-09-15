public class Darker {
    public static void main(String[] args) {
        Image img = new Image(200, 100);

        // Génération du dégradé de bleu
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int bleu = (x * 255) / (img.getWidth() - 1); // quel calcule ?
                img.setPixel(x, y, 0, 0, bleu);
            }
        }
		
		img.assombrir();

        try {
			img.save_txt("Darker_txt.ppm");
			System.out.println("Dégradé créé avec succès au format texte.");
            img.save_binaire("Darker_binaire.ppm");
            System.out.println("Dégradé créé avec succès au format binaire.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du dégradé : " + e.getMessage());
        }
    }
}