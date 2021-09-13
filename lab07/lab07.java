import static java.lang.Math.sqrt;

public class lab07
{
    public static void main(String[] args)
    {
        int vectorSize = 10;
        RW_Monitor monitor = new RW_Monitor();

        Reader[] readers = new Reader[vectorSize];
        Writer[] writers = new Writer[vectorSize];
        ReaderWriter[] readersWriters = new ReaderWriter[vectorSize];


        for (int i = 0; i < vectorSize; i++)
        {
            readers[i] = new Reader(i + 1, monitor);
            readers[i].start();
        }

        for (int i = 0; i < vectorSize; i++)
        {
            writers[i] = new Writer(i + 1, monitor);
            writers[i].start();
        }

        for (int i = 0; i < vectorSize; i++)
        {
            readersWriters[i] = new ReaderWriter(i + 1, monitor);
            readersWriters[i].start();
        }
    }
}

class RW_Monitor
{
    private int _value;
    private int _readers;
    private int _writers;

    public RW_Monitor()
    {
        _readers = 0;
        _writers = 0;
        _value = 0;
    }
    public synchronized int getValue()
    {
        return _value;
    }
    public synchronized void setValue(int value)
    {
        _value = value;
    }

    public synchronized void readerIn(int id)
    {
        try
        {
            while (_writers > 0)
            {
                System.out.println("le.leitorBloqueado(" + id + ")");
                wait();
            }
            _readers++;
            System.out.println("le.leitorLendo(" + id + ")");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void readerOut(int id)
    {
        _readers--;
        if (_readers == 0)
            this.notify();
        System.out.println ("> Saindo L"+id);
    }

    public synchronized void writerIn(int id)
    {
        try
        {
            while ((_readers > 0) || (_writers > 0))
            {
                System.out.println("le.escritorBloqueado(" + id + ")");
                wait();
            }
            _writers++;
            System.out.println("le.escritorEscrevendo(" + id + ")");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void writerOut(int id)
    {
        _writers--;
        notifyAll();
        System.out.println ("le.escritorSaindo("+id+")");
    }
}

class Reader extends Thread
{
    private static final int ITERATIONS = 10;
    private static final int DELAY = 100;

    private int _id;
    private RW_Monitor _monitor;

    public Reader(int id, RW_Monitor monitor)
    {
        _id = id;
        _monitor = monitor;
    }

    private static boolean isPrime(int n)
    {
        if (n <= 1)
            return false;


        for (int i = 2; i <= sqrt(n); i++)
            if (n % i == 0)
                return false;

        return true;
    }

    private void read()
    {
        _monitor.readerIn(_id);
        int value = _monitor.getValue();
        boolean valueIsPrime = isPrime(value);
        System.out.println("[Leitor " + _id + "] " + value + " é primo = " + valueIsPrime);
        _monitor.readerOut(_id);
    }

    @Override
    public void run()
    {
        try
        {
            for (int i = 0; i < ITERATIONS; i++)
            {
                read();
                sleep(DELAY);
            }
        }
        catch (InterruptedException e)
        {
            System.err.println(e);
            return;
        }
    }
}

class Writer extends Thread
{
    private static final int ITERATIONS = 10;
    private static final int DELAY = 100;

    private int _id;
    private RW_Monitor _monitor;

    public Writer(int id, RW_Monitor monitor)
    {
        _id = id;
        _monitor = monitor;
    }

    private void write() {
        _monitor.writerIn(_id);
        _monitor.setValue(_id);
        System.out.println("[Escritor " + _id + "] mudou valor para " + _id);
        _monitor.writerOut(_id);
    }

    @Override
    public void run()
    {
        try
        {
            for (int i = 0; i < ITERATIONS; i++)
            {
                write();
                sleep(DELAY);
            }
        }
        catch (InterruptedException e)
        {
            System.err.println(e);
            return;
        }
    }

}

class ReaderWriter extends Thread
{
    private static final int ITERATIONS = 10;
    private static final int DELAY = 100;

    private int _id;
    private RW_Monitor _monitor;

    public ReaderWriter(int id, RW_Monitor monitor)
    {
        _id = id;
        _monitor = monitor;
    }

    private static boolean isEven(int n)
    {
        return (n % 2) == 0;
    }

    private void write() {
        _monitor.writerIn(_id);
        int newValue = _monitor.getValue() * 2;
        _monitor.setValue(newValue);
        System.out.println("[LeitorEscritor " + _id + "] mudou valor para " + newValue);
        _monitor.writerOut(_id);
    }

    private void read() {
        _monitor.readerIn(_id);
        int value = _monitor.getValue();
        boolean isValueEven = isEven(value);
        System.out.println("[LeitorEscritor " + _id + "] " + value + " é par = " + isValueEven);
        _monitor.readerOut(_id);
    }

    @Override
    public void run()
    {
        try
        {
            for (int i = 0; i < ITERATIONS; i++)
            {
                read();
                write();
                sleep(DELAY);
            }
        }
        catch (InterruptedException e)
        {
            System.err.println(e);
            return;
        }
    }
}