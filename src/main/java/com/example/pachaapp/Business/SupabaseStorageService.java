package com.example.pachaapp.Business;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

@Service
public class SupabaseStorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.apiKey}")
    private String supabaseApiKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    // Método de subida actualizado en Supabase para aceptar los bytes transformados
       public String uploadFile(MultipartFile file, String path, String bucket) {
        try {
            byte[] fileBytes = file.getBytes();
            String contentType = file.getContentType();
            return uploadFile(fileBytes, path, contentType, bucket);
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo: " + e.getMessage());
        }
    }

    // Método para subir archivos desde bytes
    public String uploadFile(byte[] fileBytes, String path, String contentType, String bucket) {
        try {
            String filePath = path;
            String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + filePath;

            var client = java.net.http.HttpClient.newHttpClient();
            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Authorization", "Bearer " + supabaseApiKey)
                    .header("Content-Type", contentType)
                    .PUT(java.net.http.HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                    .build();

            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + filePath;
            } else {
                throw new RuntimeException("Error al subir archivo a Supabase: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al subir archivo a Supabase: " + e.getMessage());
        }
    }

    public String uploadFileUrl(byte[] fileBytes, String path, String contentType) {
        try {
            String filePath = path;

            String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + filePath;

            // Realizar la solicitud HTTP
            var client = java.net.http.HttpClient.newHttpClient();
            var request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create(url))
                    .header("Authorization", "Bearer " + supabaseApiKey)
                    .header("Content-Type", contentType)
                    .PUT(java.net.http.HttpRequest.BodyPublishers.ofByteArray(fileBytes))
                    .build();

            var response = client.send(request, java.net.http.HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Retornar la URL pública
                return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + filePath;
            } else {
                throw new RuntimeException("Error al subir archivo a Supabase: " + response.body());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error al subir archivo a Supabase: " + e.getMessage());
        }
    }
}