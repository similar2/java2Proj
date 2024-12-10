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
@TableName("answer")
public class Answer {
    @TableId(value = "id", type = IdType.AUTO)
    Long id;
    String ownerAccountId;
    String ownerReputation;
    String ownerUserId;
    String ownerDisplayName;
    Boolean isAccepted;
    Long score;
    Long lastActivityDate;
    Long creationDate;
    Long answerId;
    Long questionId;
    String body;
}
