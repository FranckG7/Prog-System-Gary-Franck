public class Utils {

    public static int writeInt(byte[] memory, int offset, int value) {
        // TODO: Écrire les 4 octets de 'value' dans 'memory'
        // à partir de 'offset', en big-endian.
		memory[offset] = (byte) (value >> 24);
		memory[offset + 1] = (byte)(value >> 16);
		memory[offset + 2] = (byte) (value >> 8);
		memory[offset + 3] = (byte) (value);
		
		return memory;
        
    }

    public static int readInt(byte[] memory, int offset) {
        // TODO: Reconstituer le int sur 4 octets.

        return 0;
    }

    public static int writeShort(byte[] memory, int offset, short value) {
        // TODO: Écrire les 2 octets de 'value'.
		memory[offset] = (byte) (value >> 8);
		memory[offset + 1] = (byte) (value);
		
        return memory;
    }

    public static short readShort(byte[] memory, int offset) {
        // TODO: Lire le short sur 2 octets.
        return 0;
    }
}