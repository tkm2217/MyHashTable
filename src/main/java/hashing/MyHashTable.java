package hashing;




import java.util.*;
import java.util.Dictionary;


public class MyHashTable<K,V> extends Dictionary<K,V>
       implements Map<K,V>, Cloneable, java.io.Serializable {


    private int threshold;
    private final float loadFactor;
    transient HashEntry<K, V>[] buckets;
    transient int modCount;
    transient int size;
    private transient Set<K> keys;
    private transient Collection<V> values;
    private transient Set<Map.Entry<K, V>> entries;


    private static final class HashEntry<K, V>
     extends AbstractMap.SimpleEntry<K, V>
   {
             HashEntry(K key, V value)
             {
                   super(key, value);
                 }

             public V setValue(V newVal)
             {
                  if (newVal == null)
                        throw new NullPointerException();
                   return super.setValue(newVal);
                 }
          }

    public  int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean containsKey(Object key) {
        int idx = hash(key);
             HashEntry<K, V> e = buckets[idx];
             while (e != null)
                  {
                     if (e.key.equals(key))
                           return true;
                     e = e.next;
                   }
             return false;
    }

    public boolean containsValue(Object value) {
        return false;
    }

    public Enumeration<K> keys() {
        return new KeyEnumerator();
    }

    public Enumeration<V> elements() {

        return new ValueEnumerator();
    }

    public V get(Object key) {
        int idx = hash(key);
            HashEntry<K, V> e = buckets[idx];
             while (e != null)
                  {
                    if (e.key.equals(key))
                          return e.value;
                    e = e.next;
                  }
             return null;
    }

    public V put(K key, V value) {
        int idx = hash(key);
             HashEntry<K, V> e = buckets[idx];

             // Check if value is null since it is not permitted.
             if (value == null)
                   throw new NullPointerException();
             while (e != null)
                   {
                     if (e.key.equals(key))
                          {
                             // Bypass e.setValue, since we already know value is non-null.
                            V r = e.value;
                             e.value = value;
                             return r;
                          }
                     else
                       {
                             e = e.next;
                          }
                   }

             // At this point, we know we need to add a new entry.
             modCount++;
             if (++size > threshold)
                   {
                    rehash();
                     // Need a new hash value to suit the bigger table.
                     idx = hash(key);
                   }

             e = new HashEntry<K, V>(key, value);

             e.next = buckets[idx];
             buckets[idx] = e;

             return null;
    }

    public V remove(Object key) {
        int idx = hash(key);
             HashEntry<K, V> e = buckets[idx];
             HashEntry<K, V> last = null;

             while (e != null)
                   {
                     if (e.key.equals(key))
                           {
                             modCount++;
                             if (last == null)
                                   buckets[idx] = e.next;
                             else
                              last.next = e.next;
                            size--;
                return e.value;
                           }
                    last = e;
                     e = e.next;
                   }
            return null;
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        final Map<K,V> addMap = (Map<K,V>) m;
             final Iterator<Map.Entry<K,V>> it = addMap.entrySet().iterator();
             while (it.hasNext())
                   {
                final Map.Entry<K,V> e = it.next();
                     // Optimize in case the Entry is one of our own.
                       if (e instanceof AbstractMap.SimpleEntry)
                          {
                             AbstractMap.SimpleEntry<? extends K, ? extends V> entry
                           = (AbstractMap.SimpleEntry<? extends K, ? extends V>) e;
                           put(entry.key, entry.value);
                           }
                     else
                       {
                             put(e.getKey(), e.getValue());
                          }
                   }
    }

    public void clear() {
        if (size > 0)
                  {
                     modCount++;
                     Arrays.fill(buckets, null);
                     size = 0;
                   }
    }

    public Set<K> keySet() {
        if (keys == null)
                   {
                     // Create a synchronized AbstractSet with custom implementations of
                     // those methods that can be overridden easily and efficiently.
                    Set<K> r = new AbstractSet<K>()
                     {
                           public int size()
                           {
                                 return size;
                               }

                          public Iterator<K> iterator()
                           {
                                 return new KeyIterator();
                               }

                           public void clear()
                         {
                                MyHashTable.this.clear();
                              }

                           public boolean contains(Object o)
                         {
                                if (o == null)
                                     return false;
                                 return containsKey(o);
                              }

                           public boolean remove(Object o)
                          {
                                 return MyHashTable.this.remove(o) != null;
                               }
                         };
                    // We must specify the correct object to synchronize upon, hence the
                    // use of a non-public API
                  keys = new Collections.SynchronizedSet<K>(this, r);
                }
           return keys;
         }


    public Collection<V> values() {
        if (values == null)
                   {
                     // We don't bother overriding many of the optional methods, as doing so
                     // wouldn't provide any significant performance advantage.
                     Collection<V> r = new AbstractCollection<V>()
                     {
                           public int size()
                           {
                                 return size;
                               }

                           public Iterator<V> iterator()
                           {
                                 return new ValueIterator();
                               }

                           public void clear()
                           {
                                 Hashtable.this.clear();
                               }
                         };
                     // We must specify the correct object to synchronize upon, hence the
                     // use of a non-public API
                   values = new Collections.SynchronizedCollection<V>(this, r);
                }
           return values;
    }

    public Set<Entry<K, V>> entrySet() {
        if (entries == null)
                   {
                     // Create an AbstractSet with custom implementations of those methods
                     // that can be overridden easily and efficiently.
                     Set<Map.Entry<K, V>> r = new AbstractSet<Map.Entry<K, V>>()
                     {
                           public int size()
                           {
                                 return size;
                               }

                           public Iterator<Map.Entry<K, V>> iterator()
                           {
                                 return new EntryIterator();
                               }

                          public void clear()
                           {
                                 MyHashTable.this.clear();
                               }

                           public boolean contains(Object o)
                           {
                                return getEntry(o) != null;
                               }

                           public boolean remove(Object o)
                           {
                                 HashEntry<K, V> e = getEntry(o);
                                 if (e != null)
                                       {
                                         MyHashTable.this.remove(e.key);
                                         return true;
                                       }
                                 return false;
                               }
                         };
                    // We must specify the correct object to synchronize upon, hence the
                    // use of a non-public API
                     entries = new Collections.SynchronizedSet<Map.Entry<K, V>>(this, r);
                  }
             return entries;
    }
    private int hash(Object key)
   {
            // Note: Inline Math.abs here, for less method overhead, and to avoid
            // a bootstrap dependency, since Math relies on native methods.
          int hash = key.hashCode() % buckets.length;
            return hash < 0 ? -hash : hash;
          }


          HashEntry<K, V> getEntry(Object o)
          {
            if (! (o instanceof Map.Entry))
              return null;
            K key = ((Map.Entry<K, V>) o).getKey();
            if (key == null)
              return null;

           int idx = hash(key);
            HashEntry<K, V> e = buckets[idx];
            while (e != null)
              {
                if (e.equals(o))
                  return e;
                e = e.next;
              }
            return null;
          }
    protected void rehash() {
        HashEntry<K, V>[] oldBuckets = buckets;

        int newcapacity = (buckets.length * 2) + 1;
        threshold = (int) (newcapacity * loadFactor);
        buckets = (HashEntry<K, V>[]) new HashEntry[newcapacity];

        for (int i = oldBuckets.length - 1; i >= 0; i--) {
            HashEntry<K, V> e = oldBuckets[i];
            while (e != null) {
                int idx = hash(e.key);
                HashEntry<K, V> dest = buckets[idx];

                if (dest != null) {
                    HashEntry next = dest.next;
                    while (next != null) {
                        dest = next;
                        next = dest.next;
                    }
                    dest.next = e;
                } else {
                    buckets[idx] = e;
                }

                HashEntry<K, V> next = e.next;
                e.next = null;
                e = next;
            }
        }
    }

           private class EntryEnumerator
       implements Enumeration<Entry<K,V>>
           {
             /** The number of elements remaining to be returned by next(). */
             int count = size;
             /** Current index in the physical hash table. */
             int idx = buckets.length;
             /**
 1227:      * Entry which will be returned by the next nextElement() call. It is
 1228:      * set if we are iterating through a bucket with multiple entries, or null
 1229:      * if we must look in the next bucket.
 1230:      */
            HashEntry<K, V> next;

             /**
 1234:      * Construct the enumeration.
 1235:      */
            EntryEnumerator()
             {
               // Nothing to do here.
             }

             /**
 1242:      * Checks whether more elements remain in the enumeration.
 1243:      * @return true if nextElement() will not fail.
 1244:      */
             public boolean hasMoreElements()
             {
               return count > 0;
             }

             /**
 1251:      * Returns the next element.
 1252:      * @return the next element
 1253:      * @throws NoSuchElementException if there is none.
 1254:      */
             public Map.Entry<K,V> nextElement()
             {
               if (count == 0)
                 throw new NoSuchElementException("Hashtable Enumerator");
               count--;
               HashEntry<K, V> e = next;

               while (e == null)
                 if (idx <= 0)
                   return null;
                 else
                   e = buckets[--idx];

               next = e.next;
               return e;
             }
           } // class EntryEnumerator

           private final class KeyEnumerator
       implements Enumeration<K>
   {
/**
 * This entry enumerator is used for most operations.  Only
 * <code>nextElement()</code> gives a different result, by returning just
 * the key rather than the whole element.
 */
private EntryEnumerator enumerator;

        /**
         * Construct a new KeyEnumerator
         */
        KeyEnumerator()
        {
        enumerator = new EntryEnumerator();
        }


/**
 * Returns true if the entry enumerator has more elements.
 *
 * @return true if there are more elements
 * @throws ConcurrentModificationException if the hashtable was modified
 */
public boolean hasMoreElements()
        {
        return enumerator.hasMoreElements();
        }

/**
 * Returns the next element.
 * @return the next element
 * @throws NoSuchElementException if there is none.
 */
public K nextElement()
        {
        HashEntry<K,V> entry = (HashEntry<K,V>) enumerator.nextElement();
        K retVal = null;
        if (entry != null)
        retVal = entry.key;
        return retVal;
        }
        } // class KeyEnumerator



private final class ValueEnumerator
        implements Enumeration<V>
{
    /**
     * This entry enumerator is used for most operations.  Only
     * <code>nextElement()</code> gives a different result, by returning just
     * the value rather than the whole element.
     */
    private EntryEnumerator enumerator;

    /**
     * Construct a new ValueEnumerator
     */
    ValueEnumerator()
    {
        enumerator = new EntryEnumerator();
    }


    /**
     * Returns true if the entry enumerator has more elements.
     *
     * @return true if there are more elements
     * @throws ConcurrentModificationException if the hashtable was modified
     */
    public boolean hasMoreElements()
    {
        return enumerator.hasMoreElements();
    }


    public V nextElement()
    {
        HashEntry<K,V> entry = (HashEntry<K,V>) enumerator.nextElement();
        V retVal = null;
        if (entry != null)
            retVal = entry.value;
        return retVal;
    }
}
}




