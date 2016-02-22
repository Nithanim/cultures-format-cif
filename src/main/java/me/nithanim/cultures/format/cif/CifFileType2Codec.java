package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import me.nithanim.cultures.format.common.Codec;

public class CifFileType2Codec implements Codec<EncryptedInformation> {
    @Override
    public EncryptedInformation unpack(ByteBuf buf) throws IOException {
        skipBytes(buf, 4 + 4);
        int numberOfEntries = buf.readInt();
        skipBytes(buf, 5 * 4);
        int indexLength = buf.readInt();
        ByteBuf encryptedIndexTable = buf.slice(buf.readerIndex(), indexLength);
        skipBytes(buf, indexLength);
        skipBytes(buf, 1 + 4 + 4);
        int contentLength = buf.readInt();
        ByteBuf encryptedContentTable = buf.slice(buf.readerIndex(), contentLength);
        return new EncryptedInformation(numberOfEntries, indexLength, encryptedIndexTable, contentLength, encryptedContentTable);
    }

    private static void skipBytes(ByteBuf buf, int bytes) {
        buf.readerIndex(buf.readerIndex() + bytes);
    }
}
