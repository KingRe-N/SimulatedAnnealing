public class CircularLinkedList
{
    public int size =0;
    public Node head=null;
    public Node tail=null;

    public static CircularLinkedList copy(CircularLinkedList original)
    {
        CircularLinkedList temp = new CircularLinkedList();
        for(int i = 1; i < original.getSize() + 1; i++)
        {
            temp.addNodeAtEnd(original.elementAt(i));
        }
        return temp;
    }

    public void addNodeAtStart(int data)
    {
        Node n = new Node(data);
        if(size==0)
        {
            head = n;
            tail = n;
            n.next = head;
        }else
        {
            Node temp = head;
            n.next = temp;
            head = n;
            tail.next = head;
        }
        size++;
    }

    public void addNodeAtEnd(int data)
    {
        if(size==0)
        {
            addNodeAtStart(data);
        }else
        {
            Node n = new Node(data);
            tail.next =n;
            tail=n;
            tail.next = head;
            size++;
        }
    }

    public void deleteNodeFromStart()
    {
        if(size==0)
        {
            System.out.println("\nList is Empty");
        }else
        {
            System.out.println("\ndeleting node " + head.data + " from start");
            head = head.next;
            tail.next=head;
            size--;
        }
    }

    public void updateNode(int index,int val)
    {
        Node n = head;
        while(index - 1 !=0)
        {
            n=n.next;
            index--;
        }
        n.data = val;
    }

    public int elementAt(int index)
    {
        if(index>size)
        {
            return -1;
        }
        Node n = head;
        while(index-1!=0)
        {
            n=n.next;
            index--;
        }
        return n.data;
    }

    public void print()
    {
        Node temp = head;
        if(size<=0)
        {
            System.out.print("List is empty");
        }else
        {
            do
            {
                System.out.print(" " + temp.data);
                temp = temp.next;
            }
            while(temp!=head);
        }
    }

    public int getSize()
    {
        return size;
    }

}

class Node
{
    int data;
    Node next;
    public Node(int data)
    {
        this.data = data;
    }
}
