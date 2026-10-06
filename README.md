# Visao Web Azure - MVP Inova Tronica

App web em Java (Spring Boot) que envia uma foto ao Azure AI Vision (Image Analysis 4.0) e mostra a descricao (caption) e tags.

## Requisitos
- JDK 17+
- Maven 3.8+
- Recurso Computer Vision criado no Azure (rg-vc-inova)

## Variaveis de ambiente 
- `AZURE_VISION_ENDPOINT`
- `AZURE_VISION_KEY`

## Executar (PowerShell)
```powershell
.\iniciar.ps1
```
ou manualmente:
```powershell
$env:AZURE_VISION_ENDPOINT="https://SEU-RECURSO.cognitiveservices.azure.com/"
$env:AZURE_VISION_KEY="SUA_CHAVE"
mvn spring-boot:run
```
Acesse http://localhost:8080
