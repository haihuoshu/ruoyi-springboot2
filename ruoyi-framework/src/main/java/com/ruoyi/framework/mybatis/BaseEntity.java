package com.ruoyi.framework.mybatis;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity基类
 * <p>
 * 提供主键、逻辑删除、创建/更新审计等公共字段，
 * 配合 {@link BaseEntityListener} 在插入/更新时自动填充公共字段。
 *
 * @author haihuoshu
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 主键
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.flexId)
    private Long id;
    /**
     * 是否删除
     */
    @Column(isLogicDelete = true)
    @UpdateOnDuplicate
    private Boolean deleted;
    /**
     * 创建人
     */
    private String createdBy;
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    /**
     * 更新人
     */
    @UpdateOnDuplicate
    private String updatedBy;
    /**
     * 更新时间
     */
    @UpdateOnDuplicate
    private LocalDateTime updatedTime;

}
