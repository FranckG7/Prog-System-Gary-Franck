public class Inode {

    private MemoryManager memoryManager;
    private int inodeNumber;

    public static final int INODE_SIZE = 128;
    public static final int DIRECT_POINTERS = 10;
	public static final int MAX_INODES = 32;

    public Inode(
            MemoryManager memoryManager,
            int inodeNumber) {

        this.memoryManager = memoryManager;
        this.inodeNumber = inodeNumber;
    }

    public int getInodeOffset() {
        return MemoryManager.INODE_TABLE_OFFSET + (inodeNumber * INODE_SIZE);
    }

    public int getFileType() {
		byte[] memory = memoryManager.getFilesystemMemory();
        return Utils.readInt(memory, getInodeOffset() + 4);
    }

    public int getFileSize() {
		byte[] memory = memoryManager.getFilesystemMemory();
        return Utils.readInt(memory, getInodeOffset() + 8);
    }

    public int[] getDirectPointers() {

        byte[] memory =
                memoryManager.getFilesystemMemory();

        int[] pointers =
                new int[DIRECT_POINTERS];

		for(int i = 0; i < 10; i++) {
			pointers[i] = Utils.readInt(memory, getInodeOffset() + 28 + i * 4);
		}
        // TODO:
        // Lire les 10 pointeurs directs.

        return pointers;
    }
	public void writeToMemory(
        int fileType,
        int fileSize,
        long creationTime,
        long modificationTime,
        int[] directPointers,
        int indirectPointer,
        short permissions,
        int linkCount) {
			
		byte[] memory =
				memoryManager.getFilesystemMemory();

		int offset = getInodeOffset();

		// 1
		offset += Utils.writeInt(memory, offset, inodeNumber);
		// 2
		offset += Utils.writeInt(memory, offset, fileType);
		// 3
		offset += Utils.writeInt(memory, offset, fileSize);
		// 4
		offset += Utils.writeLong(memory, offset, creationTime);
		// 5
		offset += Utils.writeLong(memory, offset, modificationTime);
		// 6
		for(int i = 0; i < DIRECT_POINTERS; i++) {
			int pointers; 
			if (directPointers != null && i < directPointers.length) {
				pointers = directPointers[i];
			} else {
				pointers = 0;
			}
			offset += Utils.writeInt(memory, offset, pointers);			
		}	
		// 7
		offset += Utils.writeInt(memory, offset, indirectPointer);
		// 8
		offset += Utils.writeShort(memory, offset, permissions);
		// 9
		offset += Utils.writeInt(memory, offset, linkCount);
}
}