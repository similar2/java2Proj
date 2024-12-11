package org.example.java2final.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("question")
public class Question {
    @TableId(value = "id", type = IdType.AUTO)
    Long id;
    String tags;
    String ownerAccountId;
    String ownerReputation;
    String ownerUserId;
    String ownerDisplayName;
    Boolean isAnswered;
    Long viewCount;
    Long answerCount;
    Long score;
    Long lastActivityDate;
    Long creationDate;
    Long questionId;
    String title;
    String content;
}
