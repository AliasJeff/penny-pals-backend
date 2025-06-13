package com.alias.service;

import com.alias.model.entity.LedgerUser;
import com.alias.model.vo.LedgerUserVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author alias
* @description 针对表【ledger_user(账本-用户权限表)】的数据库操作Service
* @createDate 2025-06-09 21:47:14
*/
public interface LedgerUserService extends IService<LedgerUser> {

    /**
     * 添加账本用户
     * @param ledgerUser 账本用户实体
     * @return 是否添加成功
     */
    boolean addLedgerUser(LedgerUser ledgerUser);

    /**
     * 批量添加账本用户
     * @param ledgerUsers 账本用户实体列表
     * @return 是否批量添加成功
     */
    boolean addLedgerUsersBatch(List<LedgerUser> ledgerUsers);

    /**
     * 更新账本用户角色
     * @param ledgerUser 账本用户实体
     * @return 是否更新成功
     */
    boolean updateLedgerUserRole(LedgerUser ledgerUser);

    /**
     * 根据账本ID查询账本用户列表
     * @param ledgerId 账本ID
     * @return 账本用户实体列表
     */
    List<LedgerUser> listByLedgerId(Long ledgerId);

    /**
     * 根据用户ID查询账本用户列表
     * @param userId 用户ID
     * @return 账本用户实体列表
     */
    List<LedgerUser> listByUserId(Long userId);

    /**
     * 逻辑删除账本用户
     * @param userId 用户ID
     * @return 是否逻辑删除成功
     */
    boolean logicDeleteByUserId(Long userId);

    /**
     * 用户主动退出账本
     * @param ledgerId 账本ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean exitLedger(Long ledgerId, Long userId);

    /**
     * 是否为账本所有者
     * @param ledgerId 账本ID
     * @param userId 用户ID
     * @return 是否为owner
     */
    boolean isUserOwner(Long ledgerId, Long userId);

    /**
     * 是否为账本编辑者
     * @param ledgerId 账本ID
     * @param userId 用户ID
     * @return 是否为editor
     */
    boolean isUserEditor(Long ledgerId, Long userId);

    /**
     * 是否为账本查看者
     * @param ledgerId 账本ID
     * @param userId 用户ID
     * @return 是否为viewer
     */
    boolean isUserViewer(Long ledgerId, Long userId);

    /**
     * 是否是账本成员
     *
     * @param ledgerId
     * @param userId
     * @return
     */
    boolean isMember(Long ledgerId, Long userId);

    /**
     * 从账本中移除用户（管理员）
     * @param ledgerId 账本ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean removeUserFromLedger(Long ledgerId, Long userId);

    /**
     * 转换为 LedgerUserVO
     * @param ledgerUsers
     * @return
     */
    List<LedgerUserVO> toLedgerUserVOList(List<LedgerUser> ledgerUsers);

}
