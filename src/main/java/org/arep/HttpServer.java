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
                Class<?> clase = Class.forName(request.split("\\(\\[")[1].split("],")[0]);
                Method[] metodos = clase.getMethods();
                for (Method m: metodos) {
                    if(m.getName().equals(request.split("\\(\\[")[1].split("\\[")[1].split("]\\)")[0])){
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


    public void unaryInvokeMethod(String className, String methodName) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        Class<?> clase = Class.forName(className);
        Method[] metodos = clase.getMethods();
        for (Method m: metodos) {
            if(m.getName().equals(methodName)){
                m.invoke(null);
            }
        }
    }

}