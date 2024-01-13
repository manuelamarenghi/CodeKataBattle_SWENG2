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