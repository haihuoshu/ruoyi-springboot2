package com.ruoyi.framework.mybatis;

import java.lang.annotation.*;

/**
 * 标记插入发生主键/唯一键冲突时需要更新的字段
 * <p>
 * 配合 {@link BaseMapperPlus#insertOnDuplicateKeyUpdate(java.util.Collection)} 使用，
 * 仅被该注解标记的字段会出现在 ON DUPLICATE KEY UPDATE 子句中。
 *
 * @author haihuoshu
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UpdateOnDuplicate {

    /**
     * 冲突时是否允许将字段更新为 NULL
     * <p>
     * false（默认）：新值为 NULL 时保留数据库原值（IFNULL 语义）；
     * true：直接使用新值覆盖（允许置 NULL）。
     *
     * @return 是否允许置 NULL
     */
    boolean setNull() default false;

}
