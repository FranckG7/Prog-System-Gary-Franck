import java.io.FileWriter;
import java.io.FileReader;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.BufferedOutputStream;
import java.io.IOException;

public class Image {
    private int width;
    private int height;
    // pixels[y][x][0=R,1=G,2=B]
    private int[][][] pixels; // pixels[y][x][0=R,1=G,2=B]

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    /**
     * Constructeur : initialise une image vide.
     */
    public Image(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int[height][width][3];
    }

    /**
     * Définit la couleur d'un pixel à la position (x, y)
     */
    public void setPixel(int x, int y, int r, int g, int b) {
        if (x >= 0 && x < width && y >= 0 && y < height) {
            pixels[y][x][0] = r;
            pixels[y][x][1] = g;
            pixels[y][x][2] = b;
        }
    }
	
	/**
	 * Définit la couleur d'un pixel deux fois plus combre à la position (x,y)
	 */
	public void assombrir() {
		for(int y=0; y < height; y++) {
			for(int x=0; x < width; x++) {
				pixels[y][x][0] =  pixels[y][x][0] / 2;
				pixels[y][x][1] =  pixels[y][x][1] / 2;
				pixels[y][x][2] =  pixels[y][x][2] / 2;
			}
		}
	}
	

    /**
     * Sauvegarde l'image au format texte PPM (P3)
     */
    public void save_txt(String filename) throws IOException {
		
            BufferedWriter writer = new BufferedWriter (new FileWriter(filename));

            writer.write("P3\n");
            // Écriture des dimensions
			writer.write(width + " " + height + "\n");
            // Écriture de la valeur maximal
			writer.write("255\n");
			
			for(int y= 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					writer.write(pixels[y][x][0] + " " + pixels[y][x][1] + " " + pixels[y][x][2] + " " );
				}
				writer.write("\n");
			}
			
            writer.close(); // Fermeture du fichier
			System.out.print("Image PPM crée avec succès\n");
    }
	
	/**
	 * Sauvegarde l'image au format binaire PPM(P6) 
	 */
	public void save_binaire(String filename) throws IOException {
		
		BufferedOutputStream Output = new BufferedOutputStream (new FileOutputStream(filename));
		String header = "P6\n" + width + " " + height + " \n255\n" ;
		Output.write(header.getBytes());
		for(int y= 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				Output.write(pixels[y][x][0]);
				Output.write(pixels[y][x][1]);
				Output.write(pixels[y][x][2]);
			}
				
		}
		Output.close();
		System.out.print("Image PPM crée avec succès\n");
	}
	
}
