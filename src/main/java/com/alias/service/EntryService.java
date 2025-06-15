package com.alias.service;

import com.alias.model.entity.Entry;
import com.alias.model.vo.EntryVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
* @author alias
* @description 针对表【entry(账目记录表)】的数据库操作Service
* @createDate 2025-06-09 21:47:07
*/
public interface EntryService extends IService<Entry> {

    /**
     * 创建账目记录
     * @param entry 账目信息
     * @return 是否成功
     */
    boolean createEntry(Entry entry);

    /**
     * 删除账目记录（逻辑删除）
     * @param entryId 账目ID
     * @param userId 当前操作用户ID，用于校验权限
     * @return 是否成功
     */
    boolean deleteEntry(Long entryId, Long userId);

    /**
     * 更新账目记录
     * @param entry 更新内容（包含 id）
     * @param userId 当前操作用户ID
     * @return 是否成功
     */
    boolean updateEntry(Entry entry, Long userId);

    /**
     * 查询用户在某个账本下的所有未删除账目记录
     * @param ledgerId 账本ID
     * @param userId 用户ID
     * @return 账目列表
     */
    List<EntryVO> listEntriesByCondition(Long ledgerId, Long userId, Date date, String category, String keyword, String orderBy, String orderDirection, Date startDate, Date endDate);

}
