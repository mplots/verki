package lv.verku.viktorina.jdbc.dao;

import lv.verku.viktorina.jdbc.dto.Voter;
import org.springframework.stereotype.Repository;

@Repository
public class VoterDao extends BaseDao<Voter> {

    @Override
    public void upsert(Voter voter) {
        Long id = jdbcTemplate.queryForObject(
        "INSERT INTO voter (profile_id, pool_tally_id) VALUES (?,?) " +
            "ON CONFLICT (profile_id, pool_tally_id) DO " +
            "UPDATE SET profile_id=excluded.profile_id, pool_tally_id=excluded.pool_tally_id " +
            "RETURNING id;",
            new Object[] {voter.getProfileId(), voter.getPoolTallyId()},
            Long.class
        );
        voter.setId(id);
    }

}
