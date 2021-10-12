import java.util.*;

public class ChunkDequeuerThread extends Thread
{
    private SynchronizedQueue<List<Integer>> queue;
    private FileMonitor monitor;

    public ChunkDequeuerThread(SynchronizedQueue<List<Integer>> queue, FileMonitor monitor)
    {
        this.queue = queue;
        this.monitor = monitor;
    }

    @Override
    public void run()
    {
        while (queue.dequeue_done == false)
        {
            List<Integer> chunk = queue.DequeueChunk();

            if(chunk == null)
                continue;

            Collections.sort(chunk);

            monitor.writer_in();
            monitor.write_to_file(chunk);
            monitor.writer_out();
        }
    }

}
