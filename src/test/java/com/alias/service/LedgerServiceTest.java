package com.alias.service;

import com.alias.model.entity.Ledger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional  // 每个测试执行完自动回滚事务，避免污染数据
public class LedgerServiceTest {

    @Resource
    private LedgerService ledgerService;

    /**
     * 创建账本测试
     */
    @Test
    public void testCreateLedger() {
        Ledger ledger = new Ledger();
        ledger.setName("测试账本");
        ledger.setDescription("这是一个测试账本");

        boolean result = ledgerService.createLedger(ledger, 1L); // 假设 userId 为 1
        assertTrue(result);
        assertNotNull(ledger.getId());
    }

    /**
     * 查询账本测试
     */
    @Test
    public void testGetLedgerById() {
        Ledger ledger = new Ledger();
        ledger.setName("查询账本");
        ledgerService.createLedger(ledger, 1L);

        Ledger found = ledgerService.getLedgerById(ledger.getId());
        assertNotNull(found);
        assertEquals("查询账本", found.getName());
    }

    /**
     * 查询用户账本列表测试
     */
    @Test
    public void testListLedgersByUserId() {
        Ledger ledger1 = new Ledger();
        ledger1.setName("账本1");
        ledgerService.createLedger(ledger1, 2L);

        Ledger ledger2 = new Ledger();
        ledger2.setName("账本2");
        ledgerService.createLedger(ledger2, 2L);

        List<Ledger> ledgers = ledgerService.listLedgersByUserId(2L);
        assertEquals(2, ledgers.size());
    }

    /**
     * 更新账本测试
     */
    @Test
    public void testUpdateLedger() {
        Ledger ledger = new Ledger();
        ledger.setName("旧账本");
        ledgerService.createLedger(ledger, 1L);

        ledger.setName("新账本");
        boolean updated = ledgerService.updateLedger(ledger);
        assertTrue(updated);

        Ledger updatedLedger = ledgerService.getLedgerById(ledger.getId());
        assertEquals("新账本", updatedLedger.getName());
    }

    /**
     * 删除账本测试（逻辑删除建议额外验证 deleteTime 字段）
     */
    @Test
    public void testDeleteLedger() {
        Ledger ledger = new Ledger();
        ledger.setName("待删除账本");
        ledgerService.createLedger(ledger, 1L);

        boolean deleted = ledgerService.deleteLedger(ledger.getId());
        assertTrue(deleted);

        Ledger found = ledgerService.getLedgerById(ledger.getId());
        assertNull(found); // 假设 deleteLedger 是逻辑删除 + 查询时忽略逻辑删除
    }
}
