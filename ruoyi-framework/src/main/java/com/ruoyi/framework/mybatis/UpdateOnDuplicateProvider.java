package com.ruoyi.framework.mybatis;

import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.provider.EntitySqlProvider;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;
import com.mybatisflex.core.util.ClassUtil;
import com.mybatisflex.core.util.StringUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.builder.annotation.ProviderContext;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 插入或更新 SQL 提供器
 * <p>
 * 在 MyBatis-Flex 批量插入 SQL 后追加 ON DUPLICATE KEY UPDATE 子句，
 * 更新列由实体字段上的 {@link UpdateOnDuplicate} 注解决定。
 *
 * @author haihuoshu
 * @see BaseMapperPlus#insertOnDuplicateKeyUpdate(Collection)
 */
public class UpdateOnDuplicateProvider {

    /**
     * 表名 -> 冲突更新列片段缓存（表结构固定，解析一次后长期复用）
     */
    private static final Map<String, Set<String>> UPDATE_COLUMNS_CACHE = new ConcurrentHashMap<>();

    /**
     * 构建完整的插入或更新 SQL
     *
     * @param params  MyBatis 参数（含 FlexConsts.ENTITIES 实体集合）
     * @param context Provider 上下文，用于获取 Mapper 类型
     * @return INSERT ... ON DUPLICATE KEY UPDATE ... 语句
     */
    public static String provideSql(Map<?, ?> params, ProviderContext context) {
        String insertBatch = EntitySqlProvider.insertBatch(params, context);
        String updateOnDuplicate = buildUpdateOnDuplicate(TableInfoFactory.ofMapperClass(context.getMapperType()));
        return insertBatch + updateOnDuplicate;
    }

    /**
     * 构建 ON DUPLICATE KEY UPDATE 子句（无标记字段时返回空串）
     *
     * @param tableInfo 表信息
     * @return 更新子句 SQL 片段
     */
    private static String buildUpdateOnDuplicate(TableInfo tableInfo) {
        Set<String> updateColumns = obtainUpdateOnDuplicateColumns(tableInfo);
        if (CollectionUtils.isNotEmpty(updateColumns)) {
            return " ON DUPLICATE KEY UPDATE " + String.join(", ", updateColumns);
        }
        return "";
    }

    /**
     * 获取冲突更新列片段，优先读取缓存
     *
     * @param tableInfo 表信息
     * @return 更新列片段集合
     */
    private static Set<String> obtainUpdateOnDuplicateColumns(TableInfo tableInfo) {
        // computeIfAbsent 保证缓存读写原子性，避免并发下重复解析
        return UPDATE_COLUMNS_CACHE.computeIfAbsent(tableInfo.getTableNameWithSchema(),
                key -> resolveUpdateOnDuplicateColumns(tableInfo));
    }

    /**
     * 解析实体类中被 {@link UpdateOnDuplicate} 标记的字段，生成更新列片段
     *
     * @param tableInfo 表信息
     * @return 更新列片段集合（保持字段声明顺序）
     */
    private static Set<String> resolveUpdateOnDuplicateColumns(TableInfo tableInfo) {
        // LinkedHashSet 保证更新列顺序稳定，便于 SQL 缓存与问题排查
        Set<String> updateColumns = new LinkedHashSet<>();
        List<Field> fieldList = ClassUtil.getAllFields(tableInfo.getEntityClass());
        if (CollectionUtils.isEmpty(fieldList)) {
            return updateColumns;
        }

        for (Field field : fieldList) {
            UpdateOnDuplicate annotation = field.getAnnotation(UpdateOnDuplicate.class);
            if (Objects.isNull(annotation)) {
                continue;
            }

            // 直接查属性-列映射表（仅收录已映射列，含主键），未映射字段（如 @Column(ignore = true)）跳过
            // 注意：不能用 getColumnByProperty，其在无映射时会回退返回属性名本身，导致防护失效
            String column = tableInfo.getPropertyColumnMapping().get(field.getName());
            if (StringUtil.noText(column)) {
                continue;
            }

            String wrappedColumn = DialectFactory.getDialect().wrap(column);
            String fragment;
            if (annotation.setNull()) {
                // 允许置 NULL：直接使用新值覆盖
                fragment = String.format("%1$s = VALUES(%1$s)", wrappedColumn);
            } else {
                // 默认：新值为 NULL 时使用 IFNULL 保留原值
                fragment = String.format("%1$s = IFNULL(VALUES(%1$s), %1$s)", wrappedColumn);
            }
            updateColumns.add(fragment);
        }

        return updateColumns;
    }

}
