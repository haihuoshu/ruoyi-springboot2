package com.ruoyi.framework.mybatis;

import com.mybatisflex.core.BaseMapper;
import com.mybatisflex.core.FlexConsts;
import com.mybatisflex.core.exception.FlexAssert;
import com.mybatisflex.core.row.Db;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.CollectionUtil;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Mapper基接口，扩展 MyBatis-Flex {@link BaseMapper}
 * <p>
 * 提供 MySQL 插入或更新（ON DUPLICATE KEY UPDATE）与批量更新能力。
 *
 * @param <T> 实体类型
 * @author haihuoshu
 */
@SuppressWarnings("unused")
public interface BaseMapperPlus<T> extends BaseMapper<T> {

    /**
     * 插入或更新：主键/唯一键冲突时更新 {@link UpdateOnDuplicate} 标记的字段
     *
     * @param entity 实体对象，不能为空
     */
    default void insertOnDuplicateKeyUpdate(T entity) {
        FlexAssert.notNull(entity, "entity can not be null");
        insertOnDuplicateKeyUpdate(Collections.singleton(entity));
    }

    /**
     * 批量插入或更新：主键/唯一键冲突时更新 {@link UpdateOnDuplicate} 标记的字段
     *
     * @param entities 实体集合，不能为空
     * @see UpdateOnDuplicateProvider#provideSql(java.util.Map, org.apache.ibatis.builder.annotation.ProviderContext)
     */
    @InsertProvider(UpdateOnDuplicateProvider.class)
    void insertOnDuplicateKeyUpdate(@Param(FlexConsts.ENTITIES) Collection<T> entities);

    /**
     * 批量更新（逐条执行 update，默认忽略 null 字段）
     * <p>
     * 注意：内部开启独立的自动提交批处理会话，不参与调用方的 Spring 事务。
     *
     * @param entities 实体集合，为空时直接返回
     */
    @SuppressWarnings("unchecked")
    default void updateBatch(List<T> entities) {
        if (CollectionUtil.isEmpty(entities)) {
            return;
        }
        // 获取真实的 Mapper 接口 Class（剔除 Spring 代理类），用于批量执行
        Class<BaseMapperPlus<T>> mapperClass = (Class<BaseMapperPlus<T>>) ClassUtil.getUsefulClass(this.getClass());
        Db.executeBatch(entities, mapperClass, BaseMapperPlus::update);
    }

}
