import java.util.*;

public class VirtualFileSystem {

    private MemoryManager memoryManager;

    public VirtualFileSystem() {
        this.memoryManager =
                new MemoryManager();
    }

    private int allocateInode() {

        byte[] memory =
                memoryManager.getFilesystemMemory();
		
		for(int i=0; i < Inode.MAX_INODES; i++) {
			Inode inode = new Inode(memoryManager, i);
			
			if(inode.getFileType() == 0) {
				return i;
			}
		}

        return -1;
    }

    public boolean createFile(
            String directory,
            String filename) {

        int inodeNum = allocateInode();

        if (inodeNum == -1) {
            return false;
        }
	
		Inode inode = new Inode(memoryManager, inodeNum);
		
		long now = System.currentTimeMillis();
		
		inode.writeToMemory(
            1,                               // fileType 
            0,                               // fileSize 
            now,                             // creationTime
            now,                             // modificationTime
            new int[Inode.DIRECT_POINTERS],  // directPointers vides
            -1,                              // indirectPointer
            (short) 0,                       // permissions
            1                                // linkCount
        );

        return true;
    }

    public MemoryManager getMemoryManager() {
        return memoryManager;
    }
}