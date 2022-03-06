package com.example.fileencryption.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.NoSuchAlgorithmException;

@Service
public class Encoder {

    public byte[] encode(String path, String encodedPath) {

        File reverseEncodedFile = new File("/tmp/reverseEncodedFile.bin");
        File file = new File(path);
        long bytesSize = file.length();

        RandomAccessFile raf = null;
        FileOutputStream fos = null;

        byte[] hash = null;

        try {
            raf = new RandomAccessFile(path, "r");
            int lastChunkSize = (int) bytesSize%1024;
            byte[] chunk = new byte[lastChunkSize];
            long pointer = bytesSize-lastChunkSize;
            fos = new FileOutputStream(reverseEncodedFile);

           hash = handleFirstChunk(raf, pointer, chunk, fos);

            chunk = new byte[1024];
            while (pointer > -1) {
                pointer -= 1024;
                hash = handleNextChunk(raf, pointer, chunk, hash, fos);
            }
            //this is not mandatory, there is an option to read the file from the end on decoding
            reversFile(reverseEncodedFile, encodedPath);
            reverseEncodedFile.delete();
        } catch (IOException ioE) {
            // problem reading, handle case
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                closeFilesStreams(raf, fos);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return hash;
    }

    private void closeFilesStreams(RandomAccessFile raf, FileOutputStream fos) throws IOException {
        if(raf !=null) {
            raf.close();
        }
        if(fos != null) {
            fos.close();
        }
    }

    private byte[] handleFirstChunk(RandomAccessFile raf, long pointer, byte[] chunk, FileOutputStream fos) throws IOException, NoSuchAlgorithmException {
        raf.seek(pointer);
        raf.read(chunk);
        fos.write(chunk);
        return Utils.getHash(chunk);
    }

    private byte[] handleNextChunk(RandomAccessFile raf, long pointer, byte[] chunk, byte[] hash, FileOutputStream fos) throws IOException, NoSuchAlgorithmException {
        raf.seek(pointer);
        raf.read(chunk);

        byte[] chunkWithHash = new byte[1056];
        System.arraycopy(chunk, 0, chunkWithHash, 0, chunk.length);
        System.arraycopy(hash, 0, chunkWithHash, chunk.length, hash.length);
        fos.write(chunkWithHash);
        return Utils.getHash(chunkWithHash);
    }

    private void reversFile(File reverseEncodedFile, String encodedPath) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(reverseEncodedFile.getPath(), "r");
        FileOutputStream fos = new FileOutputStream(encodedPath);
        long pointer = reverseEncodedFile.length();
        while (pointer > 0) {
            byte[] chunk = pointer >=1056? new byte[1056] : new byte[(int) pointer];
            pointer-=chunk.length;
            raf.seek(pointer);
            raf.read(chunk);
            fos.write(chunk);
        }
        raf.close();
        fos.close();
    }



}
