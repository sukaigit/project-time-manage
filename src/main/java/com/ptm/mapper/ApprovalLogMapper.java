package com.ptm.mapper;

import com.ptm.entity.ApprovalLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApprovalLogMapper {

    int insert(ApprovalLog approvalLog);

    int deleteById(@Param("id") Long id);

    int deleteByTimeEntryId(@Param("timeEntryId") Long timeEntryId);

    ApprovalLog selectById(@Param("id") Long id);

    List<ApprovalLog> selectByTimeEntryId(@Param("timeEntryId") Long timeEntryId);

    List<ApprovalLog> selectList(@Param("timeEntryId") Long timeEntryId,
                                 @Param("approverId") Long approverId,
                                 @Param("action") Integer action);

}
