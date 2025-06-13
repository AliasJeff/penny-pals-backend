package com.alias.mapper;

import com.alias.model.entity.Entry;
import com.alias.model.vo.EntryVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author alias
* @description 针对表【entry(账目记录表)】的数据库操作Mapper
* @createDate 2025-06-09 21:47:07
* @Entity com.alias.model.entity.Entry
*/
public interface EntryMapper extends BaseMapper<Entry> {

    List<EntryVO> listEntriesWithUser(@Param("ledgerId") Long ledgerId,
                                      @Param("userId") Long userId,
                                      @Param("date") Date date,
                                      @Param("category") String category,
                                      @Param("keyword") String keyword,
                                      @Param("orderBy") String orderBy,
                                      @Param("orderDirection") String orderDirection);

    List<EntryVO> listUserEntriesBetween(
            @Param("ledgerId") Long ledgerId,
            @Param("userId") Long userId,
            @Param("start") Date start,
            @Param("end") Date end);

    List<EntryVO> listEntriesByLedgerIds(@Param("ledgerIds") List<Long> ledgerIds);

}




