import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner; 

public class Client {
    private Socket socket; //used to establish connection between client and server
    private BufferedReader bufferedReader; // to read messages that have been sent from the client
    private BufferedWriter bufferedWriter; // to read messages that have been sent from the client
    private String clientUserName;    
    
    public Client(Socket socket,String clientUserName){
        try{
            this.socket=socket;
            this.clientUserName=clientUserName;
            this.bufferedWriter=new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName=clientUserName;
        }catch(IOException e){
            closeEverything(socket,bufferedReader,bufferedWriter);
        }
    }
    public void SendMessage(){
        try{
            bufferedWriter.write(clientUserName);
            bufferedWriter.newLine();
            bufferedWriter.flush(); 

            Scanner sc=new Scanner(System.in);
            while(socket.isConnected()){
                String message=sc.nextLine();
                bufferedWriter.write(clientUserName+" : "+message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        }catch(IOException e){
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    public void ListenForMessage(){
        new Thread(new Runnable() {
        @Override
        public void run() {
            String messageFromChat;
            while(socket.isConnected()){
                try{
                    messageFromChat=bufferedReader.readLine();
                    System.out.println(messageFromChat);
                }catch(IOException e){ 
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
            
            }
        }).start();;
}
    private void closeEverything(Socket s, BufferedReader br, BufferedWriter bw) {
        try{
            if(socket!=null) socket.close();
        if(br!=null) br.close();
        if(bw!=null) bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
public static void main(String[] args) throws UnknownHostException, IOException {
    Scanner scanner=new Scanner(System.in);
    System.out.println("Enter your username for the group chat : ");
    String username=scanner.nextLine();
    Socket s=new Socket("localhost",1234); 
    Client client=new Client(s, username);
    client.ListenForMessage();
    client.SendMessage();

    }
}
    

