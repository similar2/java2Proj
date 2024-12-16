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
@TableName("activity")
public class Activity {
    @TableId(value = "id", type = IdType.AUTO)
    Integer id;
    Long questionId;
    Long userId;
    String activityType;
    Long userReputation;
}
