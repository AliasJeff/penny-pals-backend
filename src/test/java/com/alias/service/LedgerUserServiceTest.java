package com.alias.service;

import com.alias.common.ErrorCode;
import com.alias.exception.BusinessException;
import com.alias.mapper.LedgerUserMapper;
import com.alias.model.entity.LedgerUser;
import com.alias.model.enums.LedgerRoleEnum;
import com.alias.service.impl.LedgerUserServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class LedgerUserServiceTest {

    @InjectMocks
    private LedgerUserServiceImpl ledgerUserService;

    @Mock
    private LedgerUserMapper ledgerUserMapper;

    private LedgerUser testLedgerUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        testLedgerUser = new LedgerUser();
        testLedgerUser.setId(1L);
        testLedgerUser.setUserId(100L);
        testLedgerUser.setLedgerId(200L);
        testLedgerUser.setRole(LedgerRoleEnum.OWNER.getValue());
    }

    @Test
    void testAddLedgerUser_success() {
        when(ledgerUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        when(ledgerUserMapper.insert(any(LedgerUser.class))).thenReturn(1);

        boolean result = ledgerUserService.addLedgerUser(testLedgerUser);
        assertTrue(result);
    }

    @Test
    void testAddLedgerUser_alreadyExists() {
        when(ledgerUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(testLedgerUser);
        BusinessException exception = assertThrows(BusinessException.class, () ->
                ledgerUserService.addLedgerUser(testLedgerUser));
        assertEquals(ErrorCode.OPERATION_ERROR.getCode(), exception.getCode());
    }

    @Test
    void testAddLedgerUser_invalidParams() {
        assertThrows(BusinessException.class, () ->
                ledgerUserService.addLedgerUser(null));
    }

    @Test
    void testIsUserOwner_true() {
        when(ledgerUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(testLedgerUser);
        boolean result = ledgerUserService.isUserOwner(200L, 100L);
        assertTrue(result);
    }

    @Test
    void testIsUserOwner_false() {
        when(ledgerUserMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);
        boolean result = ledgerUserService.isUserOwner(200L, 101L);
        assertFalse(result);
    }

    @Test
    void testExitLedger_success() {
        when(ledgerUserMapper.update(any(LedgerUser.class), any(QueryWrapper.class))).thenReturn(1);
        boolean result = ledgerUserService.exitLedger(200L, 100L);
        assertTrue(result);
    }

    @Test
    void testExitLedger_invalidParams() {
        assertThrows(BusinessException.class, () ->
                ledgerUserService.exitLedger(null, 100L));
    }

    @Test
    void testLogicDeleteByUserId_success() {
        when(ledgerUserMapper.update(any(LedgerUser.class), any(QueryWrapper.class))).thenReturn(1);
        boolean result = ledgerUserService.logicDeleteByUserId(100L);
        assertTrue(result);
    }

    @Test
    void testRemoveUserFromLedger_success() {
        when(ledgerUserMapper.update(any(LedgerUser.class), any(QueryWrapper.class))).thenReturn(1);
        boolean result = ledgerUserService.removeUserFromLedger(200L, 100L);
        assertTrue(result);
    }

    @Test
    void testRemoveUserFromLedger_invalid() {
        assertThrows(BusinessException.class, () -> ledgerUserService.removeUserFromLedger(null, 100L));
    }
}