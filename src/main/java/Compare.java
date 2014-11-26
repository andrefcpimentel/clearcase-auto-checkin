import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Compare {

    public void getDiff(File dirA, File dirB) throws IOException {
        File[] fileList1 = dirA.listFiles();
        File[] fileList2 = dirB.listFiles();
        Arrays.sort(fileList1);
        Arrays.sort(fileList2);
        HashMap<String, File> map1;
        if (fileList1.length < fileList2.length) {
            map1 = new HashMap<>();
            for (int i = 0; i < fileList1.length; i++) {
                map1.put(fileList1[i].getName(), fileList1[i]);
            }

            compareNow(fileList2, map1);
        } else {
            map1 = new HashMap<>();
            for (int i = 0; i < fileList2.length; i++) {
                map1.put(fileList2[i].getName(), fileList2[i]);
            }
            compareNow(fileList1, map1);
        }
    }

    public void compareNow(File[] fileArr, HashMap<String, File> map) throws IOException {
        for (int i = 0; i < fileArr.length; i++) {
            File sourceFile = fileArr[i];
            String fName = sourceFile.getName();
            File destFile = map.get(fName);
            map.remove(fName);
            if (destFile != null) {
                if (destFile.isDirectory()) {
                    getDiff(sourceFile, destFile);
                } else {
                    String cSum1 = checksum(sourceFile);
                    String cSum2 = checksum(destFile);
                    if (!cSum1.equals(cSum2)) {

                        String srcPath = sourceFile.toString();
                        String destPath = destFile.toString();
                        System.out.println(srcPath);
                        System.out.println(destPath);
                        ProcessRunner.runCommand("cleartool", "checkout", "-nc", destPath);
                        ProcessRunner.runCommand("cmd", "/c", "copy /Y " + srcPath + " " + destPath);
                        ProcessRunner.runCommand("cleartool", "checkin", "-nc", destPath);
                        ProcessRunner.runCommand("cleartool", "describe", destPath);
                    }

                }
            } else {
                if (sourceFile.isDirectory()) {
                    traverseDirectory(sourceFile);
                }

            }
        }
        Set<String> set = map.keySet();
        Iterator<String> it = set.iterator();
        while (it.hasNext()) {
            String n = it.next();
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
        for (int k = 0; k < list.length; k++) {
            if (list[k].isDirectory()) {
                traverseDirectory(list[k]);
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
            for (int i = 0; i < digest.length; i++) {
                strDigest += Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1).toUpperCase();
            }
            return strDigest;
        } catch (Exception e) {
            return null;
        }
    }
}