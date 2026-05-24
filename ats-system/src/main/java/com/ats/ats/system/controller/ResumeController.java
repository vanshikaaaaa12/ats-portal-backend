package com.ats.ats.system.controller;

import okhttp3.*;
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

            int score = 50;

            String result = "";

            String suggestions = "";

            // JAVA

            if(text.contains("java")){

                score += 10;

                result +=
                "Java Skill Detected\n";

            }

            else{

                suggestions +=
                "Add Java Skill\n";

            }

            // PYTHON

            if(text.contains("python")){

                score += 10;

                result +=
                "Python Skill Detected\n";

            }

            else{

                suggestions +=
                "Add Python Skill\n";

            }

            // SQL

            if(text.contains("sql")){

                score += 10;

                result +=
                "SQL Skill Detected\n";

            }

            else{

                suggestions +=
                "Add SQL Skill\n";

            }

            // SPRING BOOT

            if(text.contains("spring boot")){

                score += 10;

                result +=
                "Spring Boot Skill Detected\n";

            }

            else{

                suggestions +=
                "Add Spring Boot Project\n";

            }

            // CERTIFICATES

            if(text.contains("certificate")){

                score += 10;

                result +=
                "Certificates Found\n";

            }

            else{

                suggestions +=
                "Add Certifications\n";

            }

            // EXPERIENCE

            if(text.contains("internship")
            || text.contains("experience")){

                score += 10;

                result +=
                "Experience Detected\n";

            }

            else{

                suggestions +=
                "Add Internship or Experience\n";

            }

            // AI ANALYSIS

            String aiSuggestion = "";

            try{

                OkHttpClient client =
                new OkHttpClient();

                text = text
                .replace("\n", " ")
                .replace("\r", " ")
                .replace("\"", "");

                String prompt =

                "Analyze this resume like an ATS system. "
                + "Give short professional feedback "
                + "in points only. "
                + "Keep response under 200 words. "
                + "Include ATS score, missing skills, "
                + "project feedback and improvements. "
                + text;

                String json = """
                {
                  "model": "llama3-8b-8192",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(prompt);

                okhttp3.RequestBody body =

                okhttp3.RequestBody.create(
                        json,
                        okhttp3.MediaType.get(
                        "application/json")
                );

                Request request =
                new Request.Builder()

                .url(
                "https://api.groq.com/openai/v1/chat/completions"
                )

                .addHeader(
                "Authorization",
                "Bearer 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"
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

                aiSuggestion = responseData;

            }

            catch(Exception e){

                aiSuggestion =
                "AI Error : " + e.getMessage();

            }

            String company = "";

            if(score >= 90){

                company =
                "Eligible for TCS, Infosys, Accenture";

            }

            else if(score >= 75){

                company =
                "Eligible for Wipro and Capgemini";

            }

            else{

                company =
                "Need Resume Improvement";

            }

            return "ATS Score : "
                    + score
                    + "%\n\n"
                    + result
                    + "\n"
                    + company
                    + "\n\nSuggestions:\n"
                    + suggestions
                    + "\n\nAI Analysis:\n"
                    + aiSuggestion;

        }

        catch(Exception e){

            return e.getMessage();

        }

    }

}