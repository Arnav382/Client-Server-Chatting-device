import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers=new ArrayList<ClientHandler>();
    private Socket socket; //used to establish connection between client and server
    private BufferedReader bufferedReader; // to read messages that have been sent from the client
    private BufferedWriter bufferedWriter; // to read messages that have been sent from the client
    private String clientUserName;

    public ClientHandler(Socket socket){
        try{
            this.socket=socket; // initialising the socket connection between client and server 
            
            //stream of output messages
            this.bufferedWriter= new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
            this.bufferedReader=new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUserName=bufferedReader.readLine();
            clientHandlers.add(this); //adding the constructer to the arraylist 
            broadcastMessage("SERVER: "+ clientUserName+" has entered the chat");
    } catch(IOException e){
        closeEverything(socket,bufferedReader,bufferedWriter);}
}
    @Override
    public void run() {
        String messageFromClient;
        while(socket.isConnected()){
            try{
                messageFromClient=bufferedReader.readLine();
                broadcastMessage(messageFromClient);
            } catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }
    public void broadcastMessage(String message){
        for(ClientHandler client:clientHandlers){
            try{
                if(!client.clientUserName.equals(clientUserName)){
                      client.bufferedWriter.write(message);
                      client.bufferedWriter.newLine();
                      client.bufferedWriter.flush();
                }
            } catch(IOException e){
                closeEverything(socket,bufferedReader,bufferedWriter);
                break;
            }
        }
    }

    private void closeEverything(Socket socket, BufferedReader bf, BufferedWriter bw) {
        RemoveClientHandler();
        try {
        if(socket!=null) socket.close();
        if(bf!=null) bf.close();
        if(bw!=null) bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }}
    public void RemoveClientHandler(){
        clientHandlers.remove(this);
        System.out.println("SERVER : "+clientUserName+" has left the chat");
    }
}
