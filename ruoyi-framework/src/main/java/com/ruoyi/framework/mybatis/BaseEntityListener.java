package com.ruoyi.framework.mybatis;

import com.mybatisflex.annotation.InsertListener;
import com.mybatisflex.annotation.UpdateListener;
import com.mybatisflex.core.util.ObjectUtil;
import com.mybatisflex.core.util.StringUtil;
import com.ruoyi.common.utils.SecurityUtils;

import java.time.LocalDateTime;

/**
 * {@link BaseEntity} 公共字段自动填充监听器
 * <p>
 * 插入时填充主键、删除标记、创建人/创建时间，并同步填充更新人/更新时间；
 * 更新时填充更新人/更新时间。仅填充空值，不覆盖业务代码的显式赋值。
 *
 * @author haihuoshu
 */
public class BaseEntityListener implements InsertListener, UpdateListener {

    @Override
    public void onInsert(Object entity) {
        if (entity instanceof BaseEntity) {
            this.doInsert((BaseEntity) entity);
        }
    }

    @Override
    public void onUpdate(Object entity) {
        if (entity instanceof BaseEntity) {
            this.doUpdate((BaseEntity) entity);
        }
    }

    /**
     * 插入时填充公共字段
     *
     * @param entity 实体对象
     */
    public void doInsert(BaseEntity entity) {
        if (ObjectUtil.areNull(entity.getId())) {
            entity.setId(KeyGeneratorUtils.flexId());
        }
        if (ObjectUtil.areNull(entity.getDeleted())) {
            entity.setDeleted(Boolean.FALSE);
        }
        if (StringUtil.noText(entity.getCreatedBy())) {
            entity.setCreatedBy(SecurityUtils.getUsername());
        }
        if (ObjectUtil.areNull(entity.getCreatedTime())) {
            entity.setCreatedTime(LocalDateTime.now());
        }
        // 插入时同步填充更新人/更新时间，保证新增数据审计字段完整
        this.doUpdate(entity);
    }

    /**
     * 更新时填充公共字段
     *
     * @param entity 实体对象
     */
    public void doUpdate(BaseEntity entity) {
        if (StringUtil.noText(entity.getUpdatedBy())) {
            entity.setUpdatedBy(SecurityUtils.getUsername());
        }
        if (ObjectUtil.areNull(entity.getUpdatedTime())) {
            entity.setUpdatedTime(LocalDateTime.now());
        }
    }

}
