package org.example.java2final.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("\"user\"")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    Long id;
    Long badgeCounts;
    Long accountId;
    Boolean isEmployee;
    Long lastAccessDate;
    Long reputation;
    Long creationDate;
    @TableField(value = "user_id") // Explicitly map to the quoted column name
    private Long userId;
}
