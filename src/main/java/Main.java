import biz.kytech.JsonTransform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            String dataPath = args[0];
            String tmplPath = args[1];
            String data = loadFile(dataPath);
            String tmpl = loadFile(tmplPath);
            System.out.println(JsonTransform.transform(data, tmpl));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadFile(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String data = "", line;
        while ((line = reader.readLine()) != null) data += line + "\n";
        return data;
    }
}
