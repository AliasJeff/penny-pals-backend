package com.alias.service;

import com.alias.model.entity.LedgerInvite;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author alias
* @description 针对表【ledger_invite(账本邀请表)】的数据库操作Service
* @createDate 2025-06-13 15:16:31
*/
public interface LedgerInviteService extends IService<LedgerInvite> {
    /**
     * 创建邀请码（邀请记录）
     * @param ledgerId 账本ID
     * @param userId 邀请者ID
     * @return 邀请码
     */
    String createInviteCode(Long ledgerId, Long userId);

    /**
     * 用户使用邀请码加入账本
     * @param code 邀请码
     * @param invitedUserId 被邀请人ID
     */
    void joinByInviteCode(String code, Long invitedUserId);
}
