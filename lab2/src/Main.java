import ru.ifmo.rain.zakharevich.arrayset.ArraySet;

import java.util.List;
import java.util.ArrayList;
import java.util.NavigableSet;

public class Main {

    public static void main(String[] args) {

        List<Integer> list = new ArrayList<>();
        list.add(6);
        list.add(3);
        list.add(2);
        list.add(123);

        NavigableSet<Integer> set = new ArraySet<>(list);
        System.out.println(set);

        NavigableSet<Integer> set2 = set.descendingSet();
        System.out.println(set2);

        NavigableSet<Integer> set3 = set2.descendingSet();
        System.out.println(set3);


        set2.addAll(list);
    }
}
