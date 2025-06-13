package com.alias.service.impl;

import com.alias.common.ErrorCode;
import com.alias.exception.BusinessException;
import com.alias.mapper.LedgerUserMapper;
import com.alias.model.entity.LedgerUser;
import com.alias.model.entity.User;
import com.alias.model.enums.LedgerRoleEnum;
import com.alias.model.vo.LedgerUserVO;
import com.alias.service.LedgerUserService;
import com.alias.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Slf4j
@Service
public class LedgerUserServiceImpl extends ServiceImpl<LedgerUserMapper, LedgerUser>
        implements LedgerUserService {

    @Resource
    private LedgerUserMapper ledgerUserMapper;

    @Resource
    private UserService userService;

    @Override
    public boolean addLedgerUser(LedgerUser ledgerUser) {
        if (ledgerUser == null || ledgerUser.getLedgerId() == null || ledgerUser.getUserId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本用户参数无效");
        }

        // 校验是否已存在
        LedgerUser existing = this.getOne(new QueryWrapper<LedgerUser>()
                .eq("ledger_id", ledgerUser.getUserId())
                .eq("user_id", ledgerUser.getLedgerId())
                .isNull("delete_time"));

        if (existing != null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "该用户已在账本中");
        }

        LedgerUser ledgerUserToSave = new LedgerUser();
        ledgerUserToSave.setLedgerId(ledgerUser.getLedgerId());
        ledgerUserToSave.setUserId(ledgerUser.getUserId());
        ledgerUserToSave.setRole(LedgerRoleEnum.EDITOR.getValue());
        int rows = ledgerUserMapper.insert(ledgerUserToSave);
        if (rows != 1) {
            log.error("Failed to add ledger user: {}", ledgerUser);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "添加账本用户失败");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addLedgerUsersBatch(List<LedgerUser> ledgerUsers) {
        if (ledgerUsers == null || ledgerUsers.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户列表不能为空");
        }

        for (LedgerUser user : ledgerUsers) {
            if (user.getLedgerId() == null || user.getUserId() == null) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本ID或用户ID不能为空");
            }
        }

        // 检查是否有重复的用户
        for (LedgerUser user : ledgerUsers) {
            LedgerUser existing = this.getOne(new QueryWrapper<LedgerUser>()
                    .eq("ledger_id", user.getLedgerId())
                    .eq("user_id", user.getUserId())
                    .isNull("delete_time"));

            if (existing != null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "用户已存在于账本中: " + user.getUserId());
            }
        }
        // 设置默认角色为 EDITOR
        for (LedgerUser user : ledgerUsers) {
            user.setRole(LedgerRoleEnum.EDITOR.getValue());
        }
        return this.saveBatch(ledgerUsers);
    }

    @Override
    public boolean updateLedgerUserRole(LedgerUser ledgerUser) {
        if (ledgerUser == null || ledgerUser.getId() == null || ledgerUser.getRole() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "更新参数无效");
        }

        ledgerUser.setUpdateTime(new Date());

        return this.updateById(ledgerUser);
    }

    @Override
    public List<LedgerUser> listByLedgerId(Long ledgerId) {
        if (ledgerId == null || ledgerId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本ID无效");
        }

        return this.list(new QueryWrapper<LedgerUser>()
                .eq("ledger_id", ledgerId)
                .isNull("delete_time"));
    }

    @Override
    public List<LedgerUser> listByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }

        return this.list(new QueryWrapper<LedgerUser>()
                .eq("user_id", userId)
                .isNull("delete_time"));
    }

    @Override
    public boolean logicDeleteByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户ID无效");
        }

        LedgerUser updateEntity = new LedgerUser();
        Date now = new Date();
        updateEntity.setDeleteTime(now);

        int rows = this.getBaseMapper().update(
                updateEntity,
                new QueryWrapper<LedgerUser>()
                        .eq("user_id", userId)
                        .isNull("delete_time")
        );

        return rows >= 0;
    }

    @Override
    public boolean exitLedger(Long ledgerId, Long userId) {
        if (ledgerId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数无效");
        }

        LedgerUser update = new LedgerUser();
        update.setDeleteTime(new Date());

        int rows = this.getBaseMapper().update(update, new QueryWrapper<LedgerUser>()
                .eq("ledger_id", ledgerId)
                .eq("user_id", userId)
                .isNull("delete_time"));

        if (rows != 1) {
            log.error("Failed to exit ledger: ledgerId={}, userId={}", ledgerId, userId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "退出账本失败");
        }
        return true;
    }

    @Override
    public boolean isUserOwner(Long ledgerId, Long userId) {
        return checkUserRole(ledgerId, userId, LedgerRoleEnum.OWNER);
    }

    @Override
    public boolean isUserEditor(Long ledgerId, Long userId) {
        return checkUserRole(ledgerId, userId, LedgerRoleEnum.EDITOR);
    }

    @Override
    public boolean isUserViewer(Long ledgerId, Long userId) {
        return checkUserRole(ledgerId, userId, LedgerRoleEnum.VIEWER);
    }

    @Override
    public boolean isMember(Long ledgerId, Long userId) {
        return checkUserRole(ledgerId, userId, null);
    }

    /**
     * 通用角色校验方法
     */
    private boolean checkUserRole(Long ledgerId, Long userId, LedgerRoleEnum role) {
        if (ledgerId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数无效");
        }

        QueryWrapper<LedgerUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ledger_id", ledgerId);
        queryWrapper.eq("user_id", userId);
        if (role != null) {
            queryWrapper.eq("role", role.getValue());
        }
        queryWrapper.isNull("delete_time");

        LedgerUser ledgerUser = this.getOne(queryWrapper);

        return ledgerUser != null;
    }

    @Override
    public boolean removeUserFromLedger(Long ledgerId, Long userId) {
        if (ledgerId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数无效");
        }

        LedgerUser update = new LedgerUser();
        update.setDeleteTime(new Date());

        int rows = this.getBaseMapper().update(update, new QueryWrapper<LedgerUser>()
                .eq("ledger_id", ledgerId)
                .eq("user_id", userId)
                .isNull("delete_time"));

        if (rows != 1) {
            log.error("Failed to remove user from ledger: ledgerId={}, userId={}", ledgerId, userId);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "移除用户失败");
        }
        return true;
    }

    @Override
    public List<LedgerUserVO> toLedgerUserVOList(List<LedgerUser> ledgerUsers) {
        if (CollectionUtils.isEmpty(ledgerUsers)) {
            return Collections.emptyList();
        }

        List<LedgerUserVO> voList = new ArrayList<>();

        for (LedgerUser ledgerUser : ledgerUsers) {
            LedgerUserVO vo = new LedgerUserVO();

            // 拷贝 ledgerUser 的字段
            BeanUtils.copyProperties(ledgerUser, vo);

            // 查询 user 信息
            User user = userService.getById(ledgerUser.getUserId());
            if (user != null) {
                vo.setUserId(user.getId());
                vo.setUsername(user.getUsername());
                vo.setAvatar(user.getAvatar());
                vo.setEmail(user.getEmail());
                vo.setPhoneNumber(user.getPhoneNumber());
                vo.setBirthday(user.getBirthday());
                vo.setUnionId(user.getUnionId());
                vo.setOpenId(user.getOpenId());
            }

            voList.add(vo);
        }

        // 排序：先按角色优先级，再按更新时间降序
        voList.sort(Comparator
                .comparingInt((LedgerUserVO vo) -> getRolePriority(vo.getRole()))
                .thenComparing(LedgerUserVO::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder()))
        );

        return voList;
    }

    private int getRolePriority(String role) {
        if (LedgerRoleEnum.OWNER.getValue().equalsIgnoreCase(role)) {
            return 0;
        } else if (LedgerRoleEnum.EDITOR.getValue().equalsIgnoreCase(role)) {
            return 1;
        } else if (LedgerRoleEnum.VIEWER.getValue().equalsIgnoreCase(role)) {
            return 2;
        } else {
            return Integer.MAX_VALUE; // 未知角色排最后
        }
    }
}