public class Main
{
    public static class IntegerComparator implements IComparator<Integer>
    {
        @Override
        public int Compare(Integer left, Integer right) { return left - right; }
    }

    public static void main(String[] args)
    {
        TreeMap<Integer, String> map = new TreeMap<>(new IntegerComparator());
        map.Place(1, "A");
        map.Place(2, "B");
        map.Place(3, "C");
        map.Place(4, "D");
        map.Place(5, "E");
        map.Place(6, "F");
        map.Place(7 , "G");
        map.Place(8 , "H");
        map.Place(9 , "I");
        map.Place(10, "J");
        map.Place(11, "K");
        map.Place(12, "L");
        map.Place(13, "M");
        map.Place(14, "N");
        map.Place(15, "O");
        map.Place(16, "P");
        map.Place(17, "Q");
        map.Place(18, "R");
        map.Place(19, "S");

        for(int key : map.GetKeys())
            System.out.println(map.Get(key));

        map.Remove(1);
        map.Remove(2);
        map.Remove(3);
        map.Remove(4);
        map.Remove(5);
        map.Remove(6);

        for(int key : map.GetKeys())
            System.out.println(map.Get(key));

        map.Clear();

        for(int key : map.GetKeys())
            System.out.println(map.Get(key));
    }
}
