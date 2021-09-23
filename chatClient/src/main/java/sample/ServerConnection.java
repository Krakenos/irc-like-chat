package sample;

import org.json.JSONObject;

import java.io.*;
import java.net.Socket;

/**
 * Klasa nawiązująca i obsługująca połączenie z serwerem
 */
class ServerConnection {
    private BufferedWriter bw;
    private BufferedReader br;

    ServerConnection() throws IOException {
        Socket socket = new Socket("localhost", 2555);
        this.bw = new BufferedWriter(new
                OutputStreamWriter(socket.getOutputStream()));
        this.br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * @param username Nazwa użytkownika
     * @param room Nazwa pierwszego pokoju do którego dołącza użytkownik
     * @throws IOException
     */
    void connect(String username, String room) throws IOException {
        String signature = "Login";
        send(username, signature, room);
    }

    /**
     * @param username Nazwa użytkownika
     * @param text Treść wiadomości
     * @param room Pokój do którego wiadomość ma zostać wysłana
     * @throws IOException
     */
    void sendMessage(String username, String text, String room) throws IOException {
        String signature = "Message";
        JSONObject message = new JSONObject();
        //System.out.println(username + " says " + text + " to the server");
        message.put("signature", signature);
        message.put("username", username);
        message.put("room", room);
        message.put("message", text);
        this.bw.write(message.toString());
        this.bw.newLine();
        this.bw.flush();
    }

    /**
     * Funkcja odpala nowy wątek ServerListener jako daemon, do nasłuchiwania wiadomości serwera
     * @param controller Kontroler głównego okna chatu
     * @throws IOException
     */
    void listener(ChatWindowController controller) throws IOException {
        ServerListener messageListener = new ServerListener(controller, this.br);
        messageListener.setDaemon(true);
        messageListener.start();
    }

    /**
     * Funkcja wywoływana w momencie zamknięcia okna chatu. Przekazuje serwerowi informacje że użytkownik się rozłączył
     * @param username Nazwa użytkownika
     * @param room Dowolny pokój
     * @throws IOException
     */
    void disconnect(String username, String room) throws IOException {
        String signature = "Logout";
        send(username, signature, room);
    }

    /**
     * @param username Nazwa użytkownika wysyłającego wiadomość
     * @param signature Sygnatura wiadomości którą odczytuje serwer
     * @param room Pokój do którego skierowana jest wiadomość
     * @throws IOException
     */
    private void send(String username, String signature, String room) throws IOException {
        JSONObject message = new JSONObject();
        message.put("signature", signature);
        message.put("username", username);
        message.put("room", room);
        this.bw.write(message.toString());
        this.bw.newLine();
        this.bw.flush();
    }

    /**
     * Wysyła informacje do serwera że użytkownik dołącza do nowego pokoju
     * @param username Nazwa użytkownika który dołącza do pokoju
     * @param roomName Nazwa pokoju do którego ma dołączyć
     * @throws IOException
     */
    void joinRoom(String username, String roomName) throws IOException {
        String signature = "Join";
        send(username, signature, roomName);
    }

    /**
     * Wysyła informacje do serwera z którego pokoju wychodzi użytkownik
     * @param username Nazwa użytkownika który wychodzi z pokoju
     * @param roomName Nazwa pokoju z którego wychodzi użytkownik
     * @throws IOException
     */
    void quitRoom(String username, String roomName) throws IOException {
        String signature = "Quit";
        send(username, signature, roomName);
    }
}
