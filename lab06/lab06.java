public class lab06
{
    private static final int N_THREADS = 4;
    private static final int ARRAY_SIZE = 10000000;
    public static void main(String[] args)
    {
        int[] array = new int[ARRAY_SIZE];

        for (int i = 0; i < array.length; i++)
        {
            array[i] = i;
        }

        ParityData data = new ParityData(N_THREADS, array);

        Thread[] threads = new Thread[N_THREADS];

        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new ParityChecker(i, data);
        }

        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++)
        {
            try
            {
                threads[i].join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println(data.getEvenCount());
    }
}

class ParityData
{
    private int _nThreads;
    private int[] _array;
    private int _evenCount;

    public ParityData(int nThreads, int[] array)
    {
        this._nThreads = nThreads;
        this._array = array;
        this._evenCount = 0;
    }

    public synchronized void inc()
    {
        _evenCount++;
    }
    public synchronized int getValueAtIndex(int index)
    {
        return _array[index];
    }
    public synchronized int getThreadAmount()
    {
        return _nThreads;
    }
    public synchronized int getArrayLength()
    {
        return _array.length;
    }
    public int getEvenCount()
    {
        return _evenCount;
    }
}

class ParityChecker extends Thread
{
    private int _id;
    private ParityData _data;

    public ParityChecker(int id, ParityData data) {
        this._id = id;
        this._data = data;
    }

    @Override
    public void run()
    {
        int length = _data.getArrayLength();
        int nThreads = _data.getThreadAmount();

        for (int i = _id; i < length; i+= nThreads)
        {
            if(_data.getValueAtIndex(i) % 2 == 0)
                _data.inc();
        }
    }
}