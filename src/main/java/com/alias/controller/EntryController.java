package com.alias.controller;

import com.alias.common.BaseResponse;
import com.alias.common.DeleteRequest;
import com.alias.common.ErrorCode;
import com.alias.common.ResultUtils;
import com.alias.exception.BusinessException;
import com.alias.exception.ThrowUtils;
import com.alias.model.dto.entry.EntryQueryRequest;
import com.alias.model.entity.Entry;
import com.alias.model.entity.User;
import com.alias.service.EntryService;
import com.alias.service.LedgerUserService;
import com.alias.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/entry")
@Slf4j
public class EntryController {

    @Resource
    private EntryService entryService;

    @Resource
    private UserService userService;

    @Resource
    private LedgerUserService ledgerUserService;

    /**
     * 创建账目
     */
    @PostMapping("/create")
    public BaseResponse<Boolean> createEntry(@RequestBody Entry entry, HttpServletRequest request) {
        if (entry == null || entry.getLedgerId() == null || entry.getAmount() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账目信息不完整");
        }

        User user = userService.getLoginUser(request);
        entry.setUserId(user.getId());

        boolean result = entryService.createEntry(entry);
        return ResultUtils.success(result);
    }

    /**
     * 删除账目（逻辑删除）
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteEntry(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        User user = userService.getLoginUser(request);

        boolean result = entryService.deleteEntry(deleteRequest.getId(), user.getId());
        return ResultUtils.success(result);
    }

    /**
     * 更新账目
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateEntry(@RequestBody Entry entry, HttpServletRequest request) {
        if (entry == null || entry.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数错误");
        }

        log.info("entry: {}", entry);

        User user = userService.getLoginUser(request);

        boolean result = entryService.updateEntry(entry, user.getId());
        return ResultUtils.success(result);
    }

    /**
     * 查询某个账本下所有账目
     */
    @PostMapping("/listByLedger")
    public BaseResponse<List<Entry>> listLedgerEntries(@RequestBody EntryQueryRequest entryQueryRequest, HttpServletRequest request) {
        if (entryQueryRequest == null || entryQueryRequest.getLedgerId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "查询条件不能为空");
        }

        Long ledgerId = entryQueryRequest.getLedgerId();
        User user = userService.getLoginUser(request);
        ThrowUtils.throwIf(!ledgerUserService.isMember(ledgerId, user.getId()),
                ErrorCode.NO_AUTH_ERROR, "用户无权限查看该账本");

        List<Entry> list = entryService.listEntriesByCondition(ledgerId, entryQueryRequest.getUserId(),
                entryQueryRequest.getDate(), entryQueryRequest.getCategory(), entryQueryRequest.getKeyword(),
                entryQueryRequest.getOrderBy(), entryQueryRequest.getOrderDirection());
        return ResultUtils.success(list);
    }

    /**
     * 查询某个账本下用户的所有账目
     */
    @PostMapping("/listByUser")
    public BaseResponse<List<Entry>> listLedgerEntriesByUserId(
            @RequestBody EntryQueryRequest entryQueryRequest, HttpServletRequest request
    ) {
        Long ledgerId = entryQueryRequest.getLedgerId();
        Long userId = entryQueryRequest.getUserId();
        if (ledgerId == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账本和用户ID不能为空");
        }

        User user = userService.getLoginUser(request);
        ThrowUtils.throwIf(!ledgerUserService.isMember(ledgerId, user.getId()),
                ErrorCode.NO_AUTH_ERROR, "用户无权限查看该账本");

        List<Entry> list = entryService.listEntriesByCondition(ledgerId, userId,
                entryQueryRequest.getDate(), entryQueryRequest.getCategory(), entryQueryRequest.getKeyword(),
                entryQueryRequest.getOrderBy(), entryQueryRequest.getOrderDirection());
        return ResultUtils.success(list);
    }

    /**
     * 查询当前登录用户的所有账目
     */
    @PostMapping("/my/list")
    public BaseResponse<List<Entry>> listLoginUserEntries(
            @RequestBody EntryQueryRequest entryQueryRequest, HttpServletRequest request
    ) {
        User user = userService.getLoginUser(request);

        List<Entry> list = entryService.listEntriesByCondition(entryQueryRequest.getLedgerId(), user.getId(),
                entryQueryRequest.getDate(), entryQueryRequest.getCategory(), entryQueryRequest.getKeyword(),
                entryQueryRequest.getOrderBy(), entryQueryRequest.getOrderDirection());
        return ResultUtils.success(list);
    }
}
