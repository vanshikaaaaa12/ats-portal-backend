package com.ats.ats.system.controller;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@CrossOrigin("*")
public class ResumeController {

    @PostMapping("/uploadResume")
    public String uploadResume(@RequestParam("file") MultipartFile file){

        try{

            File savedFile =
            File.createTempFile("resume", ".pdf");

            file.transferTo(savedFile);

            PDDocument document =
            PDDocument.load(savedFile);

            PDFTextStripper pdfStripper =
            new PDFTextStripper();

            String text =
            pdfStripper.getText(document).toLowerCase();

            document.close();

            // AI ANALYSIS

            String aiSuggestion = "";

            try{

                OkHttpClient client =
                new OkHttpClient();

                text = text
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\"", "'");

                String prompt =

"You are an ATS Resume Analyzer. "
+ "Strictly start response with ATS Score in this format only: ATS Score: XX/100. "
+ "Then give strengths, missing skills, resume improvements and job eligibility in points. "
+ "Keep response professional and under 150 words. "
+ text;

                String apiKey =
                System.getenv("GROQ_API_KEY");

                String json =
                "{\"model\":\"llama-3.1-8b-instant\","
                + "\"messages\":["
                + "{\"role\":\"user\","
                + "\"content\":\"" + prompt + "\"}"
                + "]}";

                MediaType mediaType =
                MediaType.parse("application/json");

                RequestBody body =
                RequestBody.create(json, mediaType);

                Request request =
                new Request.Builder()

                .url(
                "https://api.groq.com/openai/v1/chat/completions"
                )

                .addHeader(
                "Authorization",
                "Bearer " + apiKey
                )

                .addHeader(
                "Content-Type",
                "application/json"
                )

                .post(body)

                .build();

                Response response =
                client.newCall(request).execute();

                String responseData =
                response.body().string();

                int start =
                responseData.indexOf("\"content\":\"");

                if(start != -1){

                    start = start + 11;

                    int end =
                    responseData.indexOf("\"}", start);

                    aiSuggestion =
                    responseData.substring(start, end)

                    .replace("\\n", "\n")
                    .replace("**", "")
                    .replace("\\\"", "\"");

                }

                else{

                    aiSuggestion =
                    "AI Analysis Not Available";

                }

            }

            catch(Exception e){

                aiSuggestion =
                "AI Error : " + e.getMessage();

            }

            return aiSuggestion;

        }

        catch(Exception e){

            return e.getMessage();

        }

    }

}