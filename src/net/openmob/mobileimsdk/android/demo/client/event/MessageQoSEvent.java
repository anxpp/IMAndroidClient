package net.openmob.mobileimsdk.android.demo.client.event;

import net.openmob.mobileimsdk.android.demo.client.message.Message;

import java.util.ArrayList;

public interface MessageQoSEvent {
    void messagesLost(ArrayList<Message> paramArrayList);

    void messagesBeReceived(String paramString);
}