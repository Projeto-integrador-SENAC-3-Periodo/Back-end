package br.edu.pe.senac.projeto_pi.service;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    private final OkHttpClient client = new OkHttpClient();

    public void enviarCredenciais(String nome, String email, String senhaProvisoria) {

        String json = """
        {
          "sender": {
            "name": "Sistema Senac",
            "email": "abigailmariagoncalvesnazario@gmail.com"
          },
          "to": [
            { "email": "%s" }
          ],
          "subject": "Acesso ao Sistema Senac",
          "htmlContent": "Olá %s,<br><br>Sua senha provisória é: <b>%s</b>"
        }
        """.formatted(email, nome, senhaProvisoria);

        Request request = new Request.Builder()
                .url("https://api.brevo.com/v3/smtp/email")
                .addHeader("api-key", apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.get("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("Email enviado: " + response.code());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
