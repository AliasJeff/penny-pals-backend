package com.alias.controller;

import com.alias.common.BaseResponse;
import com.alias.common.ErrorCode;
import com.alias.common.ResultUtils;
import com.alias.exception.BusinessException;
import com.alias.exception.ThrowUtils;
import com.alias.model.entity.Ledger;
import com.alias.model.entity.User;
import com.alias.service.LedgerService;
import com.alias.service.LedgerUserService;
import com.alias.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/ledger")
@Slf4j
public class LedgerController {

    @Resource
    private LedgerService ledgerService;

    @Resource
    private LedgerUserService ledgerUserService;

    @Resource
    private UserService userService;

    /**
     * 创建账本
     *
     * @param ledger
     * @param request
     * @return
     */
    @PostMapping("/create")
    public BaseResponse<Boolean> createLedger(@RequestBody Ledger ledger, HttpServletRequest request) {
        if (ledger == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = ledgerService.createLedger(ledger, loginUser.getId());
        return ResultUtils.success(result);
    }

    /**
     * 获取指定账本信息
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Ledger> getLedgerById(@RequestParam Long id, HttpServletRequest request) {
        Ledger ledger = ledgerService.getLedgerById(id);
        return ResultUtils.success(ledger);
    }

    /**
     * 更新账本
     *
     * @param ledger
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateLedger(@RequestBody Ledger ledger, HttpServletRequest request) {
        if (ledger == null || ledger.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本参数错误");
        }
        User loginUser = userService.getLoginUser(request);
        // 检查用户是否有权限更新该账本
        ThrowUtils.throwIf(
                !ledgerUserService.isUserOwner(ledger.getId(), loginUser.getId()) && !ledgerUserService.isUserEditor(ledger.getId(), loginUser.getId()),
                ErrorCode.NO_AUTH_ERROR,
                "用户无权限更新该账本");

        // 更新账本
        log.info("修改账本信息: {}, 用户: {}", ledger, loginUser);
        boolean result = ledgerService.updateLedger(ledger);
        return ResultUtils.success(result);
    }

    /**
     * 删除账本
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteLedger(@RequestParam Long id, HttpServletRequest request) {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本ID参数错误");
        }
        Ledger ledger = ledgerService.getLedgerById(id);
        if (ledger == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "账本不存在");
        }

        // 检查用户是否有权限更新该账本
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(
                !ledgerUserService.isUserOwner(ledger.getId(), loginUser.getId()),
                ErrorCode.NO_AUTH_ERROR,
                "用户无权限删除该账本");

        log.info("删除账本: {}, 用户: {}", id, loginUser);
        boolean result = ledgerService.deleteLedger(id);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户的账本列表
     *
     * @param request
     * @return
     */
    @GetMapping("/my/list")
    public BaseResponse<List<Ledger>> listMyLedgers(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        List<Ledger> ledgers = ledgerService.listLedgersByUserId(loginUser.getId());
        return ResultUtils.success(ledgers);
    }

}