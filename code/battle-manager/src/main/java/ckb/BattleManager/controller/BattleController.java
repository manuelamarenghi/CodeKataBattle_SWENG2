package ckb.BattleManager.controller;

/*@RestController
@RequestMapping("/api/battle")
@Slf4j
public class BattleController {
    private final BattleService battleService;

    @Autowired
    public BattleController(BattleService battleService) {
        this.battleService = battleService;
    }

    public ResponseEntity<Object> joinBattle(@RequestBody StudentBattle request) {
        log.info("[API REQUEST] Join battle request with id_battle: {}, id_student: {}", request.getIdBattle(), request.getIdStudent());
        try {
            battleService.joinBattle(request.getIdStudent(), request.getIdBattle());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Object> leaveBattle(@RequestBody StudentBattle request) {
        log.info("[API REQUEST] Leave battle request with id_battle: {}, id_student: {}", request.getIdBattle(), request.getIdStudent());
        battleService.leaveBattle(request.getIdStudent(), request.getIdBattle());
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<List<TournamentRanking>> getTournamentRanking(Long idTournament) {
        log.info("[API REQUEST] Get tournament ranking request with id_tournament: {}", idTournament);
        return ResponseEntity.ok(battleService.getTournamentRanking(idTournament));
    }

}*/
