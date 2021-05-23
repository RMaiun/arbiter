package com.arbiter.core.dto.dump;

import com.arbiter.core.domain.Player;
import com.arbiter.core.domain.Round;
import com.arbiter.core.domain.Season;
import java.util.List;

public record ImportDumpData(List<Season> seasonList,
                             List<Player> playersList,
                             List<Round> roundsList) {

}
