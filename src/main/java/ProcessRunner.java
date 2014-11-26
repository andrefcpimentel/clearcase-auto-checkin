/*
 * The MIT License (MIT)
 * Copyright (c) 2014 Baofeng Xue
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Baofeng
 *         6/25/2014.
 */
public class ProcessRunner {

    final static Logger logger = LoggerFactory.getLogger(ProcessRunner.class);

    public static void runCommand(String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        Process process = null;
        try {
            process = processBuilder.start();
            process.waitFor();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }


        if (process == null) {
            return;
        }

        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;

        try {
            while ((line = br.readLine()) != null) {
                logger.debug(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        runCommand("dir");
    }
}