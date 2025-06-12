package com.alias.controller;

import com.alias.common.BaseResponse;
import com.alias.common.ErrorCode;
import com.alias.common.ResultUtils;
import com.alias.exception.BusinessException;
import com.alias.exception.ThrowUtils;
import com.alias.model.entity.LedgerUser;
import com.alias.model.entity.User;
import com.alias.model.vo.LedgerUserVO;
import com.alias.service.LedgerUserService;
import com.alias.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/ledger/user")
@Slf4j
public class LedgerUserController {

    @Resource
    private LedgerUserService ledgerUserService;

    @Resource
    private UserService userService;

    /**
     * 添加单个账本成员
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> addLedgerUser(@RequestBody LedgerUser ledgerUser, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 仅账本 OWNER 可添加成员
        ThrowUtils.throwIf(!ledgerUserService.isUserOwner(ledgerUser.getLedgerId(), loginUser.getId()),
                ErrorCode.NO_AUTH_ERROR, "无权限添加成员");

        boolean result = ledgerUserService.addLedgerUser(ledgerUser);
        return ResultUtils.success(result);
    }

    /**
     * 批量添加账本成员
     */
    @PostMapping("/add/batch")
    public BaseResponse<Boolean> addLedgerUsersBatch(@RequestBody List<LedgerUser> ledgerUsers, HttpServletRequest request) {
        if (ledgerUsers == null || ledgerUsers.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "成员列表不能为空");
        }

        Long ledgerId = ledgerUsers.get(0).getLedgerId();
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(!ledgerUserService.isUserOwner(ledgerId, loginUser.getId()),
                ErrorCode.NO_AUTH_ERROR, "无权限添加成员");

        boolean result = ledgerUserService.addLedgerUsersBatch(ledgerUsers);
        return ResultUtils.success(result);
    }

    /**
     * 更新账本成员角色
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateLedgerUserRole(@RequestBody LedgerUser ledgerUser, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(!ledgerUserService.isUserOwner(ledgerUser.getLedgerId(), loginUser.getId()),
                ErrorCode.NO_AUTH_ERROR, "无权限修改成员角色");

        boolean result = ledgerUserService.updateLedgerUserRole(ledgerUser);
        return ResultUtils.success(result);
    }

    /**
     * 从账本中移除某个用户
     */
    @PostMapping("/remove")
    public BaseResponse<Boolean> removeUserFromLedger(@RequestParam Long ledgerId, @RequestParam Long userId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(!ledgerUserService.isUserOwner(ledgerId, loginUser.getId()),
                ErrorCode.NO_AUTH_ERROR, "无权限移除成员");

        boolean result = ledgerUserService.removeUserFromLedger(ledgerId, userId);
        return ResultUtils.success(result);
    }

    /**
     * 用户主动退出账本
     */
    @PostMapping("/exit")
    public BaseResponse<Boolean> exitLedger(@RequestParam Long ledgerId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        boolean result = ledgerUserService.exitLedger(ledgerId, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 获取账本所有成员
     */
    @GetMapping("/list")
    public BaseResponse<List<LedgerUserVO>> listLedgerUsers(@RequestParam Long ledgerId, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        // 拥有任意角色都能查看成员列表
        ThrowUtils.throwIf(
                !ledgerUserService.isUserOwner(ledgerId, loginUser.getId()) &&
                        !ledgerUserService.isUserEditor(ledgerId, loginUser.getId()) &&
                        !ledgerUserService.isUserViewer(ledgerId, loginUser.getId()),
                ErrorCode.NO_AUTH_ERROR, "无权限查看账本成员");

        List<LedgerUser> userList = ledgerUserService.listByLedgerId(ledgerId);
        List<LedgerUserVO> ledgerUserVOList = ledgerUserService.toLedgerUserVOList(userList);
        return ResultUtils.success(ledgerUserVOList);
    }
}
