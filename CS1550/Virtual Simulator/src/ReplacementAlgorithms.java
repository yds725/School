import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;

/*
 * Class for 4 replacement algorithm
 */
public class ReplacementAlgorithms {

    public final int PAGE_SIZE = (int)Math.pow(2, 12); // all page must be 4KB which is 4096
    public int frames; //number of frames as global var

    public ReplacementAlgorithms(int numberOfFrameRequested) {
        frames = numberOfFrameRequested;
    }

    /*
        NRU - Evict the page that is the oldest, preferring pages that are not dirty
     */
    public void nru(int refreshRate, String traceFile)
    {
        int accesses = 0;
        int pageFaults = 0;
        int writes = 0;
        int activeFrames = 0;

        Scanner sc = new Scanner(System.in);
        File f = new File(traceFile);

        try{
            sc = new Scanner(f);
        }
        catch(Exception e) {
            System.out.println("Cannot Open File. Exiting!");
            System.exit(0);
        }

        PTEntry[] RAM_PGTable = generateRAM();
        PTEntry[] PGTable = generatePGTable();

        while(sc.hasNextLine())
        {
            accesses++;
            if( (accesses % refreshRate) ==0)
            {
                RAM_PGTable = cleanReferencedMemory(RAM_PGTable);
            }
            String input = sc.nextLine();
            String[] split = input.split(" ");

            char accessMode = split[1].charAt(0);
            long address = Long.decode("0x"+split[0]); // change it to hexadecimal represent 32 bit address space
            int page = (int)( address / PAGE_SIZE ); // get page number

            if(page < 0 || page >= PGTable.length){
                System.out.println("Page is out of bound. Fatal Error. Exit.");
                System.exit(0);
            }
            if(PGTable[page].isValid()){
                if(accessMode == 'W') //if memory address space is write, set it as dirty
                    RAM_PGTable[PGTable[page].getFrame()].setDirty(true);
                    RAM_PGTable[PGTable[page].getFrame()].setReferenced(true);
                    System.out.println("HIT");
            }
            else{
                pageFaults++;

                if(activeFrames < frames) {

                    PGTable[page].setValid(true);
                    PGTable[page].setFrame(activeFrames);

                    if(accessMode == 'W')
                        PGTable[page].setDirty(true);

                    PGTable[page].setReferenced(true);
                    RAM_PGTable[activeFrames] = PGTable[page];
                    System.out.println("page fault. Not evicting");
                    activeFrames++;
                }
                else
                {
                    int toRemoveLocation = -1;
                    boolean remaining_ref = true;
                    boolean remaining_dirty = true;

                    for(int start_location = 0;start_location < frames; start_location++)
                    {
                        boolean current_ref = RAM_PGTable[start_location].isReferenced();
                        boolean current_dirty = RAM_PGTable[start_location].isDirty();

                        if(!current_dirty && !current_ref)
                        {
                            toRemoveLocation = start_location;
                            break;
                        }
                        else if(!current_ref)
                        {
                            if(remaining_ref)
                            {
                                toRemoveLocation = start_location;
                                remaining_ref = false;
                                remaining_dirty = true;
                            }
                        }
                        else if(!current_dirty)
                        {
                            if(remaining_ref && remaining_dirty)
                            {
                                toRemoveLocation = start_location;
                                remaining_dirty = false;
                            }
                        }
                        else
                        {
                            if(toRemoveLocation==-1)
                            {
                                toRemoveLocation = start_location;
                            }
                        }
                    }

                    if(RAM_PGTable[toRemoveLocation].isDirty()) {
                        writes++;
                        System.out.println("Page Fault. Evict dirty one");
                    }
                    else {
                        System.out.println("Page Fault - Evict clean");
                    }

                    int temp = RAM_PGTable[toRemoveLocation].getIndex();
                    PGTable[temp].setDirty(false);
                    PGTable[temp].setValid(false);
                    PGTable[temp].setFrame(-1);

                    RAM_PGTable[toRemoveLocation] = PGTable[page];

                    if(accessMode == 'W')
                        RAM_PGTable[toRemoveLocation].setDirty(true);

                    RAM_PGTable[toRemoveLocation].setValid(true);
                    RAM_PGTable[toRemoveLocation].setFrame(toRemoveLocation);
                    RAM_PGTable[toRemoveLocation].setReferenced(true);
                }
            }
        }
        printSummary("NRU",accesses,pageFaults,writes);
    }

    // FIFO - evict page that in RAM was longest (First In First Out)
    public void fifo(String file)
    {
        int memoryAccess = 0;
        int pageFault = 0;
        int writesToDisk = 0;
        int toNextLocation = 0;

        File inputFile = new File(file);
        Scanner sc = new Scanner(System.in);

        try
        {
            sc = new Scanner(inputFile);
        }
        catch(Exception e)
        {
            System.out.println("Error when opening file. Exit!");
            System.exit(0);
        }

        PTEntry[] RAM_PGTable = generateRAM();
        PTEntry[] PGTable = generatePGTable();

        while(sc.hasNextLine())
        {
            memoryAccess++;
            String input = sc.nextLine();
            String[] splitInput = input.split(" ");

            char accessMode = splitInput[1].charAt(0);
            long address = Long.decode("0x"+splitInput[0]); // change it address to hexadecimal 32 bit represent
            int page = (int)( address / PAGE_SIZE); // get page number

            if(page < 0 || page >= PGTable.length)
            {
                System.out.println("Page Out of Bound. Fatal Error. Exit!");
                System.exit(0);
            }

            if(PGTable[page].isValid()) {

                if(accessMode == 'W') //set page as write
                    RAM_PGTable[PGTable[page].getFrame()].setDirty(true);

                System.out.println("HIT");
            }
            else {
                if(RAM_PGTable[toNextLocation].getIndex() == -1)
                {
                    PGTable[page].setValid(true);
                    PGTable[page].setFrame(toNextLocation);

                    if(accessMode == 'W')
                        PGTable[page].setDirty(true);

                    RAM_PGTable[toNextLocation] = PGTable[page];
                    System.out.println("Page Fault. Not Evicting.");
                }
                else
                {
                    if(RAM_PGTable[toNextLocation].isDirty()) {
                        writesToDisk++;

                        System.out.println("Page Fault. Evict Dirty");
                    }
                    else {
                        System.out.println("Page Fault. Evict Clean");
                    }

                    int temp = RAM_PGTable[toNextLocation].getIndex();

                    PGTable[temp].setDirty(false);
                    PGTable[temp].setValid(false);
                    PGTable[temp].setFrame(-1);
                    RAM_PGTable[toNextLocation] = PGTable[page];

                    if(accessMode == 'W')
                        RAM_PGTable[toNextLocation].setDirty(true);

                    RAM_PGTable[toNextLocation].setValid(true);
                    RAM_PGTable[toNextLocation].setFrame(toNextLocation);
                }
                pageFault++;

                toNextLocation = (toNextLocation + 1) % frames;
            }
        }
        printSummary("FIFO", memoryAccess, pageFault, writesToDisk);
    }


    //
    // Clock - giving second chance to page in RAM (better implementation of second-chance alg)

    public void clock(String file)
    {
        int memoryAccess = 0;
        int pageFault = 0;
        int writesToDisk = 0;
        int clockPointTo = 0;

        File f = new File(file);
        Scanner scanner = new Scanner(System.in);

        try
        {
            scanner = new Scanner(f);
        }
        catch(Exception e)
        {
            System.out.println("Cannot Open File! Exit!");
            System.exit(0);
        }
        PTEntry[] RAM_PGTable = generateRAM();
        PTEntry[] PGTable = generatePGTable();
        while(scanner.hasNextLine())
        {
            memoryAccess++;
            String input = scanner.nextLine();
            String[] splitInput = input.split(" ");

            char accessMode = splitInput[1].charAt(0);
            long address = Long.decode("0x"+splitInput[0]); //get address as hexadecimal 32 bit represent
            int page = (int)(address/PAGE_SIZE);

            if(page < 0 || page >= PGTable.length)
            {
                System.out.println("Page Out Of Bounds. Exit.");
                System.exit(0);
            }

            if(PGTable[page].isValid())
            {
                if(accessMode == 'W') //if memory is write it is dirty
                    RAM_PGTable[PGTable[page].getFrame()].setDirty(true);

                RAM_PGTable[PGTable[page].getFrame()].setReferenced(true);
                System.out.println("HIT");
            }
            else
            {
                pageFault++;
                if(RAM_PGTable[clockPointTo].getIndex() == -1)
                {
                    PGTable[page].setValid(true);
                    PGTable[page].setFrame(clockPointTo);

                    if(accessMode == 'W')
                        PGTable[page].setDirty(true);

                    PGTable[page].setReferenced(true);
                    RAM_PGTable[clockPointTo] = PGTable[page];
                    System.out.println("Page Fault. Not evicting.");
                    clockPointTo = ( clockPointTo + 1 ) % frames;
                }
                else
                {
                    for( ; ; )
                    {
                        if(!RAM_PGTable[clockPointTo].isReferenced()) {
                            break;
                        }
                        else RAM_PGTable[clockPointTo].setReferenced(false);
                        clockPointTo = (clockPointTo + 1) % frames;
                    }

                    if(RAM_PGTable[clockPointTo].isDirty()) {
                        writesToDisk++;
                        System.out.println("Page Fault. Evict Dirty");
                    }
                    else {
                        System.out.println("Page Fault. Evict Dirty");
                    }

                    int temp = RAM_PGTable[clockPointTo].getIndex();
                    PGTable[temp].setDirty(false);
                    PGTable[temp].setValid(false);
                    PGTable[temp].setFrame(-1);
                    RAM_PGTable[clockPointTo] = PGTable[page];

                    if(accessMode == 'W')
                        RAM_PGTable[clockPointTo].setDirty(true);

                    RAM_PGTable[clockPointTo].setValid(true);
                    RAM_PGTable[clockPointTo].setFrame(clockPointTo);
                    RAM_PGTable[clockPointTo].setReferenced(true);
                    clockPointTo = (clockPointTo + 1) % frames;
                }
            }
        }
        printSummary("Clock", memoryAccess, pageFault, writesToDisk);
    }


    //
    // OPT - track all page request and get optimal solution for it.
    // This can have lowest page fault probability but it is not possible because we
    // always cannot have perfect knowledge. For this one, we assume we have perfect knowledge

    public void opt(String file)
    {
        int memoryAccess = 0;
        int pageFault = 0;
        int writesToDisk = 0;
        int activeFrames = 0;

        File f = new File(file);
        Scanner scanner = new Scanner(System.in);

        try
        {
            scanner = new Scanner(f);
        }
        catch(Exception e)
        {
            System.out.println("Cannot Open File. Exit.");
            System.exit(0);
        }

        PTEntry[] RAM_PGTable = generateRAM();
        PTEntry[] PGTable = generatePGTable();

        ArrayList<LinkedList<Integer>> optimalFutureRequest = new ArrayList<LinkedList<Integer>>(PGTable.length);

        for(int index = 0; index<PGTable.length; index++)
        {
            optimalFutureRequest.add(index,new LinkedList<Integer>());
        }

        int time = 0;

        while(scanner.hasNextLine())
        {
            String input = scanner.nextLine();
            String[] splitInput = input.split(" ");
            long address = Long.decode("0x"+splitInput[0]); //address as 32 bit hexadecimal represent
            int page = (int)(address / PAGE_SIZE);

            if(page < 0 || page >= PGTable.length)
            {
                System.out.println("Page Out Of Bounds. Fatal Error. Exit!");
                System.exit(0);
            }
            optimalFutureRequest.get(page).add(time);
            time++;
        }

        try
        {
            scanner = new Scanner(f);
        }
        catch(Exception e)
        {
            System.out.println("Cannot Open File. Exit.");
            System.exit(0);
        }
        while(scanner.hasNextLine())
        {
            memoryAccess++;
            String input = scanner.nextLine();
            String[] splitInput = input.split(" ");

            char accessMode = splitInput[1].charAt(0);
            long address = Long.decode("0x"+splitInput[0]);
            int page = (int)( address / PAGE_SIZE );

            if(page < 0 || page >= PGTable.length)
            {
                System.out.println("Page Out Of Bounds. Fatal Error. Exit!");
                System.exit(0);
            }
            if(PGTable[page].isValid())
            {
                if(accessMode == 'W')
                    RAM_PGTable[PGTable[page].getFrame()].setDirty(true);

                System.out.println("HIT");
            }
            else
            {
                pageFault++;
                if(activeFrames < frames)
                {
                    PGTable[page].setValid(true);
                    PGTable[page].setFrame(activeFrames);

                    if(accessMode == 'W')
                        PGTable[page].setDirty(true);

                    RAM_PGTable[activeFrames] = PGTable[page];
                    System.out.println("Page Fault! Not Evicting.");
                    activeFrames++;
                }
                else
                {
                    int locationToRemove = -1;
                    for(int start = 0;start < frames;start++)
                    {
                        if(optimalFutureRequest.get(RAM_PGTable[start].getIndex()).peek() == null)
                        {
                            locationToRemove = start;
                            break;
                        }
                        else
                        {
                            if(locationToRemove == -1)
                            {
                                locationToRemove = start;
                            }
                            else
                            {
                                if((int)optimalFutureRequest.get(RAM_PGTable[locationToRemove].getIndex()).peek() < (int)optimalFutureRequest.get(RAM_PGTable[start].getIndex()).peek())
                                    locationToRemove = start;
                            }
                        }
                    }
                    if(RAM_PGTable[locationToRemove].isDirty())
                    {
                        writesToDisk++;
                        System.out.println("Page Fault. Evict Dirty!");
                    }
                    else
                    {
                        System.out.println("Page Fault. Evict Clean.");
                    }
                    int temp = RAM_PGTable[locationToRemove].getIndex();

                    PGTable[temp].setDirty(false);
                    PGTable[temp].setValid(false);
                    PGTable[temp].setFrame(-1);

                    RAM_PGTable[locationToRemove] = PGTable[page];

                    if(accessMode == 'W')
                        RAM_PGTable[locationToRemove].setDirty(true);

                    RAM_PGTable[locationToRemove].setValid(true);
                    RAM_PGTable[locationToRemove].setFrame(locationToRemove);
                    RAM_PGTable[locationToRemove].setReferenced(true);
                }
            }
            optimalFutureRequest.get(page).remove();
        }
        printSummary("OPT", memoryAccess, pageFault, writesToDisk);
    }

    /////////////////////// Helper Functions ////////////////////////////////

    // Generate Page Table array (no element inside) as our PG Table
    public PTEntry[] generatePGTable()
    {
        PTEntry[] pageTable = new PTEntry[(int)Math.pow(2,20)];

        for(int i = 0 ; i < pageTable.length ; i++)
        {
            PTEntry temp = new PTEntry();

            temp.setIndex(i);

            pageTable[i] = temp;
        }
        return pageTable;
    }

    // Create Page Table array for RAM use
    public PTEntry[] generateRAM()
    {
        PTEntry[] RAM = new PTEntry[frames];
        for(int i = 0 ; i < RAM.length ; i++)
        {
            PTEntry temp = new PTEntry();
            temp.setFrame(i);
            RAM[i] = temp;
        }
        return RAM;
    }
    // Clean all referenced memory bit for NRU in RAM
    public PTEntry[] cleanReferencedMemory(PTEntry[] RAM)
    {
        for(int temp = 0; temp < frames; temp++)
        {
            RAM[temp].setReferenced(false);
        }
        return RAM;
    }

    // Print summary
    public void printSummary(String algorithm, int memoryAccess, int pgFault, int writesToDisk)
    {
        System.out.println("Algorithm: " + algorithm);
        System.out.println("Number of frames:\t" + frames);
        System.out.println("Total memory accesses:\t" + memoryAccess);
        System.out.println("Total page faults:\t" + pgFault);
        System.out.println("Total writes to disk:\t" + writesToDisk);
    }


}
