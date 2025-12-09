import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhyphoxClient {
    private final String baseUrl;

    public PhyphoxClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Double fetchIlluminance() {
        try {
            URL url = new URL(baseUrl + "/get?illum");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);

            if (conn.getResponseCode() != 200) return null;

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) response.append(line);
            in.close();

            Pattern p = Pattern.compile("\"illum\"\\s*:\\s*\\{[^}]*\"buffer\"\\s*:\\s*\\[([0-9E+\\-.,\\s]+)\\]");
            Matcher m = p.matcher(response.toString());
            if (m.find()) {
                String[] values = m.group(1).split(",");
                return Double.parseDouble(values[values.length - 1].trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}