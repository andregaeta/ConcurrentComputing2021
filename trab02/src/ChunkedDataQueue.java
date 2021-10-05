import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class ChunkedDataQueue<T>
{
    private int chunk_size;
    private int max_capacity;
    private Queue<T> queue;

    public boolean dequeue_done = false;
    public boolean enqueue_done = false;

    public ChunkedDataQueue(int chunk_size, int max_capacity)
    {
        this.chunk_size = chunk_size;
        this.max_capacity = max_capacity;
        this.queue = new LinkedList<T>();
    }

    public synchronized void Enqueue(T element)
    {
        try
        {
            while (queue.size() >= max_capacity)
                wait();
            queue.add(element);
            notifyAll();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized List<T> DequeueChunk()
    {
        var retList = new ArrayList<T>();
        try
        {
            while(queue.size() < chunk_size)
            {
                if(enqueue_done)
                {
                    dequeue_done = true;
                    notifyAll();
                    return retList;
                }
                else
                    wait();
            }

            for (int i = 0; i < chunk_size; i++)
                retList.add(queue.remove());

            notifyAll();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        return retList;
    }
}