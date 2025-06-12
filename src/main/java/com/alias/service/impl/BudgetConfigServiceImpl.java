package com.alias.service.impl;

import com.alias.common.ErrorCode;
import com.alias.exception.BusinessException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alias.model.entity.BudgetConfig;
import com.alias.service.BudgetConfigService;
import com.alias.mapper.BudgetConfigMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class BudgetConfigServiceImpl extends ServiceImpl<BudgetConfigMapper, BudgetConfig> implements BudgetConfigService {

    @Override
    public boolean saveOrUpdateBudget(BudgetConfig config) {
        if (config == null || config.getLedgerId() == null || config.getBudgetAmount() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数无效");
        }

        QueryWrapper<BudgetConfig> query = new QueryWrapper<BudgetConfig>()
                .eq("ledger_id", config.getLedgerId())
                .eq(config.getUserId() == null, "user_id", null)
                .eq(config.getUserId() != null, "user_id", config.getUserId())
                .isNull("delete_time");

        BudgetConfig existing = this.getOne(query);

        Date now = new Date();
        if (existing != null) {
            config.setId(existing.getId());
            config.setUpdateTime(now);
            return this.updateById(config);
        }

        return this.save(config);
    }

    @Override
    public boolean deleteBudget(Long id) {
        if (id == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "预算ID不能为空");
        }

        BudgetConfig config = this.getById(id);
        if (config == null || config.getDeleteTime() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "预算配置不存在");
        }

        BudgetConfig update = new BudgetConfig();
        update.setId(id);
        update.setDeleteTime(new Date());
        update.setUpdateTime(new Date());

        return this.updateById(update);
    }

    @Override
    public BudgetConfig getLedgerBudget(Long ledgerId) {
        if (ledgerId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本ID不能为空");
        }

        return this.getOne(new QueryWrapper<BudgetConfig>()
                .eq("ledger_id", ledgerId)
                .isNull("user_id")
                .isNull("delete_time"));
    }

    @Override
    public BudgetConfig getUserBudget(Long ledgerId, Long userId) {
        if (ledgerId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数无效");
        }

        return this.getOne(new QueryWrapper<BudgetConfig>()
                .eq("ledger_id", ledgerId)
                .eq("user_id", userId)
                .isNull("delete_time"));
    }

    @Override
    public List<BudgetConfig> listBudgetsByLedger(Long ledgerId) {
        if (ledgerId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本ID不能为空");
        }

        return this.list(new QueryWrapper<BudgetConfig>()
                .eq("ledger_id", ledgerId)
                .isNull("delete_time")
                .orderByAsc("user_id")); // 总体预算优先排在前面
    }
}




