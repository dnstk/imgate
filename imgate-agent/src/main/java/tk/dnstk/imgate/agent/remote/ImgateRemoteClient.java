package tk.dnstk.imgate.agent.remote;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.logging.Logger;

public class ImgateRemoteClient {

    private static final Logger LOGGER = Logger.getLogger(ImgateRemoteClient.class.getName());

    private AgentConfig agentConfig;

    private URI remoteUri;

    private AgentToken token;

    public ImgateRemoteClient(AgentConfig agentConfig) {
        this.agentConfig = agentConfig;
        this.remoteUri = URI.create(agentConfig.getRemoteUri());
    }

    public void authenticate() throws IOException {
        AgentAccess access = new AgentAccess();
        access.setAccessId(agentConfig.getAgentId());
        access.setAccessSecret(agentConfig.getAgentSecret());
        this.token = AgentToken.fromJSON(httpPost("auth/tokens", access.toJSON()));
    }

    public void postSmtpMessage(SMTPMessage message) throws IOException {
        httpPost("accounts/" + token.getAccountId() + "/messages", message.toJSON());
    }

    private String httpPost(String relativePath, String body) throws IOException {
        URL url = remoteUri.resolve(relativePath).toURL();
        LOGGER.info("post to " + url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        setHttpHeaders(connection);
        OutputStream postBody = connection.getOutputStream();
        try {
            postBody.write(body.getBytes());
            LOGGER.info("content: " + body);
            return handleReturn(connection);
        } finally {
            close(postBody);
        }
    }

    private String handleReturn(HttpURLConnection connection) throws IOException {
        int ret = connection.getResponseCode();
        if (ret >= 400 && ret < 500) {
            String errorMsg = streamToString(connection.getErrorStream());
            // client side error
            throw new IOException("Invalid request for '" + connection.getURL() + "': " + ret
                    + " - " + errorMsg);
        } else if (ret != HttpURLConnection.HTTP_OK) {
            String errorMsg = streamToString(connection.getErrorStream());
            throw new IOException("Get unexpected status code: " + ret
                    + ", with error message: \n" + errorMsg);
        } else {
            return streamToString(connection.getInputStream());
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
        if (token != null) {
            connection.setRequestProperty("X-Imgate-Token", token.getTokenId());
        }
    }

}
