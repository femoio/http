package io.femo.http.drivers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

/**
 * Created by felix on 5/11/16.
 */
public class InputBuffer {

    /*private byte[] buffer;
    private int offset;
    private int bytes;*/
    private InputStream inputStream;

    public InputBuffer(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
    }

    public Byte getNextByte() throws IOException {
        return (byte) inputStream.read();
    }

    public byte[] get(int count) throws IOException {
        byte[] data = new byte[count];
        int tRead = 0;
        while (tRead < count) {
            byte[] pData = new byte[(count - tRead) % 256 == 0 ? 256 : (count - tRead) % 256];
            int read = inputStream.read(pData);
            System.arraycopy(pData, 0, data, tRead, read);
            tRead += read;
        }
        return data;
    }

    public String readUntil(byte terminator) throws IOException {
        return readUntil(terminator, 0);
    }

    public String readUntil(byte terminator, int skipAfter) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int data;
        while ((data = inputStream.read()) != terminator) {
            byteArrayOutputStream.write(data);
        }
        for (int i = 0; i < skipAfter; i++) {
            inputStream.read();
        }
        return byteArrayOutputStream.toString();
    }

    public void pipe(int length, OutputStream ... outputStreams) throws IOException {
        byte[] data = get(length);
        for (OutputStream outputStream : outputStreams) {
            outputStream.write(data);
        }
    }

}
