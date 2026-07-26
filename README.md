# AppDeClima

Aplicativo de terminal em Java que consulta o clima atual de qualquer cidade do mundo.

## Como funciona

1. O usuário digita o nome de uma cidade
2. O programa busca as coordenadas (latitude/longitude) via geocoding
3. Usa essas coordenadas para buscar o clima atual
4. Exibe temperatura, vento e condição do tempo traduzida para português

## Tecnologias

- Java 21
- `java.net.http.HttpClient` (nativo do JDK, sem dependências externas)
- API gratuita [Open-Meteo](https://open-meteo.com/) (geocoding + forecast)
- Parsing de JSON feito manualmente, sem bibliotecas externas

## Como rodar

\`\`\`bash
javac App.java
java App
\`\`\`

Digite o nome de uma cidade quando solicitado.

## Exemplo de saída

\`\`\`
Qual o nome da cidade?
Fortaleza
Cidade: FORTALEZA
temperatura: 28.4C
Vento: 12.6km/h
Clima: Parcialmente nublado
\`\`\`                                    
