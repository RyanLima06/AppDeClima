package org.miniProjetosJava;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Locale;
import java.util.Scanner;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class App {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner value = new Scanner(System.in);
        String respostaContinuacao;
        String respostaCity;
        do {
            //entrada de dados
            System.out.println("Qual o nome da cidade? ");
            String cidade = value.nextLine();
            String cidadeCodificada = URLEncoder.encode(cidade, StandardCharsets.UTF_8);

            //https
             respostaCity = buscarJson("https://geocoding-api.open-meteo.com/v1/search?name=" + cidadeCodificada);

            //Checa se a cidade existe, se sim, continua o código.
            if (!respostaCity.contains("latitude")) {
                System.out.println("Cidade não encontrada");
                break;
            }
                //processamento da Cidade: latitude, longitude e estado

                double dadoLatitude = processarNumerosCidade(respostaCity, "latitude", 0);
                double dadoLongitude = processarNumerosCidade(respostaCity, "longitude", 0);
                String dadoEstado = processarTexto(respostaCity, "admin1", 0);
                String respostaTemp = buscarJson("https://api.open-meteo.com/v1/forecast?latitude=" + dadoLatitude + "&longitude=" + dadoLongitude + "&current=temperature_2m,wind_speed_10m,weather_code");
                int inicioCurrent = respostaTemp.indexOf("\"current\":{");
                double temperatura = processarNumerosCidade(respostaTemp, "temperature_2m", inicioCurrent);
                double vento = processarNumerosCidade(respostaTemp, "wind_speed_10m", inicioCurrent);

                //pega o código do clima e converte.
                int codigoClima = (int) processarNumerosCidade(respostaTemp, "weather_code", inicioCurrent);
                String climaDescricao = switch (codigoClima) {
                    case 0 -> "Céu limpo";
                    case 1 -> "Predominantemente limpo";
                    case 2 -> "Parcialmente nublado";
                    case 3 -> "Encoberto";
                    case 45 -> "Névoa (nevoiro)";
                    case 48 -> "Nevoeiro com depósito de geada";

                    // Garoa
                    case 51 -> "Garoa leve";
                    case 53 -> "Garoa moderada";
                    case 55 -> "Garoa densa";
                    case 56 -> "Garoa congelante leve";
                    case 57 -> "Garoa congelante densa";

                    // Chuva
                    case 61 -> "Chuva leve";
                    case 63 -> "Chuva moderada";
                    case 65 -> "Chuva forte";
                    case 66 -> "Chuva congelante leve";
                    case 67 -> "Chuva congelante forte";
                    case 80 -> "Pancadas de chuva leve";
                    case 81 -> "Pancadas de chuva moderada";
                    case 82 -> "Pancadas de chuva violenta";

                    // Neve
                    case 71 -> "Queda de neve leve";
                    case 73 -> "Queda de neve moderada";
                    case 75 -> "Queda de neve forte";
                    case 77 -> "Grãos de neve";
                    case 85 -> "Pancadas de neve leve";
                    case 86 -> "Pancadas de neve forte";

                    // Tempestades
                    case 95 -> "Trovoada leve ou moderada";
                    case 96 -> "Trovoada com granizo leve";
                    case 99 -> "Trovoada com granizo forte";

                    default -> "Código desconhecido";
                };

                System.out.println("Cidade: " + cidade.toUpperCase(Locale.ROOT));
                System.out.println("Estado: " + dadoEstado);
                System.out.println("temperatura: " + temperatura + "C");
                System.out.println("Vento: " + vento + "km/h");
                System.out.println("Clima: " + climaDescricao);
                System.out.println("deseja continuar usando a aplicacao? (S/N)");
                respostaContinuacao = value.nextLine();

        } while (!respostaContinuacao.toUpperCase(Locale.ROOT).equals("N") && respostaContinuacao.toUpperCase(Locale.ROOT).equals("S"));

        String finalizando = (!respostaCity.contains("latitude")) ? "Tente novamente!" : "Até a próxima";
        System.out.println(finalizando);
    }
    private static double processarNumerosCidade(String texto, String chaves, int apartirDe){
        int dados = texto.indexOf("\""+chaves+"\":", apartirDe);
        int size = ("\""+chaves+"\":").length();
        int virgula = texto.indexOf(",", dados);
        int chave = texto.indexOf("}", dados);
        int fim = (virgula == -1) ? chave : Math.min(virgula, chave);
        double cidadePronta = Double.parseDouble(texto.substring(dados+size, fim));
        return cidadePronta;
    }
    private static String buscarJson(String url) {
        HttpClient user = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        HttpResponse<String> dadosJson = null;
        try {
            dadosJson = user.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        String corpoJson = dadosJson.body();
            return corpoJson;
    }
    private static String processarTexto(String texto, String chaves, int apartirDe) {
        int dados = texto.indexOf("\""+chaves+"\":", apartirDe);
        int size = ("\""+chaves+"\":").length();
        int fim = texto.indexOf("\"", (dados+size)+1);

        return texto.substring(dados+size+1, fim);
    }

}