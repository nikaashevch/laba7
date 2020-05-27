import java.util.*;

public class AccountManager {
    private Client[] clients;
    private int size;

    public AccountManager(int size) {
        this.clients = new Client[size];
        this.size = 0;
    }

    public AccountManager(Client[] clients) {
        this.clients = clients;
        this.size = clients.length;
    }

    public boolean add(Client client){
        Objects.requireNonNull(client);
        if(size==clients.length){
            extendArray();
            return false;
        }else{
            hideAdd(client);
            return true;
        }
    }

    private void extendArray(){
        Individual[] buf = new Individual[clients.length*2];
        System.arraycopy(clients,0,buf,0,clients.length);
        clients = buf;
    }

    private void hideAdd(Client client){
        for(int i = 0; i<clients.length;i++){
            if(clients[i]==null){
                clients[i] = client;
                size++;
                return;
            }
        }
    }

    public boolean add(Client client,int index){
        Objects.requireNonNull(client);
        if(index < 0 || index>=size) throw new IndexOutOfBoundsException();
        if(clients[index]==null){
            clients[index] = client;
            return true;
        }else{
            return false;
        }
    }

    public Client get(int index){
        if(index < 0 || index>=size) throw new IndexOutOfBoundsException();
        return clients[index];
    }

    public Client set(Client client, int index){
        Objects.requireNonNull(client);
        if(index < 0 || index>=size) throw new IndexOutOfBoundsException();
        Client buf = clients[index];
        clients[index] = client;
        if(buf == null && client != null) size++;
        return buf;
    }

    public Client remove(int index){
        if(index < 0 || index>=size) throw new IndexOutOfBoundsException();
        Client buf = clients[index];
        clients[index] = null;
        for(int i = index;i<clients.length-1;i++){
            Client tmp = clients[i];
            clients[i] = clients[i+1];
            clients[i+1] = tmp;
        }
        clients[clients.length] = null;
        size--;
        return buf;
    }

    public boolean remove(Client client){
        Objects.requireNonNull(client);
        if(indexOf(client)>=0){
            remove(indexOf(client));
            return true;
        }else return false;
    }

    public int indexOf(Client client){
        Objects.requireNonNull(client);
        for(int i = 0; i<size;i++){
            if(clients[i].equals(client)) return i;
        }
        return -1;
    }

    //Возвращат число физ. лиц
    public int size(){
        return size;
    }

    public Client[] getClients(){
        Client[] buf = new Client[size];
        System.arraycopy(clients,0,buf,0,size);
        return buf;
    }

    public ArrayList<Client> getSortedClientsByTotalBalance(){
        Client[] buf = getClients();
        Arrays.sort(buf);
        ArrayList<Client> clients = new ArrayList<>();
        for(Client client:buf){
            clients.add(client);
        }
        return clients;
    }

    public Account getAccountWithNumber(String number){
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        for(int i = 0; i<size;i++){
            if(clients[i].get(number)!=null){
                return clients[i].get(number);
            }
        }
        return null;
    }

    public Account removeAccount(String number){
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        for(int i = 0; i<size;i++){
            Account buf = clients[i].remove(number);
            if(buf!=null){
                return buf;
            }
        }
        return null;
    }

    public Account setAccount(Account account,String number){
        Objects.requireNonNull(account);
        Objects.requireNonNull(number);
        Utils.checkNumber(number);
        for(int i = 0;i<size;i++){
            if(clients[i].indexOf(number)>0){
                return clients[i].set(account,clients[i].indexOf(number));
            }
        }
        return null;
    }

    public Set<Client> getDebtors(){
        Set<Client> debtors = new HashSet<Client>();
        for(int i = 0; i<size;i++){
            if(clients[i].getCreditAccounts().size()!=0) debtors.add(clients[i]);
        }
        return debtors;
    }

    public Set<Client> getWickedDebtors(){
        Set<Client> debtors = new HashSet<Client>();
        for(int i = 0; i<size;i++){
            if(clients[i].getCreditAccounts().size()!=0 &&
                    clients[i].getStatus()==ClientStatus.BAD) debtors.add(clients[i]);
        }
        return debtors;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<size;i++){
            sb.append(clients[i].toString()).append('\n');
        }
        return sb.toString();
    }
}
