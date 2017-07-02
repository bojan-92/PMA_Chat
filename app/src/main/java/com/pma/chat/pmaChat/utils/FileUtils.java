package com.pma.chat.pmaChat.utils;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {

    public static File createFile(File storageDir, String format) throws IOException {

        String imageFileName = createUniqueFileName(format);

        return File.createTempFile(
                imageFileName,
                "." + format,
                storageDir
        );
    }

    private static String createUniqueFileName(String format) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return format.toUpperCase() + "_" + timeStamp + "_";
    }

}
