package com.chat;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.adapter.ChatMessageAdapter;
import com.chat.database.GetDataFromDatabase;
import com.chat.database.InsertTable;
import com.connection.ConnectionDetector;
import com.custominterface.FooterAndActionbar;
import com.holder.ChildItem;
import com.holder.GroupItem;
import com.oxilo.applistyou.R;
import com.oxilo.applistyou.SplashActivity;
import com.oxilo.applistyou.StartUpActivity;
import com.oxilo.listyou.constant.AppConstant;
import com.response.User;
import com.util.SettingsDialog;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;
import org.jivesoftware.smackx.Form;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.jivesoftware.smackx.ReportedData;
import org.jivesoftware.smackx.ServiceDiscoveryManager;
import org.jivesoftware.smackx.search.UserSearchManager;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class XMPPClient extends Fragment {

    private ArrayList<String> messages = new ArrayList();
    private Handler mHandler = new Handler();
    private SettingsDialog mDialog;
    private EditText mRecipient;
    private EditText mSendText;
    private ListView mList;

    private Context mContext;
    private User user;
    private String userToChat, chatUserPic, mUserName;
    ArrayAdapter<String> adapter;
    private GroupItem groupItem;
    private FooterAndActionbar footerAndActionbar;
    private TextView Chat_person_name, chat_date;
    private Button send;
    InsertTable insertTable;
    private ImageView back_arrow;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;
    Message msg1, msg;
    Chat chat;
    private XMPPConnection connection;

    public XMPPClient(Context mContext, User user, String userToChat, String chatUserPic, String mUserName, FooterAndActionbar footerAndActionbar) {
        this.mContext = mContext;
        this.user = user;
        this.userToChat = userToChat;
        this.chatUserPic = chatUserPic;
        this.mUserName = mUserName;
        this.footerAndActionbar = footerAndActionbar;
//        Log.e("RECEIVER USER NAME", "" + mUserName);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragement_chat_window, container,
                false);
        init(rootView);
//        chat.configureXmppConnection(mContext, user);
        setCustomFont();
//        setConnection(connection);
        try {
            handleOfflineMessages(Singleton.getInstance().getConnection(), mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }

    private void setCustomFont() {
        mSendText.setTypeface(SplashActivity.mpRegular);
        send.setTypeface(SplashActivity.mpRegular);
        Chat_person_name.setTypeface(SplashActivity.mpBold);
        chat_date.setTypeface(SplashActivity.mpRegular);

        Chat_person_name.setText(mUserName);
        chat_date.setText(showCurrentDate());
    }

    private void init(View rootView) {
        cd = new ConnectionDetector(mContext);
        String to = userToChat + "@trendiser.com";
        msg1 = new Message(to, Message.Type.chat);
        insertTable = new InsertTable(StartUpActivity.databaseHelper.getReadableDatabase());
        footerAndActionbar.isActionBarToShowInFragement(false);
        groupItem = new GroupItem();
        Chat_person_name = (TextView) rootView.findViewById(R.id.contact_detail);

        chat_date = (TextView) rootView.findViewById(R.id.chat_date);
        mSendText = (EditText) rootView.findViewById(R.id.sendText);
        mList = (ListView) rootView.findViewById(R.id.listMessages);
        send = (Button) rootView.findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                ChildItem childItem = new ChildItem();
                String to = userToChat + "@trendiser.com";
                String text = mSendText.getText().toString();

                Log.e("XMPPClient", "Sending text [" + text + "] to [" + to + "]");
                 msg = new Message(to, Message.Type.chat);
//                msg.setBody(text);
                checkConnectivityAndSendMessage(msg, text);
//                Singleton.getInstance().getConnection().sendPacket(msg);

                messages.add(connection.getUser() + ":");
                messages.add(text);

                childItem.mChatSenderName = user.firstName;
                childItem.mChatText = text;
                childItem.mUserPicUrl = user.userPicUrl;
                childItem.mChatTextTime = getCurrentTime();
                childItem.mChatReceiverName = mUserName;
                childItem.mReceiverPicUrl = chatUserPic;
                groupItem.items.add(childItem);

                insertTable.addRowforChatTable(user.id, userToChat, childItem.mChatSenderName, childItem.mChatText, childItem.mUserPicUrl, childItem.mChatTextTime, showCurrentDate(), false, childItem.mChatReceiverName, childItem.mReceiverPicUrl);
                lastMessageFindOut();
                setListAdapter();

                mSendText.setText("");
            }
        });

        GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();
        if (getDataFromDatabase.checkForChatTables()) {
            groupItem = getDataFromDatabase.getAllRowsAsArrays(userToChat);
            setListAdapter();
        }

        back_arrow = (ImageView) rootView.findViewById(R.id.back_arrow);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getFragmentManager();
                fm.popBackStack();
            }
        });

        connection =  configureXmppConnection(mContext, user);
//        connection = Singleton.getInstance().getConnection();
//        setConnection();
    }

    private void lastMessageFindOut() {
        GetDataFromDatabase getDataFromDatabase = new GetDataFromDatabase();
        GroupItem updateGroupChatmessage = getDataFromDatabase.getAllRowsAsArrays(userToChat);
        for (int i = 0; i < updateGroupChatmessage.items.size(); i++) {
            ChildItem childItem = updateGroupChatmessage.items.get(i);
            if (i != updateGroupChatmessage.items.size() - 1) {
                insertTable.updateRowforChatTable(user.id, userToChat, childItem.mChatSenderName, childItem.mChatText, childItem.mUserPicUrl, childItem.mChatTextTime, showCurrentDate(), false, mUserName, chatUserPic);
            } else {
                insertTable.updateRowforChatTable(user.id, userToChat, childItem.mChatSenderName, childItem.mChatText, childItem.mUserPicUrl, childItem.mChatTextTime, showCurrentDate(), true, mUserName, chatUserPic);
            }
        }
    }

    /**
     * Called by Settings dialog when a connection is establised with the XMPP server
     *
     * @param
     */
    public void setConnection () {
        if (connection != null) {
            // Add a packet listener to get messages sent to us
            PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
            connection.addPacketListener(new PacketListener() {
                public void processPacket(Packet packet) {
                    Message message = (Message) packet;
                    if (message.getBody() != null) {
                        ChildItem childItem = new ChildItem();
                        String fromName = StringUtils.parseBareAddress(message.getFrom());
                        Log.e("XMPPClient", "Got text [" + message.getBody() + "] from [" + fromName + "]");
                        messages.add(fromName + ":");
                        messages.add(message.getBody());

                        childItem.mChatSenderName = userToChat;
                        childItem.mChatText = message.getBody();
                        childItem.mUserPicUrl = user.userPicUrl;
                        childItem.mChatTextTime = getCurrentTime();
                        childItem.mChatReceiverName = mUserName;
                        childItem.mReceiverPicUrl = chatUserPic;
                        groupItem.items.add(childItem);

                        insertTable.addRowforChatTable(user.id, userToChat, childItem.mChatSenderName, childItem.mChatText, childItem.mUserPicUrl, childItem.mChatTextTime, showCurrentDate(), false, childItem.mChatReceiverName, childItem.mReceiverPicUrl);
                        lastMessageFindOut();
                        // Add the incoming message to the list view
                        mHandler.post(new Runnable() {
                            public void run() {
                                setListAdapter();
                            }
                        });
                    }
                }
            }, filter);
        }
    }

    private void setListAdapter() {
//        adapter = new ArrayAdapter<String>(mContext,R.layout.multi_line_list_item,messages);
//        mList.setAdapter(adapter);

        ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(mContext, groupItem, user);
        mList.setAdapter(chatMessageAdapter);
    }

    private String showCurrentDate() {
        Calendar c = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd'th' MMM yyyy");
        String formattedDate = df.format(c.getTime());
//        Log.e("Current Date = ", formattedDate);

        return formattedDate;
    }

    private String getCurrentTime() {
        String time = "";
        String tr = "";
        if (new Time(System.currentTimeMillis()).getMinutes() < 10) {
            tr = "" + 0 + new Time(System.currentTimeMillis()).getMinutes();
        } else {
            tr = "" + new Time(System.currentTimeMillis()).getMinutes();
        }
        time = "" + new Time(System.currentTimeMillis()).getHours() + ":" + tr;
        return time;
    }

    private void checkConnectivityAndSendMessage(final Message msg, String text) {
        isInternetPresent = cd.isConnectingToInternet();
        // check for Internet status
        if (isInternetPresent) {
            // Internet Connection is Present
            // make HTTP requests
            if (connection.isConnected()) {
                Log.e("REC !222", "" + "BBBB");
                msg.setBody(text);
                connection.sendPacket(msg);
            } else {
                Log.e("REC !11", "" + "AAA");
                connection = configureXmppConnection(mContext, user);
//                connection.sendPacket(msg);
            }
        } else {
            // Internet connection is not present
            // Ask user to connect to Internet Connection present, please connect to internet first");
            Log.e("REC !333", "" + "CCC");
            msg1.setBody(text);
            Singleton.getInstance().setMsg(msg1);
        }
    }

    public void handleOfflineMessages(XMPPConnection connection, Context ctx) throws Exception {
        OfflineMessageManager offlineMessageManager = new OfflineMessageManager(connection);

        if (!offlineMessageManager.supportsFlexibleRetrieval()) {
            Log.d("O##QENQE!!!!", "Offline messages not supported");
            return;
        }

        if (offlineMessageManager.getMessageCount() == 0) {
            Log.d("THIS IS SWIREE", "No offline messages found on server");
        } else {
            Iterator<org.jivesoftware.smack.packet.Message> msgs = offlineMessageManager
                    .getMessages();
            while (msgs.hasNext()) {
                org.jivesoftware.smack.packet.Message msg = msgs.next();
                String fullJid = msg.getFrom();
                String bareJid = StringUtils.parseBareAddress(fullJid);
                String messageBody = msg.getBody();
                if (messageBody != null) {
                    Log.d("RETREIVED MESSAGE111", "Retrieved offline message from " + messageBody);
                    ChildItem childItem = new ChildItem();
                    childItem.mChatSenderName = userToChat;
                    childItem.mChatText = messageBody;
                    childItem.mUserPicUrl = chatUserPic;
                    childItem.mChatTextTime = getCurrentTime();
                    childItem.mChatReceiverName = mUserName;
                    childItem.mReceiverPicUrl = chatUserPic;
                    groupItem.items.add(childItem);

                    insertTable.addRowforChatTable(user.id, userToChat, childItem.mChatSenderName, childItem.mChatText, childItem.mUserPicUrl, childItem.mChatTextTime, showCurrentDate(), false, childItem.mChatReceiverName, childItem.mReceiverPicUrl);
                    lastMessageFindOut();
                    // Add the incoming message to the list view
                    mHandler.post(new Runnable() {
                        public void run() {
                            setListAdapter();
                        }
                    });
                }
            }
            offlineMessageManager.deleteMessages();
        }
    }

    private void RecieveMesssage() {
//        ConnectionConfiguration connConfig = new ConnectionConfiguration(
//                host, Integer.parseInt(port), service);
//        connConfig.setSASLAuthenticationEnabled(true);
//        connConfig.setSendPresence(false);
//        connection = new XMPPConnection(connConfig);
//        connection.connect();
//        connection.login(username, password);
        ServiceDiscoveryManager sdm = ServiceDiscoveryManager.getInstanceFor(connection);

////////////////////////////

        OfflineMessageManager offlineManager = new OfflineMessageManager(
                Singleton.getInstance().getConnection());
        try {
            Iterator<org.jivesoftware.smack.packet.Message> it = offlineManager
                    .getMessages();
            System.out.println(offlineManager.supportsFlexibleRetrieval());
            System.out.println("Number of offline messages:: " + offlineManager.getMessageCount());
            Map<String, ArrayList<Message>> offlineMsgs = new HashMap<String, ArrayList<Message>>();
            while (it.hasNext()) {
                org.jivesoftware.smack.packet.Message message = it.next();
                System.out
                        .println("receive offline messages, the Received from [" + message.getFrom()
                                + "] the message:" + message.getBody());
                String fromUser = message.getFrom().split("/")[0];

                if (offlineMsgs.containsKey(fromUser)) {
                    offlineMsgs.get(fromUser).add(message);
                } else {
                    ArrayList<Message> temp = new ArrayList<Message>();
                    temp.add(message);
                    offlineMsgs.put(fromUser, temp);
                }
            }
            // Deal with a collection of offline messages ...

//            Set<String> keys = offlineMsgs.keySet();
//            Iterator<String> offIt = keys.iterator();
//            while(offIt.hasNext())
//            {
//                String key = offIt.next();
//                ArrayList<Message> ms = offlineMsgs.get(key);
//                TelFrame tel = new TelFrame(key);
//                ChatFrameThread cft = new ChatFrameThread(key, null);
//                cft.setTel(tel);
//                cft.start();
//                for (int i = 0; i < ms.size(); i++) {
//                    tel.messageReceiveHandler(ms.get(i));
//                }
//            }
            offlineManager.deleteMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public XMPPConnection configureXmppConnection(final Context mContext, final User user) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration connConfig = new ConnectionConfiguration(AppConstant.CHAT_SERVER_HOST,
                        Integer.parseInt(AppConstant.CHAT_SERVER_PORT), "");
//                connConfig.setSendPresence(false);
//                connConfig.setReconnectionAllowed(false);
                connection = new XMPPConnection(connConfig);

                MyConnectionListener connectionListener = new MyConnectionListener(user,connection);
                try {
                    SmackAndroid.init(mContext);
                    connection.connect();
                    Singleton.getInstance().setConnection(connection);
                    if (!checkUser(connection,user));
                    Chat.createUser(connection, user);
                    loginUser(connection, user);
                    connection.addConnectionListener(new ConnectionListener() {
                        @Override
                        public void connectionClosed() {
                            Log.e("YCONENXCTIO CLOSED","" + "CONNECTION");
                            int status = 0;
                            Presence presence = new Presence(Presence.Type.unavailable);
                            presence.setStatus(connection.getUser() + status);
                            connection.sendPacket(presence);
                        }

                        @Override
                        public void connectionClosedOnError(Exception e) {

                        }

                        @Override
                        public void reconnectingIn(int i) {

                        }

                        @Override
                        public void reconnectionSuccessful() {
                            Log.e("YES SSS", "" + "123444444");
                            connection.sendPacket(msg);
                            int status = 1;
                            Presence presence = new Presence(Presence.Type.available);
                            presence.setStatus(connection.getUser() + status);
                            connection.sendPacket(presence);
                            setConnection();
                        }

                        @Override
                        public void reconnectionFailed(Exception e) {

                        }
                    });

                    Singleton.getInstance().setConnection(connection);

//                    loginUser(Singleton.getInstance().getConnection(), user);
                    Thread.sleep(3000);
                    Log.i("XMPPClient",
                            "[SettingsDialog] Connected to " + connection.getHost());
                } catch (XMPPException ex) {
                    Log.e("XMPPClient", "[SettingsDialog] Failed to connect to "
                            + connection.getHost());
                    Log.e("XMPPClient", ex.toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return connection;
    }

    private class MyConnectionListener implements ConnectionListener {
        private User user;
        private XMPPConnection connection;

        private MyConnectionListener(User user, XMPPConnection connection) {
            this.user = user;
            this.connection = connection;
        }

        @Override
        public void connectionClosed() {

        }

        @Override
        public void connectionClosedOnError(Exception e) {

        }

        @Override
        public void reconnectingIn(int i) {

        }

        @Override
        public void reconnectionSuccessful() {
            Log.e("SUCESS FULL IS CALLED", "YSS");
            loginUser(connection, user);
        }

        @Override
        public void reconnectionFailed(Exception e) {

        }
    }

    public void loginUser(XMPPConnection connection, User user) {
        try {

            connection.login(user.id, "12");
            Log.i("XMPPClient", "Logged in as " + connection.getUser());

            // Set the status to available
//            Presence presence = new Presence(Presence.Type.available);
//            connection.sendPacket(presence);

            int status = 1;
            Presence presence = new Presence(Presence.Type.available);
            presence.setStatus(connection.getUser() + status);
            connection.sendPacket(presence);

            setConnection();
        } catch (XMPPException ex) {
            Log.e("XMPPClient", "[SettingsDialog] Failed to log in as "
                    + user.listyouid);
            Log.e("XMPPClient", ex.toString());
        }
    }

    public  boolean checkUser(XMPPConnection mXMPPConnection, User user) {

        boolean isUser = false;
        UserSearchManager search = new UserSearchManager(mXMPPConnection);

        Form searchForm;
        try {
            searchForm = search.getSearchForm("search."
                    + mXMPPConnection.getServiceName());
            Form answerForm = searchForm.createAnswerForm();
            answerForm.setAnswer("Username", true);

            answerForm.setAnswer("search", user.id);

            org.jivesoftware.smackx.ReportedData data = search
                    .getSearchResults(answerForm,
                            "search." + mXMPPConnection.getServiceName());

            if (data.getRows() != null) {
                Iterator<ReportedData.Row> it = data.getRows();
                while (it.hasNext()) {
                    ReportedData.Row row = it.next();
                    Iterator iterator = row.getValues("jid");
                    if (iterator.hasNext()) {
                        isUser = true;
                        String value = iterator.next().toString();
                        Log.i("Iteartor values......", " " + value);
                    }
                    // Log.i("Iteartor values......"," "+value);
                }
            }
        } catch (XMPPException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return isUser;
    }

}
