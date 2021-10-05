import java.util.ArrayList;

public class Main
{
    public static final int chunk_size = 5;
    public static final int queue_capacity = 50;
    public static final int n_threads = 4;
    public static final String input_path = "src/io/input.txt";
    public static final String output_path = "src/io/output.txt";

    public static void main(String[] args)
    {
        var scanner = IO_Utility.open_file_read(input_path);
        int count = scanner.nextInt();

        var queue = new ChunkedDataQueue<Integer>(chunk_size, queue_capacity);
        var monitor = new FileMonitor(output_path);

        var threads = new ArrayList<Thread>();

        threads.add(new DataEnqueueThread(scanner, queue, count));
        for (int i = 0; i < n_threads - 1; i++)
        {
            threads.add(new DequeueToFileThread(queue, monitor));
        }

        threads.forEach(thread -> thread.start());
        threads.forEach(thread ->
        {
            try
            {
                thread.join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
        });

        monitor.close_file();
    }
}