package com.ruoyi.framework.mybatis;

import com.mybatisflex.core.FlexGlobalConfig;
import com.mybatisflex.core.dialect.DbType;
import com.mybatisflex.core.dialect.DialectFactory;
import com.mybatisflex.core.dialect.KeywordWrap;
import com.mybatisflex.core.dialect.LimitOffsetProcessor;
import com.mybatisflex.core.dialect.impl.CommonsDialectImpl;
import com.mybatisflex.spring.boot.MyBatisFlexCustomizer;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Flex 全局配置
 * <p>
 * starter 自动配置初始化时会以默认全局配置回调 {@link #customize(FlexGlobalConfig)}，
 *
 * @author haihuoshu
 */
@Configuration
public class MyBatisFlexConfiguration implements MyBatisFlexCustomizer {

    @Override
    public void customize(FlexGlobalConfig globalConfig) {

        // 注册公共字段自动填充监听器（同一实例同时处理插入与更新）
        BaseEntityListener listener = new BaseEntityListener();
        globalConfig.registerInsertListener(listener, BaseEntity.class);
        globalConfig.registerUpdateListener(listener, BaseEntity.class);

        // MySQL方言：不包装关键字（避免生成反引号），分页使用 LIMIT 语法
        DialectFactory.registerDialect(DbType.MYSQL, new CommonsDialectImpl(KeywordWrap.NONE, LimitOffsetProcessor.MYSQL));
    }

}
