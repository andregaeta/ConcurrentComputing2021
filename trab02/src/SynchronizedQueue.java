import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class SynchronizedQueue<T>
{
    private int max_capacity;
    private Queue<T> queue;

    public boolean dequeue_done = false;
    public boolean enqueue_done = false;

    private boolean debug;

    public SynchronizedQueue(int max_capacity, boolean debug)
    {
        this.max_capacity = max_capacity;
        this.queue = new LinkedList<T>();

        this.debug = debug;
    }

    public synchronized void Enqueue(T element)
    {
        try
        {
            while (queue.size() >= max_capacity)
            {
                if(debug) System.out.println("wait capacity");
                wait();
            }
            queue.add(element);

            if(debug) System.out.println("notify");
            notifyAll();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized T DequeueChunk()
    {
        try
        {
            while(queue.size() < 1)
            {
                if(enqueue_done)
                {
                    finish_dequeue();
                    return null;
                }
                else
                {
                    if(debug) System.out.println("wait input");
                    wait();
                }
            }

            T ret = queue.remove();

            if(debug) System.out.println("notify");
            notifyAll();

            return ret;
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public synchronized void finish_enqueue()
    {
        enqueue_done = true;
        if(debug) System.out.println("notify");
        notifyAll();
    }
    public synchronized void finish_dequeue()
    {
        dequeue_done = true;
        if(debug) System.out.println("notify");
        notifyAll();
    }
}