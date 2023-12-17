sig Student {
	
}

sig Submission {
	
}

sig Team {
	team_students: some Student,
	team_submissions: set Submission
}

enum BattleStatus { RegistrationPhase, SubmissionPhase, Closed }

sig Battle {
	teams_registered: set Team,
	min_members: one Int,
	max_members: one Int,
	battle_status: one BattleStatus,
	battle_submissions: set Submission
} {
	min_members > 0
	max_members >= min_members
}

sig Tournament {
	tournament_battles: set Battle
}

// Tournaments should not have battles in common
fact {
	all disj t1, t2 : Tournament | #(t1.tournament_battles & t2.tournament_battles) = 0
}

// In all the battles that are started the number of member of the team
// should respect the battle's rule
fact {
	all b : Battle, t : b.teams_registered | 
	b.battle_status != RegistrationPhase implies
	#(t.team_students) >= b.min_members and
	#(t.team_students) <= b.max_members
}

// A student cannot be in two different teams in the same battle
fact {
	all b : Battle | all disj t1, t2 : Team | 
	(t1 in b.teams_registered and t2 in b.teams_registered) implies
	#(t1.team_students & t2.team_students) = 0
}

// A submission can only be performed by a single team
fact aSolutionCannotBelongToTwoTeams {
	all s : Submission | one t : Team | s in t.team_submissions
}

// All the battles should belong to a tournament
fact allBattlesInATournament {
	all b : Battle | one t : Tournament | b in t.tournament_battles
}

// All the teams should belong to a battle
fact allTeamsInABattle {
	all t : Team | one b : Battle | t in b.teams_registered
}

// A battle can have submissions only if it is in a SubmissionPhase or
// it is Closed (because it had submissions before)
fact {
	all b : Battle | #(b.battle_submissions) > 0 implies (b.battle_status = SubmissionPhase or b.battle_status = Closed)
}

// For all the battles a submission belongs to only one team
fact {
	all b : Battle, s : b.battle_submissions | one t : Team | s in t.team_submissions and t in b.teams_registered
}

// There are no submissions during the registration phase
assert no_submission_in_registration_phase {
	all b: Battle | b.battle_status = RegistrationPhase implies #(b.battle_submissions) = 0
}

// If a team is subscribed in a battle the number of participants must follow the rule
assert number_students_follow_the_conditions {
	all t : Team, b : Battle | t in b.teams_registered implies 
	#(t.team_students) >= b.min_members and #(t.team_students) <= b.max_members
}
 
// A student could not be in team
assert student_could_not_be_in_team {
	all t : Team | some s : Student | s not in t.team_students
}

// A team can submit more than one solution to a single battle
assert more_than_one_submissions {
	all b : Battle, t : b.teams_registered | #(t.team_submissions & b.battle_submissions) > 1
}

check no_submission_in_registration_phase
check number_students_follow_the_conditions
check student_could_not_be_in_team
check more_than_one_submissions

pred show {
	#Tournament <= 1
	#Battle <= 1
	#Team <= 3
	#Student <= 4
	#Submission <= 5
}

//run show
