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

            // SAVE PDF FILE

            File savedFile = File.createTempFile("resume", ".pdf");

            file.transferTo(savedFile);

            // READ PDF TEXT

            PDDocument document = PDDocument.load(savedFile);

            PDFTextStripper pdfStripper = new PDFTextStripper();

            String text = pdfStripper.getText(document).toLowerCase();

            document.close();

            // ATS SCORE LOGIC

            int score = 50;

            String result = "";

            String suggestions = "";

            // JAVA

            if(text.contains("java")){

                score += 10;
                result += "Java Skill Detected\n";

            }

            else{

                suggestions += "Add Java Skill\n";

            }

            // PYTHON

            if(text.contains("python")){

                score += 10;
                result += "Python Skill Detected\n";

            }

            else{

                suggestions += "Add Python Skill\n";

            }

            // SQL

            if(text.contains("sql")){

                score += 10;
                result += "SQL Skill Detected\n";

            }

            else{

                suggestions += "Add SQL Skill\n";

            }

            // SPRING BOOT

            if(text.contains("spring boot")){

                score += 10;
                result += "Spring Boot Skill Detected\n";

            }

            else{

                suggestions += "Add Spring Boot Project\n";

            }

            // CERTIFICATES

            if(text.contains("certificate")){

                score += 10;
                result += "Certificates Found\n";

            }

            else{

                suggestions += "Add Certifications\n";

            }

            // EXPERIENCE

            if(text.contains("internship") || text.contains("experience")){

                score += 10;
                result += "Experience Detected\n";

            }

            else{

                suggestions += "Add Internship or Experience\n";

            }

            // AI ANALYSIS

            String aiSuggestion = "";

            try{

                OkHttpClient client = new OkHttpClient();

                String prompt =
                
        "Analyze this resume like an ATS system. "
        + "Give short professional feedback in points only. "
        + "Keep response under 200 words. "
        + "Include ATS score, missing skills, project feedback and improvements.\n\n"
        + text;
                String json = """
                {
                  "model": "deepseek/deepseek-chat",
                  "messages": [
                    {
                      "role": "user",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(prompt.replace("\"", ""));

                okhttp3.RequestBody body =
                        okhttp3.RequestBody.create(
                                json,
                                okhttp3.MediaType.get("application/json")
                        );

                Request request = new Request.Builder()
                        .url("https://openrouter.ai/api/v1/chat/completions")
                        .addHeader("Authorization", "Bearer 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")
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();

                String responseData = response.body().string();

                      int start = responseData.indexOf("\"content\":\"") + 11;

int end = responseData.indexOf("\",\"refusal\"");

aiSuggestion = responseData.substring(start, end);

aiSuggestion = aiSuggestion
        .replace("\\n", "\n")
        .replace("###", "")
        .replace("**", "");

            }

            catch(Exception e){

                aiSuggestion = e.getMessage();

            }

            // COMPANY ELIGIBILITY

            String company = "";

            if(score >= 90){

                company = "Eligible for TCS, Infosys, Accenture";

            }

            else if(score >= 75){

                company = "Eligible for Wipro and Capgemini";

            }

            else{

                company = "Need Resume Improvement";

            }

            // FINAL OUTPUT

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