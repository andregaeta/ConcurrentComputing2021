import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChunkEnqueuerThread extends Thread
{
    private SynchronizedQueue<List<Integer>> queue;
    private Scanner scanner;
    private int count;
    private int chunk_size;

    public ChunkEnqueuerThread(Scanner scanner, SynchronizedQueue<List<Integer>> queue, int count, int chunk_size)
    {
        this.queue = queue;
        this.scanner = scanner;
        this.count = count;
        this.chunk_size = chunk_size;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < count; i+= chunk_size)
        {
            List<Integer> list = new ArrayList<>();
            for (int j = 0; j < chunk_size; j++)
            {
                list.add(scanner.nextInt());
            }
            queue.Enqueue(list);
        }
        queue.finish_enqueue();
        scanner.close();
    }
}
