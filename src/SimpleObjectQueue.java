import Exceptions.*;
public class SimpleObjectQueue 
{

  private Object[] queue;

  private int size = 10; // minimum size
  private int head = 0;
  private int tail = 0;
  private int count = 0;
  private String id = "";

  SimpleObjectQueue(String id) 
  {
    id = id;
    queue = new Object[size];
  }

  SimpleObjectQueue(String id, int n) 
  {
    id = id;
    if (n > 1) { size = n; }
    queue = new Object[size];
  }

  public int size() { return (size); }
  public int head() { return (head); }
  public int tail() { return (tail); }
  public int count() { return (count); }
  public String id() { return (new String(id)); }

  public boolean isEmpty() { return (count == 0); }
  public boolean isFull() { return (count == size); }

  public Object get(int n) 
  {
    return (n >= 0 && n < size ? queue[n] : null);
  }

  public String print() 
  {
    String s = String.format("%s -> head: %3d\n  tail: %3d\n count: %3d",
                             id, head, tail, count);
    return s;
  } // print()

  public void add(Object o) throws QueueFullException 
  {
    if (isFull()) { throw new QueueFullException(id); }
    else 
    {
      queue[tail] = o;
      ++count;
      if (count == 1) 
      { 
        head = tail; 
      }
      tail = (tail + 1) % size;
    }
  } // add()

  public Object remove() throws QueueEmptyException 
  {
    if (isEmpty()) { throw new QueueEmptyException(id); }
    else 
    {
      Object o = queue[head];
      queue[head] = null;
      --count;
      head = (head + 1) % size;
      return (o);
    }
  } // remove()

  public void delete(Object o) 
  { // overhead of holes in list
    if (o == null || count == 0) { return ; }

    int h;
    for (h = 0; h < size; ++h) 
    {
      if (queue[h] == o) 
      {
        queue[h] = null; // a hole!
        break; // h will hold the index of the hole
      }
    }

    // holes in the queue are inconvenient, so:
    // - fill hole (tidy the queue)
    // - update head, tail, and count
    if (count > 1) 
    {
      for (int n = h, p = n + 1;
           p != tail;
           n = (n + 1) % size, p = (n + 1) % size) 
      {
        if (queue[n] == null) 
        {
          // shuffle, fill in the hole
          queue[n] = queue[p]; // does not matter if queue[p] is null
          queue[p] = null;
        }
      }
      if (h == head) { head = (head + 1) % size; }
      if (h == tail) { tail = tail > 0 ? tail - 1 : size - 1; }
      --count;
    } // if (count > 1)
    else { head = tail = count = 0; }

  } // delete()

} // class SimpleObjectQueue