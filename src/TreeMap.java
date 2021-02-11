import java.util.*;

public class TreeMap<K, V>
{
    private final IComparator<K> m_comparator;

    private Entry<K, V> m_root;
    private int         m_count;

    public TreeMap(IComparator<K> comparator)
    {
        m_comparator = comparator;

        m_root  = null;
        m_count = 0;
    }

    private void PlaceInternal(Entry<K, V> entry, Entry<K, V> other)
    {
        Entry<K, V> lastEntry = entry.GetParent();
        Entry<K, V> currEntry = entry;

        boolean right = false;

        while(currEntry != null)
        {
            int comparison = m_comparator.Compare(other.GetKey(), currEntry.GetKey());
            if(comparison < 0)
            {
                lastEntry = currEntry;
                currEntry = currEntry.GetLeft();
                right = false;
            }
            else if(comparison > 0)
            {
                lastEntry = currEntry;
                currEntry = currEntry.GetRight();
                right = true;
            }
        }

        other.SetParent(lastEntry);
        if(right)
            lastEntry.SetRight(other);
        else
            lastEntry.SetLeft(other);
    }

    public int Count() { return m_count; }

    public V Get(K key)
    {
        Entry<K, V> currEntry = m_root;

        while(currEntry != null)
        {
            int comparison = m_comparator.Compare(key, currEntry.GetKey());
            if(comparison < 0)
                currEntry = currEntry.GetLeft();
            else if(comparison > 0)
                currEntry = currEntry.GetRight();
            else
                return currEntry.GetValue();
        }

        return null;
    }

    public boolean Place(K key, V value)
    {
        Entry<K, V> lastEntry = null;
        Entry<K, V> currEntry = m_root;

        boolean right = false;

        while(currEntry != null)
        {
            int comparison = m_comparator.Compare(key, currEntry.GetKey());
            if(comparison < 0)
            {
                lastEntry = currEntry;
                currEntry = currEntry.GetLeft();
                right = false;
            }
            else if(comparison > 0)
            {
                lastEntry = currEntry;
                currEntry = currEntry.GetRight();
                right = true;
            }
            else
            {
                currEntry.SetValue(value);
                return true;
            }
        }

        Entry<K, V> newEntry = new Entry<>(key, value, lastEntry);
        if(lastEntry == null)
            m_root = newEntry;
        else
        {
            if(right)
                lastEntry.SetRight(newEntry);
            else
                lastEntry.SetLeft(newEntry);
        }
        m_count++;
        return false;
    }

    public boolean Remove(K key)
    {
        Entry<K, V> lastEntry = null;
        Entry<K, V> currEntry = m_root;

        boolean right = false;

        while(currEntry != null)
        {
            int comparison = m_comparator.Compare(key, currEntry.GetKey());
            if(comparison < 0)
            {
                lastEntry = currEntry;
                currEntry = currEntry.GetLeft();
                right = false;
            }
            else if(comparison > 0)
            {
                lastEntry = currEntry;
                currEntry = currEntry.GetRight();
                right = true;
            }
            else
            {
                Entry<K, V> leftEntry  = currEntry.GetLeft();
                Entry<K, V> rightEntry = currEntry.GetRight();

                if(leftEntry != null)
                {
                    if(lastEntry == null)
                        m_root = leftEntry;
                    else
                    {
                        if(right)
                            lastEntry.SetRight(leftEntry);
                        else
                            lastEntry.SetLeft(leftEntry);
                    }
                    PlaceInternal(leftEntry, rightEntry);
                    return true;
                }

                if(lastEntry == null)
                    m_root = rightEntry;
                else
                {
                    if(right)
                        lastEntry.SetRight(rightEntry);
                    else
                        lastEntry.SetLeft(rightEntry);
                }
                return true;
            }
        }

        return false;
    }

    public boolean ContainsKey(K key) { return Get(key) != null; }

    public void Clear()
    {
        m_root  = null;
        m_count = 0;
    }

    public KeyCollection   GetKeys()   { return GetKeys(false);   }
    public ValueCollection GetValues() { return GetValues(false); }

    public EntryCollection GetEntries() { return GetEntries(false); }

    public KeyCollection   GetKeys(boolean reverse)   { return new KeyCollection(reverse);   }
    public ValueCollection GetValues(boolean reverse) { return new ValueCollection(reverse); }

    public EntryCollection GetEntries(boolean reverse) { return new EntryCollection(reverse); }

    public static class Entry<K, V>
    {
        private final K m_key;
        private       V m_value;

        private Entry<K, V> m_left;
        private Entry<K, V> m_right;

        private Entry<K, V> m_parent;

        public Entry(K key, V value, Entry<K, V> parent)
        {
            m_key   = key;
            m_value = value;

            m_parent = parent;
        }

        public K GetKey()   { return m_key;   }
        public V GetValue() { return m_value; }

        public Entry<K, V> GetLeft()  { return m_left;  }
        public Entry<K, V> GetRight() { return m_right; }

        public Entry<K, V> GetParent() { return m_parent; }

        public void SetValue(V value) { m_value = value; }

        public void SetLeft(Entry<K, V> left)   { m_left  = left;  }
        public void SetRight(Entry<K, V> right) { m_right = right; }

        public void SetParent(Entry<K, V> parent) { m_parent = parent; }
    }

    private abstract class TreeIterator<T> implements Iterator<T>
    {
        protected Entry<K, V> m_currEntry;

        public TreeIterator() { m_currEntry = m_root; }

        public abstract Entry<K, V> NextEntry();

        @Override
        public boolean hasNext() { return m_currEntry != null; }
    }

    private abstract class ForwardIterator<T> extends TreeIterator<T>
    {
        public ForwardIterator()
        {
            if(m_currEntry == null)
                return;

            while(m_currEntry.GetLeft() != null)
                m_currEntry = m_currEntry.GetLeft();
        }

        @Override
        public Entry<K, V> NextEntry()
        {
            Entry<K, V> result = m_currEntry;

            if(m_currEntry.GetRight() != null)
            {
                m_currEntry = m_currEntry.GetRight();
                while(m_currEntry.GetLeft() != null)
                    m_currEntry = m_currEntry.GetLeft();

                return result;
            }

            while(true)
            {
                if(m_currEntry.GetParent() == null)
                {
                    m_currEntry = null;
                    return result;
                }
                if(m_currEntry.GetParent().GetLeft() == m_currEntry)
                {
                    m_currEntry = m_currEntry.GetParent();
                    return result;
                }
                m_currEntry = m_currEntry.GetParent();
            }
        }
    }

    private abstract class BackwardIterator<T> extends TreeIterator<T>
    {
        public BackwardIterator()
        {
            if(m_currEntry == null)
                return;

            while(m_currEntry.GetRight() != null)
                m_currEntry = m_currEntry.GetRight();
        }

        @Override
        public Entry<K, V> NextEntry()
        {
            Entry<K, V> result = m_currEntry;

            if(m_currEntry.GetLeft() != null)
            {
                m_currEntry = m_currEntry.GetLeft();
                while(m_currEntry.GetRight() != null)
                    m_currEntry = m_currEntry.GetRight();

                return result;
            }

            while(true)
            {
                if(m_currEntry.GetParent() == null)
                {
                    m_currEntry = null;
                    return result;
                }
                if(m_currEntry.GetParent().GetRight() == m_currEntry)
                {
                    m_currEntry = m_currEntry.GetParent();
                    return result;
                }
                m_currEntry = m_currEntry.GetParent();
            }
        }
    }

    public class ForwardKeyIterator extends ForwardIterator<K>
    {
        @Override
        public K next() { return NextEntry().GetKey(); }
    }

    public class ForwardValueIterator extends ForwardIterator<V>
    {
        @Override
        public V next() { return NextEntry().GetValue(); }
    }

    public class ForwardEntryIterator extends ForwardIterator<Entry<K, V>>
    {
        @Override
        public Entry<K, V> next() { return NextEntry(); }
    }

    public class BackwardKeyIterator extends BackwardIterator<K>
    {
        @Override
        public K next() { return NextEntry().GetKey(); }
    }

    public class BackwardValueIterator extends BackwardIterator<V>
    {
        @Override
        public V next() { return NextEntry().GetValue(); }
    }

    public class BackwardEntryIterator extends BackwardIterator<Entry<K, V>>
    {
        @Override
        public Entry<K, V> next() { return NextEntry(); }
    }

    public class KeyCollection implements Iterable<K>
    {
        private final boolean m_reverse;

        public KeyCollection(boolean reverse) { m_reverse = reverse; }

        @Override
        public Iterator<K> iterator()
        {
            if(!m_reverse)
                return new ForwardKeyIterator();
            else
                return new BackwardKeyIterator();
        }
    }

    public class ValueCollection implements Iterable<V>
    {
        private final boolean m_reverse;

        public ValueCollection(boolean reverse) { m_reverse = reverse; }

        @Override
        public Iterator<V> iterator()
        {
            if(!m_reverse)
                return new ForwardValueIterator();
            else
                return new BackwardValueIterator();
        }
    }

    public class EntryCollection implements Iterable<Entry<K, V>>
    {
        private final boolean m_reverse;

        public EntryCollection(boolean reverse) { m_reverse = reverse; }

        @Override
        public Iterator<Entry<K, V>> iterator()
        {
            if(!m_reverse)
                return new ForwardEntryIterator();
            else
                return new BackwardEntryIterator();
        }
    }
}
