import net.kyphone.JsonTransform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            String data = loadResource("sample.json");
            String template = loadResource("template.json");
            System.out.println(JsonTransform.transform(data, template));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String loadResource(String fileName) throws IOException {
        File file = new File(ClassLoader.getSystemResource(fileName).getFile());
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String data = "", line;
        while ((line = reader.readLine()) != null) data += line + "\n";
        return data;
    }
}
