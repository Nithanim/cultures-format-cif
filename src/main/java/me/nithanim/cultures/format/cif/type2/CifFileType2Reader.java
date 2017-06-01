package me.nithanim.cultures.format.cif.type2;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import me.nithanim.cultures.format.cif.EncryptedInformation;
import me.nithanim.cultures.format.cif.Reader;

public class CifFileType2Reader implements Reader<EncryptedInformation> {
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
