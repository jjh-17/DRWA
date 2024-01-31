package com.a708.drwa.game.repository;

import com.a708.drwa.game.domain.Record;
import com.a708.drwa.game.exception.RecordErrorCode;
import com.a708.drwa.game.exception.RecordException;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RecordBulkRepository {
    private final JdbcTemplate recordJdbcTemplate;

    @Transactional
    public void saveAll(List<Record> records) {
        final String sql
                = "INSERT INTO record (member_id, game_id, result, team)\n"
                + "VALUES (?, ?, ?, ?)";

        try {
            recordJdbcTemplate.batchUpdate(
                    sql,
                    new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            Record record = records.get(i);
                            ps.setInt(1, record.getMember().getId());
                            ps.setInt(2, record.getGameInfo().getGameId());
                            ps.setInt(3, record.getResult().ordinal());
                            ps.setInt(4, record.getTeam().ordinal());
                        }

                        @Override
                        public int getBatchSize() {
                            return records.size();
                        }
                    });
        } catch(Exception e) {
            throw new RecordException(RecordErrorCode.RECORD_CREATE_FAIL);
        }

    }
}
