public class vmsim {

    public static void main(String[] args) {

        if(args.length<5 || args.length>7)
        {
            System.out.println("Wrong number of arguments. " + args.length + "given. Exiting program!");
            System.exit(0);
        }

        if(!args[0].equals("-n"))
        {
            System.out.println("Needed '-n'. Wrong first argument");
            System.exit(0);
        }

        int numFrames = 0;

        try
        {
            numFrames = Integer.parseInt(args[1]);
        }
        catch(Exception exc)
        {
            System.out.println("Needed '<numframes>'. Wrong second argument.");
            System.exit(0);
        }

        if(!args[2].equals("-a"))
        {
            System.out.println("Needed '-a'. Wrong third argument.");
            System.exit(0);
        }

        String replacementAlgorithm = args[3];
        if(!replacementAlgorithm.equals("opt") && !replacementAlgorithm.equals("clock") && !replacementAlgorithm.equals("nru") && !replacementAlgorithm.equals("fifo"))
        {
            System.out.println("Needed one of '<opt, clock, fifo, nru>'. Wrong fourth argument.");
            System.exit(0);
        }

        ReplacementAlgorithms simulator = new ReplacementAlgorithms(numFrames);
        int refreshParameter = 0;

        if(replacementAlgorithm .equals("nru"))
        {
            if(args.length != 7)
            {
                System.out.println("Wrong number of arguments. NRU needs 7 arguments.");
                System.exit(0);
            }

            if(!args[4].equals("-r"))
            {
                System.out.println("Needed '-r'. Wrong 5th argument for nru.");
                System.exit(0);
            }

            try
            {
                refreshParameter = Integer.parseInt(args[5]);
            }
            catch(Exception e)
            {
                System.out.println("Needed '<refresh>'. Wrong 6th argument for refresh rate.");
                System.exit(0);
            }
        }
        else if(args.length != 5)
        {
            System.out.println("Wrong number of arguments. Needed 5 arguments for opt, fifo, clock.");
            System.exit(0);
        }

        if(replacementAlgorithm.equals("opt"))
            simulator.opt(args[4]);
        else if(replacementAlgorithm.equals("clock"))
            simulator.clock(args[4]);
        else if(replacementAlgorithm.equals("fifo"))
            simulator.fifo(args[4]);
        else if(replacementAlgorithm.equals("nru"))
            simulator.nru(refreshParameter, args[6]);
        else
        {
            System.out.println("Illegal Input. Exiting.");
            System.exit(0);
        }
    }
}

