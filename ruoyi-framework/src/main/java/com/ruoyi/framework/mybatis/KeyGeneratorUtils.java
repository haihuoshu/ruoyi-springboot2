package com.ruoyi.framework.mybatis;

import com.mybatisflex.core.keygen.KeyGeneratorFactory;
import com.mybatisflex.core.keygen.KeyGenerators;

/**
 * 主键生成工具类
 *
 * @author haihuoshu
 */
public class KeyGeneratorUtils {

    /**
     * 生成 Flex 雪花算法主键
     * <p>
     * 与 {@code @Id(keyType = KeyType.Generator, value = KeyGenerators.flexId)} 的生成策略一致，
     * 可用于业务代码中提前生成主键。
     *
     * @return 全局唯一的 String 型主键
     */
    public static Long flexId() {
        Object value = KeyGeneratorFactory.getKeyGenerator(KeyGenerators.flexId)
                .generate(null, null);

        return Long.parseLong(value.toString());
    }

}
