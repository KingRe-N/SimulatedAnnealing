class Variables
{
    static int      n = 100,    // # of spins
                    nM = 75,    // number of times metro called in each thread
                    numThreads = 100;

    static double   B = .25,      // coefficient in equation
                    C = -.12,     // coefficient in equation
                    nF = 5,     // configurations = N * n
                    numConfig = nF * n, // flips
                    T = 1.9;    // temperature
}

public class testThread
{
    public static void main(String[] args)
    {
        long startTime = System.nanoTime();

        double[] arr_M = new double[Variables.numThreads]; // array of m[j]
        double[] arr_C = new double[Variables.numThreads]; // array of c[j]
        double uM = 0.0;        // global magnetization
        double uC = 0.0;        // global Pair Correlation

        metropolis myt[] = new metropolis[Variables.numThreads];

        for (int i = 0; i < Variables.numThreads; ++i) {
            myt[i] = new metropolis();
            myt[i].start();
        }

        System.out.println("all threads spawned");

        for (int i = 0; i < Variables.numThreads; ++i) {
            try {
                if (myt[i].isAlive()) {
                    myt[i].join();
                }
            }
            catch (Exception e) {}
        }


        for (int i = 0; i < Variables.numThreads; ++i)
        {
//            System.out.println("Thread : " + i);
//            System.out.println("<m>    <cp>");

//            double[] Magnet = myt[i].magnet;        // <m>
//            double[] PairCorr = myt[i].pairCorr;    // <cp>

            double mag_mean = myt[i].mag_mean;      // m[j]
            double pairC_mean = myt[i].pairC_mean;  // c[j]

//            for(int x = 0; x<Magnet.length; x++)
//            {
//                System.out.println(Magnet[x] + "   " + PairCorr[x]);
//            }
//            System.out.println();
//            System.out.println("m[j], Average of <m>: " + mag_mean);
//            System.out.println("c[j], Average of <c>: " + pairC_mean);

            arr_M[i] = mag_mean;
            arr_C[i] = pairC_mean;

//            System.out.println();
//            System.out.println();
//            System.out.println();
        }

        for (int i = 0; i < Variables.numThreads; i++)
        {
            uM += arr_M[i];
            uC += arr_C[i];
        }

        uM          = uM/Variables.numThreads;
        uC          = uC/Variables.numThreads;

        System.out.println("uM, Average of m[j]: " + uM);
        System.out.println("uC, Average of c[j]: " + uC);

//        rel_err(arr_C);
//        var(arr_C,rel_err(arr_C));

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println(totalTime);
    }

    public static double rel_err(double[] arr_c)
    {
        double     C = Variables.C;
        double  T = Variables.T;
        double  num = C/T;
        double  cp_theory = (Math.exp(num)-Math.exp(-num)) / (Math.exp(num) + Math.exp(-num));

        double  r = 0.0;

        for(int i = 0; i < Variables.numThreads; i++)
        {
            r += (( arr_c[i]- cp_theory) / cp_theory);
        }
        r = r/Variables.numThreads;

        System.out.println("nF:       " + Variables.nF);
        System.out.println("rel_err:   " + r);
        System.out.println(r + " * " + Math.sqrt(Variables.nF) + " = " + r*Math.sqrt(Variables.nF));

        return r;
    }

    public static double var(double [] arr_c, double re)
    {
        double     C = Variables.C;
        double  T = Variables.T;
        double  variance = 0.0;
        double  num = C/T;
        double  cp_theory = (Math.exp(num)-Math.exp(-num)) / (Math.exp(num) + Math.exp(-num));

        for(int i = 0; i < Variables.numThreads; i++)
        {
            variance += Math.pow( (((arr_c[i] - cp_theory) / cp_theory) - re),2.0);
        }
        variance = variance/Variables.numThreads;
        variance = Math.sqrt(variance);

        System.out.println("nM:       " + Variables.nM);
        System.out.println("var: " + variance);
        System.out.println(variance + " * " + Math.sqrt(Variables.nM) + " = " + variance*Math.sqrt(Variables.nM));
        return variance;
    }
}