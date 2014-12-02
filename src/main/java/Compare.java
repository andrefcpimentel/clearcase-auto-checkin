import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Compare {

    public void getDiff(File dirA, File dirB) throws IOException {
        File[] fileList1 = dirA.listFiles();
        File[] fileList2 = dirB.listFiles();
        assert fileList1 != null;
        assert fileList2 != null;

        Arrays.sort(fileList1);
        Arrays.sort(fileList2);

        HashMap<String, File> map;
        if (fileList1.length < fileList2.length) {
            map = new HashMap<>();
            for (File aFileList1 : fileList1) {
                map.put(aFileList1.getName(), aFileList1);
            }

            compareNow(fileList2, map);
        } else {
            map = new HashMap<>();
            for (File aFileList2 : fileList2) {
                map.put(aFileList2.getName(), aFileList2);
            }
            compareNow(fileList1, map);
        }
    }

    public void compareNow(File[] fileArr, HashMap<String, File> map) throws IOException {
        for (File sourceFile : fileArr) {
            String fName = sourceFile.getName();
            File destFile = map.get(fName);
            map.remove(fName);
            if (destFile != null) {
                if (destFile.isDirectory()) {
                    getDiff(sourceFile, destFile);
                } else {
                    if (sourceFile.length() != destFile.length()) {
                        String cSum1 = checksum(sourceFile);
                        String cSum2 = checksum(destFile);
                        if (!cSum1.equals(cSum2)) {

                            String srcPath = sourceFile.toString();
                            String destPath = destFile.toString();
                            ProcessRunner.runCommand("cleartool", "checkout", "-nc", destPath);
                            ProcessRunner.runCommand("cmd", "/c", "copy /Y " + srcPath + " " + destPath);
                            ProcessRunner.runCommand("cleartool", "checkin", "-nc", destPath);
                            ProcessRunner.runCommand("cleartool", "describe", "-s", destPath);
                        }
                    }
                }
            } else {
                if (sourceFile.isDirectory()) {
                    traverseDirectory(sourceFile);
                }

            }
        }
        Set<String> set = map.keySet();
        for (String n : set) {
            File fileFrmMap = map.get(n);
            map.remove(n);
            if (fileFrmMap.isDirectory()) {
                traverseDirectory(fileFrmMap);
            } else {
                System.out.println(fileFrmMap.getName() + "\t\t" + "only in " + fileFrmMap.getParent());
            }
        }
    }

    public void traverseDirectory(File dir) {
        File[] list = dir.listFiles();
        assert list != null;
        for (File aList : list) {
            if (aList.isDirectory()) {
                traverseDirectory(aList);
            }

        }
    }

    public String checksum(File file) {
        try {
            InputStream fin = new FileInputStream(file);
            java.security.MessageDigest md5er = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[1024];
            int read;
            do {
                read = fin.read(buffer);
                if (read > 0) {
                    md5er.update(buffer, 0, read);
                }
            } while (read != -1);
            fin.close();
            byte[] digest = md5er.digest();
            if (digest == null) {
                return null;
            }
            String strDigest = "0x";
            for (byte aDigest : digest) {
                strDigest += Integer.toString((aDigest & 0xff) + 0x100, 16).substring(1).toUpperCase();
            }
            return strDigest;
        } catch (Exception e) {
            return null;
        }
    }
}