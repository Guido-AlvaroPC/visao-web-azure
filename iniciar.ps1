# Uso: .\iniciar.ps1
# Informa endpoint e chave sem gravar nada em arquivo.
$env:AZURE_VISION_ENDPOINT = Read-Host "Endpoint (https://...cognitiveservices.azure.com/)"
$secure = Read-Host "Chave (Key 1 NOVA)" -AsSecureString
$env:AZURE_VISION_KEY = [Runtime.InteropServices.Marshal]::PtrToStringAuto([Runtime.InteropServices.Marshal]::SecureStringToBSTR($secure))
mvn spring-boot:run
