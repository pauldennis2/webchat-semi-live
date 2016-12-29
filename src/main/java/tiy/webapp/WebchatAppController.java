package tiy.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.BindException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by erronius on 12/29/2016.
 */

@Controller
public class WebchatAppController {

    List<ReceivedChat> chatLog = new ArrayList<>();

    /*private SimpleServer myServer;
    private SimpleClient myClient;
    private String testMessage = "sample test message";
    private final String ECHO_TOKEN = "ECHO::";
    private final String SERVER_IP = "127.0.0.1";
    private final int SERVER_PORT = 8005;*/

    private final String SERVER_IP = "127.0.0.1";

    private String firstUser;
    private String secondUser;

    private SimpleClient firstUserClient;
    private SimpleServer firstUserServer;

    private SimpleClient secondUserClient;
    private SimpleServer secondUserServer;

    private final int FIRST_SERVER_PORT = 8005;
    private final int SECOND_SERVER_PORT = 8006;

    boolean listInit = false;

    Map<String, List<ReceivedChat>> chatLogMap;

    /*
    public WebchatAppController () {
        chatLog = new ArrayList<ReceivedChat>();
        chatLogMap = new HashMap<String, List<ReceivedChat>>();

        try {
            myServer = new SimpleServer(SimpleServer.PORT_NUMBER, chatLog);
            myServer.startServer();
            myClient = new SimpleClient(SERVER_IP, SERVER_PORT);
        } catch (BindException ex) {
            try {
                myServer = new SimpleServer(SimpleServer.PORT_NUMBER + 1, chatLog);
                myServer.startServer();
                myClient = new SimpleClient(SERVER_IP, SERVER_PORT + 1);
            } catch (BindException ex2) {
                System.out.println("We're in deep stuff, up stuff creek without a paddle");
            }
        }
    }*/

    public WebchatAppController () {
        chatLogMap = new HashMap<String, List<ReceivedChat>>();
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public String loginPage () {
        return "login";
    }

    @RequestMapping(path = "/login", method = RequestMethod.POST)
    public String login (HttpSession session, String userName) {
        session.setAttribute("userName", userName);
        chatLogMap.put(userName, new ArrayList<ReceivedChat>());

        if (firstUserServer == null) {
            firstUser = userName;
            System.out.println("Setting up " + userName + " as first user");
            firstUserServer = new SimpleServer(FIRST_SERVER_PORT, chatLogMap.get(userName));
            firstUserServer.startServer();
            secondUserClient = new SimpleClient(SERVER_IP, FIRST_SERVER_PORT);
        } else {
            secondUser = userName;
            System.out.println("Setting up " + userName + " as second user");
            secondUserServer = new SimpleServer(SECOND_SERVER_PORT, chatLogMap.get(userName));
            secondUserServer.startServer();
            firstUserClient = new SimpleClient(SERVER_IP, SECOND_SERVER_PORT);
        }


        return "redirect:/chat";
    }

    @RequestMapping(path = "/chat", method = RequestMethod.GET)
    public String home (Model model, HttpSession session) {
        String userName = (String) session.getAttribute("userName");
        //model.addAttribute("name", session.getAttribute("userName"));
        model.addAttribute("name", userName);
        if (!listInit) {
            chatLog.add(new ReceivedChat("Message 1"));
            chatLog.add(new ReceivedChat("Message 2"));
            chatLog.add(new ReceivedChat("Get more creative"));
            listInit = true;
        }
        //chatLogMap.get(userName).add(new ReceivedChat("Your favorite food is chicken"));
        model.addAttribute("chatLog", chatLogMap.get(userName));
        return "index";
    }

    @RequestMapping(path = "/refresh-chat", method = RequestMethod.POST)
    public String refreshChat () {
        chatLog.add(new ReceivedChat("Refreshed one more time"));
        return "redirect:/chat";
    }

    @RequestMapping(path = "/send-chat", method = RequestMethod.POST)
    public String sendChat (String chatText, HttpSession session) {
        try {
            String userName = (String) session.getAttribute("userName");
            System.out.println("Sending message " + chatText + " from " + userName);
            if (userName.equals(firstUser)) {
                firstUserClient.sendMessage(chatText);
            } else {
                secondUserClient.sendMessage(chatText);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "redirect:/chat";
    }
}
