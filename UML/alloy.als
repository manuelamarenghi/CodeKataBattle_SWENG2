sig Email {
	
}

sig Student {
	email: one Email
}

fact anEmailIsAssociatedToOnlyOneStudent {
	all disj s1, s2 : Student | #(s1.email & s2.email) = 0
}

sig Team {
	students: some Student
}

fact aStudentCannotBeInTwoTeams {
	all disj t1, t2 : Team | #(t1.students & t2.students) = 0
}

sig Battle {
	
}

sig Tournament {
	battles: some Battle
}

fact battleInOnlyOneTournament {
	all disj t1, t2 : Tournament | #(t1.battles & t2.battles) = 0
}



pred show { }

run show for 3 but 3 Tournament
