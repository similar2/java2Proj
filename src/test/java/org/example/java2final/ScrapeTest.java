package org.example.java2final;

import org.example.java2final.pojo.Activity;
import org.example.java2final.pojo.Answer;
import org.example.java2final.pojo.Question;
import org.example.java2final.pojo.User;
import org.example.java2final.repository.ActivityRepo;
import org.example.java2final.repository.AnswerRepo;
import org.example.java2final.repository.QuestionRepo;
import org.example.java2final.repository.UserRepo;
import org.example.java2final.util.DataScrapeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ScrapeTest {

    @Autowired
    private DataScrapeUtil dataScrapeUtil; // Automatically inject the DataScrapeUtil component
    @Autowired
    private QuestionRepo questionRepo;
    @Autowired

    private UserRepo userRepo;
    @Autowired

    private AnswerRepo answerRepo;
    @Autowired

    private ActivityRepo activityRepo;


    @Test
    public void test() {
        int questionCount = 0;
        int answerCount = 0;
        int userCount = 0;

        int pageOffset = 0; // Start from the first page
        int pageSize = 10; // Number of questions per page

        while (questionCount < 1000) {
            // Fetch questions with pagination
            List<Question> questions = dataScrapeUtil.scrapeQuestion(pageSize, pageOffset);

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
                        User owner = dataScrapeUtil.scrapeUserDetail(ownerUserId);
                        if (owner != null && userRepo.insertUser(owner)) {
                            userCount++;
                        }
                    }

                    // If the question is answered, fetch and insert answers
                    if (question.getIsAnswered()) {
                        List<Answer> answers = dataScrapeUtil.scrapeAnswers(String.valueOf(question.getQuestionId()), 5, 0);
                        if (answers != null) {
                            for (Answer answer : answers) {
                                // Insert the answer into the database
                                if (answerRepo.insert(answer)) {
                                    answerCount++;

                                    // Fetch and insert details of the answer's owner
                                    String answerOwnerUserId = answer.getOwnerUserId();
                                    if (answerOwnerUserId != null) {
                                        User answerOwner = dataScrapeUtil.scrapeUserDetail(answerOwnerUserId);
                                        if (answerOwner != null && userRepo.insertUser(answerOwner)) {
                                            userCount++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    List<Activity> activities;
                    activities = dataScrapeUtil.scrapeActivity(question.getQuestionId(), 30, 0);
                    if (activities != null) {
                        for (Activity activity : activities) {
                            if (activityRepo.addActivity(activity)) {
                                String activityUserId = activity.getUserId().toString();
                                User answerOwner = dataScrapeUtil.scrapeUserDetail(activityUserId);
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
}
