package com.kt.sqms.batch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * PostgreSQL 병렬 쿼리 모니터링 및 제어용 Mapper
 *
 * 목적:
 * - 병렬 워커 세션 수 모니터링
 * - 쿼리 취소 및 세션 제어
 *
 * @author Claude Code
 * @since 2026-01-12
 */
@Mapper
public interface ParallelQueryMapper {

    /**
     * 현재 세션의 PostgreSQL 백엔드 PID 조회
     *
     * @return 현재 세션의 PID
     */
    Integer selectCurrentPid();

    /**
     * 특정 리더 PID의 병렬 워커 세션 수 조회
     *
     * @param leaderPid 리더 프로세스 PID
     * @return 병렬 워커 세션 수
     */
    int selectParallelWorkerCount(@Param("leaderPid") Integer leaderPid);

    /**
     * 특정 PID의 쿼리 취소 (우아한 중단)
     *
     * @param pid 취소할 세션의 PID
     * @return 취소 성공 여부
     */
    boolean cancelQuery(@Param("pid") Integer pid);

    /**
     * 특정 PID의 세션 강제 종료
     *
     * @param pid 종료할 세션의 PID
     * @return 종료 성공 여부
     */
    boolean terminateSession(@Param("pid") Integer pid);
}
