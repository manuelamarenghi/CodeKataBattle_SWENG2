package ckb.TournamentManager.service;

import ckb.TournamentManager.dto.incoming.*;
import ckb.TournamentManager.model.Permission;
import ckb.TournamentManager.model.Tournament;
import ckb.TournamentManager.model.TournamentRanking;
import ckb.TournamentManager.repo.PermissionRepo;
import ckb.TournamentManager.repo.TournamentRankingRepo;
import ckb.TournamentManager.repo.TournamentRepo;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class TournamentService {
    private final TournamentRepo tournamentRepo;
    private final TournamentRankingRepo tournamentRankingRepo;
    private final PermissionRepo permissionRepo;

    public String createTournament(NewTournamentRequest request) {
        Tournament tournament = new Tournament();
        tournament.setRegdeadline(request.getRegdeadline());
        tournament.setStatus(true);
        tournament.setCreatorID(request.getCreatorID());
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

    public void addSubscription(@NotNull SubscriptionRequest request) {
        Tournament tournament = tournamentRepo.findByTournamentID(request.getTournamentID()).orElse(null);
        TournamentRanking ranking = new TournamentRanking();
        if (tournament == null) return;
        ranking.setTournamentID(request.getTournamentID());
        ranking.setUserID(request.getUserID());
        ranking.setScore(0);
        tournamentRankingRepo.save(ranking);
    }

    public String addPermission(PermissionRequest request) {
        Permission p = new Permission(request.getTournamentID(), request.getUserID());
        Tournament t = tournamentRepo.findByTournamentID(request.getTournamentID()).orElse(null);
        if(t.getCreatorID() == request.getCreatorID()){
        permissionRepo.save(p);
        String tournamentUrl = "http://tournament-service/tournaments/" + request.getTournamentID();
        return tournamentUrl;
        }
        else return null;
    }

    public List<TournamentRanking> getTournamentPage(GetTournamentPageRequest request) {
        List<TournamentRanking> rankings = tournamentRankingRepo.findAllByTournamentIDOrderByScoreDesc(request.getTournamentID());
        return rankings;
    }
    public List<Tournament> getAllTournaments(){
        return tournamentRepo.findAll();
    }

    public boolean closeTournament(CloseTournamentRequest request) {
        Tournament tournament = tournamentRepo.findByTournamentID(request.getTournamentID()).orElse(null);
        if(tournament.getCreatorID() == request.getCreatorID()){
            tournament.setStatus(false);
            tournamentRepo.save(tournament);
            List<Permission> p = permissionRepo.findAllByTournamentID(request.getTournamentID());
            for (Permission permission : p) {
                permissionRepo.delete(permission);
            }
            return true;
        }
        else return false;
    }

    public boolean permissionExists(Long tournamentID, Long userID) {
        return permissionRepo.findByTournamentIDAndUserID(tournamentID,userID).isPresent();
    }

    public boolean updateScore(UpdateScoreRequest request){
        List<TournamentRanking> records = tournamentRankingRepo.findAllByTournamentID(request.getTournamentID());
        System.out.println("before: "+records);
        if(records == null) return false;
        for(TournamentRanking student : records){
            if(request.getScores().containsKey(student.getUserID())){
                student.setScore(student.getScore() + request.getScores().get(student.getUserID()));
                tournamentRankingRepo.save(student);
            }
        }
        records = tournamentRankingRepo.findAllByTournamentID(request.getTournamentID());
        System.out.println("after : "+records);
        return true;
    }
}
