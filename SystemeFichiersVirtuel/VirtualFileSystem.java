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
	
	public boolean writeFile(
        int inodeNum,
        byte[] data) {

		int blocksNeeded =
				(data.length
				+ MemoryManager.BLOCK_SIZE - 1)
				/ MemoryManager.BLOCK_SIZE;

		if (blocksNeeded > Inode.DIRECT_POINTERS) {
			return false;
		}

		int[] blockPointers =
				new int[Inode.DIRECT_POINTERS];
		
		for(int i = 0; i < blocksNeeded; i++) {
			int numBlock = memoryManager.allocateBlock();
			if( numBlock == -1) {
				return false;
			}
			blockPointers[i] = numBlock;
		}

		byte[] memory =
				memoryManager.getFilesystemMemory();

		int bytesRemaining =
				data.length;

		int dataSrcOffset = 0;

		for(int i = 0; i < blocksNeeded; i++) {
			int bytesToCopy = Math.min(bytesRemaining, 
				MemoryManager.BLOCK_SIZE);
			int numBlock = blockPointers[i];
			int physicalOffset = numBlock * MemoryManager.BLOCK_SIZE;
			
			System.arraycopy(data, dataSrcOffset, memory, physicalOffset,
							 bytesToCopy);
							
			dataSrcOffset += bytesToCopy;
			bytesRemaining -= bytesToCopy;
							 
		}
		
		Inode inode = new Inode(memoryManager, inodeNum);
		long now = System.currentTimeMillis();
		
		inode.writeToMemory(
            1,                               // fileType 
            data.length,                     // fileSize 
            now,                             // creationTime
            now,                             // modificationTime
            blockPointers,                   // directPointers vides
            -1,                              // indirectPointer
            (short) 0,                       // permissions
            1                                // linkCount
        );

        return true;
	}
	
	public byte[] readFile(int inodeNum) {

		Inode inode =
				new Inode(memoryManager, inodeNum);

		int fileSize =
				inode.getFileSize();

		if (fileSize == 0) {
			return new byte[0];
		}

		byte[] fileData =
				new byte[fileSize];

		byte[] memory =
				memoryManager.getFilesystemMemory();

		int[] blockPointers =
				inode.getDirectPointers();
				
		int bytesRemaining = fileSize;
		int destOffset = 0;
		
		for(int i = 0; i < Inode.DIRECT_POINTERS && bytesRemaining > 0; i++) {
			int numBlock = blockPointers[i];
			if(numBlock > 0) {
				int bytesToRead = Math.min(bytesRemaining, 
						MemoryManager.BLOCK_SIZE);
				int physicalOffset = numBlock * MemoryManager.BLOCK_SIZE;
				
				System.arraycopy(memory, physicalOffset, fileData, 
						destOffset, bytesToRead);
				
				destOffset += bytesToRead;
				bytesRemaining -= bytesToRead;
			}
		}

		return fileData;
	}
	
	public boolean deleteFile(int inodeNum) {
		if(inodeNum < 0 || inodeNum >= Inode.MAX_INODES) {
			return false;
		}
		
		Inode inode = new Inode(memoryManager, inodeNum);
		
		if(inode.getFileType() == 0) {
			return false;
		}
		
		int[] blockPointers = inode.getDirectPointers();
		
		for(int i = 0; i < Inode.DIRECT_POINTERS; i++) {
			int numBlock = blockPointers[i];
			if (numBlock > 0) {
				memoryManager.setBlockUsed(numBlock, false);
			}
		}
		
		long now = System.currentTimeMillis();
		
		inode.writeToMemory(
			0,
			0,
			now,
			now,
			new int[Inode.DIRECT_POINTERS],
			-1,
			(short) 0,
			0
		);
		return true;
	}	
	
	public MemoryManager getMemoryManager() {
        return memoryManager;
    }
	
}