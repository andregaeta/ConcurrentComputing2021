import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileMonitor
{
    BufferedWriter buffered_writer;
    private int reader_count;
    private int writer_count;

    public FileMonitor(String file_path)
    {
        this.buffered_writer = IO_Utility.open_file_write(file_path);
    }

    public synchronized void write_to_file(List<Integer> values)
    {
        IO_Utility.write_to_file(buffered_writer, values);
    }

    public void close_file()
    {
        try
        {
            buffered_writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void reader_in()
    {
        try
        {
            while (writer_count > 0)
                wait();
            reader_count++;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void reader_out()
    {
        reader_count--;
        if (reader_count == 0)
            this.notify();
    }

    public synchronized void writer_in()
    {
        try
        {
            while ((reader_count > 0) || (writer_count > 0))
            {
                wait();
            }

            writer_count++;
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void writer_out()
    {
        writer_count--;
        notifyAll();
    }
}
