import java.util.*;

public class Individual implements Client, Cloneable {
    private String title;
    private Account[] accounts;
    private int size;
    private int creditScore;

    public static final int SIXTEEN = 16;
    public static final int ZERO = 0;

    public Individual(String title) {
        this.title = title;
        this.accounts = new Account[SIXTEEN];
        this.size = ZERO;
        this.creditScore = ZERO;
    }

    public Individual(int size, String title) {
        this.title = title;
        this.accounts = new Account[size];
        this.size = ZERO;
        this.creditScore = ZERO;
    }

    public Individual(String title, Account[] accounts) {
        this.title = title;
        this.accounts = accounts;
        this.size = accounts.length;
        this.creditScore = ZERO;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean add(Account account) {
        Objects.requireNonNull(account);
        if (size == accounts.length) {
            extendArray();
            return false;
        } else {
            hideAdd(account);
            return true;
        }
    }

    private void hideAdd(Account account) {
        for (int i = 0; i < accounts.length; i++) {
            if (accounts[i] == null) {
                accounts[i] = account;
                size++;
                return;
            }
        }
    }

    private void extendArray() {
        Account[] buf = new Account[accounts.length * 2];
        System.arraycopy(accounts, 0, buf, 0, accounts.length);
        accounts = buf;
    }

    @Override
    public int getCreditScore() {
        return creditScore;
    }

    @Override
    public void addCreditScore(int creditScore) {
        this.creditScore += creditScore;
    }

    @Override
    public boolean add(Account account, int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Objects.requireNonNull(account);
        if (accounts[index] == null) {
            accounts[index] = account;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Account get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        return accounts[index];
    }

    @Override
    public Account get(String number) {
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        for (int i = 0; i < size; i++) {
            if (accounts[i].checkNumber(number)) return accounts[i];
        }
        return null;
    }

    @Override
    public boolean hasAccountWithNumber(String number) {
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        for (int i = 0; i < size; i++) {
            if (accounts[i].checkNumber(number)) return true;
        }
        return false;
    }

    @Override
    public Account set(Account account, int index) {
        Objects.requireNonNull(account);
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Account buf = accounts[index];
        accounts[index] = account;
        return buf;
    }

    @Override
    public Account remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Account buf = accounts[index];
        accounts[index] = null;
        for (int i = index; i < accounts.length - 1; i++) {
            Account tmp = accounts[i];
            accounts[i] = accounts[i + 1];
            accounts[i + 1] = tmp;
        }
        accounts[accounts.length] = null;
        size--;
        return buf;
    }

    @Override
    public Account remove(String number) {
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        return remove(indexOf(number));
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
        for (int i = 0; i < size; i++) {
            if (accounts[i].getNumber().equals(number)) return i;
        }
        return -1;
    }

    @Override
    public int indexOf(Account account) {
        Objects.requireNonNull(account);
        for (int i = 0; i < size; i++) {
            if (accounts[i].equals(account)) return i;
        }
        return -1;
    }

    //Возвращает общее число счетов
    @Override
    public int size() {
        return size;
    }

    //С учётом того что после каждого удаления массив сдвигается, нулов быть не должно
    @Override
    public Account[] toArray() {
        Account[] buf = new Account[size];
        System.arraycopy(accounts, 0, buf, 0, size);
        return buf;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Client\nname:");
        sb.append(title).append("\ncreditScore: ").append(creditScore);
        for (int i = 0; i < size; i++) {
            sb.append(accounts[i].toString()).append('\n');
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
        Individual that = (Individual) obj;
        return size == that.size &&
                creditScore == that.creditScore &&
                Objects.equals(title, that.title) &&
                Arrays.equals(accounts, that.accounts);
    }

    public Object clone() throws CloneNotSupportedException {
        Individual buf = new Individual(size, title);
        buf.creditScore = this.creditScore;
        Account[] bufAcs = new Account[accounts.length];
        System.arraycopy(accounts, 0, bufAcs, 0, size);
        buf.accounts = bufAcs;
        return buf;
    }

    @Override
    public Iterator<Account> iterator() {
        return new AccountIterator(accounts);
    }

    @Override
    public int compareTo(Client o) {
        if (getTotalBalance() > o.getTotalBalance()) return 1;
        if (getTotalBalance() < o.getTotalBalance()) return -1;
        else {
            return title.compareTo(o.getTitle());
        }
    }

    private class AccountIterator implements Iterator<Account> {
        private Account[] acs;
        private int targetIndex = 0;

        public AccountIterator(Account[] accounts) {
            acs = accounts;
        }

        @Override
        public boolean hasNext() {
            return acs[targetIndex] != null;
        }

        @Override
        public Account next() {
            Account buf = acs[targetIndex++];
            remove();
            return buf;
        }

        @Override
        public void remove() {
            acs[targetIndex] = null;
        }
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean contains(Object o) {
        for(int i = 0;i<size;i++){
            if(accounts[i].equals(o)) return true;
        }
        return false;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        Account[] accounts = (Account[]) a;
        System.arraycopy(this.accounts,0,accounts,0,size);
        return (T[]) accounts;
    }

    @Override
    public void clear() {
        this.accounts = new Account[SIXTEEN];
        size = 0;
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
            flag &= contains(iterator.next());
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
