package me.nithanim.cultures.format.cif.type1;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import me.nithanim.cultures.format.cif.EncryptedInformation;
import me.nithanim.cultures.format.cif.Reader;

public class CifFileType1Reader implements Reader<EncryptedInformation> {
    @Override
    public EncryptedInformation unpack(ByteBuf buf) throws IOException {
        int numberOfEntries = buf.readInt();
        skipBytes(buf, 4 + 4);
        int indexLength = buf.readInt();
        ByteBuf encryptedIndexTable = buf.slice(buf.readerIndex(), indexLength * 4);
        skipBytes(buf, 2 + 4);
        int contentLength = buf.readInt();
        ByteBuf encryptedContentTable = buf.slice(buf.readerIndex(), contentLength);
        return new EncryptedInformation(numberOfEntries, indexLength, encryptedIndexTable, contentLength, encryptedContentTable);
    }

    private static void skipBytes(ByteBuf buf, int bytes) {
        buf.readerIndex(buf.readerIndex() + bytes);
    }
}
