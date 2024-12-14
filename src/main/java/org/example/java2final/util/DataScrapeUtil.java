package org.example.java2final.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.java2final.pojo.Answer;
import org.example.java2final.pojo.Question;
import org.example.java2final.pojo.User;
import org.example.java2final.pojo.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class DataScrapeUtil {

    private final WebClient webClient;
    @Value("${stack-exchange.key}")
    private String key;
    private final ObjectMapper mapper;


    public DataScrapeUtil(WebClient webClient, ObjectMapper mapper) {
        this.webClient = webClient;
        this.mapper = mapper;
    }


    @Retryable(value = {WebClientResponseException.class},
            maxAttempts = 10, backoff = @Backoff(delay = 1000L))
    public List<Question> scrapeQuestion(int pageSize, int pageOffset) {
        try {
            // Example StackExchange API call: Get questions tagged with "java" from Stack Overflow
            String responseMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/questions")
                            .queryParam("order", "desc")
                            .queryParam("sort", "activity")
                            .queryParam("tagged", "java")  // Filter by "java" tag
                            .queryParam("site", "stackoverflow")
                            .queryParam("filter", "withbody")
                            .queryParam("pagesize", Math.min(pageSize, 100))  // Ensure pagesize <= 100
                            .queryParam("page", Math.max(pageOffset, 1))     // Ensure page >= 1
                            .queryParam("key", key)                         // Include API key if necessary
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode itemsNode;

            // Parse the JSON response and extract the "items" array
            itemsNode = mapper.readTree(responseMono).path("items");
            // Convert each item in the "items" array to a Question object
            List<Question> questions = new ArrayList<>();
            for (JsonNode itemNode : itemsNode) {
                questions.add(parseStackQuestion(itemNode)); // Apply the parse method
            }

            System.out.println("Parsed Questions: " + questions);
            return questions;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON response", e);
        }
    }

    @Retryable(value = {WebClientResponseException.class},
            maxAttempts = 10, backoff = @Backoff(delay = 1000L))
    public List<Answer> scrapeAnswers(String questionId, int pageSize, int pageOffset) {
        // Fetch answers from the StackExchange API
        String responseMono = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/questions/" + questionId + "/answers")
                        .queryParam("order", "desc")
                        .queryParam("sort", "creation")
                        .queryParam("site", "stackoverflow")
                        .queryParam("filter", "withbody")               // Include the body field
                        .queryParam("pagesize", Math.min(pageSize, 100))  // Ensure pagesize <= 100
                        .queryParam("page", Math.max(pageOffset, 1))     // Ensure page >= 1
                        .queryParam("key", key)                        // Include API key if necessary
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .block();

        JsonNode itemsNode;
        try {
            // Parse the response and extract the "items" array
            itemsNode = mapper.readTree(responseMono).path("items");
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON response", e);
        }

        // Convert each item in the "items" array to an Answer object
        List<Answer> answers = new ArrayList<>();
        for (JsonNode itemNode : itemsNode) {
            answers.add(parseStackAnswer(itemNode)); // Apply the parse method
        }

        System.out.println("Parsed Answers: " + answers);
        return answers;
    }

    @Retryable(value = {WebClientResponseException.class},
            maxAttempts = 10, backoff = @Backoff(delay = 1000L))
    public User scrapeUserDetail(String userId) {
        try {
            // Fetch user details from the StackExchange API
            String responseMono = webClient.get()
                    .uri(String.format("/users/" + userId + "?site=stackoverflow"))
//            uriBuilder -> uriBuilder
//                    .path("/users/" + userId)
//                    .queryParam("site", "stackoverflow")
//                    .queryParam("key", key)                        // Include API key if necessary
//                    .build()
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            JsonNode userNode;
            // Parse the response and extract the first user item
            JsonNode itemsNode = mapper.readTree(responseMono).path("items");
            userNode = itemsNode.isArray() && !itemsNode.isEmpty() ? itemsNode.get(0) : null; // Get the first user node
            if (userNode == null) {
                throw new RuntimeException("No user found for userId: " + userId);
            }

            // Parse the user node into a User object
            User user = parseStackUser(userNode);

            System.out.println("Parsed User: " + user);
            return user;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON response", e);
        }
    }

    @Retryable(value = {WebClientResponseException.class},
            maxAttempts = 10, backoff = @Backoff(delay = 1000L))
    public List<Activity> scrapeActivity(Long questionId, int pageSize, int pageOffset) {
        try {
            String fullUrl = "questions/" + questionId + "/timeline" +
                    "?site=stackoverflow" +
                    "&pagesize=" + Math.min(pageSize, 100) +
                    "&page=" + Math.max(pageOffset, 1) +
                    "&key=" + key;  // Include the API key if necessary

            // Fetch activities for a specific question
            String responseMono = webClient.get()
                    .uri(fullUrl)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            JsonNode itemsNode;

            // Parse the JSON response and extract the "items" array
            itemsNode = mapper.readTree(responseMono).path("items");
            // Convert each item in the "items" array to an Activity object
            List<Activity> activities = new ArrayList<>();
            for (JsonNode itemNode : itemsNode) {
                Activity activity = parseStackActivity(itemNode);
                if (!Objects.equals(activity.getActivityType(), "vote_aggregate") && itemNode.path("owner").path("user_type").asText().equals("registered")) {
                    activities.add(activity); // Apply the parse method
                }
            }
            return activities;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON response", e);
        }
    }

    private Activity parseStackActivity(JsonNode itemNode) {
        Activity activity = new Activity();
        activity.setQuestionId(itemNode.path("question_id").asLong());
        activity.setUserId(itemNode.path("owner").path("user_id").asLong());
        activity.setActivityType(itemNode.path("timeline_type").asText());
        activity.setUserReputation(itemNode.path("owner").path("reputation").asLong());
        return activity;
    }

    private Question parseStackQuestion(JsonNode stackQuestion) {
        Question question = new Question();
        question.setTags(stackQuestion.get("tags").toString()); // Convert tags array to a string
        question.setOwnerAccountId(stackQuestion.path("owner").path("account_id").asText(null)); // Null if missing
        question.setOwnerReputation(stackQuestion.path("owner").path("reputation").asText(null));
        question.setOwnerUserId(stackQuestion.path("owner").path("user_id").asText(null));
        question.setOwnerDisplayName(stackQuestion.path("owner").path("display_name").asText(null));
        question.setIsAnswered(stackQuestion.path("is_answered").asBoolean(false));
        question.setViewCount(stackQuestion.path("view_count").asLong(0));
        question.setAnswerCount(stackQuestion.path("answer_count").asLong(0));
        question.setScore(stackQuestion.path("score").asLong(0));
        question.setLastActivityDate(stackQuestion.path("last_activity_date").asLong(0));
        question.setCreationDate(stackQuestion.path("creation_date").asLong(0));
        question.setQuestionId(stackQuestion.path("question_id").asLong(0));
        question.setTitle(stackQuestion.path("title").asText(null));
        question.setContent(stackQuestion.path("body").asText(null));
        return question;
    }

    private Answer parseStackAnswer(JsonNode stackAnswer) {
        Answer answer = new Answer();
        answer.setOwnerAccountId(stackAnswer.path("owner").path("account_id").asText(null));
        answer.setOwnerReputation(stackAnswer.path("owner").path("reputation").asText(null));
        answer.setOwnerUserId(stackAnswer.path("owner").path("user_id").asText(null));
        answer.setOwnerDisplayName(stackAnswer.path("owner").path("display_name").asText(null));
        answer.setIsAccepted(stackAnswer.path("is_accepted").asBoolean(false));
        answer.setScore(stackAnswer.path("score").asLong(0));
        answer.setLastActivityDate(stackAnswer.path("last_activity_date").asLong(0));
        answer.setCreationDate(stackAnswer.path("creation_date").asLong(0));
        answer.setAnswerId(stackAnswer.path("answer_id").asLong(0));
        answer.setQuestionId(stackAnswer.path("question_id").asLong(0));
        answer.setBody(stackAnswer.path("body").asText(null));
        return answer;
    }

    private User parseStackUser(JsonNode stackUser) {
        User user = new User();
        user.setBadgeCounts(stackUser.path("badge_counts").path("bronze").asLong(0) +
                stackUser.path("badge_counts").path("silver").asLong(0) * 2 +
                stackUser.path("badge_counts").path("gold").asLong(0) * 3); // Sum of badges
        user.setAccountId(stackUser.path("account_id").asLong(0));
        user.setIsEmployee(stackUser.path("is_employee").asBoolean(false));
        user.setLastAccessDate(stackUser.path("last_access_date").asLong(0));
        user.setReputation(stackUser.path("reputation").asLong(0));
        user.setCreationDate(stackUser.path("creation_date").asLong(0));
        user.setUserId(stackUser.path("user_id").asLong(0));
        return user;
    }
}
