package ckb.BattleManager.controller;

/*@RestController
@RequestMapping("/api/team")
@Slf4j
public class TeamController {
    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @GetMapping("/team")
    public ResponseEntity<Team> getTeam(@RequestBody IdLong idTeam) {
        log.info("[API REQUEST] Get team request with id: {}", idTeam.getId());
        try {
            return ResponseEntity.ok(teamService.getTeam(idTeam.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/teams")
    public ResponseEntity<List<Team>> getTeamsOfBattle(@RequestBody IdLong idBattle) {
        log.info("[API REQUEST] Get teams of battle request with id: {}", idBattle.getId());
        try {
            return ResponseEntity.ok(teamService.getListTeam(idBattle.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Object> assignScore(@RequestBody PairTeamScore request) {
        log.info("[API REQUEST] Assign score request with id_team: {}, score: {}", request.getIdTeam(), request.getScore());
        try {
            teamService.assignScore(request.getIdTeam(), request.getScore());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    public ResponseEntity<Object> assignPersonalScore(@RequestBody PairTeamScore request) {
        log.info("[API REQUEST] Assign personal score request with id_team: {}, score: {}", request.getIdTeam(), request.getScore());
        try {
            teamService.assignPersonalScore(request.getIdTeam(), request.getScore());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //TODO: finish because i need also the battle_id
    public ResponseEntity<Object> registerStudentToTeam(@RequestBody StudentTeam request) {
        log.info("[API REQUEST] Register student to team request with id_team: {}, id_student: {}", request.getIdTeam(), request.getIdStudent());
        try {
            teamService.registerStudentToTeam(request.getIdStudent(), request.getIdTeam());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //TODO: finish because i need also the battle_id
    public ResponseEntity<Object> inviteStudentToTeam(@RequestBody StudentTeam request) {
        log.info("[API REQUEST] Invite student to team request with id_team: {}, id_student: {}", request.getIdTeam(), request.getIdStudent());
        try {
            teamService.inviteStudentToTeam(request.getIdStudent(), request.getIdTeam());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
*/