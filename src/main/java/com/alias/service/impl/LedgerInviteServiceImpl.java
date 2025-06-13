package com.alias.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alias.common.ErrorCode;
import com.alias.constant.LedgerInviteConstant;
import com.alias.exception.BusinessException;
import com.alias.exception.ThrowUtils;
import com.alias.mapper.LedgerInviteMapper;
import com.alias.model.entity.LedgerUser;
import com.alias.model.enums.LedgerRoleEnum;
import com.alias.service.LedgerUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.alias.model.entity.LedgerInvite;
import com.alias.service.LedgerInviteService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.UUID;

/**
* @author alias
* @description 针对表【ledger_invite(账本邀请表)】的数据库操作Service实现
* @createDate 2025-06-13 15:16:31
*/
@Service
public class LedgerInviteServiceImpl extends ServiceImpl<LedgerInviteMapper, LedgerInvite>
    implements LedgerInviteService{

    @Resource
    private LedgerUserService ledgerUserService;

    @Override
    public String createInviteCode(Long ledgerId, Long userId) {
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        LedgerInvite invite = new LedgerInvite();
        invite.setLedgerId(ledgerId);
        invite.setUserId(userId);
        invite.setCode(code);
        invite.setExpireTime(DateUtil.offsetDay(new Date(), LedgerInviteConstant.INVITE_CODE_VALID_DAYS)); // 默认7天有效

        this.save(invite);
        return code;
    }

    @Override
    public void joinByInviteCode(String code, Long invitedUserId) {
        LedgerInvite invite = lambdaQuery()
                .eq(LedgerInvite::getCode, code)
                .isNull(LedgerInvite::getDeleteTime)
                .one();

        if (invite == null || invite.getExpireTime().before(new Date())) {
            if (invite != null && invite.getExpireTime().before(new Date())) {
                invite.setDeleteTime(new Date());
                this.updateById(invite);
            }
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "邀请码无效或已过期");
        }

        // 添加 LedgerUser
        LedgerUser newUser = new LedgerUser();
        newUser.setLedgerId(invite.getLedgerId());
        newUser.setUserId(invitedUserId);
        newUser.setRole(LedgerRoleEnum.EDITOR.getValue());
        ledgerUserService.addLedgerUser(newUser);

        // 更新邀请记录
        invite.setInvitedUserId(invitedUserId);
        invite.setDeleteTime(new Date());
        this.updateById(invite);
    }
}




