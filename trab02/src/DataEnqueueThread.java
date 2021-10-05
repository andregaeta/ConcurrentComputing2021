import java.io.*;
import java.util.Queue;
import java.util.Scanner;

public class DataEnqueueThread extends Thread
{
    private ChunkedDataQueue<Integer> queue;
    private Scanner scanner;
    private int count;

    public DataEnqueueThread(Scanner scanner, ChunkedDataQueue<Integer> queue, int count)
    {
        this.queue = queue;
        this.scanner = scanner;
        this.count = count;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < count; i++)
        {
            queue.Enqueue(scanner.nextInt());
        }
        queue.enqueue_done = true;
        scanner.close();
    }
}
