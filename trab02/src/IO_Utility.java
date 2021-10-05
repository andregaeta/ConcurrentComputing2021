import java.io.*;
import java.util.List;
import java.util.Scanner;

public class IO_Utility
{
    public static BufferedWriter open_file_write(String file_path)
    {
        try
        {
           return new BufferedWriter(new FileWriter(file_path));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Scanner open_file_read(String file_path)
    {
        try
        {
            return new Scanner(new FileReader(file_path));
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static void write_to_file(BufferedWriter writer, List<Integer> values)
    {
        if(values.isEmpty())
            return;
        try
        {
            for (var value : values)
            {
                writer.append(Integer.toString(value));
                writer.append(" ");
            }

            writer.append("\n");
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
