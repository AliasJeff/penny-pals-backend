package com.alias.service;

import com.alias.model.entity.BudgetConfig;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author alias
* @description 针对表【budget_config(预算配置表)】的数据库操作Service
* @createDate 2025-06-09 21:46:56
*/
public interface BudgetConfigService extends IService<BudgetConfig> {

    /**
     * 创建或更新预算配置（若存在则更新）
     * @param config 预算配置
     * @return 是否成功
     */
    boolean saveOrUpdateBudget(BudgetConfig config);

    /**
     * 删除预算配置（逻辑删除）
     * @param id 预算配置ID
     * @return 是否成功
     */
    boolean deleteBudget(Long id);

    /**
     * 获取账本的总体预算（userId 为空）
     * @param ledgerId 账本ID
     * @return 总体预算配置（可能为 null）
     */
    BudgetConfig getLedgerBudget(Long ledgerId);

    /**
     * 获取某用户在某账本中的预算
     * @param ledgerId 账本ID
     * @param userId 用户ID
     * @return 个人预算配置（可能为 null）
     */
    BudgetConfig getUserBudget(Long ledgerId, Long userId);

    /**
     * 查询账本中所有有效的预算配置（含个人和总预算）
     * @param ledgerId 账本ID
     * @return 预算配置列表
     */
    List<BudgetConfig> listBudgetsByLedger(Long ledgerId);
}
