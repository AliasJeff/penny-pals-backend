package com.alias.controller;

import com.alias.common.BaseResponse;
import com.alias.common.ResultUtils;
import com.alias.model.entity.User;
import com.alias.service.LedgerInviteService;
import com.alias.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/invite")
public class LedgerInviteController {

    @Resource
    private LedgerInviteService ledgerInviteService;

    @Resource
    private UserService userService;

    /**
     * 生成邀请码
     */
    @PostMapping("/create")
    public BaseResponse<String> createInvite(@RequestParam Long ledgerId, HttpServletRequest request) {
        User user = userService.getLoginUser(request);

        String code = ledgerInviteService.createInviteCode(ledgerId, user.getId());

        return ResultUtils.success(code);
    }

    /**
     * 使用邀请码加入账本
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinByInviteCode(@RequestParam String code, HttpServletRequest request) {
        User user = userService.getLoginUser(request);

        ledgerInviteService.joinByInviteCode(code, user.getId());
        return ResultUtils.success(true);
    }
}
