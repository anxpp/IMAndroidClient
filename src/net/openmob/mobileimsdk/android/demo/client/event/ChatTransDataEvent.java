package net.openmob.mobileimsdk.android.demo.client.event;

public interface ChatTransDataEvent {
    void onTransBuffer(String paramString1, int paramInt, String paramString2);

    void onErrorResponse(int paramInt, String paramString);
}