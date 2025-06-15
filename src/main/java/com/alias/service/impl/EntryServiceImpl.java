package com.alias.service.impl;

import com.alias.common.ErrorCode;
import com.alias.exception.BusinessException;
import com.alias.model.vo.EntryVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alias.model.entity.Entry;
import com.alias.service.EntryService;
import com.alias.mapper.EntryMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class EntryServiceImpl extends ServiceImpl<EntryMapper, Entry> implements EntryService {

    @Resource
    private EntryMapper entryMapper;

    @Override
    public boolean createEntry(Entry entry) {
        if (entry == null || entry.getLedgerId() == null || entry.getUserId() == null || entry.getAmount() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数无效");
        }

        return this.save(entry);
    }

    @Override
    public boolean deleteEntry(Long entryId, Long userId) {
        if (entryId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数无效");
        }

        Entry entry = this.getById(entryId);
        if (entry == null || entry.getDeleteTime() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "账目不存在");
        }

        // 只允许记录人自己删除
        if (!entry.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限删除该账目");
        }

        Entry update = new Entry();
        update.setId(entryId);
        update.setDeleteTime(new Date());

        return this.updateById(update);
    }

    @Override
    public boolean updateEntry(Entry entry, Long userId) {
        if (entry == null || entry.getId() == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数无效");
        }

        Entry old = this.getById(entry.getId());
        if (old == null || old.getDeleteTime() != null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "账目不存在");
        }

        if (!old.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限修改该账目");
        }

        return this.updateById(entry);
    }

    @Override
    public List<EntryVO> listEntriesByCondition(Long ledgerId, Long userId, Date date, String category, String keyword, String orderBy, String orderDirection, Date startDate, Date endDate) {
        return entryMapper.listEntriesWithUser(ledgerId, userId, date, startDate, endDate, category, keyword, orderBy, orderDirection);
    }
}




