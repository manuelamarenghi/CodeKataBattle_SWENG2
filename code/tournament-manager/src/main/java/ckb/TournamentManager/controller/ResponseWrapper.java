package ckb.TournamentManager.controller;

import ckb.TournamentManager.model.TournamentRanking;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper implements Serializable {
    private List<Long> battles;
    private List<TournamentRanking> tournamentRankings;
}
