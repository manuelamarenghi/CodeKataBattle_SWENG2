package ckb.TournamentManager.service;

import ckb.TournamentManager.dto.GetTournamentPageRequest;
import ckb.TournamentManager.dto.NewTournamentRequest;
import ckb.TournamentManager.dto.PermissionRequest;
import ckb.TournamentManager.dto.SubscriptionRequest;
import ckb.TournamentManager.model.Permission;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.repo.PermissionRepo;
import ckb.TournamentManager.repo.TournamentRankingRepo;
import ckb.TournamentManager.repo.TournamentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor

public class TournamentService {
    private TournamentRepo tournamentRepo;
    private TournamentRankingRepo tournamentRankingRepo;
    private PermissionRepo permissionRepo;

    public String createTournament(NewTournamentRequest request) {
        Tournament tournament = new Tournament();
        tournament.setRegdeadline(request.getReg_deadline());
        tournament.setStatus(true);
        tournamentRepo.save(tournament);
        String tournamentUrl = "http://tournament-service/tournaments/" + tournament.getTournamentID();
        return tournamentUrl;
    }

    public Tournament getTournament(Long id) {
        return tournamentRepo.findByTournamentID(id).orElse(null);
    }

    public void deleteTournament(Long id) {
        tournamentRepo.deleteById(id);
    }
    public boolean isSubscribed(Long tournamentID, Long userID){
        return tournamentRankingRepo.findByTournamentIDAndUserID(tournamentID,userID).isPresent();
    }

    public void addSubscription(SubscriptionRequest request) {
        Tournament tournament = tournamentRepo.findByTournamentID(request.getTournamentId()).orElse(null);
        TournamentRanking ranking = new TournamentRanking();
        if (tournament == null) return;
        ranking.setTournamentID(request.getTournamentId());
        ranking.setScore(0);
        ranking.setUserID(request.getUserId());
        tournamentRankingRepo.save(ranking);
    }

    public String addPermission(PermissionRequest request) {
        Permission p = new Permission(request.getTournamentId(), request.getUserId());
        permissionRepo.save(p);
        String tournamentUrl = "http://tournament-service/tournaments/" + request.getTournamentId();
        return tournamentUrl;
    }

    public List<TournamentRanking> getTournamentPage(GetTournamentPageRequest request) {
        Tournament tournament = tournamentRepo.findByTournamentID(request.getTournamentId()).orElse(null);
        List<TournamentRanking> rankings = tournamentRankingRepo.findByTournamentIDOrderByScore(request.getTournamentId());
        return rankings;
    }

    public void closeTournament(Long tournamentId) {
        Tournament tournament = tournamentRepo.findByTournamentID(tournamentId).orElse(null);
        tournament.setStatus(false);
        tournamentRepo.save(tournament);
    }

}
