package net.openmob.mobileimsdk.android.demo.client.event;

public interface ChatBaseEvent {
    void onLoginMessage(int paramInt1, int paramInt2);

    void onLinkCloseMessage(int paramInt);
}