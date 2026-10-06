package br.com.inovatronica.visaoweb.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
public class AzureVisionService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public AzureVisionService(
            @Value("${azure.vision.endpoint}") String endpoint,
            @Value("${azure.vision.key}") String chaveAzure,
            ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        String endpointLimpo = endpoint.endsWith("/")
                ? endpoint.substring(0, endpoint.length() - 1)
                : endpoint;
        this.restClient = RestClient.builder()
                .baseUrl(endpointLimpo)
                .defaultHeader("Ocp-Apim-Subscription-Key", chaveAzure)
                .build();
    }

    public ResultadoAnalise analisar(byte[] arquivoImagem) {
        try {
            String resposta = restClient.post()
                    .uri("/computervision/imageanalysis:analyze"
                            + "?api-version=2024-02-01"
                            + "&features=caption,tags"
                            + "&gender-neutral-caption=true"
                            + "&language=en")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                    .body(arquivoImagem)
                    .retrieve()
                    .body(String.class);

            JsonNode json = objectMapper.readTree(resposta);

            String descricao = "Nenhuma descricao foi retornada.";
            double confianca = 0.0;
            if (json.has("captionResult")) {
                descricao = json.path("captionResult").path("text").asText(descricao);
                confianca = json.path("captionResult").path("confidence").asDouble();
            }

            List<Tag> tags = new ArrayList<>();
            if (json.path("tagsResult").has("values")) {
                for (JsonNode t : json.path("tagsResult").path("values")) {
                    tags.add(new Tag(t.path("name").asText(), t.path("confidence").asDouble()));
                }
            }
            return new ResultadoAnalise(descricao, confianca, tags);

        } catch (Exception erro) {
            throw new RuntimeException(
                    "Nao foi possivel analisar a imagem na Azure AI Vision: " + erro.getMessage(), erro);
        }
    }

    public record ResultadoAnalise(String descricao, double confiancaDescricao, List<Tag> tags) {}

    public record Tag(String nome, double confianca) {}
}
