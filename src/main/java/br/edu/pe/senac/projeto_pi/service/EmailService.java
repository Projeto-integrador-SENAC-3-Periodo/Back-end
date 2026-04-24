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

    @Value("${brevo.sender.email}")
    private String senderEmail;

    private static final String SENDER_NAME = "Sistema Senac";
    private final OkHttpClient client = new OkHttpClient();

    // ─── Credenciais de acesso ────────────────────────────────────

    public void enviarCredenciais(String nome, String email, String senhaProvisoria) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
              <h2 style="color:#003399">Bem-vindo ao Sistema Senac</h2>
              <p>Olá, <strong>%s</strong>!</p>
              <p>Sua conta foi criada. Use as credenciais abaixo para acessar o sistema:</p>
              <table style="background:#f4f4f4;padding:16px;border-radius:8px;width:100%%">
                <tr><td><strong>Email:</strong></td><td>%s</td></tr>
                <tr><td><strong>Senha provisória:</strong></td><td style="color:#cc0000">%s</td></tr>
              </table>
              <p style="color:#888;font-size:12px;margin-top:24px">
                Por segurança, altere sua senha no primeiro acesso.
              </p>
            </div>
            """.formatted(nome, email, senhaProvisoria);

        enviar(email, "Acesso ao Sistema Senac", html);
    }

    // ─── Atividade APROVADA ───────────────────────────────────────

    /**
     * Envia email ao aluno informando que sua atividade foi APROVADA.
     *
     * @param nomeAluno       nome do aluno
     * @param emailAluno      email do aluno
     * @param tituloAtividade título da atividade aprovada
     * @param horasAprovadas  quantidade de horas creditadas
     * @param horasLimite     teto de horas do curso
     */
    public void enviarAprovacaoAtividade(String nomeAluno,
                                         String emailAluno,
                                         String tituloAtividade,
                                         int horasAprovadas,
                                         int horasLimite) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
              <div style="background:#28a745;padding:16px;border-radius:8px 8px 0 0">
                <h2 style="color:#fff;margin:0">✅ Atividade Aprovada</h2>
              </div>
              <div style="border:1px solid #ddd;border-top:none;padding:24px;border-radius:0 0 8px 8px">
                <p>Olá, <strong>%s</strong>!</p>
                <p>Sua atividade foi <strong style="color:#28a745">APROVADA</strong> pelo coordenador.</p>
                <table style="width:100%%;background:#f9f9f9;padding:16px;border-radius:8px;border-collapse:collapse">
                  <tr>
                    <td style="padding:8px;color:#555"><strong>Atividade:</strong></td>
                    <td style="padding:8px">%s</td>
                  </tr>
                  <tr>
                    <td style="padding:8px;color:#555"><strong>Horas creditadas:</strong></td>
                    <td style="padding:8px;color:#28a745;font-weight:bold">%dh</td>
                  </tr>
                  <tr>
                    <td style="padding:8px;color:#555"><strong>Limite do curso:</strong></td>
                    <td style="padding:8px">%dh</td>
                  </tr>
                </table>
                <p style="margin-top:20px">
                  Acesse o sistema para acompanhar seu progresso de horas complementares.
                </p>
                <p style="color:#888;font-size:12px;margin-top:24px">
                  Este é um email automático do Sistema Senac. Não responda.
                </p>
              </div>
            </div>
            """.formatted(nomeAluno, tituloAtividade, horasAprovadas, horasLimite);

        enviar(emailAluno, "✅ Atividade Aprovada — " + tituloAtividade, html);
    }

    // ─── Atividade REPROVADA ──────────────────────────────────────

    /**
     * Envia email ao aluno informando que sua atividade foi REPROVADA,
     * incluindo o motivo e orientação para reenvio.
     *
     * @param nomeAluno        nome do aluno
     * @param emailAluno       email do aluno
     * @param tituloAtividade  título da atividade reprovada
     * @param motivoReprovacao feedback obrigatório do coordenador
     */
    public void enviarReprovacaoAtividade(String nomeAluno,
                                          String emailAluno,
                                          String tituloAtividade,
                                          String motivoReprovacao) {
        String html = """
            <div style="font-family:Arial,sans-serif;max-width:600px;margin:auto">
              <div style="background:#dc3545;padding:16px;border-radius:8px 8px 0 0">
                <h2 style="color:#fff;margin:0">❌ Atividade Reprovada</h2>
              </div>
              <div style="border:1px solid #ddd;border-top:none;padding:24px;border-radius:0 0 8px 8px">
                <p>Olá, <strong>%s</strong>!</p>
                <p>Sua atividade foi <strong style="color:#dc3545">REPROVADA</strong> pelo coordenador.</p>
                <table style="width:100%%;background:#f9f9f9;padding:16px;border-radius:8px;border-collapse:collapse">
                  <tr>
                    <td style="padding:8px;color:#555;vertical-align:top"><strong>Atividade:</strong></td>
                    <td style="padding:8px">%s</td>
                  </tr>
                  <tr>
                    <td style="padding:8px;color:#555;vertical-align:top"><strong>Motivo:</strong></td>
                    <td style="padding:8px;color:#dc3545">%s</td>
                  </tr>
                </table>
                <div style="background:#fff3cd;border:1px solid #ffc107;padding:16px;border-radius:8px;margin-top:20px">
                  <p style="margin:0"><strong>💡 O que fazer agora?</strong></p>
                  <p style="margin:8px 0 0">
                    Acesse o sistema, corrija os pontos indicados pelo coordenador
                    e reenvie o comprovante corrigido.
                  </p>
                </div>
                <p style="color:#888;font-size:12px;margin-top:24px">
                  Este é um email automático do Sistema Senac. Não responda.
                </p>
              </div>
            </div>
            """.formatted(nomeAluno, tituloAtividade, motivoReprovacao);

        enviar(emailAluno, "❌ Atividade Reprovada — " + tituloAtividade, html);
    }

    // ─── Helper interno ───────────────────────────────────────────

    /**
     * Dispara a chamada HTTP para a API Brevo.
     * Falhas de envio são logadas mas não propagam exceção
     * para não interromper o fluxo principal da aplicação.
     */
    private void enviar(String destinatario, String assunto, String htmlContent) {
        String json = """
            {
              "sender": { "name": "%s", "email": "%s" },
              "to": [ { "email": "%s" } ],
              "subject": "%s",
              "htmlContent": "%s"
            }
            """.formatted(
                SENDER_NAME,
                senderEmail,
                destinatario,
                assunto,
                htmlContent.replace("\"", "\\\"").replace("\n", "").replace("\r", "")
        );

        Request request = new Request.Builder()
            .url("https://api.brevo.com/v3/smtp/email")
            .addHeader("api-key", apiKey)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json, MediaType.get("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("[EmailService] Email enviado para " + destinatario + " | Status: " + response.code());
            } else {
                System.err.println("[EmailService] Falha ao enviar para " + destinatario + " | Status: " + response.code());
            }
        } catch (Exception e) {
            System.err.println("[EmailService] Erro ao enviar email para " + destinatario + ": " + e.getMessage());
        }
    }
}
