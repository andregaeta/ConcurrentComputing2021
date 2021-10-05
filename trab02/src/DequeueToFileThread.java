import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class DequeueToFileThread extends Thread
{
    private ChunkedDataQueue<Integer> queue;
    private FileMonitor monitor;

    public DequeueToFileThread(ChunkedDataQueue<Integer> queue, FileMonitor monitor)
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
            Collections.sort(chunk);
            monitor.writer_in();
            monitor.write_to_file(chunk);
            monitor.writer_out();
        }
    }
}
