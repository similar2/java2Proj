package org.example.java2final.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.java2final.pojo.Answer;
import org.example.java2final.pojo.Question;
import org.example.java2final.pojo.User;
import org.example.java2final.pojo.Activity;
import org.example.java2final.repository.ActivityRepo;
import org.example.java2final.repository.AnswerRepo;
import org.example.java2final.repository.QuestionRepo;
import org.example.java2final.repository.UserRepo;
import org.springframework.beans.factory.annotation.Value;
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
    private final QuestionRepo questionRepo;
    private final UserRepo userRepo;
    private final AnswerRepo answerRepo;
    private final ActivityRepo activityRepo;


    public DataScrapeUtil(WebClient webClient, ObjectMapper mapper, QuestionRepo questionRepo, UserRepo userRepo, AnswerRepo answerRepo, ActivityRepo activityRepo) {
        this.webClient = webClient;
        this.mapper = mapper;
        this.questionRepo = questionRepo;
        this.userRepo = userRepo;
        this.answerRepo = answerRepo;
        this.activityRepo = activityRepo;
    }

    public void dataScrape() {
        int questionCount = 0;
        int answerCount = 0;
        int userCount = 0;

        int pageOffset = 0; // Start from the first page
        int pageSize = 100; // Number of questions per page

        while (questionCount < 1000) {
            synchronized (this) {
                try {
                    wait(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            // Fetch questions with pagination
            List<Question> questions = scrapeQuestion(pageSize, pageOffset);

            // Break the loop if no questions are returned (end of pagination)
            if (questions == null || questions.isEmpty()) {
                System.out.println("No more questions available. Terminating scrape.");
                break;
            }

            for (Question question : questions) {
                // Insert the question into the database
                if (questionRepo.insert(question)) {
                    questionCount++;

                    // Fetch and insert the question owner's details
                    String ownerUserId = question.getOwnerUserId();
                    if (ownerUserId != null) {
                        User owner = scrapeUserDetail(ownerUserId);
                        if (owner != null && userRepo.insertUser(owner)) {
                            userCount++;
                        }
                    }

                    // If the question is answered, fetch and insert answers
                    if (question.getIsAnswered()) {
                        List<Answer> answers = scrapeAnswers(String.valueOf(question.getQuestionId()), 5, 0);
                        if (answers != null) {
                            for (Answer answer : answers) {
                                // Insert the answer into the database
                                if (answerRepo.insert(answer)) {
                                    answerCount++;

                                    // Fetch and insert details of the answer's owner
                                    String answerOwnerUserId = answer.getOwnerUserId();
                                    if (answerOwnerUserId != null) {
                                        User answerOwner = scrapeUserDetail(answerOwnerUserId);
                                        if (answerOwner != null && userRepo.insertUser(answerOwner)) {
                                            userCount++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    List<Activity> activities;
                    activities = this.scrapeActivity(question.getQuestionId(), 30, 0);
                    if (activities != null) {
                        for (Activity activity : activities) {
                            if (activityRepo.addActivity(activity)) {
                                String activityUserId = activity.getUserId().toString();
                                User answerOwner = scrapeUserDetail(activityUserId);
                                if (answerOwner != null && userRepo.insertUser(answerOwner)) {
                                    userCount++;
                                }
                            }
                        }
                    }
                }
            }

            // Increment pageOffset for the next batch of questions
            pageOffset++;
        }

        // Print the scrape summary
        System.out.println(userCount + " users scraped.");
        System.out.println(answerCount + " answers scraped.");
        System.out.println(questionCount + " questions scraped.");
    }

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
        } catch (WebClientResponseException.Forbidden e) {
            String responseBody = e.getResponseBodyAsString();
            System.out.println(responseBody);
            return null;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON response", e);
        }
    }

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

    public User scrapeUserDetail(String userId) {
        try {
            // Fetch user details from the StackExchange API
            String responseMono = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users/" + userId)
                            .queryParam("site", "stackoverflow")
                            .queryParam("key", key)                        // Include API key if necessary
                            .build())
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
        } catch (WebClientResponseException e) {
            String response = e.getResponseBodyAsString();
            System.out.println("Error processing JSON response: " + response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON response", e);
        }
        return null;
    }

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
        } catch (WebClientResponseException e) {
            System.out.println("WebClient response error: " + e.getMessage());
            return null;
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
