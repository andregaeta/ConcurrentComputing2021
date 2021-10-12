import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main
{
    public static final boolean debug = false;

    public static void main(String[] args)
    {
        int n_threads = 0;
        int chunk_size = 0;

        if(args.length < 4)
        {
            System.out.println("Input incorreto. <n_threads_consumidoras> <chunk_size> <input_path> <output_path>");
            return;
        }
        try
        {
            n_threads = Integer.parseInt(args[0]);
            chunk_size = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException e)
        {
            System.out.println("Input incorreto. <n_threads_consumidoras> <chunk_size> <input_path> <output_path>");
            e.printStackTrace();
            return;
        }

        String input_path = args[2];
        String output_path = args[3];
        int queue_capacity = 10;

        Scanner scanner = IO_Utility.open_file_read(input_path);
        int count = scanner.nextInt();

        var queue = new SynchronizedQueue<List<Integer>>(queue_capacity, debug);
        var monitor = new FileMonitor(output_path);

        var threads = new ArrayList<Thread>();

        threads.add(new ChunkEnqueuerThread(scanner, queue, count, chunk_size));
        for (int i = 0; i < n_threads; i++)
        {
            threads.add(new ChunkDequeuerThread(queue, monitor));
        }

        long start_time = System.currentTimeMillis();

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

        System.out.println("Elapsed time: " + (System.currentTimeMillis() - start_time) + " ms");
    }

    public static void main_split(String[] args)
    {
        int n_threads = 0;
        int chunk_size = 0;

        if(args.length < 4)
        {
            System.out.println("Input incorreto. <n_threads_consumidoras> <chunk_size> <input_path> <output_path>");
            return;
        }
        try
        {
            n_threads = Integer.parseInt(args[0]);
            chunk_size = Integer.parseInt(args[1]);
        }
        catch(NumberFormatException e)
        {
            System.out.println("Input incorreto. <n_threads_consumidoras> <chunk_size> <input_path> <output_path>");
            e.printStackTrace();
            return;
        }

        String input_path = args[2];
        String output_path = args[3];
        int queue_capacity = 10000000;

        Scanner scanner = IO_Utility.open_file_read(input_path);
        int count = scanner.nextInt();

        var queue = new SynchronizedQueue<List<Integer>>(queue_capacity, debug);
        var monitor = new FileMonitor(output_path);

        var enqueuer = new ChunkEnqueuerThread(scanner, queue, count, chunk_size);
        long start_time = System.currentTimeMillis();
        enqueuer.run();
        try
        {
            enqueuer.join();
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        System.out.println("Enqueuer time: " + (System.currentTimeMillis() - start_time));

        var threads = new ArrayList<Thread>();

        for (int i = 0; i < n_threads; i++)
        {
            threads.add(new ChunkDequeuerThread(queue, monitor));
        }

        start_time = System.currentTimeMillis();

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

        System.out.println("Dequeuer time: " + (System.currentTimeMillis() - start_time));
    }


    static void main_test(int n_threads)
    {
        var threads = new ArrayList<Thread>();
        for (int i = 0; i < n_threads; i++)
        {
            threads.add(new TestThread());
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
    }
}
