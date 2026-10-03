import java.util.*;

public class VirtualFileSystem {

    private MemoryManager memoryManager;

    public VirtualFileSystem() {
        this.memoryManager = new MemoryManager();
    }

    private int allocateInode() {

        byte[] memory =  memoryManager.getFilesystemMemory();
		
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

        int numInode = allocateInode();

        if (numInode == -1) {
            return false;
        }
	
		Inode inode = new Inode(memoryManager, numInode);
		
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

		int maxBlocks = Inode.DIRECT_POINTERS + (memoryManager.BLOCK_SIZE / 4);
		
		if (blocksNeeded > maxBlocks) {
			return false;
		}

		int[] directPointers =
				new int[Inode.DIRECT_POINTERS];
		int indirectPointer = -1;
		
		int directCount = Math.min(blocksNeeded, Inode.DIRECT_POINTERS);
		for(int i = 0; i < directCount; i++) {
			int numBlock = memoryManager.allocateBlock();
			if( numBlock == -1) {
				return false;
			}
			directPointers[i] = numBlock;
		}
		
		byte[] memory =
				memoryManager.getFilesystemMemory();
				
		if (blocksNeeded > Inode.DIRECT_POINTERS) {
            indirectPointer = memoryManager.allocateBlock();
            if (indirectPointer == -1) {
				return false;
			}

            for (int i = Inode.DIRECT_POINTERS; i < blocksNeeded; i++) {
                int numBlock = memoryManager.allocateBlock();
                if (numBlock == -1) {
					return false;
				}

                int indirectOffset = indirectPointer * MemoryManager.BLOCK_SIZE + (i - Inode.DIRECT_POINTERS) * 4;
                Utils.writeInt(memory, indirectOffset, numBlock);
            }
        }

		int bytesRemaining =
				data.length;

		int dataSrcOffset = 0;

		for(int i = 0; i < blocksNeeded; i++) {
			int numBlock;
			if (i < Inode.DIRECT_POINTERS) {
                numBlock = directPointers[i];
            } else {
                int indirectOffset = indirectPointer * MemoryManager.BLOCK_SIZE + (i - Inode.DIRECT_POINTERS) * 4;
                numBlock = Utils.readInt(memory, indirectOffset);
            }
			
			int bytesToCopy = Math.min(bytesRemaining, 
				MemoryManager.BLOCK_SIZE);
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
            directPointers,                  // directPointers vides
            indirectPointer,                 // indirectPointer
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

		int[] directPointers =
				inode.getDirectPointers();
		
		int indirectPointer =
				inode.getIndirectPointer();
				
		int bytesRemaining = fileSize;
		int destOffset = 0;
		int blocksToRead = (fileSize + MemoryManager.BLOCK_SIZE -1) / MemoryManager.BLOCK_SIZE;
		
		for(int i = 0; i < blocksToRead ; i++) {
			int numBlock;
			if ( i < Inode.DIRECT_POINTERS) {
				numBlock = directPointers[i];
			} else {
				int indirectOffset = indirectPointer * MemoryManager.BLOCK_SIZE + (i - Inode.DIRECT_POINTERS) * 4;
                numBlock = Utils.readInt(memory, indirectOffset);
			}
			
			if ( numBlock > 0) {
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
		
		int indirectPointer = inode.getIndirectPointer();
		
        if (indirectPointer > 0) {
            int fileSize = inode.getFileSize();
            int totalBlocks = (fileSize + MemoryManager.BLOCK_SIZE - 1) / MemoryManager.BLOCK_SIZE;
            int indirectBlocksCount = totalBlocks - Inode.DIRECT_POINTERS;

            byte[] memory = memoryManager.getFilesystemMemory();
            for (int i = 0; i < indirectBlocksCount; i++) {
                int indirectOffset = indirectPointer * MemoryManager.BLOCK_SIZE + i * 4;
                int numBlock = Utils.readInt(memory, indirectOffset);
                if (numBlock > 0) {
                    memoryManager.setBlockUsed(numBlock, false);
                }
            }
            memoryManager.setBlockUsed(indirectPointer, false);
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