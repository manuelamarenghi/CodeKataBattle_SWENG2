function createTournament() {
    var deadlineInput = document.getElementById("deadline");
    var deadlineValue = deadlineInput.value ;
    var deadlineDate = new Date(deadlineValue);
    console.log(deadlineDate);
    var fetchOptions = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            creatorID: localStorage.getItem("user_id"),
            name: document.getElementById("TournamentName").value,
            regdeadline: deadlineDate,
        })
    };
    console.log(fetchOptions);
    fetch("http://localhost:8080/api/tournament/new-tournament",fetchOptions)
        .then(function (response) {
            if (response.status <300) {
                alert("Torneo creato con successo.");
                window.location.href = "tournaments.html";
            } else {
                alert("Errore durante la creazione del torneo. Riprova.");
                if (response.status >= 400) {
                    window.location.href = "tournaments.html";
                }
            }
            closeModal('createTournamentModal');
        })
        .catch(function (err) {
            alert("Errore durante la richiesta.");
            window.location.href = "index.html";
            closeModal('createTournamentModal');    
        })
}

function grantPermission(){
    var educatorRoleId = document.getElementById("educatorRole").value;
    var tournamentId = document.getElementById("tournamentId").value;   
    var permissionValue = document.querySelector('input[name="permission"]:checked');
    if (permissionValue && permissionValue.value === "yes"){
    var fetchOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                tournamentID: tournamentId,
                userID: educatorRoleId,
                creatorID: localStorage.getItem("user_id"),
            }),
    };
    fetch("http://localhost:8080/api/tournament/permission", fetchOptions)
            .then(function (response) {
                if (response.status <300) {
                    alert("Permesso creato.");
                    window.location.href = "tournaments.html";
                } else {
                    alert("Permesso non aggiunto. Riprova.");
                    if (response.status >= 400) {
                        window.location.href = "tournaments.html";
                    }
                }
            })
            .catch(error => {
                console.error("Errore nella richiesta HTTP:", error);
            });
    } else {
        console.log("Permission not granted");
    }
    closeModal('PermissionModal');
}

function updateUserInfo(){
     var fetchOptions = {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                id: localStorage.getItem("user_id"),
                email: document.getElementById("email").value,
                fullName: document.getElementById("fullName").value,
                password: document.getElementById("password").value,
                role: localStorage.getItem("user_role"),
            }),
    }
    console.log(fetchOptions);
    fetch("http://localhost:8080/api/account/update", fetchOptions)
        .then(function (response) {
            if (response.status <300) {
                alert("Informazioni aggiornate con successo.");
                localStorage.removeItem("logged_in", localStorage.getItem("logged_in"));
            localStorage.removeItem("user_id", localStorage.getItem("user_id"));
            localStorage.removeItem("user_email", localStorage.getItem("user_email"));
            localStorage.removeItem("user_role", localStorage.getItem("user_role"));
                window.location.href = "login.html";
            } else {
                alert("Errore durante l'aggiornamento delle informazioni. Riprova.");
                if (response.status >= 400) {
                    window.location.href = "account.html";
                }
            }
        })

}



