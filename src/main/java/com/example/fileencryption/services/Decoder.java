package com.example.fileencryption.services;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Service
public class Decoder {

    private final int CHUNK_WITH_HASH_SIZE = 1056;

    public boolean decode(String encodedPath, byte[] h0) {

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(encodedPath);
            long fileLength = new File(encodedPath).length();
            byte[] hash = Arrays.copyOf(h0, h0.length);
            while (fileLength > 0) {
                byte[] chunkWithHash = getNextChunkBuffer(fileLength);
                inputStream.read(chunkWithHash);
                hash = validateAndHandleChunk(hash, chunkWithHash);
                fileLength -= CHUNK_WITH_HASH_SIZE;
            }
        } catch (IOException ioE) {
            // problem reading, handle case
            return false;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private byte[] getNextChunkBuffer(long fileSize) {
        return fileSize > CHUNK_WITH_HASH_SIZE ? new byte[CHUNK_WITH_HASH_SIZE] : new byte[(int) fileSize];
    }

    private byte[] validateAndHandleChunk(byte[] hash, byte[] chunkWithHash) throws NoSuchAlgorithmException {
        if(!Arrays.equals(hash, Utils.getHash(chunkWithHash))){
            throw new IllegalArgumentException("ERROR: The file contains invalid content");
        }
        //TODO do something with the chunk
        byte[] chunk = getChunk(chunkWithHash);
        return getHash(chunkWithHash);
    }

    private byte[] getChunk(byte[] chunkWithHash) {
        return Arrays.copyOfRange(chunkWithHash , 0, 1024);
    }

    private byte[] getHash(byte[] chunkWithHash) throws NoSuchAlgorithmException {
        return Arrays.copyOfRange(chunkWithHash , chunkWithHash.length-32, chunkWithHash.length);
    }


}
