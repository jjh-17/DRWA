package com.a708.drwa.game.repository;

import com.a708.drwa.game.domain.Record;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RecordBulkRepository {
    private final JdbcTemplate recordJdbcTemplate;

    @Transactional
    public void saveAll(List<Record> records) {
        String sql
                = "INSERT INTO record (member_id, game_id, result, team)"
                + "VALUES (?, ?, ?, ?)";

        recordJdbcTemplate.batchUpdate(
                sql,
                records,
                records.size(),
                (PreparedStatement ps, Record record) -> {
                    ps.setInt(1, record.getMember().getId());
                    ps.setInt(2, record.getGameInfo().getGameId());
                    ps.setObject(3, record.getRecordId());
                    ps.setObject(4, record.getTeam());
                });
    }
}
