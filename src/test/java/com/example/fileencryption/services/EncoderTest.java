package com.example.fileencryption.services;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


public class EncoderTest {

    Encoder encoder = new Encoder();

    @Test
    public void encode_simpleBinFile_returnCorrectHashCode() throws IOException, NoSuchAlgorithmException {
        URL url = Thread.currentThread().getContextClassLoader().getResource("zero.bin");
        byte[] hash = encoder.encode(url.getPath(), "/tmp/encodedFile.bin");
        assertEquals("e6e19106304b9665184e957d5dbf58ad36c55b52bf8d993dae4340c96bd8e81f",Utils.toHexString(hash));
        validateEncodedHashCode();
    }

    private void validateEncodedHashCode() throws IOException, NoSuchAlgorithmException {
        File f = new File("/tmp/encodedFile.bin");
        FileInputStream is = new FileInputStream(f);
        assertEquals("3857d5a53a726b96fe0de7f1b7bd0239c56aae4a5f9b9a75428cff6f850aeb56", Utils.toHexString(Utils.getHash(is.readAllBytes())));
        is.close();
    }

    @Test
    public void encode_fileDoesNotExist_hushIsNull() throws IOException, NoSuchAlgorithmException {
        assertNull(encoder.encode("fileDoesNotExist.bin", "/tmp/encodedFile.bin"));
    }
}