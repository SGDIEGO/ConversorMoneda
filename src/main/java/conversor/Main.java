package conversor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class Main {
    // Estructuras
    public static class ConversionInfo {
        private float monto;
        private String monedaInicial, monedaFinal;
        private LocalDateTime hora;

        public float getMonto() {
            return monto;
        }

        public String getMonedaFinal() {
            return monedaFinal;
        }

        public String getMonedaInicial() {
            return monedaInicial;
        }

        public LocalDateTime getHora() {
            return hora;
        }

        public ConversionInfo(float monto, String monedaInicial, String monedaFinal, LocalDateTime hora) {
            this.monto = monto;
            this.monedaInicial = monedaInicial;
            this.monedaFinal = monedaFinal;
            this.hora = hora;
        }
    }

    // Variables
    public static String urlConnection = "https://v6.exchangerate-api.com/v6/9d7d6b1fa1a0363a606b1254/latest/";
    public static List<ConversionInfo> conversiones = new ArrayList<ConversionInfo>();

    // Funciones
    public static HttpResponse<String> getResponse(HttpClient cliente, String tipoMoneda)
            throws IOException, InterruptedException {
        String url = urlConnection + tipoMoneda;
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(url)).build();

        return cliente.send(req, HttpResponse.BodyHandlers.ofString());
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Obteniendo tasas de cambio
        HttpClient client = HttpClient.newHttpClient();

        // Entrada de usuario
        Scanner scann = new Scanner(System.in);

        do {
            int opcion;
            // Menu de opciones
            do {
                // Limpiar consola
                System.out.print("\033\143");
                System.out.println("1. Conversion de numero.\n2. Historial.");
                opcion = scann.nextInt();
                if (opcion >= 1 && opcion <= 2)
                    break;

            } while (true);

            switch (opcion) {
                // Conversion
                case 1:
                    try {
                        // Ingresar monto
                        System.out.print("Ingrese monto: ");
                        float monto = scann.nextFloat();
                        System.out.print("\033\143");

                        // Tipo de moneda inicial
                        System.out.println(
                                "Ingrese tipo de moneda actual:\n1. ARS\n2. BOB\n3. BRL\n4. CLP\n5. COP\n6. USD\n ");
                        String monedaActual = scann.next();
                        System.out.print("\033\143");

                        // Request a api
                        HttpResponse<String> response = getResponse(client, monedaActual);
                        JsonObject JsonResponse = new Gson().fromJson(response.body(),
                                JsonObject.class);

                        // Tipo de moneda a convertir
                        System.out.println(
                                "Ingrese tipo de moneda a cambiar:\n1. ARS\n2. BOB\n3. BRL\n4. CLP\n5. COP\n6. USD\n ");
                        String monedaConvertir = scann.next();
                        System.out.print("\033\143");

                        // Obtener tasa de cambio
                        float tasaCambio = JsonResponse.getAsJsonObject("conversion_rates").get(monedaConvertir)
                                .getAsFloat();

                        // Mostrar monto
                        System.out.print("Monto es: ");
                        System.out.println(monto * tasaCambio);

                        // Agregar conversion a registro de conversiones
                        conversiones.add(new ConversionInfo(monto, monedaActual, monedaConvertir,
                                LocalDateTime.now()));
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                // Historial
                case 2:
                    if (conversiones.isEmpty()) {
                        System.out.println("No tiene registros aun!");
                        break;
                    }
                    System.out.println("|monto      |tipo moneda    |tipo cambio    |fecha      |");
                    for (ConversionInfo c : conversiones) {
                        System.out.printf("|%f        |%s      |%s      |%s|\n", c.getMonto(), c.getMonedaInicial(),
                                c.getMonedaFinal(),
                                c.getHora());
                    }
                    break;
                default:
                    break;
            }

            // Bucle
            System.out.println("Desea seguir: (si/ no)");
        } while (scann.next().compareTo("si") == 0);
        System.out.println("Gracias!");
        scann.close();
    }
}