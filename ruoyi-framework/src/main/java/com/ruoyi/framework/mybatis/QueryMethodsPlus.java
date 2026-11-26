package com.ruoyi.framework.mybatis;

import com.mybatisflex.core.query.QueryColumn;
import com.mybatisflex.core.query.QueryTable;
import com.mybatisflex.core.table.TableInfo;
import com.mybatisflex.core.table.TableInfoFactory;

/**
 * QueryWrapper 查询方法扩展
 *
 * @author haihuoshu
 */
@SuppressWarnings("unused")
public class QueryMethodsPlus {

    /**
     * 构建实体对应表的所有列（table.*）
     * <p>
     * 用于多表联查时选取指定表的全部列，避免列名歧义。
     *
     * @param entityClass 实体类（需标注 @Table）
     * @return 形如 table.* 的查询列
     */
    public static QueryColumn allColumn(Class<?> entityClass) {
        TableInfo tableInfo = TableInfoFactory.ofEntityClass(entityClass);
        QueryTable queryTable = new QueryTable(tableInfo.getSchema(), tableInfo.getTableName());
        return new QueryColumn(queryTable, "*");
    }

}
