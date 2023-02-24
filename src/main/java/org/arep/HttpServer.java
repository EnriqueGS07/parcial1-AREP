package org.arep;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;

public class HttpServer {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(36000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }

        while (true) {
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine = "", request = "";

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                if (inputLine.startsWith("GET /consulta?")) {
                    request = inputLine.split("=")[1].split(" ")[0];
                    System.out.println(request);
                    System.out.println(request.split("\\(")[0]);
                }
                if (!in.ready()) {
                    break;
                }
            }
            System.out.println(request);
            if(request.split("\\(")[0].equals("Class")){
                Class<?> clase = Class.forName(request.split("\\(")[1].split("\\)")[0]);
                Method[] metodos = clase.getMethods();
                Field[] campos = clase.getFields();
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n" +
                        "<!DOCTYPE html>"+
                        "<html>"+
                        "<head>"+
                        "<title>chatGPT</title>\n"+
                        "<meta charset=\"UTF-8\">\n"+
                        "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"+
                        "</head>\n"+
                        "<body>\n"+
                        "<h1>CHAT gpt2</h1>\n"+
                        "<form action=\"/chat\">\n"+
                        "    <label for=\"name\">metodo:</label><br>\n"+
                        "    <input type=\"text\" id=\"name\" name=\"name\" value=\"\"><br><br>\n"+
                        "</form>\n"+
                        "<div id=\"getrespmsg\">" +
                        "Metodos" + Arrays.toString(metodos) + "\n" + "Campos" + Arrays.toString(campos) +
                        "</div>\n"+
                        "</body>\n"+
                        "</html>";
            }
            else if(request.split("\\(")[0].equals("invoke")){
                Class<?> clase = Class.forName(request.split("\\(")[1].split(",")[0]);
                Method[] metodos = clase.getMethods();
                for (Method m: metodos) {
                    if(m.getName().equals(request.split("\\(")[1].split(",%20")[1].split("\\)")[0])){
                        Object ret = m.invoke(null);
                        outputLine = "HTTP/1.1 200 OK\r\n"
                                + "Content-Type: text/html\r\n"
                                + "\r\n" +
                                "<!DOCTYPE html>"+
                                "<html>"+
                                "<head>"+
                                "<title>chatGPT</title>\n"+
                                "<meta charset=\"UTF-8\">\n"+
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"+
                                "</head>\n"+
                                "<body>\n"+
                                "<h1>CHAT gpt2</h1>\n"+
                                "<form action=\"/chat\">\n"+
                                "    <label for=\"name\">metodo:</label><br>\n"+
                                "    <input type=\"text\" id=\"name\" name=\"name\" value=\"\"><br><br>\n"+
                                "</form>\n"+
                                "<div id=\"getrespmsg\">" +
                                "Resultado: " + ret.toString() +
                                "</div>\n"+
                                "</body>\n"+
                                "</html>";
                    }
                }
            }
            else if(request.split("\\(")[0].equals("unaryInvoke")){
                Class<?> clase = Class.forName(request.split("\\(")[1].split(",")[0]);
                String meth = request.split("\\(")[1].split(",%20")[1];
                String type = request.split("\\(")[1].split(",%20")[2];
                String val = request.split("\\(")[1].split(",%20")[3].split("\\)")[0];
                Object valor = null;
                if(Objects.equals(type, "int")){
                    valor = Integer.parseInt(val);
                } else if (type.equals("String")) {
                    valor = val;
                }
                Method[] metodos = clase.getMethods();
                for (Method m: metodos) {
                    if(m.getName().equals(request.split("\\(")[1].split(",%20")[1])){
                        
                        Object ret = m.invoke(null, valor);
                        outputLine = "HTTP/1.1 200 OK\r\n"
                                + "Content-Type: text/html\r\n"
                                + "\r\n" +
                                "<!DOCTYPE html>"+
                                "<html>"+
                                "<head>"+
                                "<title>chatGPT</title>\n"+
                                "<meta charset=\"UTF-8\">\n"+
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"+
                                "</head>\n"+
                                "<body>\n"+
                                "<h1>CHAT gpt2</h1>\n"+
                                "<form action=\"/chat\">\n"+
                                "    <label for=\"name\">metodo:</label><br>\n"+
                                "    <input type=\"text\" id=\"name\" name=\"name\" value=\"\"><br><br>\n"+
                                "</form>\n"+
                                "<div id=\"getrespmsg\">" +
                                "Resultado: " + ret.toString() +
                                "</div>\n"+
                                "</body>\n"+
                                "</html>";
                    }
                }
            }
            else if(request.split("\\(")[0].equals("binaryInvoke")){
                Class<?> clase = Class.forName(request.split("\\(")[1].split(",")[0]);
                String meth = request.split("\\(")[1].split(",%20")[1];
                String type = request.split("\\(")[1].split(",%20")[2];
                String val1 = request.split("\\(")[1].split(",%20")[3];
                String type2 = request.split("\\(")[1].split(",%20")[4];
                String val2 = request.split("\\(")[1].split(",%20")[5].split("\\)")[0];
                Object valor = null;
                Object valor2 = null;
                System.out.println(meth + type + val1 + type2 + val2);
                if(Objects.equals(type, "int")){
                    valor = Integer.parseInt(val1);
                } else if (type.equals("String")) {
                    valor = val1;
                } else if (type2.equals("double")) {
                    valor = Double.parseDouble(val1);
                }
                if(Objects.equals(type2, "int")){
                    valor2 = Integer.parseInt(val2);
                } else if (type2.equals("String")) {
                    valor2 = val2;
                } else if (type2.equals("double")) {
                    valor2 = Double.parseDouble(val2);
                }
                Object[] vals = new Object[2];
                vals[0] = valor;
                vals[1] = valor2;
                System.out.println();
                Method[] metodos = clase.getMethods();
                for (Method m: metodos) {
                    if(m.getName().equals(request.split("\\(")[1].split(",%20")[1])){

                        Object ret = m.invoke(null, vals);
                        outputLine = "HTTP/1.1 200 OK\r\n"
                                + "Content-Type: text/html\r\n"
                                + "\r\n" +
                                "<!DOCTYPE html>"+
                                "<html>"+
                                "<head>"+
                                "<title>chatGPT</title>\n"+
                                "<meta charset=\"UTF-8\">\n"+
                                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"+
                                "</head>\n"+
                                "<body>\n"+
                                "<h1>CHAT gpt2</h1>\n"+
                                "<form action=\"/chat\">\n"+
                                "    <label for=\"name\">metodo:</label><br>\n"+
                                "    <input type=\"text\" id=\"name\" name=\"name\" value=\"\"><br><br>\n"+
                                "</form>\n"+
                                "<div id=\"getrespmsg\">" +
                                "Resultado: " + ret.toString() +
                                "</div>\n"+
                                "</body>\n"+
                                "</html>";
                    }
                }
            }
            else{
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html\r\n"
                        + "\r\n" +
                        new String(Files.readAllBytes(Paths.get("src/main/resources/index.html")));
            }
            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }

    }



}