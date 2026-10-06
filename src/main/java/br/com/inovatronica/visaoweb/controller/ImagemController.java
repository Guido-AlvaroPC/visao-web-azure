package br.com.inovatronica.visaoweb.controller;

import br.com.inovatronica.visaoweb.service.AzureVisionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.Set;

@Controller
public class ImagemController {

    private final AzureVisionService azureVisionService;

    private static final Set<String> FORMATOS_ACEITOS =
            Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

    public ImagemController(AzureVisionService azureVisionService) {
        this.azureVisionService = azureVisionService;
    }

    @GetMapping("/")
    public String paginaInicial() {
        return "index";
    }

    @PostMapping("/analisar")
    public String analisarImagem(@RequestParam("imagem") MultipartFile imagem, Model model) {
        try {
            if (imagem.isEmpty()) {
                model.addAttribute("erro", "Selecione uma imagem antes de analisar.");
                return "index";
            }
            String tipo = imagem.getContentType();
            if (tipo == null || !FORMATOS_ACEITOS.contains(tipo)) {
                model.addAttribute("erro", "Formato invalido. Envie JPG, PNG, GIF ou WEBP.");
                return "index";
            }

            byte[] bytes = imagem.getBytes();
            AzureVisionService.ResultadoAnalise r = azureVisionService.analisar(bytes);

            model.addAttribute("imagemPreview",
                    "data:" + tipo + ";base64," + Base64.getEncoder().encodeToString(bytes));
            model.addAttribute("nomeArquivo", imagem.getOriginalFilename());
            model.addAttribute("descricao", r.descricao());
            model.addAttribute("confianca", String.format("%.2f%%", r.confiancaDescricao() * 100));

            List<AzureVisionService.Tag> tags = r.tags().stream().limit(10).toList();
            model.addAttribute("tags", tags);

        } catch (Exception erro) {
            model.addAttribute("erro", "Erro ao processar a imagem: " + erro.getMessage());
        }
        return "index";
    }
}
