import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;

public class Entity implements Client, Cloneable {
    private String title;
    private node head = new node(null, null);
    private int size = 0;
    private int creditScore;

    public Entity(String title) {
        this.title = title;
        this.creditScore = 0;
    }

    public Entity(String title, Account[] accounts) {
        this.title = title;
        for (Account account : accounts) {
            add(account);
        }
        this.creditScore = 0;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title);
    }

    @Override
    public int getCreditScore() {
        return creditScore;
    }

    @Override
    public void addCreditScore(int creditScore) {
        this.creditScore += creditScore;
    }

    //В целом это нормально, объект в памяти есть? - есть. К нему токо надо обратиться правильно
    //Но госпади, из-за желания уменьшить количество доп переменных
    //"связывание" выглядит почти как в порно
    @Override
    public boolean add(Account account) {
        Objects.requireNonNull(account);
        if (size == 0) {
            head.next = new node(head, account);
            head.next.next = head;
            head.prev = head.next;
        } else {
            node oldPrev = head.prev;
            head.prev = new node(oldPrev, account);
            head.prev.next = head;
            oldPrev.next = head.prev;
        }
        size++;
        return true;
    }

    @Override
    public boolean add(Account account, int index) {
        Objects.requireNonNull(account);
        if (index < 0) throw new IndexOutOfBoundsException();
        if (size == 0 || size < index) return add(account);
        else {
            //Такс, вот тут нюанс, голова как бы есть
            //но вот надо ли её при проходе считать
            //Это большой вопрос, поэтому тут так будет
            //Голова есть но мы про это забудем
            //И след элемент после головы будет нулевым #profit
            node buf = head.next;
            for (int i = 0; i < index - 1; i++) {
                buf = buf.next;
            }
            node prev = buf;
            node next = buf.next;
            prev.next = new node(prev, account);
            prev.next.next = next;//Просто чистое порно
            next.prev = prev.next;
            size++;
            return true;//Госпади, как же я хочу вставить в код картинку, надеюсь когда нибудь такое прикрутят
        }
    }

    @Override
    public Account get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        node buf = head.next;
        for (int i = 0; i < index; i++) {
            buf = buf.next;
        }
        return buf.value;
    }

    @Override
    public Account get(String number) {
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        node buf = head.next;
        while (buf.value != null) {
            if (buf.value.checkNumber(number)) return buf.value;
            buf = buf.next;
        }
        return null;
    }

    @Override
    public boolean hasAccountWithNumber(String number) {
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        for (Account account : this) {
            if (account.checkNumber(number)) return true;
        }
        return false;
    }

    @Override
    public Account set(Account account, int index) {
        Objects.requireNonNull(account);
        if (index < 0 || size <= index) throw new IndexOutOfBoundsException();
        node buf = head.next;
        for (int i = 0; i < index; i++) {
            buf = buf.next;
        }
        Account tmp = buf.value;
        buf.value = account;
        return tmp;
    }

    @Override
    public Account remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        if (size == 1) {
            Account buf = head.next.value;
            head = new node(null, null);
            size--;
            return buf;
        } else {
            node buf = head.next;
            for (int i = 0; i < index; i++) {
                buf = buf.next;
            }
            Account tmp = buf.value;
            node prev = buf.prev;
            node next = buf.next;
            prev.next = next;
            next.prev = prev;
            size--;
            return tmp;
        }
    }

    @Override
    public Account remove(String number) {
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        node buf = head.next;
        while (buf.value != null) {
            if (buf.value.checkNumber(number)) {
                Account tmp = buf.value;
                if (buf == head) {
                    head = new node(null, null);
                } else {
                    node prev = buf.prev;
                    node next = buf.next;
                    prev.next = next;
                    next.prev = prev;
                }
                size--;
                return tmp;
            }
        }
        return null;
    }

    @Override
    public boolean remove(Object o) {
        Account account = (Account) o;
        Objects.requireNonNull(account);
        if (indexOf(account) >= 0) {
            remove(indexOf(account));
            return true;
        } else return false;
    }

    @Override
    public int indexOf(String number) {
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        node buf = head.next;
        int index = 0;
        while (buf.value != null) {
            if (buf.value.checkNumber(number)) return index;
            buf = buf.next;
            index++;
        }
        return -1;
    }

    @Override
    public int indexOf(Account account) {
        Objects.requireNonNull(account);
        int index = 0;
        for(Account acc: this){
            if(acc.equals(account)) return index;
            index++;
        }
        return -1;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Account[] toArray() {
        Account[] buf = new Account[size];
        node tmp = head.next;
        for (int i = 0; i < size; i++) {
            buf[i] = tmp.value;
            tmp = tmp.next;
        }
        return buf;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Client\nname:");
        sb.append(title).append("\ncreditScore: ").append(creditScore);
        node buf = head.next;
        while (buf.value != null) {
            sb.append(buf.value.toString()).append('\n');
            buf = buf.next;
        }
        sb.append("total: ").append(getTotalBalance());
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int hash = title.hashCode() ^ creditScore;
        for(Account account:this){
            hash^=account.hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Entity that = (Entity) obj;
        boolean flag = size == that.size &&
                creditScore == that.creditScore &&
                Objects.equals(title, that.title);
        node buf = head.next;
        int index = 0;
        while (buf.value != null) {
            flag &= buf.value.equals(that.get(index++));
            buf = buf.next;
        }
        return flag;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Entity buf = new Entity(title);
        buf.creditScore = this.creditScore;
        node bufNode = head.next;
        while (bufNode.value != null) {
            buf.add(bufNode.value);
            bufNode = bufNode.next;
        }
        return buf;
    }

    @Override
    public int compareTo(Client o) {
        if (getTotalBalance() > o.getTotalBalance()) return 1;
        if (getTotalBalance() < o.getTotalBalance()) return -1;
        else {
            return title.compareTo(o.getTitle());
        }
    }

    @Override
    public Iterator<Account> iterator() {
        return new AccountIterator(head.next);
    }

    private class AccountIterator implements Iterator<Account> {
        private node acs;

        //Госпади ору, компилятор это читает ахахах))) --- (347 строка)
        //Ля, если б моя нода реализовывала итератор, внутри нёё другая нода хранилище
        //Сюда бы она скармиливалась, и госпади чтобы было

        //public node(node node){
        //  this.node = node;
        //}

        //просто ору))))))))))))))))))))))

        public AccountIterator(node node) {
            this.acs = node;
        }

        @Override
        public boolean hasNext() {
            return acs.value != null;
        }

        @Override
        public Account next() {
            Account buf = acs.value;
            acs = acs.next;
            return buf;
        }
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean contains(Object o) {
        Iterator iterator = iterator();
        while(iterator.hasNext()){
            if(iterator.next().equals(o)) return true;
        }
        return false;
    }

    @Override
    public void clear() {
        this.head = new node(null, null);
        size = 0;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Account[] accounts = (Account[]) a;
        Account[] buf = toArray();
        System.arraycopy(buf,0,accounts,0,size);
        return (T[])accounts;
    }

    @Override
    public boolean addAll(Collection<? extends Account> c) {
        Iterator iterator = c.iterator();
        while(iterator.hasNext()){
            add((Account) iterator.next());
        }
        return true;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        boolean flag = true;
        Iterator iterator = c.iterator();
        while(iterator.hasNext()){
            flag&=contains(iterator.next());
        }
        return flag;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean flag = false;
        Iterator iterator = c.iterator();
        while(iterator.hasNext()){
            flag|=remove(iterator.next());
        }
        return flag;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean flag = false;
        Iterator iterator = iterator();
        while(iterator.hasNext()){
            Account account = (Account) iterator.next();
            if(!c.contains(account)) flag|=remove(account);
        }
        return flag;
    }
}

class node {
    node next;
    node prev;
    Account value;

    node(node prev, Account value) {
        this.prev = prev;
        this.value = value;
    }
}
