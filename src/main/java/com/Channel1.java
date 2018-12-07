package com;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class Channel1 {

    private static void ReadFile() throws Exception{
        RandomAccessFile randomAccessFile = new RandomAccessFile("data/nio-data.txt", "rw");
        FileChannel inChannel = randomAccessFile.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(1024);


        int bytesRead = inChannel.read(buffer);
        while (bytesRead != -1) {
            System.out.printf("read bytes: %d\n", bytesRead);

            System.out.printf("current pos: %d\n", buffer.position());

            byte[] arr = new byte[bytesRead];

            buffer.flip();
            System.out.printf("pos after flip: %d\n", buffer.position());
            buffer.get(arr);
            System.out.println("get data: \n" + new String(arr));
            System.out.println(" ---------------------------------");

            //buffer.rewind().limit(30);
            buffer.rewind();
            System.out.printf("pos after rewind: %d\n", buffer.position());
            arr = new byte[30];

            buffer.get(arr, 0, 30);
            System.out.println("get data: " + new String(arr));

            System.out.println(" ---------------------------------");

            arr = new byte[buffer.remaining()];
            System.out.println("buf remaining: " + buffer.remaining());
            ByteBuffer newBuffer = buffer.slice();
            System.out.println("buffer limit: " + buffer.limit());
            System.out.println("buffer pos: " + buffer.position());

            newBuffer.get(arr, 0, newBuffer.remaining());
            System.out.println("slice get data: \n" + new String(arr));

            System.out.println(" ---------------------------------");

            ByteBuffer dupBuffer = buffer.duplicate();
            System.out.println("dupBuffer limit: " + dupBuffer.limit());
            System.out.println("dupBuffer pos: " + dupBuffer.position());
            dupBuffer.rewind();
            byte[] dupArray = new byte[dupBuffer.remaining()];
            dupBuffer.get(dupArray);
            System.out.println("dupBuffer data: \n" + new String(dupArray));



//            while (buffer.hasRemaining()){
//                System.out.println((char) buffer.get());
//            }

            buffer.clear();

            bytesRead = inChannel.read(buffer);
        }
        randomAccessFile.close();
    }

    private static void ReadMappedFile() throws Exception{
        RandomAccessFile randomAccessFile = new RandomAccessFile("data/nio-data.txt", "rw");
        System.out.println("file size: "+randomAccessFile.length());
        FileChannel inChannel = randomAccessFile.getChannel();
        MappedByteBuffer mappedByteBuffer = inChannel.map(FileChannel.MapMode.READ_WRITE, 0, randomAccessFile.length());

//        while (mappedByteBuffer.hasRemaining()) {
//            System.out.println((char)mappedByteBuffer.get());
//        }
        byte[] newbytes = new byte[30];

        mappedByteBuffer.get(newbytes);

        mappedByteBuffer.rewind();

        mappedByteBuffer.get(newbytes);
        System.out.println("re get data: " + new String(newbytes));

        mappedByteBuffer.clear();

        randomAccessFile.close();
    }

    private static void writeFile() throws  Exception {

        RandomAccessFile outFile = new RandomAccessFile("data/nio-data-new.txt", "rw");
        FileChannel outChannel = outFile.getChannel();

        RandomAccessFile randomAccessFile = new RandomAccessFile("data/nio-data.txt", "rw");
        FileChannel inChannel = randomAccessFile.getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(200);
        buffer.clear();

        int bytesRead = inChannel.read(buffer);

        int lineNum = 0;
        while (bytesRead >= 0 || buffer.position() != 0) {

            lineNum++;
            StringBuilder line = new StringBuilder("line ");
            line.append(lineNum).append(": ");

            byte[] data = line.toString().getBytes();


            buffer.flip();
            buffer.put(data);
            outChannel.write(buffer);
            System.out.println("pos: " + buffer.position());
            buffer.compact();
            System.out.println("pos after compact: " + buffer.position());
            buffer.clear();
            bytesRead = inChannel.read(buffer);
        }

    }

    public static void main( String[] args ) throws Exception
    {

        //ReadFile();
        writeFile();
        //ReadMappedFile();
        System.out.println( "end of example!" );
    }
}
