/*
 * The MIT License (MIT)
 * Copyright (c) 2014 Baofeng Xue
 */


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author Baofeng
 *         6/25/2014.
 */
public class ProcessRunner {


    public static void runCommand(String... command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true);

        String test = System.getProperty("test");
        String debug = System.getProperty("debug");

        if ("true".equals(debug)) {
            for (String s : command) {
                System.out.print(s + " ");
            }
            System.out.println();
        }


        if ("true".equals(test)) {
            return;
        }

        Process process = null;
        try {
            process = processBuilder.start();
            process.waitFor();

        } catch (Exception e) {
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
                if (line.contains("version")) {
                    System.out.println(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        runCommand("dir");
    }
}
