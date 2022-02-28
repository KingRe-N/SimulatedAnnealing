import java.util.concurrent.ThreadLocalRandom;

public class metropolis extends Thread
{

    private volatile int nM = Variables.nM;     // number of times metro called in each thread

    public double mag_mean = 0.0;               // m[j] mean of each thread
    public double pairC_mean = 0.0;             // c[j] mean of each thread

    public double magnet[] = new double[nM];    // magnetization per spin       array of <m>
    public double pairCorr[] = new double[nM];  // pair correlation per spin    array of <cp>

    @Override
    public void run()
    {
        for(int i =0; i < nM; i++)
        {
            double result[] = metro();
            magnet[i] =  result[0];
            pairCorr[i] = result[1];
        }

        mag_mean = calc_mag_mean(magnet, nM);
        pairC_mean = calc_pairC_mean(pairCorr, nM);
    }


    public static double[] metro()
    {
        CircularLinkedList configCurr = initialConfig(Variables.C,Variables.n);
        CircularLinkedList configNext = CircularLinkedList.copy(configCurr);

        for(int k = 1; k <= Variables.numConfig; k++)
        {
            int index = ThreadLocalRandom.current().nextInt(1, Variables.n+1);

            if (configNext.elementAt(index) == 1) configNext.updateNode(index, -1);
            else configNext.updateNode(index, 1);


            if(Sigma(configNext,Variables.n,Variables.B,Variables.C) < Sigma(configCurr,Variables.n,Variables.B,Variables.C))
            {
                configCurr = CircularLinkedList.copy(configNext);
            }
            else
            {
                double randomNum = ThreadLocalRandom.current().nextDouble(0, 1);
                double pPower = -(  (Sigma(configNext,Variables.n,Variables.B,Variables.C) - Sigma(configCurr, Variables.n,Variables.B,Variables.C)) / Variables.T );
                double p = Math.pow(Math.E, pPower);

                if (randomNum < p)
                {
                    configCurr = CircularLinkedList.copy(configNext);

                } else
                {
                    configNext = CircularLinkedList.copy(configCurr);
                }
            }
        }

        double  magnetizationFC = magnetizationPS(configCurr,Variables.n),
                pairCorrelationFC = pairCorPS(configCurr,Variables.n);

        return new double[] {magnetizationFC, pairCorrelationFC};

        }

    public static double Sigma(CircularLinkedList config, int n, double B, double C)
    {
        double sum = 0;

        for(int i = 1; i < n; i++)
        {
            sum += (      (B * config.elementAt(i))      +       (C * config.elementAt(i) * config.elementAt(i+1))    );
        }
        sum = sum + (      (B * config.elementAt(n))      +     (C * config.elementAt(n) * config.elementAt(1))      );

        return -sum;
    }

    public static CircularLinkedList initialConfig(double C, int n){
        CircularLinkedList Config = new CircularLinkedList();
        if(C >= 0) {
            for(int i = 0; i < n; i++)
            {
                Config.addNodeAtEnd(1);
            }
        }else{
            for(int i = 0; i < n/2; i++)
            {
                Config.addNodeAtEnd(-1);
                Config.addNodeAtEnd(1);
            }
            if(n % 2 == 1) Config.addNodeAtEnd(-1);
        }
        return Config;
    }

    //    Compute the magnetization per spin using Current Config    1/n Sigma i=1 -> n (Si)
    public static double magnetizationPS(CircularLinkedList fconfig, int n)
    {
        double sum = 0;
        for (int i = 1; i <= n; i++)
        {
            sum += fconfig.elementAt(i);
        }
        double MpS = sum / n;
        return MpS;
    }

    //    Compute the Pair correlation per spin using Current Config    1/n Sigma i=1 -> n (Si * Si+1)
    public static double pairCorPS (CircularLinkedList fconfig, int n)
    {
        double sum = 0;
        for (int i = 1; i<n; i++)
        {
            sum += (fconfig.elementAt(i) * fconfig.elementAt(i+1));
        }
        sum = sum + (fconfig.elementAt(n) * fconfig.elementAt(1));
        double PCpS = sum / n;
        return PCpS;
    }

    public static double calc_mag_mean(double[] magnet,int nM)
    {
        double meanM = 0.0;
        for (int i = 0; i <= nM-1; i++)
        {
            meanM += magnet[i];
        }
        meanM = meanM / nM;

        return meanM;
    }

    public static double calc_pairC_mean(double[] pairCorr,int nM)
    {
        double meanP = 0.0;
        for (int i = 0; i <= nM-1; i++)
        {
            meanP += pairCorr[i];
        }
        meanP = meanP / nM;
        return meanP;
    }

}
