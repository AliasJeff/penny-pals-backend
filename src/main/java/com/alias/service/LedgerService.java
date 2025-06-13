package com.alias.service;

import com.alias.model.entity.Ledger;
import com.alias.model.vo.LedgerDetailVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author alias
* @description 针对表【ledger(账本表)】的数据库操作Service
* @createDate 2025-06-09 21:47:11
*/
public interface LedgerService extends IService<Ledger> {

    /**
     * 获取账本信息
     * @param id 账本ID
     * @return 账本实体
     */
    Ledger getLedgerById(Long id);

    /**
     * 获取账本具体信息，包括user, entries
     * @param id
     * @return
     */
    LedgerDetailVO getLedgerDetailById(Long id);

    /**
     * 根据用户ID获取账本列表
     * @param userId 用户ID
     * @return 账本列表
     */
    List<Ledger> listLedgersByUserId(Long userId);

    /**
     * 创建账本
     * @param ledger 账本实体
     * @param userId 创建者用户ID
     * @return 是否创建成功
     */
    boolean createLedger(Ledger ledger, long userId);

    /**
     * 更新账本信息
     * @param ledger 账本实体
     * @return 是否更新成功
     */
    boolean updateLedger(Ledger ledger);

    /**
     * 删除账本
     * @param id 账本ID
     * @return 是否删除成功
     */
    boolean deleteLedger(Long id);

    /**
     * 根据用户ID获取账本具体信息，包括user, entries
     * @param userId
     * @return
     */
    List<LedgerDetailVO> getLedgerDetailListByUserId(Long userId);
}
