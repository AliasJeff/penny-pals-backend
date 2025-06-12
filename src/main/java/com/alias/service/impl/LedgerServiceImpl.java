package com.alias.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alias.common.ErrorCode;
import com.alias.exception.BusinessException;
import com.alias.mapper.LedgerUserMapper;
import com.alias.model.entity.LedgerUser;
import com.alias.model.entity.User;
import com.alias.model.enums.LedgerRoleEnum;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alias.model.entity.Ledger;
import com.alias.service.LedgerService;
import com.alias.mapper.LedgerMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author alias
* @description 针对表【ledger(账本表)】的数据库操作Service实现
* @createDate 2025-06-09 21:47:11
*/
@Slf4j
@Service
public class LedgerServiceImpl extends ServiceImpl<LedgerMapper, Ledger>
    implements LedgerService{

    @Resource
    private LedgerMapper ledgerMapper;

    @Resource
    private LedgerUserMapper ledgerUserMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createLedger(Ledger ledger, long userId) {

        if (ledger == null || StrUtil.isBlank(ledger.getName())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本参数无效");
        }

        int insertResult = ledgerMapper.insert(ledger);
        if (insertResult != 1 || ledger.getId() == null) {
            log.error("创建账本失败: {}, userId: {}", ledger, userId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账本创建失败");
        }

        LedgerUser ledgerUser = new LedgerUser();
        ledgerUser.setLedgerId(ledger.getId());
        ledgerUser.setUserId(userId);
        ledgerUser.setRole(LedgerRoleEnum.OWNER.getValue());

        int userInsertResult = ledgerUserMapper.insert(ledgerUser);
        if (userInsertResult != 1) {
            log.error("创建账本用户关系失败: {}, userId: {}", ledgerUser, userId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账本用户关系创建失败");
        }

        return true;
    }

    @Override
    public Ledger getLedgerById(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本ID无效");
        }

        QueryWrapper<Ledger> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                    .isNull("delete_time");
        Ledger ledger = ledgerMapper.selectOne(queryWrapper);
        if (ledger == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "账本不存在");
        }
        return ledger;
    }

    @Override
    public boolean updateLedger(Ledger updatedLedger) {
        if (updatedLedger == null || updatedLedger.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本参数无效");
        }
        // 只允许更新特定字段
        Ledger existingLedger = ledgerMapper.selectById(updatedLedger.getId());
        if (existingLedger == null || existingLedger.getDeleteTime() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "账本不存在或已被删除");
        }
        // 检查更新的字段是否允许修改
        Ledger ledgerToUpdate = new Ledger();
        ledgerToUpdate.setId(updatedLedger.getId());
        if (StrUtil.isNotBlank(updatedLedger.getName())) {
            ledgerToUpdate.setName(updatedLedger.getName());
        }
        if (StrUtil.isNotBlank(updatedLedger.getDescription())) {
            ledgerToUpdate.setDescription(updatedLedger.getDescription());
        }
        if (updatedLedger.getIcon() != null) {
            ledgerToUpdate.setIcon(updatedLedger.getIcon());
        }
        int i = ledgerMapper.updateById(ledgerToUpdate);
        if (i != 1) {
            log.error("更新账本失败: {}", updatedLedger);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "账本更新失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteLedger(Long id) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本ID无效");
        }
        Date now = new Date();

        // 逻辑删除账本
        Ledger ledger = new Ledger();
        ledger.setId(id);
        ledger.setDeleteTime(now);
        ledgerMapper.updateById(ledger);

        return true;
    }

    @Override
    public List<Ledger> listLedgersByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }
        QueryWrapper<LedgerUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                    .isNull("delete_time");
        List<LedgerUser> ledgerUsers = ledgerUserMapper.selectList(queryWrapper);
        if (ledgerUsers.isEmpty()) {
            return Collections.emptyList();
        }
        List<Long> ledgerIds = ledgerUsers.stream()
                .map(LedgerUser::getLedgerId)
                .collect(Collectors.toList());

        QueryWrapper<Ledger> ledgerQueryWrapper = new QueryWrapper<>();
        ledgerQueryWrapper.in("id", ledgerIds)
                          .isNull("delete_time");
        return ledgerMapper.selectList(ledgerQueryWrapper);
    }

}




