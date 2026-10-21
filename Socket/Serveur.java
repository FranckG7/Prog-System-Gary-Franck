import java.net.*;
import java.io.*;

public class Serveur {
    public static void main(String[] args) throws Exception {
		
        ServerSocket serverSocket = new ServerSocket(5000);
		System.out.println("Serveur démarré sur le port 5000");

        while(true) {
			
            Socket client = serverSocket.accept();
			System.out.println("Client connecté : " + client.getInetAddress());
            BufferedReader in = new BufferedReader(
				new InputStreamReader(client.getInputStream())
			);
			
			PrintWriter out = new PrintWriter(client.getOutputStream(), true);
			String ligne;
			
			while ((ligne = in.readLine()) != null) {
				System.out.println("Reçu : " + ligne);
				out.println("ECHO: " + ligne);
			}
			
			System.out.println("Client déconnecté");
			in.close();
			out.close();
			client.close();
        }
    }
}