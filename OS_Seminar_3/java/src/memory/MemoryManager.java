package src.memory;
import java.util.*; 
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;

public class MemoryManager {

	private int myNumberOfPages;
	private int myPageSize; // In bytes
	private int myNumberOfFrames;
	private int[] myPageTable; // -1 if page is not in physical memory
	private byte[] myRAM; // physical memory RAM
	private RandomAccessFile myPageFile;
	private int myNextFreeFramePosition = 0;
	private int myNumberOfpageFaults = 0;
	private int myPageReplacementAlgorithm = 0;
	private Deque<Integer> linkedListDeque = new LinkedList<>();   
	private HashSet<Integer> myPageTableSet;

	public MemoryManager(int numberOfPages, int pageSize, int numberOfFrames, String pageFile,
			int pageReplacementAlgorithm) {

		myNumberOfPages = numberOfPages;
		myPageSize = pageSize;
		myNumberOfFrames = numberOfFrames;
		myPageReplacementAlgorithm = pageReplacementAlgorithm;
		//initPageTable();
		myRAM = new byte[myNumberOfFrames * myPageSize];
		myPageTableSet = new HashSet<Integer>(numberOfFrames);

		try {

			myPageFile = new RandomAccessFile(pageFile, "r");

		} catch (FileNotFoundException ex) {
			System.out.println("Can't open page file: " + ex.getMessage());
		}
	}

	/* private void initPageTable() {
		myPageTable = new int[myNumberOfPages];
		for (int n = 0; n < myNumberOfPages; n++) {
			myPageTable[n] = -1;
		}
	} */

	public byte readFromMemory(int logicalAddress) {
		int pageNumber = getPageNumber(logicalAddress);
		int offset = getPageOffset(logicalAddress);
		
		// If page is not loaded in main memory,
		// find in approximately O(1) lookup time
		if (!myPageTableSet.contains(pageNumber)) {
			pageFault(pageNumber);
		}
		// If this page exists in the table set
		else if (myPageReplacementAlgorithm == Seminar3.LRU_PAGE_REPLACEMENT)
		{
			// Move page to head of linked list
			linkedListDeque.remove(pageNumber);
			linkedListDeque.addFirst(pageNumber);
		}
		int frame = new ArrayList<Integer>(myPageTableSet).indexOf(pageNumber);
		int physicalAddress = frame * myPageSize + offset;
		byte data = myRAM[physicalAddress];
		System.out.print("Virtual address: " + logicalAddress);
		System.out.print(" Physical address: " + physicalAddress);
		System.out.println(" Value: " + data); 
		return data;
	}

	private void handlePageFault(int pageNumber) {
		// myNextFreeFramePosition is never greater than
		// the length of myPageTable, therefore no error handling.
		myPageTableSet.add(pageNumber);
		linkedListDeque.add(pageNumber);
		myNextFreeFramePosition++;
		myNumberOfpageFaults++;
	}

	private void handlePageFaultFIFO(int pageNumber) {
		// Check if page deque size is at MAX capacity,
		// if true then remove item from page table set.
		if(linkedListDeque.size() == myNumberOfFrames) {
			myPageTableSet.remove(linkedListDeque.removeLast());
		}
		// Add page to head of queue
		linkedListDeque.addFirst(pageNumber);
		myPageTableSet.add(pageNumber);
		myNumberOfpageFaults++;
	}

	private void handlePageFaultLRU(int pageNumber) {
		handlePageFaultFIFO(pageNumber);
	}
	
	private int getPageNumber(int logicalAddress) {
		return Math.floorDiv(logicalAddress, 256);
	}

	private int getPageOffset(int logicalAddress) {
		return logicalAddress % 256;
	}

	private void pageFault(int pageNumber) {
		if (myPageReplacementAlgorithm == Seminar3.NO_PAGE_REPLACEMENT)
			handlePageFault(pageNumber);

		if (myPageReplacementAlgorithm == Seminar3.FIFO_PAGE_REPLACEMENT)
			handlePageFaultFIFO(pageNumber);

		if (myPageReplacementAlgorithm == Seminar3.LRU_PAGE_REPLACEMENT)
			handlePageFaultLRU(pageNumber);

		readFromPageFileToMemory(pageNumber);
	}

	private void readFromPageFileToMemory(int pageNumber) {
		try {
			int frame = new ArrayList<Integer>(myPageTableSet).indexOf(pageNumber);
			System.out.println(frame);
			myPageFile.seek(pageNumber * myPageSize);
			for (int b = 0; b < myPageSize; b++)
				myRAM[frame * myPageSize + b] = myPageFile.readByte();
		} catch (IOException ex) {

		}
	}

	public int getNumberOfPageFaults() {
		return myNumberOfpageFaults;
	}

	

	
}


class Cache {
	Map<Integer, Integer> cache;        
	int size;         
	public Cache(int size){            
	  cache = new LinkedHashMap<>();            
	   this.size = size;        
	}
 }