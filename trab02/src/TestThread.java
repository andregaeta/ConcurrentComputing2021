import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestThread extends Thread
{
    @Override
    public void run()
    {
        Random random =  new Random();
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10000000; i++)
        {
            list.add(random.nextInt());
        }

        long start_time = System.currentTimeMillis();
        for (int i = 0; i < list.size(); i++)
        {
            var a = list.get(i);
            a++;
            list.set(i, a);
        }
        System.out.println("Sort time: " + (System.currentTimeMillis() - start_time));
    }
}
