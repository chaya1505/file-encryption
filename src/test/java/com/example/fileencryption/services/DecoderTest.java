package com.example.fileencryption.services;

import org.junit.jupiter.api.Test;

import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class DecoderTest {

    Decoder decoder = new Decoder();

    @Test
    public void decode_correctEncodedFile_success(){
        byte[] h0 = Utils.hexStringToByteArray("e6e19106304b9665184e957d5dbf58ad36c55b52bf8d993dae4340c96bd8e81f");
        URL url = Thread.currentThread().getContextClassLoader().getResource("encodedFile.bin");
        boolean res = decoder.decode(url.getPath(), h0 );
        assertTrue(res);
    }

    @Test
    public void decode_incorrectH0_shouldGetIllegalArgumentException(){
        byte[] h0 = Utils.hexStringToByteArray("e6e19106304b9665184e957d5dbf58ad36c55b52bf8d993dae4340c96bd8e777");
        URL url = Thread.currentThread().getContextClassLoader().getResource("encodedFile.bin");

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> decoder.decode(url.getPath(), h0 ),
                "Expected doThing() to throw, but it didn't"
        );

        assertEquals(thrown.getMessage(),"ERROR: The file contains invalid content");
    }

    @Test
    public void decode_incorrectEncodedFile_shouldGetIllegalArgumentException(){
        byte[] h0 = Utils.hexStringToByteArray("e6e19106304b9665184e957d5dbf58ad36c55b52bf8d993dae4340c96bd8e81f");
        URL url = Thread.currentThread().getContextClassLoader().getResource("zero.bin");

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> decoder.decode(url.getPath(), h0 ),
                "Expected doThing() to throw, but it didn't"
        );

        assertEquals(thrown.getMessage(),"ERROR: The file contains invalid content");
    }

    @Test
    public void decode_fileDoesNotExist_returnFalse(){
        byte[] h0 = Utils.hexStringToByteArray("e6e19106304b9665184e957d5dbf58ad36c55b52bf8d993dae4340c96bd8e81f");
        boolean res = decoder.decode("fileDoesNotExist.bin", h0 );
        assertFalse(res);
    }

}