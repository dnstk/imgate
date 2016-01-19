package tk.dnstk.imgate.agent.remote;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

public class ImgateRemoteClient {

    private static final Logger LOGGER = Logger.getLogger(ImgateRemoteClient.class.getName());

    private AgentConfig agentConfig;

    private String agentId;

    private String accountId;

    private URI remoteUri;

    public ImgateRemoteClient(AgentConfig agentConfig) {
        this.agentConfig = agentConfig;
        this.remoteUri = URI.create(agentConfig.getRemoteUri());
        // TODO
        this.accountId = "12345";
    }

    public void postSmtpMessage(SMTPMessage message) throws IOException {
        httpPost("accounts/" + accountId + "/messages", message.toJSON());
    }

    private void httpPost(String relativePath, String body) throws IOException {
        URL url = remoteUri.resolve(relativePath).toURL();
        LOGGER.info("post to " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        setHttpHeaders(connection);
        OutputStream postBody = connection.getOutputStream();
        try {
            postBody.write(body.getBytes());
            LOGGER.info("content: " + body);
            handleReturn(connection);
        } finally {
            close(postBody);
        }
    }

    private void handleReturn(HttpURLConnection connection) throws IOException {
        int ret = connection.getResponseCode();
        if (ret >= 400 && ret < 500) {
            // client side error
            throw new IOException("Invalid request for '" + connection.getURL() + "': " + ret
                    + " - " + connection.getResponseMessage());
        } else if (ret != HttpURLConnection.HTTP_OK) {
            String errorMsg = streamToString(connection.getErrorStream());
            throw new IOException("Get unexpected status code: " + ret
                    + ", with error message: \n" + errorMsg);
        }
    }

    private String streamToString(InputStream stream) throws IOException {
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        try {
            byte[] buf = new byte[1024];
            int len;
            while ((len = stream.read(buf)) != -1) {
                bao.write(buf, 0, len);
            }
            // TODO encoding
            return bao.toString();
        } finally {
            close(stream);
            close(bao);
        }
    }

    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void setHttpHeaders(HttpURLConnection connection) {
        // TODO property
        connection.setConnectTimeout(2000);
        connection.setReadTimeout(5000);
        // TODO
        connection.setRequestProperty("X-Auth-Token", "");
    }

}
