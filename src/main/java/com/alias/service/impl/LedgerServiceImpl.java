package com.alias.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alias.common.ErrorCode;
import com.alias.exception.BusinessException;
import com.alias.exception.ThrowUtils;
import com.alias.mapper.EntryMapper;
import com.alias.mapper.LedgerUserMapper;
import com.alias.model.entity.Entry;
import com.alias.model.entity.LedgerUser;
import com.alias.model.entity.User;
import com.alias.model.enums.LedgerRoleEnum;
import com.alias.model.vo.EntryVO;
import com.alias.model.vo.LedgerDetailVO;
import com.alias.model.vo.LedgerUserVO;
import com.alias.service.EntryService;
import com.alias.service.LedgerUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alias.model.entity.Ledger;
import com.alias.service.LedgerService;
import com.alias.mapper.LedgerMapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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

    @Resource
    private LedgerUserService ledgerUserService;

    @Resource
    private EntryMapper entryMapper;

    @Resource
    private EntryService entryService;

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

        ThrowUtils.throwIf(ledger == null, ErrorCode.NOT_FOUND_ERROR, "账本不存在");
        return ledger;
    }

    @Override
    public LedgerDetailVO getLedgerDetailById(Long id) {
        Ledger ledger = getLedgerById(id);
        ThrowUtils.throwIf(ledger == null, ErrorCode.NOT_FOUND_ERROR, "账本不存在");

        List<LedgerUser> ledgerUsers = ledgerUserService.listByLedgerId(ledger.getId());
        List<LedgerUserVO> ledgerUserVOList = ledgerUserService.toLedgerUserVOList(ledgerUsers);

        QueryWrapper<Entry> entryQueryWrapper = new QueryWrapper<>();
        entryQueryWrapper.eq("ledger_id", ledger.getId());
        entryQueryWrapper.isNull("delete_time");
        entryQueryWrapper.orderByDesc("date");
        List<EntryVO> entries = entryService.listEntriesByCondition(ledger.getId(), null, null, null, null, "date", "desc", null, null);

        return getLedgerDetailVO(ledger, ledgerUserVOList, entries);
    }

    @NotNull
    private static LedgerDetailVO getLedgerDetailVO(Ledger ledger, List<LedgerUserVO> ledgerUserVOList, List<EntryVO> entries) {
        LedgerDetailVO ledgerDetailVO = new LedgerDetailVO();
        ledgerDetailVO.setId(ledger.getId());
        ledgerDetailVO.setName(ledger.getName());
        ledgerDetailVO.setIcon(ledger.getIcon());
        ledgerDetailVO.setDescription(ledger.getDescription());
        ledgerDetailVO.setCreateTime(ledger.getCreateTime());
        ledgerDetailVO.setUpdateTime(ledger.getUpdateTime());

        ledgerDetailVO.setMembers(ledgerUserVOList);
        ledgerDetailVO.setEntries(entries);
        if (!entries.isEmpty()) {
            Date updateTime = entries.stream().map(EntryVO::getUpdateTime).max(Date::compareTo).get();
            ledgerDetailVO.setUpdateTime(updateTime);
        }
        return ledgerDetailVO;
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
    public List<LedgerDetailVO> getLedgerDetailListByUserId(Long userId) {
        List<Ledger> ledgers = listLedgersByUserId(userId);
        if (CollUtil.isEmpty(ledgers)) {
            return Collections.emptyList();
        }

        List<Long> ledgerIds = ledgers.stream().map(Ledger::getId).collect(Collectors.toList());

        // 查询所有账本的成员
        QueryWrapper<LedgerUser> ledgerUserQueryWrapper = new QueryWrapper<>();
        ledgerUserQueryWrapper.in("ledger_id", ledgerIds);
        ledgerUserQueryWrapper.isNull("delete_time");
        List<LedgerUser> allMembers = ledgerUserMapper.selectList(ledgerUserQueryWrapper);
        Map<Long, List<LedgerUser>> membersMap = allMembers.stream().collect(Collectors.groupingBy(LedgerUser::getLedgerId));

        // 查询所有账目
        List<EntryVO> allEntries = entryMapper.listEntriesByLedgerIds(ledgerIds);
        // 分组：Map<ledgerId, List<EntryVO>>
        Map<Long, List<EntryVO>> entriesMap = allEntries.stream()
                .collect(Collectors.groupingBy(EntryVO::getLedgerId));

        // 组装VO
        List<LedgerDetailVO> ledgerDetailVOList = new ArrayList<>();
        for (Ledger ledger : ledgers) {
            List<LedgerUser> members = membersMap.getOrDefault(ledger.getId(), Collections.emptyList());
            List<LedgerUserVO> memberVOs = ledgerUserService.toLedgerUserVOList(members);
            List<EntryVO> entries = entriesMap.getOrDefault(ledger.getId(), Collections.emptyList());

            LedgerDetailVO ledgerDetailVO = getLedgerDetailVO(ledger, memberVOs, entries);

            ledgerDetailVOList.add(ledgerDetailVO);
        }

        ledgerDetailVOList.sort(Comparator.comparing(LedgerDetailVO::getUpdateTime, Comparator.nullsLast(Comparator.reverseOrder())));

        return ledgerDetailVOList;
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




