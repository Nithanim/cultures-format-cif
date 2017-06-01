package me.nithanim.cultures.format.cif;

import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import java.io.IOException;
import java.util.ArrayList;
import javax.activation.UnsupportedDataTypeException;
import me.nithanim.cultures.format.cif.type1.CifFileType1Codec;
import me.nithanim.cultures.format.cif.type2.CifFileType2Codec;

public class CifFileReader implements Reader<CifFile> {
    @Override
    public CifFile unpack(ByteBuf buf) throws IOException {
        int magic = buf.readInt();

        Codec<EncryptedInformation> codec;
        
        byte type;
        if (magic == 65601) {
            codec = new CifFileType1Codec();
            type = 1;
        } else if (magic == 1021) {
            codec = new CifFileType2Codec();
            type = 2;
        } else {
            throw new UnsupportedDataTypeException("The given data is not a cif file!");
        }

        EncryptedInformation inf = codec.unpack(buf);

        ByteBuf indexTable = inf.getEncryptedIndexTable();
        indexTable = indexTable.writerIndex(indexTable.capacity()).copy();
        indexTable.writerIndex(0);
        EncryptionUtil.decryptCommon(inf.getEncryptedIndexTable(), indexTable);
        indexTable.readerIndex(0);

        ByteBuf contentTable = inf.getEncryptedContentTable();
        contentTable = contentTable.writerIndex(contentTable.capacity()).copy();
        contentTable.writerIndex(0);
        EncryptionUtil.decryptCommon(inf.getEncryptedContentTable(), contentTable);
        contentTable.readerIndex(0);

        ArrayList<String> lines = new ArrayList<>();
        boolean isCifFile = isCifFile(indexTable, contentTable);
        while (indexTable.readableBytes() > 0) {
            int index = indexTable.readInt();

            contentTable.readerIndex(index);

            if (isCifFile) { //cif
                byte meta = contentTable.readByte();
                String line = javaStringFromCString(contentTable);
                if (meta == 1) {
                    lines.add('[' + line + ']');
                } else {
                    lines.add(line);
                }
            } else { //.tab or .sal
                String line = javaStringFromCString(contentTable);
                lines.add(line);
            }
        }
        indexTable.release();
        contentTable.release();
        return new CifFile(type, lines);
    }

    private boolean isCifFile(ByteBuf indexTable, ByteBuf contentTable) {
        //guessing the type based on 0x1 and 0x2
        int count = 0;
        indexTable.readerIndex(0);
        while (indexTable.readableBytes() > 0) {
            int i = indexTable.readInt();
            byte b = contentTable.getByte(i);
            if (1 == b || b == 2) {
                count++;
            }
        }
        indexTable.readerIndex(0);
        return count == indexTable.capacity() / 4;
    }

    protected String javaStringFromCString(ByteBuf buf) {
        int start = buf.readerIndex();
        int length = 0;
        for (; buf.readableBytes() > 0; length++) {
            byte b = buf.readByte();
            if (b == 0) {
                break;
            }
        }

        buf.readerIndex(start);
        byte[] arr = new byte[length];
        buf.readBytes(arr);
        return new String(arr, CharsetUtil.ISO_8859_1);
    }
}
